package com.subscribehub.app.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.subscribehub.app.domain.Article;
import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleSearchCondition;
import com.subscribehub.app.dto.QArticleDto;
import com.subscribehub.app.repository.support.Querydsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.subscribehub.app.domain.QArticle.article;
import static com.subscribehub.app.domain.QSite.site;
import static com.subscribehub.app.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class ArticleAdvancedRepository extends Querydsl4RepositorySupport {
    public ArticleAdvancedRepository() {
        super(Article.class);
    }

    public Page<ArticleDto> searchPagination(ArticleSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable, contentQuery -> contentQuery
                .select(new QArticleDto(site.siteUrl.concat(article.url), article.title, article.written_date))
                .from(article)
                .join(article.site, site)
                .join(article.user, user)
                .where(
                        emailEq(condition.getEmail()),
                        titleContainsKeywords(condition.getKeywordList()),
                        siteEq(condition.getSiteId())
                ), countQuery -> countQuery
                .selectFrom(article)
                .join(article.site, site)
                .where(
                        titleContainsKeywords(condition.getKeywordList()),
                        siteEq(condition.getSiteId())
                )
        );
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