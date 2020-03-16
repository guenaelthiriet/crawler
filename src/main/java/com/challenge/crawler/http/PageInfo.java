package com.challenge.crawler.http;

import com.challenge.crawler.repository.HtmlPageEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Builder
@Getter
@ToString
public class PageInfo {
    private HtmlPageEntity htmlPageEntity;
    private Collection<String> linkOnPage;
}
