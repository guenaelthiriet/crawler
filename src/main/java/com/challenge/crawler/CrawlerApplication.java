package com.challenge.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CrawlerApplication {

    public static void main(String[] args) {
        ApplicationContext appContext = SpringApplication.run(CrawlerApplication.class, args);
        Crawler crawler = appContext.getBean(Crawler.class);
        crawler.crawl(null);
    }

    private static void usage() {
        System.out.println("Run using gradle wrapper: ./gradlew bootRun -Pargs=--app.baseUrl=https://google.com");
    }

}
