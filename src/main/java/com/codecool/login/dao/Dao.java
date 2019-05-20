package com.codecool.login.dao;

import java.util.List;

import com.codecool.login.model.Session;

public interface Dao {

    public int create(Session session) throws DaoException;

    public int update(Session session) throws DaoException;

    public List<Session> getSessions() throws DaoException;
}