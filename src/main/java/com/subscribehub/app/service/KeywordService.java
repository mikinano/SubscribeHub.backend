package com.subscribehub.app.service;

import com.subscribehub.app.domain.Keyword;
import com.subscribehub.app.domain.User;
import com.subscribehub.app.repository.KeywordRepository;
import com.subscribehub.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

    public List<String> getKeywords(String userEmail) {
        return keywordRepository.findKeywordListByUser(userEmail);
    }

    public void putKeywords(List<String> keywords, String userEmail) {
        User user = userRepository.findOneByEmail(userEmail);
        keywordRepository.removeAllByUser(user);

        for (String keyword : keywords) {
            keywordRepository.save(new Keyword(user, keyword));
        }
    }
    public List<String> findKeywordListByUser(String userEmail) {
        return keywordRepository.findKeywordListByUser(userEmail);
    }
}
