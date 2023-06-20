package com.samarth.user.service.services;

import com.samarth.user.service.entitites.User;

import java.util.List;

public interface UserService {


    User saveUser(User user);

    List<User> getAllUser();

    User getUser(String userId);

    boolean deleteUser(String userId);


    User updateUser(String userId);
}
