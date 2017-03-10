package com.maddob;

import com.maddob.server.MadWebServerVerticle;
import io.vertx.core.Vertx;

/**
 * This is the entry point of the application
 *
 */
public class App 
{
    public static void main( String[] args ) {
        Vertx vertx = Vertx.vertx();
        MadWebServerVerticle madServer = new MadWebServerVerticle();
        vertx.deployVerticle(madServer);
    }
}
