package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationVerticle3 extends AbstractVerticle{

    private int a = 10;
    private int b = 5;

    private static final Logger logger = LoggerFactory.getLogger(MultiOperationVerticle2.class);

    private Future<Integer> performAddition() {
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(3000, id -> {
            int result = a + b;
            promise.complete(result);
        });
        return promise.future();
    }

    private Future<Integer> performMultiplication() {
        Promise<Integer> promise = Promise.promise();
        vertx.setTimer(3000, id -> {
            int finalResult = (a * b);
            promise.complete(finalResult);
        });
        return promise.future();
    }

    @Override
    public void start() throws Exception {
        Future<Integer> additionFuture = performAddition();
        Future<Integer> multiplicationFuture = performMultiplication();

        Future.all(additionFuture,multiplicationFuture).onSuccess(result -> {
            logger.info("Addition is {}",additionFuture.result());
            logger.info("Multiplication is {}",multiplicationFuture.result());
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MultiOperationVerticle3());
    }
}
