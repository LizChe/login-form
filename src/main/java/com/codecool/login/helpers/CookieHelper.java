package com.codecool.login.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.net.HttpCookie;

public class CookieHelper {

    public List<HttpCookie> parse(String cookieString) {
        List<HttpCookie> cookies = new ArrayList<>();
        String cookieName;
        String cookieValue;

        if (cookieString == null || cookieString.isEmpty()) {
            return cookies;
        }

        for (String cookie : cookieString.split(";")) {
            int indexOfEq = cookie.indexOf('=');
            cookieName = cookie.substring(0, indexOfEq);
            cookieValue = cookie.substring(indexOfEq + 1);
            cookies.add(new HttpCookie(cookieName, cookieValue));
        }
        return cookies;
    }

    public Optional<HttpCookie> findCookieBy(String name, List<HttpCookie> cookies) {
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals(name))
                return Optional.ofNullable(cookie);
        }
        return Optional.empty();
    }
}