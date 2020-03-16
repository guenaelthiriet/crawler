package com.challenge.crawler;

import com.challenge.crawler.http.HtmlFetcher;
import com.challenge.crawler.http.PageInfo;
import com.challenge.crawler.repository.HtmlPageEntity;
import com.challenge.crawler.repository.HtmlPageRepository;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlerTest {

    @Mock
    private HtmlFetcher htmlFetcher;

    @Mock
    private HtmlPageRepository repository;

    @InjectMocks
    Crawler crawler;

    @Test
    void crawlNoLinks() {
        String url = "http://superhost.com";
        ReflectionTestUtils.setField(crawler, "baseUrl", url);

        HtmlPageEntity htmlPageEntity = mock(HtmlPageEntity.class);
        when(htmlPageEntity.getHttpStatus()).thenReturn(200);

        PageInfo pageInfo = mock(PageInfo.class);
        when(htmlFetcher.fetch(any())).thenReturn(pageInfo);
        when(pageInfo.getHtmlPageEntity()).thenReturn(htmlPageEntity);
        when(pageInfo.getLinkOnPage()).thenReturn(Collections.emptyList());

        crawler.crawl(null);

        verify(repository, times(1)).findByUrl(url);
        verify(repository, times(1)).save(any());
        verify(htmlFetcher, times(1)).fetch(any());
    }

    @Test
    void crawlOneLink() {
        String url = "http://superhost.com";
        ReflectionTestUtils.setField(crawler, "baseUrl", url);

        HtmlPageEntity htmlPageEntity = mock(HtmlPageEntity.class);
        when(htmlPageEntity.getHttpStatus()).thenReturn(200);

        PageInfo pageInfo = mock(PageInfo.class);
        when(htmlFetcher.fetch(any())).thenReturn(pageInfo);
        when(pageInfo.getHtmlPageEntity()).thenReturn(htmlPageEntity);
        when(pageInfo.getLinkOnPage())
                .thenReturn(Lists.newArrayList(url + "/contact"))
                .thenReturn(Collections.emptyList());

        crawler.crawl(null);

        verify(repository, times(1)).findByUrl(url);
        verify(repository, times(1)).findByUrl(url + "/contact");
        verify(repository, times(2)).save(any());
        verify(htmlFetcher, times(2)).fetch(any());
    }

    @Test
    void crawlOneLinkYields404() {
        String url = "http://superhost.com";
        ReflectionTestUtils.setField(crawler, "baseUrl", url);

        HtmlPageEntity htmlPageEntity = mock(HtmlPageEntity.class);
        when(htmlPageEntity.getHttpStatus())
                .thenReturn(200)
                .thenReturn(404);

        PageInfo pageInfo = mock(PageInfo.class);
        when(htmlFetcher.fetch(any())).thenReturn(pageInfo);
        when(pageInfo.getHtmlPageEntity()).thenReturn(htmlPageEntity);
        when(pageInfo.getLinkOnPage())
                .thenReturn(Lists.newArrayList(url + "/contact"))
                .thenReturn(Collections.emptyList());

        crawler.crawl(null);

        verify(repository, times(1)).findByUrl(url);
        verify(repository, times(1)).findByUrl(url + "/contact");
        verify(repository, times(2)).save(any());
        verify(htmlFetcher, times(2)).fetch(any());
    }
}