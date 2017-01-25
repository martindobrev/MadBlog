package com.maddob.server;

import com.maddob.data.Article;
import com.maddob.data.ArticleProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * WebServer verticle for the blog
 *
 * Starts a HTTP server on start and stops it on stop
 *
 * Renders three page types with help of templates:
 * - home page
 * - articles overview
 * - article detailed view
 *
 * Thymeleaf is used as a template engine
 *
 * Simple in-memory data storage is used for the articles
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

        // The default template resolver does not render template fragments,
        // this can be fixed by a custom FileTempateResolver, but only if the
        // path to the templates folder is accessible
        final URL templatesUrl = getClass().getClassLoader().getResource("templates");
        if (templatesUrl != null) {
            final String templatesPath = templatesUrl.getPath();
            if (templatesPath != null) {
                final FileTemplateResolver resolver = new FileTemplateResolver();
                resolver.setTemplateMode("HTML");
                resolver.setPrefix(templatesPath + "/");
                resolver.setSuffix(".html");
                engine.getThymeleafTemplateEngine().setTemplateResolver(resolver);
            }
        }

        // Render the home page
        router.get("/").handler(ctx -> {
            ctx.put("BASE_URL", baseUrl);
            ctx.put("CURRENT_PAGE", "");
            ctx.put("title", "MADDOB | Home");
            engine.render(ctx, "index", res -> {
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
            ctx.put("CURRENT_PAGE", "articles");
            ctx.put("title", "MADDOB | Articles overview");
            List<Article> articles = articleProvider.getAllArticles();
            ctx.put("articles", articles);
            engine.render(ctx, "articles", res -> {
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
                ctx.put("CURRENT_PAGE", "article");
                ctx.put("title", article.getTitle());
                ctx.put("content", article.getContent());
                ctx.put("created", article.getCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                engine.render(ctx, "article", res -> {
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
