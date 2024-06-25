package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiboSeriesVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MultiOperationVerticle2.class);

    @Override
    public void start() throws Exception {
        JsonObject config = config();
        int n = config.getInteger("n");
        this.calculateFib(n);
        int n1 = config.getInteger("n1");
        this.calculateFib(n1);
    }

    private void calculateFib(int n) {
        int firstTerm = 0;
        int secondTerm = 1;

        for(int i = 0; i < n; i++) {
            logger.info("{} where n = {}", firstTerm,n);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int nextTerm = firstTerm + secondTerm;
            firstTerm = secondTerm;
            secondTerm = nextTerm;
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        JsonObject config = new JsonObject().put("n",10).put("n1",20);
        DeploymentOptions options = new DeploymentOptions().setConfig(config).setThreadingModel(ThreadingModel.WORKER);
        vertx.deployVerticle(new FiboSeriesVerticle(),options);
    }
}
