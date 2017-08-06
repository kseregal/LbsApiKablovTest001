package ru.onego.lbsapikablovtest001;

/**
 * Created by Серега on 03.08.2017.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpConnector {

    private static final String BOUNDARY = "boundary";
    private static final byte[] boundaryBytes = BOUNDARY.getBytes();

    public static byte[] doRequest(String surl, byte[] postData) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(surl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            if (postData != null && postData.length > 0) {
                urlConnection.setDoOutput(true);
                outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                outputStream.write(postData);
                outputStream.flush();
            }
            int responseCode = urlConnection.getResponseCode();
            Log.d("LBS","responseCode = "+responseCode);
            int len = urlConnection.getContentLength();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            if (len > 0) {
                int offset = 0;
                byte[] response = new byte[len];
                int bytesRead = inputStream.read(response, offset, len);
                while (bytesRead < len) {
                    int bytesReadCurr = inputStream.read(response, offset + bytesRead, len - bytesRead);
                    if (bytesReadCurr != -1) {
                        bytesRead += bytesReadCurr;
                    } else {
                        return null;
                    }
                }
                return response;
            }
        } catch (Exception e) {
            Log.e("LBS","http error = "+e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static byte[] encodeMIME(String[] names, String[] contentTypes, byte[][] values) {
        if (values == null) {
            return null;
        }
        try {
            ByteArrayOutputStream sb = new ByteArrayOutputStream();
            for (int i = 0; i < names.length; i++) {
                sb.write('-');
                sb.write('-');
                sb.write(boundaryBytes);
                sb.write("\r\nContent-Disposition: form-data; name=\"".getBytes());
                sb.write(names[i].getBytes());
                sb.write('\"');
                if (contentTypes != null && !contentTypes[i].equals("")) {
                    sb.write("\r\nContent-Type: ".getBytes());
                    sb.write(contentTypes[i].getBytes());
                }
                sb.write("\r\n\r\n".getBytes());
                sb.write(values[i]);
                sb.write("\r\n".getBytes());
            }
            sb.write('-');
            sb.write('-');
            sb.write(boundaryBytes);
            sb.write("--\r\n".getBytes());
            return sb.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
