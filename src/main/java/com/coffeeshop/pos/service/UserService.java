package com.coffeeshop.pos.service;

import com.coffeeshop.pos.dao.UserDao;
import com.coffeeshop.pos.model.User;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public User login(String username, String password) {
        User user = userDao.findByUsername(username);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }
}