package javier.com.mydorm1.controller;

import javier.com.mydorm1.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/util")
public class UtilController {
    private final Utils utils;

    @GetMapping("/generate-random-string")
    @PreAuthorize("hasAuthority('generate randomString')")
    public ResponseEntity<String> generateRandString(){
        return ResponseEntity.ok(utils.getRandomString());
    }

    @GetMapping("/test")
    public ResponseEntity<Date> test(){
        return ResponseEntity.ok(utils.test());
    }
}
