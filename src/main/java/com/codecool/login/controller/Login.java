package com.codecool.login.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;

import java.net.HttpCookie;
import java.net.URLDecoder;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.codecool.login.helpers.CookieHelper;
import com.codecool.login.service.LoginService;

import com.codecool.login.model.Session;

public class Login implements HttpHandler {

    private LoginService loginService;
    private CookieHelper cookieHelper;
    private static final String SESSION_COOKIE_NAME = "BAKED_COOKIE";

    public Login() {
        loginService = new LoginService();
        cookieHelper = new CookieHelper();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        Optional<HttpCookie> cookie = getSessionIdCookie(httpExchange);

        if (method.equals("GET") && !cookie.isPresent()) {
            String response = getNewSessionModel();
            sendResponse(httpExchange, response);
        }

        if (cookie.isPresent() && method.equals("GET")) {
            handleCookieIsPresent(httpExchange);
        }

        if (method.equals("POST")) {
            handlePOST(httpExchange);
        }
    }

    private String getSessionIdFromCookie(HttpExchange httpExchange) {
        Optional<HttpCookie> cookie = getSessionIdCookie(httpExchange);
        String sessionId = "";
        if (cookie.isPresent()) {
            sessionId = cookie.get().getValue().replace("\"", "");
        }
        return sessionId;
    }

    private void handleCookie(HttpExchange httpExchange, String sessionId) {
        Optional<HttpCookie> cookie = Optional.of(new HttpCookie(SESSION_COOKIE_NAME, sessionId));
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
    }

    private Optional<HttpCookie> getSessionIdCookie(HttpExchange httpExchange) {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parse(cookie);
        return cookieHelper.findCookieBy(SESSION_COOKIE_NAME, cookies);
    }

    private void handleCookieIsPresent(HttpExchange httpExchange) throws IOException {
        String sessionId = getSessionIdFromCookie(httpExchange);
        List<Session> sessions = loginService.getUserBy(sessionId);
        String response;
        if (sessions.isEmpty()) {
            response = getNewSessionModel();
        } else {
            String userName = sessions.get(0).getUserName();
            response = getOldSessionModel(userName);
        }
        sendResponse(httpExchange, response);
    }

    private String getNewSessionModel() {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("template/login.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("newSession", "newSession");
        return template.render(model);
    }

    private String getOldSessionModel(String userName) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("template/login.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("oldSession", "oldSession");
        model.with("name", userName);
        return template.render(model);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        writeOutput(httpExchange, response);
    }

    private void redirect(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.getResponseHeaders().set("Location", "/login");
        httpExchange.sendResponseHeaders(302, response.getBytes().length);
        writeOutput(httpExchange, response);
    }

    private void writeOutput(HttpExchange httpExchange, String response) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private Map<String, String> getPostInputs(HttpExchange httpExchange) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String data = bufferedReader.readLine();
        return parse(data);
    }

    private static Map<String, String> parse(String data) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = data.split("&");
        String[] keyValue;
        String value;

        for (String pair : pairs) {
            keyValue = pair.split("=");
            value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

    private void handlePOST(HttpExchange httpExchange) throws IOException {
        Map<String, String> inputs = getPostInputs(httpExchange);
        for (Map.Entry<String, String> entry : inputs.entrySet()) {
            if (entry.getKey().contains("login-button")) {
                handleLoginPOST(httpExchange, inputs);
            } else if (entry.getKey().contains("logout-button")) {
                handleLogoutPOST(httpExchange);
            }
        }
    }

    private void handleLoginPOST(HttpExchange httpExchange, Map<String, String> inputs) throws IOException {
        String sessionId = loginService.getSessionId();
        handleCookie(httpExchange, sessionId);
        String userName = inputs.get("login-name");
        String userPassword = inputs.get("login-password");
        loginService.handleSession(userName, userPassword, sessionId);
        String response = getOldSessionModel(userName);
        redirect(httpExchange, response);
    }

    private void handleLogoutPOST(HttpExchange httpExchange) throws IOException {
        String sessionId = getSessionIdFromCookie(httpExchange);
        loginService.delete(sessionId);
        String response = getNewSessionModel();
        redirect(httpExchange, response);
    }
}