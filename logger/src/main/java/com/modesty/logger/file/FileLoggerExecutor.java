package com.modesty.logger.file;

import android.text.TextUtils;

import com.modesty.logger.base.LogSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

class FileLoggerExecutor {
    private static final String URL_UPLOAD_LOG = LogSettings.instance().getLogUploadUrl();
    private static final String KEY_LOG_FILE_COUNTER = "log_file_counter";
    private static final String DIR_ZIPPED_LOG_FILE = "dir_zipped_log_file";
    private static final String LOG_FILE_PREFIX = "log_file_";
    private static final int MAX_BYTES_PER_FILE = 10485760;
    private static final int MAX_CHARS_PER_LOG = 10240;
    private static final int CAPACITY_OF_QUEUE = 30;
    private final BlockingQueue<String> mLogQueue;
    private final FileLoggerExecutor.Worker mWorker;
    private final Object mMutex;
    private final AtomicInteger mCounter;
    private File mCurrentActiveFile;
    private OutputStream mOutputStream;
    private volatile boolean mStartLogConsumer;
    private volatile boolean mCreateNewFile;

    private FileLoggerExecutor() {
        this.mWorker = new FileLoggerExecutor.Worker();
        this.mMutex = new Object();
        this.mStartLogConsumer = false;
        this.mCreateNewFile = true;
        this.mLogQueue = new ArrayBlockingQueue(30);
        this.mCounter = new AtomicInteger(SharedPrefsMgr.getInstance(LogSettings.instance().getContext()).getInt("log_file_counter", 0));
    }

    public static FileLoggerExecutor getInstance() {
        return FileLoggerExecutor.SingletonHolder.INSTANCE;
    }

    public void enqueue(String log) {
        Object var2 = this.mMutex;
        synchronized(this.mMutex) {
            this.startLogConsumerWorker();
            this.createNewFile();
        }

        try {
            this.mLogQueue.put(log);
        } catch (InterruptedException var4) {
            var4.printStackTrace();
        }

    }

    private void createNewFile() {
        try {
            if(this.mCreateNewFile) {
                this.mCurrentActiveFile = new File(Utils.getLogFileDirectory(), "log_file_" + this.mCounter.get());
                if(!this.mCurrentActiveFile.getParentFile().exists()) {
                    this.mCurrentActiveFile.getParentFile().mkdirs();
                }

                this.mOutputStream = new FileOutputStream(this.mCurrentActiveFile, true);
                SharedPrefsMgr.getInstance(LogSettings.instance().getContext()).putInt("log_file_counter", this.mCounter.get());
                this.mCreateNewFile = false;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
            this.mCreateNewFile = true;
        }

    }

    private void startLogConsumerWorker() {
        try {
            if(!this.mStartLogConsumer) {
                this.mWorker.setDaemon(true);
                this.mWorker.start();
                this.mStartLogConsumer = true;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
            if(this.mWorker != null) {
                this.mWorker.interrupt();
            }
        }

    }

    private void logToFile(String logMsg) {
        if(this.mOutputStream != null) {
            if(!TextUtils.isEmpty(logMsg) && logMsg.length() <= 10240) {
                try {
                    this.mOutputStream.write(logMsg.getBytes());
                    this.mOutputStream.flush();
                    if(Utils.getTotalBytes(this.mCurrentActiveFile) >= 10485760) {
                        this.uploadFile();
                    }
                } catch (IOException var3) {
                    var3.printStackTrace();
                }

            }
        }
    }

    private void uploadFile() {
        ThreadPoolService.execute(new UploadTask(URL_UPLOAD_LOG, this.compressLogFiles(Utils.getLogFileDirectory().listFiles()), new Callback() {
            void onSuccess(String json) {
                super.onSuccess(json);
                FileLoggerExecutor.this.onUploadSucceed();
            }

            void onFailure() {
                super.onFailure();
                FileLoggerExecutor.this.onUploadFailed();
            }

            void onError(Throwable e) {
                super.onError(e);
                FileLoggerExecutor.this.onUploadFailed();
            }
        }));
    }

    private void onUploadSucceed() {
        File zipDir = new File(Utils.getLogFileDirectory(), "dir_zipped_log_file");
        File[] zipFiles = zipDir.listFiles();
        File[] logFiles = zipFiles;
        int var4 = zipFiles.length;

        int var5;
        for(var5 = 0; var5 < var4; ++var5) {
            File zipFile = logFiles[var5];
            if(!zipFile.delete()) {
                boolean var7 = zipDir.delete();
            }
        }

        logFiles = Utils.getLogFileDirectory().listFiles();
        File[] var9 = logFiles;
        var5 = logFiles.length;

        for(int var10 = 0; var10 < var5; ++var10) {
            File logFile = var9[var10];
            if(!logFile.delete()) {
                boolean var8 = logFile.delete();
            }
        }

        Utils.closeSilently(this.mOutputStream);
        this.mCreateNewFile = true;
        if(Utils.isEmpty(Utils.getLogFileDirectory().listFiles())) {
            this.mCounter.set(0);
        }

    }

    private void onUploadFailed() {
        Utils.closeSilently(this.mOutputStream);
        this.mCounter.incrementAndGet();
        this.mCreateNewFile = true;
    }

    private File compressLogFiles(File logFile) {
        File[] files = new File[]{logFile};
        return this.compressLogFiles(files);
    }

    private File compressLogFiles(File[] logFiles) {
        File zipDir = new File(Utils.getLogFileDirectory(), "dir_zipped_log_file");
        if(!zipDir.exists()) {
            boolean var3 = zipDir.mkdirs();
        }

        File zipFile = new File(zipDir, UUID.randomUUID().toString());
        ZipUtil.writeToZip(logFiles, zipFile);
        return zipFile;
    }

    class Worker extends Thread {
        Worker() {
        }

        public void run() {
            while(FileLoggerExecutor.this.mStartLogConsumer) {
                try {
                    String log = (String)FileLoggerExecutor.this.mLogQueue.take();
                    if(!TextUtils.isEmpty(log)) {
                        FileLoggerExecutor.this.logToFile(log);
                    }
                } catch (InterruptedException var2) {
                    FileLoggerExecutor.this.mStartLogConsumer = false;
                }
            }

        }
    }

    private static class SingletonHolder {
        private static final FileLoggerExecutor INSTANCE = new FileLoggerExecutor();

        private SingletonHolder() {
        }
    }
}

