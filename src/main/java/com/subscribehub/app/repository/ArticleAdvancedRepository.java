package com.subscribehub.app.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.subscribehub.app.domain.Article;
import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleSearchCondition;
import com.subscribehub.app.dto.QArticleDto;
import com.subscribehub.app.repository.support.Querydsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.subscribehub.app.domain.QArticle.article;
import static com.subscribehub.app.domain.QSite.site;
import static com.subscribehub.app.domain.QUser.user;
import static com.subscribehub.app.domain.QUserSite.userSite;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class ArticleAdvancedRepository extends Querydsl4RepositorySupport {
    public ArticleAdvancedRepository() {
        super(Article.class);
    }

    public Page<ArticleDto> searchPagination(String userEmail,
                                             Long siteId,
                                             Pageable pageable,
                                             List<String> keywordList,
                                             LocalDateTime startDate,
                                             LocalDateTime endDate
                                             ) {
        return applyPagination(pageable, contentQuery -> contentQuery
                .select(new QArticleDto(article.articleNum, article.url, userSite.nickname,
                        article.title, article.writer, article.written_date,
                        article.viewCount, article.recommendCount, article.commentCount))
                .from(article)
                .join(article.userSite, userSite)
                .join(article.site, site)
                .join(article.user, user)
                .where(
                        emailEq(userEmail),
                        titleContainsKeywords(keywordList),
                        siteEq(siteId),
                        timeBetween(startDate, endDate)
                ), countQuery -> countQuery
                .selectFrom(article)
                .join(article.userSite, userSite)
                .join(article.site, site)
                .join(article.user, user)
                .where(
                        emailEq(userEmail),
                        titleContainsKeywords(keywordList),
                        siteEq(siteId),
                        timeBetween(startDate, endDate)
                )
        );
    }

    private BooleanExpression timeBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate == null && endDate == null ? null : article.written_date.between(startDate, endDate);
    }

    private BooleanExpression emailEq(String email) {
        return hasText(email) ? user.email.eq(email) : null;
    }

    private BooleanExpression titleContainsKeywords(List<String> keywordList) {
        if (keywordList == null || keywordList.isEmpty()) return null;

        BooleanExpression expression = null;
        for (String keyword : keywordList) {
            BooleanExpression keywordExpression = article.title.like("%" + keyword + "%");
            expression = (expression != null) ? expression.or(keywordExpression) : keywordExpression;
        }

        return expression;
    }

    private BooleanExpression siteEq(Long siteId) {
        return siteId != null ? site.id.eq(siteId) : null;
    }
}