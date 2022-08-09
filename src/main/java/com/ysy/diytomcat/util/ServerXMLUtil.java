package com.ysy.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import com.ysy.diytomcat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ServerXMLUtil {
    public static List<Context> getContexts() {
        List<Context> result = new ArrayList<>();
        //获取 server.xml 的内容
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        //转换成 jsoup document
        Document d = Jsoup.parse(xml);
        //查询所有的 Context 节点
        Elements es = d.select("Context");
        //遍历这些节点，并获取对应的 path和docBase ，以生成 Context 对象， 然后放进 result 返回。
        for (Element e : es) {
            String path = e.attr("path");
            String docBase = e.attr("docBase");
            Context context = new Context(path, docBase);
            result.add(context);
        }
        return result;
    }

    public static String getHostName() {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element host = d.select("Host").first();
        return host.attr("name");
    }
}