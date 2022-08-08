package com.ysy.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.ysy.diytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request {

    private String requestString;
    private String uri;
    private Socket socket;

    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)||requestString.equals(""))
            return;
        parseUri();
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

    public String getUri() {
        return uri;
    }

    public String getRequestString() {
        return requestString;
    }

}
