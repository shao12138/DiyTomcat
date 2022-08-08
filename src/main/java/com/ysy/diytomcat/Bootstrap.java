package com.ysy.diytomcat;

import cn.hutool.core.io.*;
import cn.hutool.core.util.*;
import cn.hutool.log.LogFactory;
import cn.hutool.system.*;
import com.ysy.diytomcat.http.*;
import com.ysy.diytomcat.util.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Bootstrap {

    public static void main(String[] args) {

        try {
            logJVM();
            int port = 18080;
            //把端口占用提示信息注释掉，当真正异常发生的时候，就会打印出来
//            if (!NetUtil.isUsableLocalPort(port)) {
//                System.out.println(port + " 端口已经被占用了!");
//                return;
//            }
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket s = ss.accept();
                Request request = new Request(s);
                System.out.println("浏览器的输入信息： \r\n" + request.getRequestString());
                System.out.println("uri： " + request.getUri());

                Response response = new Response();
                String uri = request.getUri();
                //首先判断 uri 是否为空，如果为空就不处理了。 什么情况为空呢？ 在 TestTomcat 里的 NetUtil.isUsableLocalPort(port) 这段代码就会导致为空。
                if (null == uri)
                    continue;
                System.out.println(uri);
                //如果是 "/", 那么依然返回原字符串。
                if ("/".equals(uri)) {
                    String html = "Hello DIY Tomcat from how2j.cn";
                    response.getWriter().println(html);
                } else {
                    //接着处理文件，首先取出文件名，比如访问的是 /a.html, 那么文件名就是 a.html
                    String fileName = StrUtil.removePrefix(uri, "/");
                    //然后获取对应的文件对象 file
                    File file = FileUtil.file(Constant.rootFolder, fileName);
                    if (file.exists()) {
                        //如果文件存在，那么获取内容并通过 response.getWriter 打印。
                        String fileContent = FileUtil.readUtf8String(file);
                        response.getWriter().println(fileContent);
                    } else {
                        //如果文件不存在，那么打印 File Not Found。
                        response.getWriter().println("File Not Found");
                    }
                }
                //把返回 200 响应重构到了一个独立的方法里，看上去更清爽了。
                handle200(s, response);
            }
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }
    private static void logJVM() {
        Map<String,String> infos = new LinkedHashMap<>();
        infos.put("Server version", "How2J DiyTomcat/1.0.1");
        infos.put("Server built", "2020-04-08 10:20:22");
        infos.put("Server number", "1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

        Set<String> keys = infos.keySet();
        for (String key : keys) {
            LogFactory.get().info(key+":\t\t" + infos.get(key));
        }
    }
    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();
        //根据 response 对象上的 contentType ，组成返回的头信息，并且转换成字节数组。
        byte[] body = response.getBody();
        //获取主题信息部分，即 html 对应的 字节数组。
        byte[] responseBytes = new byte[head.length + body.length];
        //拼接头信息和主题信息，成为一个响应字节数组。
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        //close 自动 flush
        s.close();
    }
}