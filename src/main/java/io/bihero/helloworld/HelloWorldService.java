package io.bihero.helloworld;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

@WebApiServiceGen
public interface HelloWorldService {

    static HelloWorldService create(Vertx vertx, JsonObject config) {
        return new DefaultHelloWorldService(vertx, config);
    }

    void getHelloWorld(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);

}
