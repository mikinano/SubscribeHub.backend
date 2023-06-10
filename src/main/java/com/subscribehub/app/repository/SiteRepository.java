package com.subscribehub.app.repository;

import com.subscribehub.app.domain.Site;
import com.subscribehub.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SiteRepository extends JpaRepository<Site, Long> {

    @Query("select s from Site s where s.id = :id")
    Site findOneById(@Param("id") Long id);
}
