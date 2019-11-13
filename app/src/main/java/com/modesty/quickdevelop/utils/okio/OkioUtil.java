package com.modesty.quickdevelop.utils.okio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * IO缓存数据走向是：
 * -> 从输入流读出到缓冲区
 * -> 从输入流缓冲区copy到 b[]
 * -> 将 b[] copy 到输出流缓冲区
 * -> 输出流缓冲区读出数据到输出流
 *
 * 1.Okio最亮眼的操作，就是设计出了Segment存储数据，通过Buffer进行缓冲管理，并在Buffer.write()则里，
 * 通过移动引用而不是真实数据，是减少数据copy进而交换数据的关键
 *
 * 2.想较于Java原生IO的缓冲方案，双流操中，或者说以Buffer来代替 写入/写出 的 byte[]，减少了copy的过程，
 * 通过Segment的移动达到目的。
 *
 * 3.
 *
 *
 *
 *
 *
 * IO的类型
 * 1.阻塞io：用户进程阻塞，知道io就绪且io处理（读写）完成，阻塞才解除。
 * 2.非阻塞IO：用户进程不阻塞，需要不断轮询io是否就绪，很占用cpu资源，所以一般不用
 * 3.多路复用IO：用户进程阻塞，也是轮询io是否就绪，但是和非阻塞IO不一样的是，轮询io的是系统进程而非应用进程，实际使用
 *   了代理（select/poll/epoll），可以避免cpu空转，所以效率较高，而且1个进程可以管理多个socket，java nio使用的就是多路复用模型。
 * 4.信号驱动式 I/O：（有点类似回调）用户进程不阻塞，而是安装一个信号处理函数，当数据准备好时，进程会收到一个 SIGIO
 *   信号，可以在信号处理函数中调用 I/O 操作函数处理数据。
 * 5.异步io：用户进程不阻塞，发起io请求（读写）后就可以做自己的事了，io就绪等待和io操作交给操作系统去完成，完成后调用
 *   用户进程处理函数，所以整个过程都是异步的。
 * Created by lixiang on 2019-09-03
 */
public class OkioUtil {

    /**
     * 复制
     *
     * @param sourcePath 原始文件路径
     * @param copyPath   复制路径
     */
    public static void copy(String sourcePath, String copyPath) {
        BufferedSource bufferedSource;
        Source source = null;

        Sink sink = null;
        BufferedSink bufferedSink;

        try {
            //构造带缓存的输入流
            source = Okio.source(new File(sourcePath));
            bufferedSource = Okio.buffer(source);

            //构造带缓存的输出流
             sink = Okio.sink(new File(copyPath));
             bufferedSink = Okio.buffer(sink);

            int bufferSize = 8 * 1024; // 8kb

            //复制文件
            while (!bufferedSource.exhausted()){
                bufferedSource.read(
                        bufferedSink.buffer(),
                        bufferSize);
                //输出流缓存写出
                bufferedSink.emit();

            }

            source.close();
            sink.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
