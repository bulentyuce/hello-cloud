package com.sebastian_daschner.hello_cloud;

import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloCloudAcceptanceIT {

    @Rule
    public HelloCloud helloCloud = new HelloCloud();

    @Rule
    public Processor processor = new Processor();

    @Test
    public void getSimpleGreeting() {
        String greeting = helloCloud.getGreeting();
        assertThat(greeting).isEqualTo("Hello cloud");

        greeting = helloCloud.getGreeting();
        assertThat(greeting).isEqualTo("Hello cloud");
    }

    @Test
    public void getCustomGreeting() {
        checkGreetingInvocation("World", "Hello World");

        checkGreetingInvocation("Cloud", "Hello Cloud");

        assertThat(processor.getRequestedNames()).containsExactly("World", "Cloud");
    }

    private void checkGreetingInvocation(String name, String answer) {
        processor.answerForName(name, answer);
        String greeting = helloCloud.getGreeting(name);

        processor.verifyInvocation(name);
        assertThat(greeting).isEqualTo(answer);
    }

}