package com.maddob.data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class Article
 *
 * Represents a blog article
 *
 * Created by martindobrev on 1/17/17.
 */
public class Article {

    /** Unique id of the article */
    private UUID id;

    /** Title of the article */
    private String title;

    /** Timestamp of the creation */
    private LocalDateTime created;

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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime createdAt) {
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
