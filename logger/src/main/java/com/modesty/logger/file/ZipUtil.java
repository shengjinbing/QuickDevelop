package com.modesty.logger.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

class ZipUtil {
    ZipUtil() {
    }

    public static void writeToZip(File[] writeFiles, File zipFile) {
        if(zipFile.exists()) {
            zipFile.delete();
        }

        ZipOutputStream os = null;

        try {
            zipFile.createNewFile();
            os = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            if(writeFiles != null && writeFiles.length > 0) {
                byte[] buffer = new byte[1024];

                for(int i = 0; i < writeFiles.length; ++i) {
                    FileInputStream in = null;

                    try {
                        File file = writeFiles[i];
                        os.putNextEntry(new ZipEntry(file.getName()));
                        in = new FileInputStream(file);

                        int readNum;
                        while((readNum = in.read(buffer)) != -1) {
                            os.write(buffer, 0, readNum);
                        }

                        os.closeEntry();
                    } catch (Exception var18) {
                        var18.printStackTrace();
                    } finally {
                        Utils.closeSilently(in);
                    }
                }
            }
        } catch (Throwable var20) {
            var20.printStackTrace();
        } finally {
            Utils.closeSilently(os);
        }

    }

    public static void writeToZip(File originalFile, File zipFile) {
        ZipOutputStream os = null;
        FileInputStream in = null;

        try {
            boolean fileCreated;
            if(zipFile.exists()) {
                fileCreated = zipFile.delete();
            }

            fileCreated = zipFile.createNewFile();
            in = new FileInputStream(originalFile);
            os = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            os.putNextEntry(new ZipEntry(originalFile.getName()));
            //int readNum = false;
            byte[] buffer = new byte[1024];

            int readNum;
            while((readNum = in.read(buffer)) != -1) {
                os.write(buffer, 0, readNum);
            }

            os.closeEntry();
        } catch (Throwable var10) {
            var10.printStackTrace();
        } finally {
            Utils.closeSilently(in);
            Utils.closeSilently(os);
        }

    }
}

