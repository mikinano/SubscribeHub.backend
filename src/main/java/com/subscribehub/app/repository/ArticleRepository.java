package com.subscribehub.app.repository;

import com.subscribehub.app.domain.Article;
import com.subscribehub.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
