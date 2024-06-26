package com.globalpayex.routes;

import com.globalpayex.FirstHttpServer;
import com.globalpayex.entities.Book;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BooksRoute {

    private static int lastUsedId = 3;

    private static final Logger logger = LoggerFactory.getLogger(BooksRoute.class);

    // dummy database
    private static ArrayList<Book> books;

    public static Router init(Router router) {

        books = new ArrayList<Book>();
        books.add(new Book(1,"book 1",900,1000));
        books.add(new Book(2,"prog in java",850,950));
        books.add(new Book(3,"scala programming",920,1500));


        router.get("/books").handler(BooksRoute::getAllBooks);
        router.get("/books/:bookId").handler(BooksRoute::getBook);
        router.post("/books").handler(BooksRoute::newBook);
        return router;
    }

    private static void newBook(RoutingContext routingContext) {

        Book book = routingContext.body().asPojo(Book.class);
        book.setId(++lastUsedId);
        books.add(book);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("Content-Type","application-json")
                .end(JsonObject.mapFrom(book).encode());
    }

    private static void getBook(RoutingContext routingContext) {
        int bookId = Integer.parseInt(routingContext.pathParam("bookId"));
        List<Book> foundBookList = books.stream()
                .filter(book -> book.getId() == bookId)
                .collect(Collectors.toList());

        if(!foundBookList.isEmpty()) {
            Book book = foundBookList.get(0);

            routingContext
                    .response()
                    .putHeader("Content-Type","application/json")
                    .end(JsonObject.mapFrom(book).encode());
        }
        else{
            JsonObject data = new JsonObject()
                    .put("message",String.format("Book with id %s not found",bookId));

            routingContext
                    .response()
                    .putHeader("Content-Type","application/json")
                    .setStatusCode(404)
                    .end(data.encode());
        }

    }

    private static void getAllBooks(RoutingContext routingContext) {
        JsonArray data = new JsonArray(books);
            routingContext
                    .response()
                    .putHeader("Content-Type","application/json")
                    .end(data.encode());
    }
}
