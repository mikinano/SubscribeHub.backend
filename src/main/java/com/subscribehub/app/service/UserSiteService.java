package com.subscribehub.app.service;

import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.UserSiteDto;
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

    public List<UserSiteDto> searchSiteDto(String userEmail) {
        return userSiteAdvancedRepository.searchSiteDto(userEmail);
    }

    public void save(UserSite userSite) {
        userSiteRepository.save(userSite);
    }
}
