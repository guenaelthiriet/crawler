package com.challenge.crawler;

import com.challenge.crawler.http.HtmlFetcher;
import com.challenge.crawler.http.PageInfo;
import com.challenge.crawler.repository.HtmlPageRepository;
import com.challenge.crawler.repository.HtmlPageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Log4j2
public class Crawler {

    private final HtmlFetcher htmlFetcher;
    private final HtmlPageRepository repository;

    @Value("${app.baseUrl}")
    private String baseUrl;

    public void crawl(String url) {
        if (url == null) {
            url = baseUrl;
            log.info("Crawler started at {}", baseUrl);
        }

        if (isPresentInDb(url)) {
            // Content already downloaded
            return;
        }

        PageInfo pageInfo = htmlFetcher.fetch(url);

        // Store the result
        repository.save(pageInfo.getHtmlPageEntity());

        if (!HttpStatus.valueOf(pageInfo.getHtmlPageEntity().getHttpStatus()).is2xxSuccessful()) {
            // Going down that path seems perilous, don't crawl
            return;
        }

        // Parse links
        for (String linkUrl : pageInfo.getLinkOnPage()) {
            parseLinks(linkUrl);
        }
    }

    private void parseLinks(String linkUrl) {
        // Only get the content on a given domain, no subdomain
        try {
            URL otherUrl = new URL(linkUrl);
            if (new URL(baseUrl).getHost().compareTo(otherUrl.getHost()) == 0) {
                crawl(linkUrl);
            }
        } catch (MalformedURLException e) {
            // something went wrong with the URL on the page
            // TODO can we find a better return code for this case?
            repository.save(new HtmlPageEntity(linkUrl, HttpStatus.SERVICE_UNAVAILABLE.value(), ""));
        }
    }

    private boolean isPresentInDb(String url) {
        return repository.findByUrl(url) != null;
    }

}
