package com.ysy.diytomcat.catalina;

import com.ysy.diytomcat.util.Constant;
import com.ysy.diytomcat.util.ServerXMLUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host {
    private String name;
    //contextMap 其实就是本来在 bootstrap 里的 contextMap , 只不过挪到这里来了。
    private Map<String, Context> contextMap;

    public Host() {
        this.contextMap = new HashMap<>();
        this.name = ServerXMLUtil.getHostName();

        scanContextsOnWebAppsFolder();
        scanContextsInServerXML();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //创建scanContextsInServerXML， 通过 ServerXMLUtil 获取 context, 放进 contextMap里。
    private void scanContextsInServerXML() {
        List<Context> contexts = ServerXMLUtil.getContexts();
        for (Context context : contexts) {
            contextMap.put(context.getPath(), context);
        }
    }

    //创建 scanContextsOnWebAppsFolder 方法，用于扫描 webapps 文件夹下的目录，对这些目录调用 loadContext 进行加载。
    private void scanContextsOnWebAppsFolder() {
        File[] folders = Constant.webappsFolder.listFiles();
        for (File folder : folders) {
            if (!folder.isDirectory())
                continue;
            loadContext(folder);
        }
    }

    //加载这个目录成为 Context 对象。
    private void loadContext(File folder) {
        String path = folder.getName();
        if ("ROOT".equals(path))
            path = "/";
        else
            path = "/" + path;

        String docBase = folder.getAbsolutePath();
        Context context = new Context(path, docBase);

        contextMap.put(context.getPath(), context);
    }

    public Context getContext(String path) {
        return contextMap.get(path);
    }
}
