package com.pks.spring_sms_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class HomeController {
    @GetMapping({" ","/"})
    public ResponseEntity<String> home(){
        log.info("Inside home");
        return new ResponseEntity<>("Home Page", HttpStatus.OK);
    }
}
