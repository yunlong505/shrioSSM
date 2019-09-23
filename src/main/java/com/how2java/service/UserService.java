package com.how2java.service;

import com.how2java.pojo.User;

import java.util.List;

public interface UserService {
    public String getPassword(String name);
    public User getByName(String name);

    public List<User> list();
    public void add(User user);
    public void delete(Long id);
    public User get(Long id);
    public void update(User user);
}