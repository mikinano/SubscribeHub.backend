package com.subscribehub.app.service.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscribehub.app.domain.Article;
import com.subscribehub.app.domain.User;
import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.UserSiteDto;
import com.subscribehub.app.repository.ArticleRepository;
import com.subscribehub.app.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
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
    private final KeywordService keywordService;
    public void doCrawling(User user, UserSite userSite) throws Exception {
        // SSL 체크
        SetSSL.init();

        Long siteId = userSite.getSite().getId();
        if (siteId == 1) {
            dcCrawling(user, userSite);
        } else if (siteId == 2) {
            fmKorCrawling(user, userSite);
        }
    }

    private void dcCrawling(User user, UserSite userSite) {
        List<Article> articleList = new ArrayList<>();
        final String siteUrl = "https://gall.dcinside.com/";

        try {
            // URL에 접속하여 HTML 문서 가져오기
            Document doc = Jsoup.connect(userSite.getUrl()).get();

            // 게시글 목록을 포함한 요소 선택하기
            Elements posts = doc.select(".gall_list tbody tr");

            // 각 게시글에 대한 정보 출력
            for (Element post : posts) {
                Element numberElement = post.selectFirst(".gall_num");
                Element checkArticle = post.selectFirst(".gall_tit a em");

                // 게시글 필터링
                if (numberElement.text().matches("-?\\d+")
                        && !(checkArticle.hasClass("icon_ad")
                            || checkArticle.hasClass("icon_survey")
                            || checkArticle.hasClass("icon_notice"))) {
                    // 게시글의 번호, 제목, 링크 가져오기
                    Long articleNum = Long.parseLong(numberElement.text());

                    Element linkElement = post.selectFirst(".gall_tit a");
                    String title = linkElement.text();
                    String link = siteUrl + linkElement.attr("href");

                    // 게시글의 글쓴이 가져오기
                    Element writerElement = post.selectFirst(".gall_writer");
                    String writer = writerElement.text();

                    // 게시글의 댓글수, 조회수, 추천수 가져오기
                    long commentCount = 0L;
                    Element replyElement = post.selectFirst(".gall_tit .reply_numbox");
                    if (replyElement != null) {
                        commentCount = Long.parseLong(replyElement.text().replaceAll("[\\[\\]]", ""));
                    }

                    Element viewElement = post.selectFirst(".gall_count");
                    Long viewCount = Long.parseLong(viewElement.text());

                    Element recommendElement = post.selectFirst(".gall_recommend");
                    Long recommendCount = Long.parseLong(recommendElement.text());

                    // 게시글의 날짜 가져오기
                    Element dateElement = post.selectFirst(".gall_date");
                    String dateString = dateElement.attr("title");

                    LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

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
            boolean flag = true;
            for (Article existingArticle : existingArticles) {
                if (existingArticle.getArticleNum().equals(article.getArticleNum())) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                articleRepository.save(article);
            }
        }
    }

    private void fmKorCrawling(User user, UserSite userSite) {
        List<Article> articleList = new ArrayList<>();
        final String siteUrl = "https://www.fmkorea.com/";

        try {
            // URL에 접속하여 HTML 문서 가져오기
            Document doc = Jsoup.connect(userSite.getUrl()).get();

            // 게시글 목록을 포함한 요소 선택하기
            Elements posts = doc.select(".gall_list tbody tr");

            // 각 게시글에 대한 정보 출력
            for (Element post : posts) {
                Element numberElement = post.selectFirst(".gall_num");
                Element checkArticle = post.selectFirst(".gall_tit a em");

                // 게시글 필터링
                if (numberElement.text().matches("-?\\d+")
                        && !(checkArticle.hasClass("icon_ad")
                        || checkArticle.hasClass("icon_survey")
                        || checkArticle.hasClass("icon_notice"))) {
                    // 게시글의 번호, 제목, 링크 가져오기
                    Long articleNum = Long.parseLong(numberElement.text());

                    Element linkElement = post.selectFirst(".gall_tit a");
                    String title = linkElement.text();
                    String link = siteUrl + linkElement.attr("href");

                    // 게시글의 글쓴이 가져오기
                    Element writerElement = post.selectFirst(".gall_writer");
                    String writer = writerElement.text();

                    // 게시글의 댓글수, 조회수, 추천수 가져오기
                    long commentCount = 0L;
                    Element replyElement = post.selectFirst(".gall_tit .reply_numbox");
                    if (replyElement != null) {
                        commentCount = Long.parseLong(replyElement.text().replaceAll("[\\[\\]]", ""));
                    }

                    Element viewElement = post.selectFirst(".gall_count");
                    Long viewCount = Long.parseLong(viewElement.text());

                    Element recommendElement = post.selectFirst(".gall_recommend");
                    Long recommendCount = Long.parseLong(recommendElement.text());

                    // 게시글의 날짜 가져오기
                    Element dateElement = post.selectFirst(".gall_date");
                    String dateString = dateElement.attr("title");

                    LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

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
            boolean flag = true;
            for (Article existingArticle : existingArticles) {
                if (existingArticle.getArticleNum().equals(article.getArticleNum())) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                articleRepository.save(article);
            }
        }
    }

    public String getArticleContent(Long articleId) throws Exception {
        Optional<Article> article = articleRepository.findById(articleId);
        String url = "";
        if (article.isPresent()) {
            url = article.get().getUrl();
        } else {
            return "잘못된 게시글 ID입니다.";
        }

        // SSL 체크
        SetSSL.init();

        if (article.get().getSite().getId() == 1) {
            return getDcArticleContent(url);
        } else {
            return "존재하지 않는 사이트입니다.";
        }
    }

    public String getDcArticleContent(String url) {
        try {
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

    public List<UserSiteDto> searchUserSites(String searchWord, Long siteId) throws Exception {
        // SSL 체크
        SetSSL.init();

        if (siteId == 1) {
            return searchDcUserSites(searchWord);
        } else if (siteId == 2) {
            return searchFmKorUserSites(searchWord);
        } else {
            return null;
        }
    }

    private List<UserSiteDto> searchDcUserSites(String searchWord) {
        String url = "https://search.dcinside.com/gallery/q/" + searchWord;
        List<UserSiteDto> list = new ArrayList<>();

        try {
            // HTTP GET 요청을 보내고 HTML 문서를 가져옵니다.
            Document doc = Jsoup.connect(url).get();

            // 갤러리 검색 결과를 가져옵니다.
            Elements galleries = doc.select(".integrate_cont_list li");

            // 갤러리 정보를 출력합니다.
            for (Element gallery : galleries) {
                Element anchor = gallery.selectFirst("a.gallname_txt");
                String name = anchor.text();
                String href = anchor.attr("href");

                list.add(new UserSiteDto(href, name));
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    private List<UserSiteDto> searchFmKorUserSites(String searchWord) {
        String siteUrl = "https://www.fmkorea.com/";
        ObjectMapper objectMapper = new ObjectMapper();
        List<UserSiteDto> userSiteDtoList = new ArrayList<>();

        try {
            URL jsonUrl = new URL("https://www.fmkorea.com/files/board_search_data.json");
            JsonNode root = objectMapper.readTree(jsonUrl);

            if (root.isArray()) {
                for (JsonNode node : root) {
                    String url = siteUrl + node.get("mid").asText();
                    String nickname = node.get("label").asText();
                    UserSiteDto userSiteDto = new UserSiteDto(url, nickname);
                    userSiteDtoList.add(userSiteDto);
                }
            }

            return userSiteDtoList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private List<UserSiteDto> searchPpUserSites(String searchWord) {
//        final String siteUrl = "https://www.ppomppu.co.kr";
//        List<UserSiteDto> list = new ArrayList<>();
//
//        try {
//            String url = "https://www.ppomppu.co.kr/search_bbs.php?keyword=%C0%DA%C0%AF";
//            Document doc = Jsoup.connect(url).get();
//
//            Elements boardItems = doc.select(".results_board#search_bbs_name_result .conts ul li");
//            for (Element boardItem : boardItems) {
//                String boardName = boardItem.selectFirst(".title").text();
//                String boardLink = siteUrl + boardItem.selectFirst("a").attr("href");
//
//                list.add(new UserSiteDto(boardLink, boardName));
//            }
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
