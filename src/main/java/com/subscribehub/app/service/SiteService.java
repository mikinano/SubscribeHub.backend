package com.subscribehub.app.service;

import com.subscribehub.app.domain.Site;
import com.subscribehub.app.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteService {
    private final SiteRepository siteRepository;

    public Site findOneById(Long id) {
        return siteRepository.findOneById(id);
    }
}
