package com.maddob.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

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

    @Override
    public void start() throws Exception {
        super.start();
        server = getVertx().createHttpServer();
        Router router = Router.router(getVertx());
        router.route("/*").handler(StaticHandler.create());
        server.requestHandler(router::accept).listen(8080);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.close();
    }
}
