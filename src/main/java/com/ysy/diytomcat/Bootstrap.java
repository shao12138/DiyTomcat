package com.ysy.diytomcat;

import com.ysy.diytomcat.catalina.*;

public class Bootstrap {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}