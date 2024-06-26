package com.globalpayex.routes;
import com.globalpayex.entities.Book;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
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
    private static final Logger logger = LoggerFactory.getLogger(StudentRoute.class);
    private static MongoClient mongoClient;
    public static Router init(Router router, Vertx vertx, JsonObject config) {
        mongoClient = MongoClient.createShared(vertx,config);
        router.get("/students").handler(StudentRoute::getAllStudents);
        router.get("/students/:studentId").handler(StudentRoute::getStudent);
        router.post("/students").handler(StudentRoute::newStudent);
        return router;
    }
    private static void newStudent(RoutingContext routingContext) {
        JsonObject requestJson = routingContext.body().asJsonObject();
        mongoClient.insert("students",requestJson);
        logger.info("Students {}",requestJson);
        routingContext.response().putHeader("Content-Type","application/json").setStatusCode(201).end(requestJson.encode());
    }
    private static void getStudent(RoutingContext routingContext) {
        String studentId = routingContext.pathParam("studentId");
        JsonObject query = new JsonObject().put("_id",new JsonObject().put("$oid",studentId));
        Future<JsonObject> future = mongoClient.findOne("students",query,null);
        future.onSuccess(studentObject -> {
            if(studentObject == null) {
                routingContext
                        .response()
                        .setStatusCode(404)
                        .end("Student not Found!");
            }
            else{
                JsonObject responseJson = mapToResponseJson(studentObject);
                routingContext
                        .response()
                        .putHeader("Content-Type","application/json").end(responseJson.encode());
            }
        });
        future.onFailure(exception -> {
            logger.error("error in fetching students {}",exception.getMessage(),"{}",exception.getCause());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Server Error");
        });
    }
    private static void getAllStudents(RoutingContext routingContext) {
        List<String> genderQp = routingContext.queryParam("gender");
        List<String> countryQp= routingContext.queryParam("country");

        JsonObject query = new JsonObject();
        if(!genderQp.isEmpty()) {
            query.put("gender",genderQp.get(0));
        }
        if(!countryQp.isEmpty()){
            query.put("address.country",countryQp.get(0));
        }
        Future<List<JsonObject>> future = mongoClient
                .find("students", query);
        future.onSuccess(studentJsonObject -> {
            logger.info("students {}",studentJsonObject);
            List<JsonObject> responseJson = studentJsonObject.stream().map(StudentRoute::mapToResponseJson).collect(Collectors.toList());
            JsonArray responseData = new JsonArray(responseJson);
            routingContext
                    .response()
                    .putHeader("Content-Type","application/json").end(responseData.encode());
        });
        future.onFailure(exception -> {
            logger.error("error in fetching students {}",exception.getMessage(),"{}",exception.getCause());
            routingContext
                    .response()
                    .setStatusCode(500)
                    .end("Server Error");
        });
    }
    private static JsonObject mapToResponseJson(JsonObject dbJson) {
        JsonObject responseJson = new JsonObject();
//        responseJson.put("_id",dbJson.getJsonObject("_id").getString("$oid"));
        responseJson.put("_id",dbJson.getString("_id"));
        responseJson.put("username",dbJson.getString("username"));
        responseJson.put("gender",dbJson.getString("gender"));
        responseJson.put("email",dbJson.getString("email"));
        return responseJson;
    }
}
 