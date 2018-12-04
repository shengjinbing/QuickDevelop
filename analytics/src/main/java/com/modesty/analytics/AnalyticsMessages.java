package com.modesty.analytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.ArrayMap;


import com.modesty.analytics.net.HttpMethod;
import com.modesty.analytics.net.HttpService;
import com.modesty.analytics.net.NetConstants;
import com.modesty.analytics.net.RemoteService;
import com.modesty.analytics.utils.JSONUtils;
import com.modesty.analytics.utils.Logger;
import com.modesty.analytics.utils.TrackerConstants;
import com.modesty.analytics.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Manage communication of events with the internal database and servers.
 *
 * @author lixiang
 * @since 2018/5/18
 */
/* package */ class AnalyticsMessages {
    private static final String LOGTAG = "AnalyticsMessages";

    private final Object mHandlerLock = new Object();
    private final long RETRY_INTERVAL_ON_FLUSH_FAILED = 10 * 1000L;

    // Messages for worker thread
    private static final int ENQUEUE_EVENTS = 1;
    private static final int FLUSH_QUEUE = 2;
    private static final int HARD_KILL = 3;
    private static final int SOFT_KILL = 4;
    private static final int UPLOAD_APP_LIST = 5;

    private final Context mContext;
    private final AnalyticsConfig mConfig;

    private Handler mHandler;
    private boolean mHasUploadedAppList = true;

    @SuppressLint("StaticFieldLeak")
    private static volatile AnalyticsMessages sInstance;

    private AnalyticsMessages(final Context context) {
        mContext = context;
        mConfig = getConfig(context);
        mHandler = createHandler();
    }

    public static AnalyticsMessages getInstance(final Context context) {
        if(sInstance == null){
            synchronized (AnalyticsMessages.class){
                if(sInstance == null){
                    sInstance = new AnalyticsMessages(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    void eventsMessage(final JSONObject eventDescription) {
        final Message m = Message.obtain();
        m.what = ENQUEUE_EVENTS;
        m.obj = eventDescription;

        runMessage(m);
    }

    void uploadUserAppList() {
        final Message m = Message.obtain();
        m.what = UPLOAD_APP_LIST;

        runMessage(m);
    }

    void postToServer() {
        final Message m = Message.obtain();
        m.what = FLUSH_QUEUE;

        runMessage(m);
    }

    void hardKill() {
        final Message m = Message.obtain();
        m.what = HARD_KILL;

        runMessage(m);
    }

    void softKill() {
        final Message m = Message.obtain();
        m.what = SOFT_KILL;

        runMessage(m);
    }

    private boolean isDead() {
        synchronized (mHandlerLock) {
            return mHandler == null;
        }
    }

    private void runMessage(Message msg) {
        synchronized (mHandlerLock) {
            if (mHandler == null) {
                // We died under suspicious circumstances. Don't try to send any more events.
                logAboutMessage("handler is dead, dropping a message: " + msg.what);
            } else {
                mHandler.sendMessage(msg);
            }
        }
    }

    private Handler createHandler() {
        final HandlerThread thread = new HandlerThread("AnalyticsMessages", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        return new AnalyticsMessageHandler(thread.getLooper());
    }

    private DbAdapter makeDbAdapter(Context context) {
        return new DbAdapter(context);
    }

    private AnalyticsConfig getConfig(Context context) {
        return AnalyticsConfig.getInstance(context);
    }

    private RemoteService getPoster() {
        return new HttpService();
    }

    private void logAboutMessage(String message) {
        Logger.v(LOGTAG, message + " (Thread " + Thread.currentThread().getId() + ")");
    }

    private void logAboutMessage(String message, Throwable e) {
        Logger.e(LOGTAG, message + " (Thread " + Thread.currentThread().getId() + ")", e);
    }

    private class AnalyticsMessageHandler extends Handler {
        private final long mFlushInterval;
        private final boolean mDisableFallback;
        private final DbAdapter mDbAdapter;

        private long mFlushCount = 0;
        private long mAveFlushFrequency = 0;
        private long mLastFlushTime = -1;

        AnalyticsMessageHandler(Looper looper) {
            super(looper);
            mDbAdapter = makeDbAdapter(mContext);
            mDisableFallback = mConfig.getDisableFallback();
            mFlushInterval = mConfig.getFlushInterval();
        }

        private void updateFlushFrequency() {
            final long now = System.currentTimeMillis();
            final long newFlushCount = mFlushCount + 1;

            if (mLastFlushTime > 0) {
                final long flushInterval = now - mLastFlushTime;
                final long totalFlushTime = flushInterval + (mAveFlushFrequency * mFlushCount);
                mAveFlushFrequency = totalFlushTime / newFlushCount;

                final long seconds = mAveFlushFrequency / 1000;
                logAboutMessage("Average send frequency approximately " + seconds + " seconds.");
            }

            mLastFlushTime = now;
            mFlushCount = newFlushCount;
        }

        private void cleanExpiredEvents() {
            mDbAdapter.cleanupEvents(System.currentTimeMillis() - mConfig.getDataExpiration(), DbAdapter.Table.EVENTS);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                int returnCode = DbAdapter.DB_UNDEFINED_CODE;

                if (msg.what == ENQUEUE_EVENTS) {
                    final JSONObject message = (JSONObject) msg.obj;
                    logAboutMessage("Queuing event for sending later");
                    logAboutMessage("    " + message.toString());
                    returnCode = mDbAdapter.addJSON(message, DbAdapter.Table.EVENTS);
                } else if (msg.what == FLUSH_QUEUE) {
                    logAboutMessage("Flushing queue due to scheduled or forced flush");
                    updateFlushFrequency();
                    sendAllData(mDbAdapter, TrackerConstants.DATA_TYPE_EVENT);
                } else if (msg.what == UPLOAD_APP_LIST) {
                    logAboutMessage("send user app list");
                    sendUserAppListInternal();
                } else if (msg.what == HARD_KILL) {
                    Logger.w(LOGTAG, "Worker received a hard kill. Dumping all events and force-killing. Thread id " + Thread.currentThread().getId());
                    synchronized (mHandlerLock) {
                        mDbAdapter.deleteDB();
                        Looper.myLooper().quit();
                        mHandler = null;
                    }
                } else if (msg.what == SOFT_KILL) {
                    Logger.w(LOGTAG, "Worker received a soft kill. Thread id " + Thread.currentThread().getId());
                    synchronized (mHandlerLock) {
                        Looper.myLooper().quit();
                        mHandler = null;
                    }
                } else {
                    Logger.e(LOGTAG, "Unexpected message received by AnalyticsMessages worker: " + msg);
                }

                if (returnCode >= mConfig.getBulkUploadLimit() ||
                        returnCode == DbAdapter.DB_OUT_OF_MEMORY_ERROR) {
                    logAboutMessage("Flushing queue due to bulk upload limit");
                    updateFlushFrequency();
                    sendAllData(mDbAdapter, TrackerConstants.DATA_TYPE_EVENT);
                } else if (returnCode > 0 && !hasMessages(FLUSH_QUEUE) &&
                        System.currentTimeMillis() - mLastFlushTime >= mConfig.getFlushInterval()) {
                    logAboutMessage("Queue depth " + returnCode + " - Adding flush in " + mFlushInterval);
                    sendEmptyMessage(FLUSH_QUEUE);
                }
            } catch (final RuntimeException e) {
                Logger.e(LOGTAG, "Worker threw an unhandled exception", e);
                synchronized (mHandlerLock) {
                    mHandler = null;
                    try {
                        Looper.myLooper().quit();
                        Logger.e(LOGTAG, "Analytics will not process any more analytics messages", e);
                    } catch (final Exception tooLate) {
                        Logger.e(LOGTAG, "Could not halt looper", tooLate);
                    }
                }
            }
        }

        private void sendAllData(DbAdapter dbAdapter, int dataType) {
            if (!Utils.isNetworkConnected(mContext)) {
                logAboutMessage("Not flushing data to server because the device is not connected to the internet.");
                return;
            }

            if (dataType == TrackerConstants.DATA_TYPE_USER_APP_LIST) {
                sendUserAppListInternal();
            } else if(dataType == TrackerConstants.DATA_TYPE_EVENT){
                if (mDisableFallback) {
                    sendData(dbAdapter,
                            DbAdapter.Table.EVENTS,
                            new String[]{mConfig.getEventsEndpoint()},
                            dataType);
                } else {
                    sendData(dbAdapter,
                            DbAdapter.Table.EVENTS,
                            new String[]{mConfig.getEventsEndpoint(), mConfig.getEventsFallbackEndpoint()},
                            dataType);
                }
            }
        }

        private Map<String, Object> buildRequestParams(String jsonArray, int dataType) {
            JSONArray _jsonArray = null;
            try {
                _jsonArray = new JSONArray(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return buildRequestParams(_jsonArray, dataType);
        }

        private Map<String, Object> buildRequestParams(JSONArray jsonArray, int dataType) {
            final Map<String, Object> params = new ArrayMap<>();
            try {
                params.putAll(Analytics.getInstance().getCommonParams());
                params.put("info_list", jsonArray);
            } catch (final Exception e) {
                Logger.e(LOGTAG, "AnalyticsMessages#AnalyticsMessageHandler#buildRequestParams " + new JSONObject(params), e);
            }
            return params;
        }

        private void sendUserAppListInternal() {
            if (mHasUploadedAppList) {
                logAboutMessage("user app list has already uploaded");
                return;
            }

            final JSONArray jsonArray = Utils.getInstalledApps(mContext);
            final Map<String, Object> params = buildRequestParams(jsonArray, TrackerConstants.DATA_TYPE_USER_APP_LIST);
            final RemoteService poster = getPoster();
            final String url = mConfig.getEventsEndpoint();

            byte[] response;
            try {
                response = poster.performRequest(HttpMethod.POST, url, params, mConfig.getSSLSocketFactory());

                if (null == response) {
                    logAboutMessage("Response was null, unexpected failure posting to " + url + ".");
                    mHasUploadedAppList = false;
                } else {
                    final JSONObject jsonResponse = JSONUtils.stringToJsonObject(new String(response, Charset.defaultCharset()));
                    final int code = jsonResponse.optInt("status", NetConstants.NO_DATA);
                    if (code == NetConstants.TRACK_OK) {
                        logAboutMessage("Successfully posted to " + url + ": \n" + "");
                        logAboutMessage("Response was " + jsonResponse);
                        mHasUploadedAppList = true;
                    } else {
                        logAboutMessage("Failed posted to " + url + ": \n" + "msg is " + jsonResponse.optString("msg"));
                        mHasUploadedAppList = false;
                    }
                }

            } catch (final Exception e) {
                logAboutMessage("Cannot post message to " + url + ".", e);
                mHasUploadedAppList = false;
            }
        }

        private void sendData(DbAdapter dbAdapter, DbAdapter.Table table, String[] urls, int dataType) {
            final RemoteService poster = getPoster();
            String[] eventsData = dbAdapter.generateDataString(table);
            Integer queueCount = 0;
            if (eventsData != null) {
                queueCount = Integer.valueOf(eventsData[2]);
            }

            while (eventsData != null && queueCount > 0) {
                final String lastId = eventsData[0];
                final String rawMessage = eventsData[1];
                final Map<String, Object> params = buildRequestParams(rawMessage, dataType);

                boolean deleteEvents = false;
                byte[] response;
                for (String url : urls) {
                    try {
                        response = poster.performRequest(HttpMethod.POST, url, params, mConfig.getSSLSocketFactory());

                        if (null == response) {
                            deleteEvents = false;
                            logAboutMessage("Response was null, unexpected failure posting to " + url + ".");
                        } else {
                            final JSONObject jsonResponse = JSONUtils.stringToJsonObject(new String(response, Charset.defaultCharset()));
                            final int code = jsonResponse.optInt("code", NetConstants.NO_DATA);
                            if (code == NetConstants.TRACK_OK) {
                                deleteEvents = true;
                                logAboutMessage("Successfully posted to " + url + ": \n" + rawMessage);
                                logAboutMessage("Response was " + jsonResponse);
                            } else {
                                deleteEvents = false;
                                logAboutMessage("Failed posted to " + url + ": \n" + "msg is " + jsonResponse.optString("msg"));
                            }
                        }

                    } catch (final Exception e) {
                        deleteEvents = false;
                        logAboutMessage("Cannot post message to " + url + ".", e);
                        break;
                    }
                }

                if (deleteEvents) {
                    logAboutMessage("Not retrying this batch of events, deleting them from DB.");
                    dbAdapter.cleanupEvents(lastId, table);
                } else {
                    removeMessages(FLUSH_QUEUE);
                    sendEmptyMessageDelayed(FLUSH_QUEUE, RETRY_INTERVAL_ON_FLUSH_FAILED);
                    logAboutMessage("Retrying this batch of events in " + RETRY_INTERVAL_ON_FLUSH_FAILED + " seconds");
                    break;
                }

                eventsData = dbAdapter.generateDataString(table);
                if (eventsData != null) {
                    queueCount = Integer.valueOf(eventsData[2]);
                }
            }
        }
    }

}
