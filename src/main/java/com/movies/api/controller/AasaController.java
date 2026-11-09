package com.movies.api.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class AasaController {

    @GetMapping(
            value = "/.well-known/apple-app-site-association",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> getAppleAppSiteAssociation()
            throws IOException {

        ClassPathResource resource = new ClassPathResource(
                "aasa/apple-app-site-association"
        );

        String json = StreamUtils.copyToString(
                resource.getInputStream(),
                StandardCharsets.UTF_8
        );

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(
                        CacheControl
                                .maxAge(1, TimeUnit.HOURS)
                                .cachePublic()
                )
                .body(json);
    }
}