package com.globalpayex

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class FirstHttpServerSpec extends Specification {

    Vertx vertx
    DeploymentOptions options

    def setup() {
        vertx = Vertx.vertx()
        options = new DeploymentOptions()
                .setConfig(new JsonObject().put("port", 8083).put("connection_string", "mongodb+srv://admin:admin123@cluster0.jggywij.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0").put("db_name", "college_db").put("useObjectId", true));
        vertx.deployVerticle(new FirstHttpServer(), options);
    }

    def cleanup() {
        vertx.close()
    }

    def "test the deployment of the first http verticle"() {
        given:
        def actualDeploymentId = new BlockingVariable<String>()

        when:
        vertx.deployVerticle("com.globalpayex.FirstHttpServer", options)
                .onSuccess (deploymentId->actualDeploymentId.set(deploymentId))
                .onFailure (exception ->actualDeploymentId.set(''))
        then:
        actualDeploymentId.get().size()>0
    }
}
