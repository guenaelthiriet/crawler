package com.challenge.crawler.repository;


import org.springframework.data.jpa.repository.JpaRepository;

// Store html pages. the key the the url
// Not storing images or other data for now
public interface HtmlPageRepository extends JpaRepository<HtmlPageEntity, Long> {
    HtmlPageEntity findByUrl(String url);
}
