package com.sebastian_daschner.hello_cloud;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.rules.ExternalResource;

import javax.json.Json;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;

public class Processor extends ExternalResource {

    @Override
    protected void before() throws Throwable {
        configureFor("192.168.99.100", 32502);
        resetAllRequests();

        stubFor(post("/greeting-processor/resources/greetings")
                .willReturn(jsonResponse("default")));
    }

    public void answerForName(String name, String answer) {
        stubFor(post("/greeting-processor/resources/greetings")
                .withRequestBody(jsonRequest(name))
                .willReturn(jsonResponse(answer)));
    }

    private StringValuePattern jsonRequest(String name) {
        return equalToJson("{\"name\":\"" + name + "\"}", true, true);
    }

    private ResponseDefinitionBuilder jsonResponse(String greeting) {
        return okForJson(singletonMap("greeting", greeting));
    }

    public void verifyInvocation(String name) {
        verify(1, postRequestedFor(urlEqualTo("/greeting-processor/resources/greetings"))
                .withRequestBody(jsonRequest(name)));
    }

    public List<String> getRequestedNames() {
        return getAllServeEvents().stream()
                .map(ServeEvent::getRequest)
                .sorted(comparing(LoggedRequest::getLoggedDate))
                .filter(r -> r.getUrl().equals("/greeting-processor/resources/greetings"))
                .map(r -> extractName(r.getBodyAsString()))
                .collect(Collectors.toList());
    }

    private String extractName(String jsonResponse) {
        return Json.createReader(new StringReader(jsonResponse)).readObject().getString("name");
    }

    @Override
    protected void after() {
        resetAllRequests();
    }

}
