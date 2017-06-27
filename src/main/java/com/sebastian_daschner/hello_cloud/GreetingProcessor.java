package com.sebastian_daschner.hello_cloud;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class GreetingProcessor {

    private WebTarget target;
    private Client client;

    @PostConstruct
    private void initClient() {
        client = ClientBuilder.newClient();
        target = client.target("http://greeting-processor:8080/greeting-processor/resources/greetings");
    }

    public String createGreeting(String name) {
        JsonObject entity = buildEntity(name);
        JsonObject response = invokeTarget(entity);
        return extractGreeting(response);
    }

    private JsonObject buildEntity(String name) {
        return Json.createObjectBuilder()
                .add("name", name)
                .build();
    }

    private JsonObject invokeTarget(JsonObject entity) {
        return target.request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.json(entity))
                .invoke(JsonObject.class);
    }

    private String extractGreeting(JsonObject response) {
        return response.getString("greeting", null);
    }

    @PreDestroy
    public void close() {
        client.close();
    }

}
