package com.globalpayex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailVerticle extends AbstractVerticle {
    private MongoClient mongoClient;
    private MailClient mailClient;


    private static final Logger logger = LoggerFactory.getLogger(EmailVerticle.class);

    @Override
    public void start() throws Exception {
        mongoClient = MongoClient.createShared(vertx, config());
        MailConfig config = new MailConfig()
                .setHostname(config().getString("emailHostName"))
                .setPort(config().getInteger("emailPort"))
                .setStarttls(StartTLSOptions.REQUIRED)
                .setUsername(config().getString("emailUsername"))
                .setPassword(config().getString("emailPassword"));

        mailClient = MailClient.createShared(vertx, config);
        vertx
                .eventBus()
                .consumer("new.student", this::handleNewStudentEmail);
    }

    private void handleNewStudentEmail(Message<JsonObject> message) {
        String newStudentId = message.body().getString("_id");
        JsonObject query = new JsonObject()
                .put("_id", newStudentId);
        this.mongoClient.findOne("students", query, null)
                .onSuccess(this::handleStudentJson)
                .onFailure(this::handleStudentJsonFailure);
    }

    private void handleStudentJson(JsonObject studentDbJson) {
        if (studentDbJson != null) {
            MailMessage message = new MailMessage();
            message.setFrom(config().getString("emailUsername"));
            message.setTo(studentDbJson.getString("email"));
            message.setSubject("Welcome to the College Portal");
            message.setText(
                    String.format("Hey %s,Welcome to thr college portal", studentDbJson.getString("username"))
            );
            mailClient.sendMail(message)
                    .onSuccess(mailResult -> logger.info("email sent to studet {}", studentDbJson.getString("email"
                    )))
                    .onFailure(exception -> logger.error("error sending email {}", exception.getMessage()));
        }
    }

    private void handleStudentJsonFailure(Throwable throwable) {
        logger.info("Student not found {}", throwable.getMessage());
    }

}

