package com.subscribehub.app.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.subscribehub.app.domain.*;
import com.subscribehub.app.dto.QUserSiteDto;
import com.subscribehub.app.dto.UserSiteDto;
import com.subscribehub.app.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.subscribehub.app.domain.QArticle.article;
import static com.subscribehub.app.domain.QSite.site;
import static com.subscribehub.app.domain.QUser.user;
import static com.subscribehub.app.domain.QUserSite.userSite;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class UserSiteAdvancedRepository extends Querydsl4RepositorySupport {
    public UserSiteAdvancedRepository() {
        super(UserSite.class);
    }
}