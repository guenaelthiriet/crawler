package com.challenge.crawler.repository;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@Getter
@Setter
@ToString
public class HtmlPageEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String url;

    private Integer httpStatus;

    @Lob
    private String html;

    public HtmlPageEntity() {
        this.id = null;
        this.url = null;
        this.html = null;
        this.httpStatus = null;
    }

    public HtmlPageEntity(String url, Integer httpStatus, String html) {
        this.url = url;
        this.httpStatus = httpStatus;
        this.html = html;
    }

}
