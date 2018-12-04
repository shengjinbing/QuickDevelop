package com.modesty.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * @author wangzhiyuan
 * @since 2018/3/6
 */

public class FileUtils {
    public static boolean createFileDir(String fileDir) {
        File dir = new File(fileDir);
        return dir.exists() || dir.mkdir();
    }

    public static boolean createFileDir(File dir) {
        return dir.exists() || dir.mkdir();
    }

    public static void writeToFile(String fileDir, String fileName, String content) {
        if (fileDir == null || fileName == null || content == null) {
            return;
        }
        if (!createFileDir(fileDir)) {
            return;
        }
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(fileDir + fileName, true);
            osw = new OutputStreamWriter(fos);
            osw.write(content);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(fos);
            IOUtils.closeSilently(osw);
        }
    }

    /**
     * Read a text file into a String, optionally limiting the length.
     */
    public static String readTextFile(File file) {
        InputStream is = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        String text = null;
        try {
            is = new FileInputStream(file);
            bis = new BufferedInputStream(is);
            bos = new ByteArrayOutputStream();
            int len;
            byte[] data = new byte[1024];
            do {
                len = bis.read(data);
                if (len > 0) bos.write(data, 0, len);
            } while (len == data.length);
            text = bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(is);
            IOUtils.closeSilently(bis);
            IOUtils.closeSilently(bos);
        }
        return text;
    }

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(in);
        }
        return base64;
    }

    /**
     * Writes string to file. Basically same as "echo -n $string > $filename"
     */
    public static void stringToFile(String filename, String string) {
        FileWriter out = null;
        try {
            out = new FileWriter(filename);
            out.write(string);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(out);
        }
    }

    public static InputStream stringToStream(String content) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static String streamToString(InputStream is) throws IOException {
        String content = null;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int i = -1;
            while ((i = is.read()) != -1) {
                bos.write(i);
            }
            content = bos.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    public static String getStringFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        ByteArrayOutputStream os = null;
        String content = null;
        try {
            fis = context.openFileInput(fileName);
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            content = os.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(fis);
            IOUtils.closeSilently(os);
        }
        return content;
    }

    public static InputStream getStreamFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fis;
    }

    public static void saveStringToFile(Context context, String content, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            IOUtils.closeSilently(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将scheme为file的uri转成FileProvider 提供的content uri
     */
    public static Uri convertFileUriToFileProviderUri(Context context, Uri uri) {
        if (uri == null) return null;
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            return getUriForFile(context, new File(uri.getPath()));
        }
        return uri;

    }

    /**
     * 创建一个用于拍照图片输出路径的Uri (FileProvider)
     */
    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, getFileProviderName(context), file);
    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }

    /**
     * 把Uri 解析出文件绝对路径
     */
    public static String parseOwnUri(Context context, Uri uri) {
        if (uri == null) return null;
        String path;
        if (TextUtils.equals(uri.getAuthority(), getFileProviderName(context))) {
            path = new File(uri.getPath()).getAbsolutePath();
        } else {
            path = uri.getPath();
        }
        return path;
    }

    public static String getFileStreamPath(Context context, String name){
        String absFileName = null;
        try{
            File file = context.getFileStreamPath(name);
            if(file != null && file.exists()){
                absFileName = file.getAbsolutePath();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return absFileName;
    }
}
