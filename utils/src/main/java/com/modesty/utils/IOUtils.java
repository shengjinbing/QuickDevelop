package com.modesty.utils;

import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;

/**
 * @author wangzhiyuan
 * @since 2018/2/23
 */

public class IOUtils {

    public static byte[] inputToBytes(InputStream is) {
        if(is == null){
            return null;
        }

        ByteArrayOutputStream bos = null;
        byte[] result = null;

        try{
            bos = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                bos.write(buff, 0, rc);
            }

            result = bos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
            result = null;
        }finally {
            closeSilently(bos);
        }

        return result;
    }

    public static void closeSilently(@Nullable Closeable c) {
        if (c == null) return;
        try {
            c.close();
            c = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
