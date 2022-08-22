package com.ysy.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.ysy.diytomcat.catalina.Context;
import com.ysy.diytomcat.catalina.Engine;
import com.ysy.diytomcat.catalina.Service;
import com.ysy.diytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request extends BaseRequest {

    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;

    private Service service;
    private String method;

    public Request(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString) || requestString.equals(""))
            return;
        parseUri();
        //在构造方法中调用 parseContext(), 倘若当前 Context 的路径不是 "/", 那么要对 uri进行修正，比如 uri 是 /a/index.html， 获取出来的 Context路径不是 "/”， 那么要修正 uri 为 /index.html。
        parseContext();
        parseMethod();
        if (!"/".equals(context.getPath())) {
            uri = StrUtil.removePrefix(uri, context.getPath());
            if (StrUtil.isEmpty(uri))
                uri = "/";
        }
    }

    private void parseMethod() {
        method = StrUtil.subBefore(requestString, " ", false);
    }

    //增加解析Context 的方法， 通过获取uri 中的信息来得到 path. 然后根据这个 path 来获取 Context 对象。 如果获取不到，比如 /b/a.html, 对应的 path 是 /b, 是没有对应 Context 的，那么就获取 "/” 对应的 ROOT Context。
    private void parseContext() {
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContext(uri);
        if (null != context)
            return;
        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path)
            path = "/";
        else
            path = "/" + path;

        context = service.getEngine().getDefaultHost().getContext(path);
        if (null == context)
            context = service.getEngine().getDefaultHost().getContext("/");
    }

    //解析 http请求字符串， 这里面就调用了 MiniBrowser里重构的 readBytes 方法。
    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is, false);
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

    public String getMethod() {
        return method;
    }

    public String getRequestString() {
        return requestString;
    }
}
