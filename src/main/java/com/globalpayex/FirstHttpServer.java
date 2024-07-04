package com.globalpayex;

import com.globalpayex.routes.AppRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FirstHttpServer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(FirstHttpServer.class);


    @Override
    public void start() throws Exception {


        Future<HttpServer> serverFuture = vertx.createHttpServer()
                .requestHandler(AppRouter.init(vertx, config()))
                .listen(config().getInteger("port"));
        serverFuture.onSuccess(httpServer -> logger.info("Server running on port {}", httpServer.actualPort()));

        serverFuture.onFailure(exception -> logger.info("Cannot start server {}", exception.getMessage()));
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("port", 8083).put("connection_string", "mongodb+srv://admin:admin123@cluster0.jggywij.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0").put("db_name", "college_db").put("useObjectId", true));
        vertx.deployVerticle(new FirstHttpServer(), options);
    }
}
