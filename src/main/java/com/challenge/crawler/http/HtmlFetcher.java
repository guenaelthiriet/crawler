package com.challenge.crawler.http;

import com.challenge.crawler.repository.HtmlPageEntity;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@NoArgsConstructor
@Log4j2
public class HtmlFetcher {

    private final static String LINKS_SELECTOR = "a";
    private static final String ABS_HREF = "abs:href";

    private static final int READ_TIMEOUT = 2000;

    public PageInfo fetch(String url) {
        log.info("Fetching url {}", url);
        try {
            Connection connection = Jsoup.connect(url);
            connection.timeout(READ_TIMEOUT);
            Document doc = connection.get();
            return PageInfo.builder()
                    .htmlPageEntity(new HtmlPageEntity(url, HttpStatus.OK.value(), doc.html()))
                    .linkOnPage(doc.select(LINKS_SELECTOR).eachAttr(ABS_HREF))
                    .build();
        } catch (HttpStatusException e) {
            return PageInfo.builder()
                    .htmlPageEntity(new HtmlPageEntity(url, e.getStatusCode(), ""))
                    .build();
        } catch (IOException e) {
            // TODO: we might be able to get more detailed return codes from the library
            return PageInfo.builder()
                    .htmlPageEntity(new HtmlPageEntity(url, HttpStatus.SERVICE_UNAVAILABLE.value(), ""))
                    .build();
        }
    }
}


