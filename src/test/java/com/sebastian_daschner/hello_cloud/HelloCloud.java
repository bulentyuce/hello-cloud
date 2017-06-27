package com.sebastian_daschner.hello_cloud;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.rules.ExternalResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.locks.LockSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloCloud extends ExternalResource {

    private static final int STARTUP_TIMEOUT = 30;
    private static final int STARTUP_PING_DELAY = 2;
    private static final String SIMPLE_URI = "http://192.168.99.100:30776/hello-cloud/resources/hello";
    private static final String CUSTOM_URI = "http://192.168.99.100:30776/hello-cloud/resources/hello/{name}";

    private WebTarget simpleGreetingTarget;
    private Client client;

    @Override
    protected void before() throws Throwable {
        client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT, 1000);
        simpleGreetingTarget = client.target(SIMPLE_URI);
        waitForApplicationStartUp();
    }

    public String getGreeting() {
        final Response response = requestSimpleGreeting();
        assertStatus(response, Response.Status.OK);
        return response.readEntity(String.class);
    }

    private Response requestSimpleGreeting() {
        return simpleGreetingTarget.request(MediaType.TEXT_PLAIN).get();
    }

    public String getGreeting(String name) {
        final Response response = requestCustomGreeting(name);
        assertStatus(response, Response.Status.OK);
        return response.readEntity(String.class);
    }

    private Response requestCustomGreeting(String name) {
        return client.target(CUSTOM_URI)
                .resolveTemplate("name", name)
                .request(MediaType.TEXT_PLAIN).get();
    }

    private void assertStatus(final Response response, final Response.Status expectedStatus) {
        assertThat(response.getStatus()).isEqualTo(expectedStatus.getStatusCode());
    }

    private void waitForApplicationStartUp() {
        final long timeout = System.currentTimeMillis() + STARTUP_TIMEOUT * 1000;
        while (simpleGreetingTarget.request().head().getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            System.out.println("waiting for application startup");
            LockSupport.parkNanos(1_000_000_000 * STARTUP_PING_DELAY);
            if (System.currentTimeMillis() > timeout)
                throw new AssertionError("Application wasn't started before timeout!");
        }
    }

}
