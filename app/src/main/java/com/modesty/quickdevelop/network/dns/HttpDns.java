package com.modesty.quickdevelop.network.dns;



import android.util.Log;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.local.Resolver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

import static java.net.InetAddress.getAllByName;
import static java.net.InetAddress.getByName;

/**
 * 使用七牛的dns
 * Created by lixiang on 2019-08-14
 */
public class HttpDns implements Dns {
    private DnsManager dnsManager;
    public HttpDns(){
        try {
            IResolver[] resolvers = new IResolver[1];
            resolvers[0] = new Resolver(getByName("119.29.29.29"));
            dnsManager = new DnsManager(NetworkInfo.normal, resolvers);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        Log.d("HttpDns","lookup=="+hostname);
        if (dnsManager == null){
            //当构造失败时使用默认解析方式
            return Dns.SYSTEM.lookup(hostname);
        }

        try {
            String[] ips = dnsManager.query(hostname);  //获取HttpDNS解析结果
            if (ips == null || ips.length == 0) {
                return Dns.SYSTEM.lookup(hostname);
            }

            List<InetAddress> result = new ArrayList<>();
            for (String ip : ips) {  //将ip地址数组转换成所需要的对象列表
                result.addAll(Arrays.asList(getAllByName(ip)));
                Log.d("HttpDns",""+ip);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //当有异常发生时，使用默认解析
        return Dns.SYSTEM.lookup(hostname);
    }
}
