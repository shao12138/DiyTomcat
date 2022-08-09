package com.ysy.diytomcat.catalina;

import com.ysy.diytomcat.util.ServerXMLUtil;

import java.util.List;

public class Engine {
    private String defaultHost;
    private List<Host> hosts;
    private Service service;

    public Engine(Service service) {
        this.defaultHost = ServerXMLUtil.getEngineDefaultHost();
        this.hosts = ServerXMLUtil.getHosts(this);
        this.service = service;
        checkDefault();
    }

    private void checkDefault() {
        if (null == getDefaultHost())
            throw new RuntimeException("the defaultHost" + defaultHost + " does not exist!");
    }

    public Service getService() {
        return service;
    }

    public Host getDefaultHost() {
        for (Host host : hosts) {
            if (host.getName().equals(defaultHost))
                return host;
        }
        return null;
    }
}
