package com.ysy.diytomcat.util;

import java.io.File;
import static com.ysy.diytomcat.util.Constant.webXmlFile;
import cn.hutool.core.io.FileUtil;
import com.ysy.diytomcat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebXMLUtil {
    public static String getWelcomeFile(Context context) {
        String xml = FileUtil.readUtf8String(webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("welcome-file");
        for (Element e : es) {
            String welcomeFileName = e.text();
            File f = new File(context.getDocBase(), welcomeFileName);
            if (f.exists())
                return f.getName();
        }
        return "index.html";
    }
}
