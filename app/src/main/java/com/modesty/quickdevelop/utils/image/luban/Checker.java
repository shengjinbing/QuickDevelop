package com.modesty.quickdevelop.utils.image.luban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum Checker {
  SINGLE;

  private static final String TAG = "Luban";

  private static List<String> format = new ArrayList<>();
  /**
   * .jpg和.jpeg
   * 1.透明性：它不支持透明性
   * 2.动画：它不支持动画
   * 3.有损压缩
   */
  private static final String JPG = ".jpg";
  private static final String JPEG = ".jpeg";
  /**
   * 1、类型：Png这种图片格式包括了许多类，但是在实践的大致中可以分为256色的png和全色的png，你完全可以用256色的png代替gif，用全色的png代替jpeg。
   * 2、透明性：png是完全支持alpha透明的（透明、半透明、不透明）
   * 3、动画：它不支持动画
   */
  private static final String PNG = ".png";
  /**
   * 加载速度更快，图片的体积只有jpg的2/3，美中不足的是，webp是一种有损压缩，而且目前支持这种图片格式的
   */
  private static final String WEBP = ".webp";
  /**
   * 1、透明性：gif是一种布尔透明类型，即它可以使全透明，也可是全不透明，但是它并没有半透明的（alpha透明）。
   * 2、动画：gif格式支持动画。
   * 3、无损耗性：gif是一种无损耗的图像格式，这也意味着你可以对gif图片做任何操作也不会使得图像质量产生损耗。
   * 4、水平扫描：gif是使用一种叫做LZW的算法进行压缩的，当压缩gif的过程中，像素是由上到下水平压缩的，这也意味着同等条件下，横向的gif图片比竖向的gif更加小。例如500*10的图片比10*500的图片更加小。
   * 5、间隔渐进显示：gif支持可选择性的间隔渐进显示。
   */
  private static final String GIF = ".gif";

  private final byte[] JPEG_SIGNATURE = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

  static {
    format.add(JPG);
    format.add(JPEG);
    format.add(PNG);
    format.add(WEBP);
    format.add(GIF);
  }

  /**
   * Determine if it is JPG.
   *
   * @param is image file input stream
   */
  boolean isJPG(InputStream is) {
    return isJPG(toByteArray(is));
  }

  /**
   * Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
   */
  int getOrientation(InputStream is) {
    return getOrientation(toByteArray(is));
  }

  private boolean isJPG(byte[] data) {
    if (data == null || data.length < 3) {
      return false;
    }
    byte[] signatureB = new byte[]{data[0], data[1], data[2]};
    return Arrays.equals(JPEG_SIGNATURE, signatureB);
  }

  private int getOrientation(byte[] jpeg) {
    if (jpeg == null) {
      return 0;
    }

    int offset = 0;
    int length = 0;

    // ISO/IEC 10918-1:1993(E)
    while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
      int marker = jpeg[offset] & 0xFF;

      // Check if the marker is MVPActivityModelImpl padding.
      if (marker == 0xFF) {
        continue;
      }
      offset++;

      // Check if the marker is SOI or TEM.
      if (marker == 0xD8 || marker == 0x01) {
        continue;
      }
      // Check if the marker is EOI or SOS.
      if (marker == 0xD9 || marker == 0xDA) {
        break;
      }

      // Get the length and check if it is reasonable.
      length = pack(jpeg, offset, 2, false);
      if (length < 2 || offset + length > jpeg.length) {
        Log.e(TAG, "Invalid length");
        return 0;
      }

      // Break if the marker is EXIF in APP1.
      if (marker == 0xE1 && length >= 8
          && pack(jpeg, offset + 2, 4, false) == 0x45786966
          && pack(jpeg, offset + 6, 2, false) == 0) {
        offset += 8;
        length -= 8;
        break;
      }

      // Skip other markers.
      offset += length;
      length = 0;
    }

    // JEITA CP-3451 Exif Version 2.2
    if (length > 8) {
      // Identify the byte order.
      int tag = pack(jpeg, offset, 4, false);
      if (tag != 0x49492A00 && tag != 0x4D4D002A) {
        Log.e(TAG, "Invalid byte order");
        return 0;
      }
      boolean littleEndian = (tag == 0x49492A00);

      // Get the offset and check if it is reasonable.
      int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
      if (count < 10 || count > length) {
        Log.e(TAG, "Invalid offset");
        return 0;
      }
      offset += count;
      length -= count;

      // Get the count and go through all the elements.
      count = pack(jpeg, offset - 2, 2, littleEndian);
      while (count-- > 0 && length >= 12) {
        // Get the tag and check if it is orientation.
        tag = pack(jpeg, offset, 2, littleEndian);
        if (tag == 0x0112) {
          int orientation = pack(jpeg, offset + 8, 2, littleEndian);
          switch (orientation) {
            case 1:
              return 0;
            case 3:
              return 180;
            case 6:
              return 90;
            case 8:
              return 270;
          }
          Log.e(TAG, "Unsupported orientation");
          return 0;
        }
        offset += 12;
        length -= 12;
      }
    }

    Log.e(TAG, "Orientation not found");
    return 0;
  }

  String extSuffix(InputStreamProvider input) throws IOException {
    Bitmap bitmap = BitmapFactory.decodeStream(input.open(), null, new BitmapFactory.Options());
    String suffix = TextUtils.isEmpty(input.getPath()) ? "" : input.getPath().substring(input.getPath().lastIndexOf("."), input.getPath().length());

    if (bitmap.hasAlpha()) {
      return PNG;
    } else if (TextUtils.isEmpty(suffix)) {
      return JPG;
    } else if (!format.contains(suffix)) {
      return JPG;
    }

    return suffix;
  }

  boolean needCompress(int leastCompressSize, String path) {
    if (leastCompressSize > 0) {
      File source = new File(path);
      return source.exists() && source.length() > (leastCompressSize << 10);
    }
    return true;
  }

  private int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
    int step = 1;
    if (littleEndian) {
      offset += length - 1;
      step = -1;
    }

    int value = 0;
    while (length-- > 0) {
      value = (value << 8) | (bytes[offset] & 0xFF);
      offset += step;
    }
    return value;
  }

  private byte[] toByteArray(InputStream is) {
    if (is == null) {
      return new byte[0];
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int read;
    byte[] data = new byte[4096];

    try {
      while ((read = is.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, read);
      }
    } catch (Exception ignored) {
      return new byte[0];
    } finally {
      try {
        buffer.close();
      } catch (IOException ignored) {
      }
    }

    return buffer.toByteArray();
  }
}
