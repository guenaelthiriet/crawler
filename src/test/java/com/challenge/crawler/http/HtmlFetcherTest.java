package com.challenge.crawler.http;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;
import wiremock.org.apache.http.HttpStatus;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.io.Resources.getResource;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = HtmlFetcher.class)
@AutoConfigureWireMock(port = 0)
class HtmlFetcherTest {

    private final HtmlFetcher htmlFetcher = new HtmlFetcher();

    @Value("${wiremock.server.port}")
    private Integer port;

    @AfterEach
    void resetMocks() {
        WireMock.resetAllRequests();
    }

    @Test
    void fetchNoLinks() throws IOException {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse().withBody(Resources.toString(getResource("no-links.html"), Charsets.UTF_8))));

        PageInfo pageInfo = htmlFetcher.fetch(String.format("http://localhost:%d", port));
        System.out.println(pageInfo);

        verify(1, getRequestedFor(urlEqualTo("/")));
        assertThat(pageInfo.getLinkOnPage().size(), Is.is(0));
    }

    @Test
    void fetchOneLink() throws IOException {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse().withBody(Resources.toString(getResource("one-link.html"), Charsets.UTF_8))));

        PageInfo pageInfo = htmlFetcher.fetch(String.format("http://localhost:%d", port));
        System.out.println(pageInfo);

        verify(1, getRequestedFor(urlEqualTo("/")));
        assertThat(pageInfo.getLinkOnPage().size(), Is.is(1));
        assertThat(pageInfo.getLinkOnPage().toArray()[0], Is.is(String.format("http://localhost:%d/target-url", port)));
    }

    @Test
    void fetchNotFound() throws IOException {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND)));

        PageInfo pageInfo = htmlFetcher.fetch(String.format("http://localhost:%d", port));
        System.out.println(pageInfo);

        verify(1, getRequestedFor(urlEqualTo("/")));
        assertThat(pageInfo.getLinkOnPage(), IsNull.nullValue());
        assertThat(pageInfo.getHtmlPageEntity().getHttpStatus(), Is.is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    void fetchTimeout() throws IOException {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(3000)));

        PageInfo pageInfo = htmlFetcher.fetch(String.format("http://localhost:%d", port));
        System.out.println(pageInfo);

        verify(1, getRequestedFor(urlEqualTo("/")));
        assertThat(pageInfo.getLinkOnPage(), IsNull.nullValue());
        assertThat(pageInfo.getHtmlPageEntity().getHttpStatus(), Is.is(HttpStatus.SC_SERVICE_UNAVAILABLE));
    }
}