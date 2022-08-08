package com.ysy.diytomcat.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class Response {
    private StringWriter stringWriter;
    private PrintWriter writer;
    //contentType就是对应响应头信息里的 Content-type ，默认是 "text/html"。
    private String contentType;

    public Response() {
        //用于提供一个 getWriter() 方法，这样就可以像 HttpServletResponse 那样写成 response.getWriter().println(); 这种风格了。
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    public String getContentType() {
        return contentType;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public byte[] getBody() throws UnsupportedEncodingException {
        String content = stringWriter.toString();
        byte[] body = content.getBytes("utf-8");
        return body;
    }
}
