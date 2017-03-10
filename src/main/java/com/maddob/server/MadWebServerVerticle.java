package com.maddob.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maddob.data.Article;
import com.maddob.data.ArticleProvider;
import com.maddob.data.InMemoryArticleProvider;
import com.maddob.file.FileProvider;
import com.maddob.file.FolderFileProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

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
     * Constant to be used for configuration of the initial database
     */
    public static final String CONFIG_DB_JSON_PATH = "DB_JSON_PATH";

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
    private FileProvider fileProvider;

    @Override
    public void start() throws Exception {
        super.start();

        // Initialize article provider
        String databaseLocalShareMapName = config().getString(CONFIG_DB, DB_NAME_DEFAULT);
        final int PORT = config().getInteger(CONFIG_HTTP_PORT, HTTP_PORT_DEFAULT);
        LocalMap<String, Article> localDatabase = vertx.sharedData().getLocalMap(databaseLocalShareMapName);
        articleProvider = new InMemoryArticleProvider(localDatabase);

        // Load articles from a file, if a path is specified
        String databasePath = config().getString(CONFIG_DB_JSON_PATH, "db/DefaultMadArticlesDatabase.json");
        FileSystem fileSystem = vertx.fileSystem();
        Buffer buffer = fileSystem.readFileBlocking(databasePath);

        JsonObject json = buffer.toJsonObject();
        Logger.getAnonymousLogger().info(json.toString());

        // Trying to import articles from a file
        JsonArray array = json.getJsonArray("articles");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        array.stream().forEach(articleJson -> {
            Logger.getAnonymousLogger().info("Importing article " + articleJson.toString());
            try {
                Article article = mapper.readValue(articleJson.toString(), Article.class);
                Logger.getAnonymousLogger().info("----> SUCCESS");
                articleProvider.addArticle(article);
            } catch (IOException e) {
                Logger.getAnonymousLogger().info("!!!-> ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        });
        if (articleProvider.getAllArticles().size() > 0) {
            Logger.getAnonymousLogger().info("Successfully loaded " + articleProvider.getAllArticles().size() + " articles");
        } else {
            Logger.getAnonymousLogger().info("No articles were loaded! Please make sure you provided a valid path to a json-article-database!");
        }

        // initialize the file provider
        fileProvider = new FolderFileProvider("/Users/martindobrev/Temp/vertxupload/files");

        server = getVertx().createHttpServer();
        Router router = Router.router(getVertx());



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
            ctx.put("BASE_URL", "http://" + ctx.request().host());
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
            ctx.put("BASE_URL", "http://" + ctx.request().host());
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
            ctx.put("BASE_URL", "http://" + ctx.request().host());
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
                ctx.put("BASE_URL", "http://" + ctx.request().host());
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

        // Render admin home page
        router.get("/admin").handler(ctx -> {
            ctx.put("BASE_URL", "http://" + ctx.request().host());
            ctx.put("CURRENT_PAGE", "admin");
            ctx.put("title", "MADDOB | Admin");
            engine.render(ctx, "admin", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        router.route("/admin/files/upload").handler(
                BodyHandler.create().setUploadsDirectory("/Users/martindobrev/vertxupload/temp"));
        router.post("/admin/files/upload").handler(ctx -> {
            Set<FileUpload> uploads = ctx.fileUploads();
            // save all uploaded files
            uploads.forEach(fileUpload -> fileProvider.saveFile(fileUpload));
            ctx.response().end();
        });

        router.route("/*").handler(StaticHandler.create());
        server.requestHandler(router::accept).listen(PORT);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.close();
    }
}