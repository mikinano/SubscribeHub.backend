package com.subscribehub.app.repository;

import com.subscribehub.app.domain.Keyword;
import com.subscribehub.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    @Query("select k.wordString from Keyword k join k.user u where u.email = :userEmail")
    List<String> findKeywordListByUser(@Param("userEmail") String userEmail);

    void removeAllByUser(User user);
}
