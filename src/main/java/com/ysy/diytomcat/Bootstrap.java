package com.ysy.diytomcat;

import com.ysy.diytomcat.catalina.*;

public class Bootstrap {
    public static void main(String[] args) {
        System.out.println(Object.class.getClassLoader());
        System.out.println(Bootstrap.class.getClassLoader());
        Server server = new Server();
        server.start();
    }
}