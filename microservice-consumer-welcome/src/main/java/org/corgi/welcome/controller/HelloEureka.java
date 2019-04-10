package org.corgi.welcome.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class HelloEureka {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/welcome")
    public String callProvider() {
        return restTemplate.getForObject("http://microservice-provider-hello/sayHello/", String.class);
    }
}
