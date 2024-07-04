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

public class BooksRoute {

    private static final Logger logger = LoggerFactory.getLogger(BooksRoute.class);
    private static MongoClient mongoClient;


    private static ArrayList<Book> books;
    private static int lastUserId = 3;


    public static Router init(Router router, Vertx vertx, JsonObject config) {
        mongoClient = MongoClient.createShared(vertx, config);
//        books = new ArrayList<>();
//        books.add(new Book(1, "java",5600.0,200));
//        books.add(new Book(2, "Python",3600.0,100));
//        books.add(new Book(3, "Machine Learning",2600.0,600));

        router.get("/books").handler(BooksRoute::getAllBooks);
//        router.get("/books/:bookId").handler(BooksRoute::getBook);
//        router.post("/books").handler(BooksRoute::createNewBook);
        return router;
    }

//    private static void createNewBook(RoutingContext routingContext) {
//          Book book = routingContext.body().asPojo(Book.class);
//          book.setId(++lastUserId);
//          books.add(book);
//          logger.info("new Book {}", book);
//          routingContext.response()
//                  .setStatusCode(201)
//                  .putHeader("Content-Type", "application/json")
//                  .end(JsonObject.mapFrom(book).encode());
//
//    }

//    private static void getBook(RoutingContext routingContext) {
//        int bookId = Integer.parseInt(routingContext.pathParam("bookId"));
//        List<Book> founddBookList = books.stream()
//                .filter(book -> book.getId()==bookId)
//                .collect(Collectors.toList());
//        if(!founddBookList.isEmpty())  {
//            Book book = founddBookList.get(0);
//
//            routingContext
//                    .response()
//                    .putHeader("Content-Type", "application/json")
//                    .end(JsonObject.mapFrom(book).encode());
//        }
//        else {
//            JsonObject data= new JsonObject()
//                    .put("message",String.format("Book with id %s not found",bookId));
//            routingContext
//                    .response()
//                    .putHeader("Content-Type", "application/json")
//                    .setStatusCode(404)
//                    .end(data.encode());
//        }
//    }

    private static void getAllBooks(RoutingContext routingContext) {
        JsonObject query = buildGetAllBooksQuery(routingContext);

        Future<List<JsonObject>> future = mongoClient
                .find("books", query);
        future.onSuccess(bookJsonObjects -> {
            logger.info("book {}", bookJsonObjects);

            List<JsonObject> responseJson = bookJsonObjects
                    .stream()
                    .map(BooksRoute::mongoDbToResponseJson)
                    .collect(Collectors.toList());
            JsonArray responseData = new JsonArray(responseJson);
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .end(responseData.encode());
        });

        future.onFailure(exception -> {
            logger.error("error is fetching books {}", exception.getMessage());
            routingContext
                    .response()
                    .setStatusCode(501)
                    .end("Server error");
        });

    }

    private static JsonObject buildGetAllBooksQuery(RoutingContext routingContext) {
        List<String> priceQp = routingContext.queryParam("price");
        List<String> pagesQp = routingContext.queryParam("pages");

        JsonObject query = new JsonObject();
        JsonArray orConditions = new JsonArray();
        if (priceQp.size() > 0) {
            orConditions.add(new JsonObject().put("details.price", new JsonObject().put("$gt",
                    Integer.parseInt(priceQp.get(0)))));

        }
        if (pagesQp.size() > 0) {
            orConditions.add(new JsonObject().put("details.pages", new JsonObject().put("$gt", Integer.parseInt(pagesQp.get(0)))));
        }
        if (orConditions.size() > 0) {
            query.put("$or", orConditions);
        }

        return query;

    }

    private static JsonObject mongoDbToResponseJson(JsonObject dbJson) {
        JsonObject responseJson = new JsonObject();
        responseJson.put("_id", dbJson.getString("_id"));
        responseJson.put("title", dbJson.getString("book_title"));
        responseJson.put("price", dbJson.getJsonObject("details").getDouble("price"));
        responseJson.put("pages", dbJson.getJsonObject("details").getDouble("pages"));
        return responseJson;
    }
}
