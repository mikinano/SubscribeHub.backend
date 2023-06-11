package com.subscribehub.app.service;

import com.subscribehub.app.domain.User;
import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.AddUserSiteRequest;
import com.subscribehub.app.dto.UserSiteDto;
import com.subscribehub.app.repository.SiteRepository;
import com.subscribehub.app.repository.UserRepository;
import com.subscribehub.app.repository.UserSiteAdvancedRepository;
import com.subscribehub.app.repository.UserSiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSiteService {
    private final UserSiteAdvancedRepository userSiteAdvancedRepository;
    private final UserSiteRepository userSiteRepository;
    private final UserRepository userRepository;
    private final SiteRepository siteRepository;

    public List<UserSite> userSiteList(String userEmail) {
        User user = userRepository.findOneByEmail(userEmail);
        return userSiteRepository.findAllByUser(user);
    }

    public void putUserSite(User user, List<AddUserSiteRequest> request) {
        userSiteRepository.deleteAllByUser(user);

        for (AddUserSiteRequest addUserSiteRequest : request) {
            userSiteRepository.save(new UserSite(user, siteRepository.findOneById(addUserSiteRequest.getSiteId()), addUserSiteRequest.getUrl(), addUserSiteRequest.getNickname()));
        }
    }
}
