package org.max.exam.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/item")
public class ItemController {

	@ApiOperation(value = "health check", notes = "health check")
    @GetMapping(value = "/public/health_check")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("ALIVE", HttpStatus.OK);
    }
	
}
