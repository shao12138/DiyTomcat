package com.ysy.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.ysy.diytomcat.catalina.Context;
import com.ysy.diytomcat.catalina.Host;
import com.ysy.diytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request {

    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    private Host host;

    public Request(Socket socket, Host host) throws IOException {
        this.socket = socket;
        this.host = host;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString) || requestString.equals(""))
            return;
        parseUri();
        //在构造方法中调用 parseContext(), 倘若当前 Context 的路径不是 "/", 那么要对 uri进行修正，比如 uri 是 /a/index.html， 获取出来的 Context路径不是 "/”， 那么要修正 uri 为 /index.html。
        parseContext();
        if (!"/".equals(context.getPath()))
            uri = StrUtil.removePrefix(uri, context.getPath());
    }

    //增加解析Context 的方法， 通过获取uri 中的信息来得到 path. 然后根据这个 path 来获取 Context 对象。 如果获取不到，比如 /b/a.html, 对应的 path 是 /b, 是没有对应 Context 的，那么就获取 "/” 对应的 ROOT Context。
    private void parseContext() {
        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path)
            path = "/";
        else
            path = "/" + path;

        context = host.getContext(path);
        if (null == context)
            context = host.getContext("/");
    }

    //解析 http请求字符串， 这里面就调用了 MiniBrowser里重构的 readBytes 方法。
    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is);
        requestString = new String(bytes, "utf-8");
    }

    //解析真实链接
    private void parseUri() {
        String temp = StrUtil.subBetween(requestString, " ", " ");
        //不包括？，链接即是真实链接
        if (!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }
        //包括？，截取？之前的字符串
        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;
    }

    public Context getContext() {
        return context;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestString() {
        return requestString;
    }

}
