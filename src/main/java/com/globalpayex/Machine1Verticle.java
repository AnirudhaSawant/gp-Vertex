package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Machine1Verticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Machine1Verticle.class);
    @Override
    public void start() throws Exception {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("port", 8083)
                        .put("connection_string", "mongodb+srv://admin:admin123@cluster0.ckmvxkb.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0").put("db_name", "college_db")
                        .put("useObjectId", true)
                        .put("emailHostName","smtp.gmail.com")
                        .put("emailPort",587)
                        .put("emailUsername","sawantanirudha20@gmail.com")
                        .put("emailPassword","uqim pzhf abab pany"));



        vertx.deployVerticle(new FirstHttpServer(), options);
        logger.info("Machine 1 Verticle Deployed");
    }

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions())
                .onSuccess(vertex -> {
                    vertex.deployVerticle(new Machine1Verticle());
                })
                .onFailure(exception -> logger.error("error in deploying machine 1 {} ",exception.getMessage()));
    }
}
