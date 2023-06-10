package com.subscribehub.app.service.crawler;

import com.subscribehub.app.domain.Article;
import com.subscribehub.app.domain.Site;
import com.subscribehub.app.domain.User;
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

@Service
@Transactional
@RequiredArgsConstructor
public class CrawlerService {
    private final ArticleRepository articleRepository;
    public void doCrawling(String fullUrl, User user, Site site) throws Exception {
        if (site.getId() == 1) {
            dcCrawling(fullUrl, user, site);
        }
    }

    private void dcCrawling(String url, User user, Site site) throws Exception {
        List<Article> articleList = new ArrayList<>();
        // SSL 체크
        SetSSL.init();
        try {
            // URL에 접속하여 HTML 문서 가져오기
            Document doc = Jsoup.connect(url).get();

            // 게시글 목록을 포함한 요소 선택하기
            Elements posts = doc.select(".gall_list tbody tr");

            // 각 게시글에 대한 정보 출력
            for (Element post : posts) {
                // 게시글의 data-type 값 가져오기
                String dataType = post.attr("data-type");

                // 게시글의 제목과 링크 가져오기
                Element linkElement = post.selectFirst(".gall_tit a");
                String title = linkElement.text();
                String link = linkElement.attr("href");

                // data-type이 "icon-notice"이거나 "일반"이 아닌 경우 제외
                if (!dataType.equals("icon-notice") && post.select(".gall_subject").text().equals("일반")) {
                    System.out.println("제목: " + title);
                    System.out.println("링크: " + link);

                    // 게시글의 날짜 가져오기
                    Element dateElement = post.selectFirst(".gall_date");
                    String dateString = dateElement.attr("title");

                    LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    System.out.println("날짜: " + date);
                    articleList.add(new Article(user, site, link, title, date));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        articleRepository.saveAll(articleList);
    }
}
