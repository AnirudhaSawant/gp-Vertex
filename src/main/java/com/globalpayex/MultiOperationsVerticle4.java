package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiOperationsVerticle4 extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MultiOperationsVerticle4.class);

    private void performAddition(int a, int b) {
        logger.info("Addition is {}", a + b);
    }

    private int calculateFib(int n) {
        int firstTerm = 0;
        int secondTerm = 1;
        int nextTerm = 0;

        for(int i = 0; i < n; i++) {
            logger.info("{} where n = {}", firstTerm,n);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            nextTerm = firstTerm + secondTerm;
            firstTerm = secondTerm;
            secondTerm = nextTerm;
        }
        return nextTerm;
    }

    private void performMultiplication(int a, int b) {
        logger.info("Multiplication is {}", a * b);
    }

    private void readFile(String filePath) {
        OpenOptions options = new OpenOptions().setCreate(false).setRead(true);
        Future<AsyncFile> readFileFuture = vertx.fileSystem().open(filePath,options);
        readFileFuture.onSuccess(asyncFile -> {
            asyncFile.handler(System.out::println)
                    .exceptionHandler(exception -> logger.error("error reading file {}",exception.getMessage()));
        });
        readFileFuture.onFailure(exception -> logger.error("error opening file {}",exception.getMessage()));
    }

    @Override
    public void start() throws Exception {
        int a = config().getInteger("a");
        int b = config().getInteger("b");

        vertx.setTimer(1000, id -> this.performAddition(a,b));
        vertx.setTimer(1000, id -> this.performMultiplication(a,b));
        vertx.setTimer(1000, id -> this.readFile("build.gradle"));

        /*
        vertx.executeBlocking(() -> {
            this.calculateFib(a);
            return 0;
        });

        vertx.executeBlocking(() -> {
            this.calculateFib(b);
            return 0;
        });
         */

        vertx.executeBlocking(() -> this.calculateFib(a), ar -> {
            if(ar.succeeded()) {
                int r = ar.result();
                logger.info("blocking operation result is {}",r);
            }
        });

        vertx.executeBlocking(() -> this.calculateFib(b), ar -> {
            if(ar.succeeded()) {
                int r = ar.result();
                logger.info("blocking operation result is {}",r);
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("a",10).put("b",5));
        vertx.deployVerticle(new MultiOperationsVerticle4(),options);
    }
}
