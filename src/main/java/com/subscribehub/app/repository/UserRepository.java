package com.subscribehub.app.repository;

import java.util.Optional;

import com.subscribehub.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.email = :userEmail")
    User findOneByEmail(@Param("userEmail") String email);
}