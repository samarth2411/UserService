package com.samarth.user.service.repositories;

import com.samarth.user.service.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
