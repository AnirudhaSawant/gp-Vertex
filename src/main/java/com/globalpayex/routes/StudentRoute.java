package com.globalpayex.routes;

import com.globalpayex.entities.Book;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentRoute {

    private static final Logger logger = LoggerFactory.getLogger(BooksRoute.class);

    private static MongoClient mongoClient;

    public static Router init(Router router, Vertx vertx, JsonObject config) {
        mongoClient = MongoClient.createShared(vertx, config);
        router.get("/students").handler(StudentRoute::getAllStudents);
        router.get("/students/:studentId").handler(StudentRoute::getStudent);
        router.post("/students").handler(routingContext -> createNewStudent(routingContext,vertx));


        return router;
    }

    private static void createNewStudent(RoutingContext routingContext, Vertx vertx) {
        JsonObject requestJson = routingContext.body().asJsonObject();
        Future<String> future = mongoClient.insert("students", requestJson)
                        .onSuccess(studentId -> {
                            requestJson.put("_id",studentId);
                            vertx
                                    .eventBus()
                                    .publish("new.student",new JsonObject().put("_id",studentId));
                            routingContext.response()
                                    .setStatusCode(201)
                                    .putHeader("Content-Type", "application/json")
                                    .end(requestJson.encode());
                        });
        future.onFailure(exception -> logger.info("error in saving the student {}",exception.getMessage()));
    }

    private static void getStudent(RoutingContext routingContext) {
        String studentId = routingContext.pathParam("studentId");
        JsonObject query = new JsonObject()
                .put("_id", new JsonObject().put("$oid", studentId));
        Future<JsonObject> future = mongoClient.findOne("students", query, null);
        future.onSuccess(studentObject -> {
            if (studentObject == null) {
                routingContext
                        .response()
                        .setStatusCode(404)
                        .end("Student not found");
            } else {
                JsonObject responseJson = mapDbToResponseJson((studentObject));
                routingContext
                        .response()
                        .putHeader("Content-Type", "application/json")
                        .end(responseJson.encode());
            }
        });
        future.onFailure(exception -> {
            logger.error("Error in fetching students data {} - Reason {}", exception.getMessage(), exception.getCause());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Server Connection Error");
        });
    }

    private static void getAllStudents(RoutingContext routingContext) {
        List<String> genderQp = routingContext.queryParam("gender");
        List<String> countryQp = routingContext.queryParam("country");

        JsonObject query = new JsonObject();
        JsonArray orCondition = new JsonArray();

        /* JsonObject query = new JsonObject();
        if(genderQp.size()>0){
            query.put("gender",genderQp.get(0));
        }
        if (countryQp.size()>0) {
            query.put("address.country",countryQp.get(0));
        } */
        if (genderQp.size() > 0) {
            orCondition.add(new JsonObject().put("gender", genderQp.get(0)));
        }
        if (countryQp.size() > 0) {
            orCondition.add(new JsonObject().put("address.country", countryQp.get(0)));
        }
        query.put("$or", orCondition);
        Future<List<JsonObject>> future = mongoClient
                .find("students", query);
        future.onSuccess(studentJsonObjects -> {
            logger.info("Students {}", studentJsonObjects);
            List<JsonObject> responseJson = studentJsonObjects
                    .stream()
                    .map(StudentRoute::mapDbToResponseJson)
                    .collect(Collectors.toList());
            JsonArray responseData = new JsonArray(responseJson);
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .end(responseData.encode());
        });
        future.onFailure(exception -> {
            logger.error("Error in fetching students data {} - Reason {}", exception.getMessage(), exception.getCause());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Server Connection Error");
        });
    }

    private static JsonObject mapDbToResponseJson(JsonObject dbJson) {
        JsonObject responseJson = new JsonObject();
        /* responseJson.put("_id",dbJson
                .getJsonObject("_id")
                .getString("$oid")); */
        responseJson.put("_id", dbJson.getString("_id"));
        responseJson.put("username", dbJson.getString("username"));
        responseJson.put("gender", dbJson.getString("gender"));
        responseJson.put("email", dbJson.getString("email"));
        return responseJson;
    }

}
 