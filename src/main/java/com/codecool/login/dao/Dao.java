package com.codecool.login.dao;

import com.codecool.login.model.Session;

public interface Dao {

    public void create(Session session);

    public void delete(Session session);

    public void update(Session session);

}