package com.sebastian_daschner.hello_cloud;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("hello")
public class HelloResource {

    @Inject
    Greetings greetings;

    @GET
    public String hello() {
        return greetings.hello();
    }

}
