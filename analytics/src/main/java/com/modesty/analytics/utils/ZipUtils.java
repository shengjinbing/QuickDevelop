package com.modesty.analytics.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author lixiang
 */

public class ZipUtils {

	/**
	 * 使用zip进行压缩
	 */
	public static String zip(String content) {
		if (content == null) return null;
		byte[] compressed;
		ByteArrayOutputStream bos = null;
		ZipOutputStream zos = null;
		String compressedStr = null;
		try {
			bos = new ByteArrayOutputStream();
			zos = new ZipOutputStream(bos);
			zos.putNextEntry(new ZipEntry("0"));
			zos.write(content.getBytes());
			zos.closeEntry();
			compressed = bos.toByteArray();
			// 压缩后 aes加密 然后base64
			AES aes = new AES();
			compressedStr = aes.aesEncrypt(compressed);
		} catch (IOException e) {
			compressed = null;
		} finally {
			Utils.closeSilently(zos);
			Utils.closeSilently(bos);
		}
		return compressedStr;
	}

	/**
	 * 使用zip进行解压缩
	 */
	public static String unzip(String zippedContent) {
		if (zippedContent == null) {
			return null;
		}

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed = null;
		try {
			byte[] compressed = Base64.decode(zippedContent, Base64.DEFAULT);
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(new AES().aesDecrypt(compressed));
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			decompressed = null;
		} finally {
			Utils.closeSilently(zin);
			Utils.closeSilently(in);
			Utils.closeSilently(out);
		}
		return decompressed;
	}

	/**
	 * 使用gzip进行压缩
	 */
	public static String gzip(String content) {
		if (content == null || content.length() == 0) {
			return content;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(content.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Utils.closeSilently(gzip);
		}
		// 压缩后 aes加密 然后base64
		return new AES().aesEncrypt(out.toByteArray());
	}

	/**
	 * 使用gzip进行解压缩
	 */
	public static String unGzip(String gzippedContent) {
		if (gzippedContent == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		byte[] compressed = null;
		String decompressed = null;
		try {
			compressed = Base64.decode(gzippedContent, Base64.DEFAULT);
			in = new ByteArrayInputStream(new AES().aesDecrypt(compressed));
			ginzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Utils.closeSilently(ginzip);
			Utils.closeSilently(in);
			Utils.closeSilently(out);
		}

		return decompressed;
	}

}
