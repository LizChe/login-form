package com.codecool.login.model;

public class Session {

    private final int id;
    private String userName;
    private String userPassword;
    private String sessionId;

    public Session(Builder builder) {
        this.id = builder.id;
        this.userName = builder.userName;
        this.userPassword = builder.userPassword;
        this.sessionId = builder.sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getId() {
        return id;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static class Builder {
        private int id;
        private String userName;
        private String userPassword;
        private String sessionId;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withUserPassword(String password) {
            this.userPassword = userPassword;
            return this;
        }

        public Builder withSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Session build() {
            return new Session(this);
        }
    }
}