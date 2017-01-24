package com.maddob.server;

import com.maddob.data.Article;
import com.maddob.data.ArticleProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * WebServer verticle for the blog
 *
 * Starts a HTTP server on start and stops it on stop
 *
 * For now only gets access to all static content that is provided
 * in the webroot folder
 *
 * Created by martindobrev on 1/12/17.
 */
public class MadWebServerVerticle extends AbstractVerticle {

    private HttpServer server;
    private final ArticleProvider articleProvider;

    public MadWebServerVerticle(ArticleProvider articleProvider) {
        this.articleProvider = articleProvider;
    }

    @Override
    public void start() throws Exception {
        super.start();
        server = getVertx().createHttpServer();
        Router router = Router.router(getVertx());

        final String baseUrl = "http://localhost:25300";

        // In order to use a Thymeleaf template we first need to create an engine
        final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();

        // Render the home page
        router.get("/").handler(ctx -> {
            ctx.put("BASE_URL", baseUrl);
            ctx.put("title", "MADDOB | Home");
            engine.render(ctx, "templates/index.html", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        // Render a list of all available articles
        router.get("/articles").handler(ctx -> {
            ctx.put("BASE_URL", baseUrl);
            ctx.put("title", "MADDOB | Articles overview");
            List<Article> articles = articleProvider.getAllArticles();
            ctx.put("articles", articles);
            engine.render(ctx, "templates/articles.html", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        // Render a specified article (by title)
        router.get("/articles/:title").handler(ctx -> {
            String title = ctx.request().getParam("title");
            Article article = articleProvider.getArticleByTitle(title);
            if (null == article) {
                ctx.fail(404);
            } else {
                ctx.put("BASE_URL", baseUrl);
                ctx.put("title", article.getTitle());
                ctx.put("content", article.getContent());
                ctx.put("created", article.getCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                engine.render(ctx, "templates/article.html", res -> {
                    if (res.succeeded()) {
                       ctx.response().end(res.result());
                    } else {
                       ctx.fail(res.cause());
                    }
                });
            }
        });

        router.route("/*").handler(StaticHandler.create());
        server.requestHandler(router::accept).listen(25300);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.close();
    }
}
