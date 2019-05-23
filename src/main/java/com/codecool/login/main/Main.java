package com.codecool.login.main;

import java.io.IOException;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

import com.codecool.login.controller.Login;
import com.codecool.login.controller.Static;


public class Main {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/login", new Login());
        server.createContext("/static", new Static());
        server.setExecutor(null);
        server.start();
    }
}