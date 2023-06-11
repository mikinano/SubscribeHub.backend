package com.subscribehub.app.repository;

import com.subscribehub.app.domain.User;
import com.subscribehub.app.domain.UserSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSiteRepository extends JpaRepository<UserSite, Long> {
    void deleteAllByUser(User user);

    List<UserSite> findAllByUser(User user);
}
