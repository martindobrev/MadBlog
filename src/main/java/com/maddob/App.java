package com.maddob;

import com.maddob.data.Article;
import com.maddob.data.ArticleProvider;
import com.maddob.data.InMemoryArticleProvider;
import com.maddob.server.MadWebServerVerticle;
import io.vertx.core.Vertx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This is the entry point of the application
 *
 */
public class App 
{
    public static void main( String[] args ) {
        Vertx vertx = Vertx.vertx();

//        Article helloWorldArticle = new Article();
//        helloWorldArticle.setId(UUID.randomUUID());
//        helloWorldArticle.setContent("Hello World!");
//        helloWorldArticle.setPublished(true);
//        helloWorldArticle.setTitle("hello");
//        helloWorldArticle.setCreated(LocalDateTime.now());
//
//        Article dummyArticle = new Article();
//        dummyArticle.setId(UUID.randomUUID());
//        dummyArticle.setContent("Dummy Article");
//        dummyArticle.setPublished(true);
//        dummyArticle.setTitle("dummy");
//        dummyArticle.setCreated(LocalDateTime.now().minusDays(2));
//
//        ArticleProvider dummyArticleProvider = new InMemoryArticleProvider();
//        dummyArticleProvider.addArticle(helloWorldArticle);
//        dummyArticleProvider.addArticle(dummyArticle);

        MadWebServerVerticle madServer = new MadWebServerVerticle();
        vertx.deployVerticle(madServer);
    }
}
