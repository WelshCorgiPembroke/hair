package org.corgi.hello.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloEureka {
    @Value("${server.port}")
    private String port;

    @RequestMapping("/sayHello")
    public String sayHello() {
        return "hello eureka, this is provider: " + port;
    }
}
