package com.gudiantales.characterbook.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MainController {
    @GetMapping("/home")
    public String home(){
        log.info("main controller");
        return "123Hello world!!!!!";
    }
}
