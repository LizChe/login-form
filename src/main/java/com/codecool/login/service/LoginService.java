package com.codecool.login.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.codecool.login.dao.Dao;
import com.codecool.login.dao.DaoException;
import com.codecool.login.dao.SessionDaoImpl;

import com.codecool.login.model.Session;

import com.codecool.login.view.View;

public class LoginService {

    private View view;
    private Dao dao;

    public LoginService() {
        view = new View();
        dao = new SessionDaoImpl();
    }

    public String getSessionId() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] digestedId = getDigestedId();
        return encoder.encodeToString(digestedId);
    }

    public List<Session> getUserBy(String sessionId) {
        List<Session> sessions = new ArrayList<>();
        try {
            sessions = dao.getSessions(sessionId);
            view.printSuccess("Successfully retrieved: " + sessions.size() + " sessions by sessionId: " + sessionId);
        } catch (DaoException e) {
            view.printError(e.getMessage());
        }
        return sessions;
    }

    public void handleSession(String userName, String userPassword, String sessionId) {
        List<Session> sessions = findUserBy(userName, userPassword);
        Session session = new Session.Builder()
                .withUserName(userName)
                .withUserPassword(userPassword)
                .withSessionId(sessionId)
                .build();
        if (sessions.isEmpty()) {
            create(session);
        } else {
            updateSession(session, sessionId);
        }
    }

    public void delete(String sessionId) {
        try {
            int affectedRows = dao.delete(sessionId);
            view.printSuccess("Successfully deleted session id.\n" + affectedRows + " rows have been affected.");
        } catch (DaoException e) {
            view.printError("Failed to delete session id.\n" + e.getMessage());
        }
    }

    private List<Session> findUserBy(String userName, String userPassword) {
        List<Session> sessions = new ArrayList<>();
        try {
            sessions = dao.getSessions(userName, userPassword);
            view.printSuccess("Successfully retrieved: " + sessions.size() + " sessions.\n");
        } catch (DaoException e) {
            view.printError(e.getMessage());
        }
        return sessions;
    }

    private void create(Session session) {
        try {
            int affectedRows = dao.create(session);
            view.printSuccess("Successfully created session:\n" + affectedRows + " rows have been affected.");
        } catch (DaoException e) {
            view.printError(e.getMessage());
        }
    }

    private void updateSession(Session session, String sessionId) {
        session.setSessionId(sessionId);
        try {
            int affectedRows = dao.update(session);
            view.printSuccess("Successfully updated session:\n" + affectedRows + " rows have been affected.");
        } catch (DaoException e) {
            view.printError(e.getMessage());
        }
    }

    private byte[] getDigestedId() {
        byte[] result = null;
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            String randomNumber = Integer.valueOf(secureRandom.nextInt()).toString();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            result = messageDigest.digest(randomNumber.getBytes());
        } catch (NoSuchAlgorithmException e) {
            view.printError(e.getMessage());
        }
        return result;
    }
}