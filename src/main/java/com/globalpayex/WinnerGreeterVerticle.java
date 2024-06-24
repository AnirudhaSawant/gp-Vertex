package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class WinnerGreeterVerticle extends AbstractVerticle {

    public static final List<String> students = Arrays.asList("Mehul","Prathmesh","Viraj","Jane","Anirudha");

    private static final Logger logger = LoggerFactory.getLogger(WinnerGreeterVerticle.class);

    @Override
    public void start() throws Exception {
        logger.info("Verticle starts");
        vertx.setTimer(1000,id -> logger.info("And"));
        vertx.setTimer(2000,id -> logger.info("the"));
        vertx.setTimer(3000,id -> logger.info("Winner"));
        vertx.setTimer(4000,id -> logger.info("is"));
        vertx.setTimer(9000,this::handleWinner);
    }

    private void handleWinner(Long aLong) {
        var random = new Random();
        String winner = students.get(random.nextInt(students.size()));
        logger.info(winner);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        //vertx.deployVerticle(new WinnerReaderVerticle());
        DeploymentOptions options = new DeploymentOptions().setInstances(2);
        vertx.deployVerticle("com.globalpayex.WinnerGreeterVerticle",options);
    }
}
