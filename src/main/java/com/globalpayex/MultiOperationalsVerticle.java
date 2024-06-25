package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationalsVerticle extends AbstractVerticle {

    private int a = 10;
    private int b = 5;

    private static final Logger logger = LoggerFactory.getLogger(MultiOperationalsVerticle.class);

    @Override
    public void start() throws Exception {
        logger.info("Verticle Starts!");
        vertx.setTimer(5000, id1 -> {
            int result = a + b;
            logger.info("Addition: {}", result);
            vertx.setTimer(3000, id2 -> {
                int multiResult = a * b + result;
                logger.info("Multiplication: {}", multiResult);
            });
        });
    }


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MultiOperationalsVerticle());
    }
}
