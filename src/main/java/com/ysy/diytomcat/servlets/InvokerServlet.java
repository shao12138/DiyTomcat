package com.ysy.diytomcat.servlets;

import cn.hutool.core.util.ReflectUtil;
import com.ysy.diytomcat.catalina.Context;
import com.ysy.diytomcat.http.Request;
import com.ysy.diytomcat.http.Response;
import com.ysy.diytomcat.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class InvokerServlet extends HttpServlet {
    private static InvokerServlet instance = new InvokerServlet();

    public static synchronized InvokerServlet getInstance() {
        return instance;
    }
    private InvokerServlet() {

    }
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;

        String uri = request.getUri();
        Context context = request.getContext();
        String servletClassName = context.getServletClassName(uri);

        Object servletObject = ReflectUtil.newInstance(servletClassName);
        ReflectUtil.invoke(servletObject, "service", request, response);
        response.setStatus(Constant.CODE_200);
    }
}
