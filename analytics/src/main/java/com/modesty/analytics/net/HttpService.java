package com.modesty.analytics.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;


import com.modesty.analytics.utils.Logger;
import com.modesty.analytics.utils.Utils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * An HTTP utility class for internal use in the library. Not thread-safe.
 *
 * @author lixiang
 * @since 2018/5/18
 */
public class HttpService implements RemoteService {

    private static final String LOGTAG = "HttpService";

    @Override
    public boolean isOnline(Context context) {
        boolean isOnline;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            if (netInfo == null) {
                isOnline = true;
                Logger.v(LOGTAG, "A default network has not been set so we cannot be certain whether we are offline");
            } else {
                isOnline = netInfo.isAvailable() && netInfo.isConnectedOrConnecting();
                Logger.v(LOGTAG, "ConnectivityManager says we " + (isOnline ? "are" : "are not") + " online");
            }
        } catch (final SecurityException e) {
            isOnline = true;
            Logger.v(LOGTAG, "Don't have permission to check connectivity, will assume we are online");
        }
        return isOnline;
    }

    private byte[] performPostRequest(String endpointUrl,
                                      Map<String, Object> params,
                                      SSLSocketFactory socketFactory) {

        // the while(retries) loop is a workaround for a bug in some Android HttpURLConnection
        // libraries- The underlying library will attempt to reuse stale connections,
        // meaning the second (or every other) attempt to connect fails with an EOFException.
        // Apparently this nasty retry logic is the current state of the workaround art.
        int retries = 0;
        boolean succeeded = false;
        byte[] response = null;

        while (retries < 3 && !succeeded) {
            InputStream in = null;
            OutputStream out = null;
            HttpURLConnection connection = null;

            try {
                final URL url = new URL(endpointUrl);
                connection = (HttpURLConnection) url.openConnection();
                if (null != socketFactory && connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(socketFactory);
                }
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("accept", "application/json");
                connection.setDoOutput(true);
                connection.setRequestMethod(HttpMethod.POST.toString());

                if(Utils.isEmpty(params)) break;
                final JSONObject jsonObject = new JSONObject(params);
                final String json = jsonObject.toString();

                if (!TextUtils.isEmpty(json)) {
                    byte[] bytes = json.getBytes();
                    connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                    out = connection.getOutputStream();
                    out.write(json.getBytes("UTF-8"));
                    out.flush();
                }

                in = connection.getInputStream();
                response = slurp(in);
                succeeded = true;
            } catch (final Exception e) {
                retries = retries + 1;
                if (retries >= 3) {
                    Logger.v(LOGTAG, "Could not connect to analytics service after three retries.");
                }
            } finally {
                Utils.closeSilently(out);
                Utils.closeSilently(in);
                if (null != connection)
                    connection.disconnect();
                    connection = null;
            }
        }

        if (succeeded && response != null) {
            final String json = new String(response, Charset.defaultCharset());
            Logger.v(LOGTAG, "perform request : result is " + json);
        }

        return response;
    }

    private String appendParams(String endpointUrl, Map<String, Object> params) {
        String additionalParams = null;

        if (null != params) {
            final Uri.Builder builder = new Uri.Builder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                builder.appendQueryParameter(param.getKey(), param.getValue().toString());
            }
            additionalParams = builder.build().getEncodedQuery();
        }

        if (endpointUrl.endsWith("?")) {
            endpointUrl = endpointUrl + additionalParams;
        } else if (endpointUrl.indexOf("?") > 1) {
            if (endpointUrl.endsWith("&")) {
                endpointUrl = endpointUrl + additionalParams;
            } else {
                endpointUrl = endpointUrl + "&" + additionalParams;
            }
        } else {
            endpointUrl = endpointUrl + "?" + additionalParams;
        }

        return endpointUrl;
    }

    private byte[] performGetRequest(final String endpointUrl,
                                     final Map<String, Object> params,
                                     final SSLSocketFactory socketFactory) {

        boolean succeeded = false;
        byte[] response = null;
        InputStream in = null;
        HttpURLConnection connection = null;

        try {
            final URL url = new URL(appendParams(endpointUrl, params));
            connection = (HttpURLConnection) url.openConnection();
            if (null != socketFactory && connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(socketFactory);
            }
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod(HttpMethod.GET.toString());

            in = connection.getInputStream();
            response = slurp(in);
            succeeded = true;

        } catch (final Exception e) {
            Logger.e(LOGTAG, "exception occurs, " + e.toString(), e);
        } finally {
            if (null != in)
                Utils.closeSilently(in);
            if (null != connection)
                connection.disconnect();
                connection = null;
        }

        if (succeeded && response != null) {
            final String json = new String(response, Charset.defaultCharset());
            Logger.v(LOGTAG, "perform request : result is " + json);
        }

        return response;
    }

    @Override
    public byte[] performRequest(final HttpMethod method,
                                 final String endpointUrl,
                                 final Map<String, Object> params,
                                 final SSLSocketFactory socketFactory) {
        Logger.v(LOGTAG, "Attempting request to " + endpointUrl + ", http method is " + method);

        if (method == HttpMethod.POST) {
            return performPostRequest(endpointUrl, params, socketFactory);
        } else if (method == HttpMethod.GET) {
            return performGetRequest(endpointUrl, params, socketFactory);
        }
        return null;
    }

    private static byte[] slurp(final InputStream inputStream)
            throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[8192];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

}
