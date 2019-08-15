package org.max.learning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping(value = "/health_check")
    public Mono<String> findCityById() {
        return Mono.create(stringMonoSink -> stringMonoSink.success("alive"));
    }
    
}
