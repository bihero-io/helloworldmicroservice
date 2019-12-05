package io.bihero.helloworld;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.client.WebClient;

public class DefaultHelloWorldService implements HelloWorldService {

    private final Vertx vertx;

    private final JsonObject config;

    private final WebClient webClient;

    public DefaultHelloWorldService(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;
        this.webClient = WebClient.create(this.vertx);
    }

    @Override
    public void getHelloWorld(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        getHelloWord().compose(this::getHelloWorld).setHandler(v ->
                resultHandler.handle(
                        Future.succeededFuture(OperationResponse.completedWithPlainText(Buffer.buffer(v.result())))
                ));
    }

    @Override
    public void getDoc(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        vertx.fileSystem().readFile("doc.yaml", buffResult ->
                resultHandler.handle(Future.succeededFuture(
                        OperationResponse.completedWithPlainText(buffResult.result()))
                ));
    }

    private Future<String> getHelloWord() {
        Future<String> future = Future.future();
        webClient.get(config.getInteger("hello-service-port"), config.getString("hello-service-host"), "/").send(ar ->
                future.handle(Future.succeededFuture(ar.result().bodyAsString())));
        return future;
    }

    private Future<String> getHelloWorld(String helloWord) {
        Future<String> future = Future.future();
        webClient.get(config.getInteger("world-service-port"), config.getString("world-service-host"), "/").send(ar ->
                future.handle(Future.succeededFuture(helloWord + " " + ar.result().bodyAsString())));
        return future;
    }

}