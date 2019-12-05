package io.bihero.helloworld;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@Testcontainers
@ExtendWith(VertxExtension.class)
public class HelloWorldServiceTest {

    @Container
    private static final GenericContainer helloServiceContainer = new GenericContainer("bihero/hello")
            .withExposedPorts(8081);

    @Container
    private static final GenericContainer worldServiceContainer = new GenericContainer("bihero/world")
            .withExposedPorts(8082);

    @Test
    @DisplayName("Test 'helloworld' microservice respond by 'Hello World' string and doc in OpenAPI format")
    public void testHelloWorld(Vertx vertx, VertxTestContext testContext) {
        WebClient webClient = WebClient.create(vertx);

        Checkpoint deploymentCheckpoint = testContext.checkpoint();
        Checkpoint requestCheckpoint = testContext.checkpoint(2);

        HelloWorldVerticle verticle = spy(new HelloWorldVerticle());
        JsonObject config = new JsonObject().put("serverPort", 8083)
                                            .put("serverHost", "0.0.0.0")
                                            .put("hello-service-host", helloServiceContainer.getContainerIpAddress())
                                            .put("world-service-host", worldServiceContainer.getContainerIpAddress())
                                            .put("hello-service-port", helloServiceContainer.getMappedPort(8081))
                                            .put("world-service-port", worldServiceContainer.getMappedPort(8082));
        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(verticle, deploymentOptions, testContext.succeeding(id -> {
            deploymentCheckpoint.flag();
            // test GET /
            webClient.get(8083, "localhost", "/")
                    .as(BodyCodec.string())
                    .send(testContext.succeeding(resp -> {
                        assertThat(resp.body()).isEqualTo("Hello World");
                        assertThat(resp.statusCode()).isEqualTo(200);
                        requestCheckpoint.flag();
                    }));
            // test GET /doc
            webClient.get(8083, "localhost", "/doc")
                    .as(BodyCodec.string())
                    .send(testContext.succeeding(resp -> {
                        try {
                            assertThat(resp.body()).isEqualTo(IOUtils.toString(this.getClass()
                                    .getResourceAsStream("../../../doc.yaml"), "UTF-8"));
                            assertThat(resp.statusCode()).isEqualTo(200);
                            requestCheckpoint.flag();
                        } catch (Exception e) {
                            requestCheckpoint.flag();
                            testContext.failNow(e);
                        }
                    }));
        }));
    }

}
