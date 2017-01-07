import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Created by Dmitriy Chalienko on 07.01.2017.
 */
@RunWith(VertxUnitRunner.class)
public class ChatTest {
    private Vertx vertx;
    private int port = 8080;
    private Logger log = LoggerFactory.getLogger(ChatTest.class);

    @Before
    public void setUp(TestContext context) throws IOException {
        VerticleLoader.load(context.asyncAssertSuccess());
        vertx = VerticleLoader.getVertx();
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void loadVerticleTest(TestContext context) {
        log.info("*** loadVerticleTest ***");

        Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/", response ->
        {
            context.assertEquals(response.statusCode(), 200);
            context.assertEquals(response.headers().get("content-type"), "text/html");
            response.bodyHandler(body ->
            {
                context.assertTrue(body.toString().contains("<title>Chat</title>"));
                async.complete();
            });
        });
    }

    @Test
    public void eventBusTest(TestContext context) {
        log.info("*** eventBusTest ***");

        Async async = context.async();
        EventBus eb = vertx.eventBus();
        eb.consumer("chat.to.server").handler(message ->
        {
            String getMsg = message.body().toString();
            context.assertEquals(getMsg, "hello");
            async.complete();
        });

        eb.publish("chat.to.server", "hello");
    }

}