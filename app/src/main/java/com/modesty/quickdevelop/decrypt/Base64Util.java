package com.modesty.quickdevelop.decrypt;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Base64是网络上最常见的用于传输8Bit字节代码的编码方式之一，Base64并不是安全领域的加密算法，其实
 * Base64只能算是一个编码算法，对数据内容进行编码来适合传输。标准Base64编码解码无需额外信息即完全
 * 可逆，即使你自己自定义字符集设计一种类Base64的编码方式用于数据加密，在多数场景下也较容易破解。
 * Base64编码本质上是一种将二进制数据转成文本数据的方案。对于非二进制数据，是先将其转换成二进制形式
 * ，然后每连续6比特（2的6次方=64）计算其十进制值，根据该值在A--Z,MVPActivityModelImpl--z,0--9,+,/ 这64个字符中找到对
 * 应的字符，最终得到一个文本字符串。
 * 基本规则如下几点：
 * 1、标准Base64只有64个字符（英文大小写、数字和+、/）以及用作后缀等号；
 * 2、Base64是把3个字节变成4个可打印字符，所以Base64编码后的字符串一定能被4整除（不算用作后缀的等号）；
 * 3、等号一定用作后缀，且数目一定是0个、1个或2个。这是因为如果原文长度不能被3整除，Base64要在后面添加\0凑齐3n位。为了正确还原，添加了几个\0就加上几个等号。显然添加等号的数目只能是0、1或2；
 * 4、严格来说Base64不能算是一种加密，只能说是编码转换。
 * <p>
 * Base64编码的用处？
 * 1.在计算机中任何数据都是按ascii码存储的，而ascii码的128～255之间的值是不可见字符。而在网络上交换
 * 数据时，比如说从A地传到B地，往往要经过多个路由设备，由于不同的设备对字符的处理方式有一些不同，这样
 * 那些不可见字符就有可能被处理错误，这是不利于传输的。所以就先把数据先做一个Base64编码，统统变成可见字符，这样出错的可能性就大降低了。
 * <p>
 * Created by lixiang
 * on 2018/12/25
 */
public class Base64Util {

    /**
     * 针对Base64.DEFAULT参数说明
     * 无论是编码还是解码都会有一个参数Flags，Android提供了以下几种
     * 1.DEFAULT 这个参数是默认，使用默认的方法来加密
     * 2.NO_PADDING 这个参数是略去加密字符串最后的”=”
     * 3.NO_WRAP 这个参数意思是略去所有的换行符（设置后CRLF就没用了）
     * 4.CRLF 这个参数看起来比较眼熟，它就是Win风格的换行符，意思就是使用CR LF这一对作为一行的结尾而不是Unix风格的LF
     * 5.URL_SAFE 这个参数意思是加密时不使用对URL和文件名有特殊意义的字符来作为加密字符，具体就是以-和_取代+和/
     *
     * 四分之三个字符表示一个字符（6位变成一个字符）不够用0补齐；
     * 用文本方式传非文本格式的内容，比如图片；
     * @param data
     * @return
     */
    public static String encodeToString(String data) {
        return Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
    }

    public static String decode(String encodedString) {
        return new String(Base64.decode(encodedString, Base64.DEFAULT));
    }

    /**
     * 对文件进行Base64编码
     *
     * @param file
     * @return
     */
    public static String encodeFile(File file) {
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 对Base64解码后的字符串写进文件
     *
     * @param encodedString
     * @param desFile
     */
    public static void decodeFile(String encodedString, File desFile) {
        FileOutputStream fos = null;
        try {
            byte[] decodeBytes = Base64.decode(encodedString.getBytes(), Base64.DEFAULT);
            fos = new FileOutputStream(desFile);
            fos.write(decodeBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
