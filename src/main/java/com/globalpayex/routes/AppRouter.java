package com.globalpayex.routes;


import io.vertx.core.Vertx;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;


public class AppRouter {

    public static Router init(Vertx vertx, JsonObject config) {

        Router router = Router.router(vertx);
        router.post().handler(BodyHandler.create());
        router = BooksRoute.init(router);
        router = StudentRoute.init(router,vertx,config);

        return router;
    }
}
