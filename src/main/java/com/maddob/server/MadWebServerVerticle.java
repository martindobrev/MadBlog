package com.maddob.server;

import com.maddob.data.Article;
import com.maddob.data.ArticleProvider;
import com.maddob.data.InMemoryArticleProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

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
 * - about page
 *
 * Thymeleaf is used as a template engine
 *
 * Simple in-memory data storage is used for the articles
 *
 * Created by martindobrev on 1/12/17.
 */
public class MadWebServerVerticle extends AbstractVerticle {

    /**
     * Constant to be used for configuration of the in-local-memory database
     */
    public static final String CONFIG_DB = "DB";

    /**
     * Constant to be sued for configuration of the HTTP port for the web server
     */
    public static final String CONFIG_HTTP_PORT = "http.port";

    /**
     * Default name of the in-local-memory database location in the vertx shared data
     */
    public static final String DB_NAME_DEFAULT = "MadArticles";

    /**
     * Default http port that will be used in case not provided with a config
     */
    private static final int HTTP_PORT_DEFAULT = 25300;

    private HttpServer server;
    private ArticleProvider articleProvider;

    @Override
    public void start() throws Exception {
        super.start();

        // Initialize article provider
        String databaseLocalShareMapName = config().getString(CONFIG_DB, DB_NAME_DEFAULT);
        int port = config().getInteger(CONFIG_HTTP_PORT, HTTP_PORT_DEFAULT);
        LocalMap<String, Article> localDatabase = vertx.sharedData().getLocalMap(databaseLocalShareMapName);
        articleProvider = new InMemoryArticleProvider(localDatabase);

        server = getVertx().createHttpServer();
        Router router = Router.router(getVertx());
        final String baseUrl = "http://localhost:" + port;

        // In order to use a Thymeleaf template we first need to create an engine
        // We also set the template resolver to be a class loader template resolver
        // This is necessary, because the other types will not work properly when
        // running the application from a fat jar
        final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        engine.getThymeleafTemplateEngine().setTemplateResolver(templateResolver);
        engine.getThymeleafTemplateEngine().addDialect(new Java8TimeDialect());

        // Render the home page
        router.get("/").handler(ctx -> {
            ctx.put("BASE_URL", baseUrl);
            ctx.put("CURRENT_PAGE", "");
            ctx.put("title", "MADDOB | Home");
            List<Article> articles = articleProvider.getLatestArticles(4);
            ctx.put("articles", articles);
            engine.render(ctx, "index", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        // Render the about page
        router.get("/about").handler(ctx -> {
            ctx.put("BASE_URL", baseUrl);
            ctx.put("CURRENT_PAGE", "about");
            ctx.put("title", "MADDOB | About");
            engine.render(ctx, "about", res -> {
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
                ctx.put("article", article);
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
        server.requestHandler(router::accept).listen(port);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.close();
    }
}
