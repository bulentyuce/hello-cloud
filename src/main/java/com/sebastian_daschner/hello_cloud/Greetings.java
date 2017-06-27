package com.sebastian_daschner.hello_cloud;

import javax.inject.Inject;

public class Greetings {

    @Inject
    GreetingProcessor greetingProcessor;

    public String hello() {
        return "Hello cloud";
    }

    public String hello(String name) {
        return greetingProcessor.createGreeting(name);
    }

}
