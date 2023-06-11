package com.subscribehub.app.service.crawler;

import com.subscribehub.app.domain.Article;
import com.subscribehub.app.domain.Site;
import com.subscribehub.app.domain.User;
import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CrawlerService {
    private final ArticleRepository articleRepository;
    public void doCrawling(User user, UserSite userSite, List<ArticleDto> updatedList) throws Exception {
        if (userSite.getSite().getId() == 1) {
            dcCrawling(user, userSite, updatedList);
        }
    }

    private void dcCrawling(User user, UserSite userSite, List<ArticleDto> updatedList) throws Exception {
        List<Article> articleList = new ArrayList<>();
        // SSL 체크
        SetSSL.init();
        try {
            // URL에 접속하여 HTML 문서 가져오기
            Document doc = Jsoup.connect(userSite.getUrl()).get();

            // 게시글 목록을 포함한 요소 선택하기
            Elements posts = doc.select(".gall_list tbody tr");

            // 각 게시글에 대한 정보 출력
            for (Element post : posts) {
                // 게시글의 data-type 값 가져오기
                String dataType = post.attr("data-type");

                // 게시글의 번호, 제목, 링크 가져오기
                Element numberElement = post.selectFirst(".gall_num");
                Long articleNum = Long.parseLong(numberElement.text());

                Element linkElement = post.selectFirst(".gall_tit a");
                String title = linkElement.text();
                String link = linkElement.attr("href");

                // 게시글의 글쓴이 가져오기
                Element writerElement = post.selectFirst(".gall_writer");
                String writer = writerElement.text();

                // data-type이 "icon-notice"이거나 "일반"이 아닌 경우 제외
                if (!dataType.equals("icon-notice") && post.select(".gall_subject").text().equals("일반")) {
                    System.out.println("제목: " + title);
                    System.out.println("링크: " + link);

                    // 게시글의 댓글수, 조회수, 추천수 가져오기
                    Long commentCount = 0L;
                    Element replyElement = post.selectFirst(".gall_count .reply_num");
                    if (replyElement != null) {
                        commentCount = Long.parseLong(replyElement.text());
                    }

                    Element viewElement = post.selectFirst(".gall_count .gall_count_view");
                    Long viewCount = Long.parseLong(viewElement.text());

                    Element recommendElement = post.selectFirst(".gall_recommend");
                    Long recommendCount = Long.parseLong(recommendElement.text());

                    // 게시글의 날짜 가져오기
                    Element dateElement = post.selectFirst(".gall_date");
                    String dateString = dateElement.attr("title");

                    LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    System.out.println("날짜: " + date);
                    articleList.add(new Article(user, userSite.getSite(), userSite, articleNum, link, title, writer, date, viewCount, recommendCount, commentCount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Long> articleNums = articleList.stream().map(Article::getArticleNum).distinct().collect(Collectors.toList());

        // 한 번의 Select 쿼리로 해당 Article 가져오기
        List<Article> existingArticles = articleRepository.findByUserSiteAndArticleNumIn(userSite, articleNums);

        // 이미 존재하는 게시글인지 확인하여 저장하기
        for (Article article : articleList) {
            if (!existingArticles.contains(article)) {
                articleRepository.save(article);
                updatedList.add(new ArticleDto(
                        article.getArticleNum(),
                        article.getUrl(),
                        article.getUserSite().getNickname(),
                        article.getTitle(),
                        article.getWriter(),
                        article.getWritten_date(),
                        article.getViewCount(),
                        article.getRecommendCount(),
                        article.getCommentCount()));
            }
        }
    }

    public String getArticleContent(Long articleId) {
        try {
            Optional<Article> article = articleRepository.findById(articleId);
            String url = "";
            if (article.isPresent()) {
                url = article.get().getUrl();
            } else {
                return "잘못된 게시글 ID입니다.";
            }

            // URL에 접속하여 HTML 문서 가져오기
            Document doc = Jsoup.connect(url).get();

            // 게시글 내용을 포함한 요소 선택하기
            Element contentElement = doc.selectFirst(".write_div");
            if (contentElement != null) {
                return contentElement.text();
            } else {
                return "게시글 내용을 가져올 수 없습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "오류로 인해 게시글을 불러올 수 없습니다.";
        }
    }
}
