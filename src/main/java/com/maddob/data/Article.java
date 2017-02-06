package com.maddob.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.vertx.core.shareddata.Shareable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Class Article
 *
 * Represents a blog article
 *
 * Created by martindobrev on 1/17/17.
 */
public class Article implements Shareable {

    /** Unique id of the article */
    private UUID id;

    /** Title of the article */
    private String title;

    /** Timestamp of the creation */
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate created;

    /** Flag to publish the article */
    private boolean published;

    /** Main article content - usually markdown or html */
    private String content;

    ////////////////////////// GETTERS & SETTERS /////////////////////////

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate createdAt) {
        this.created = createdAt;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
