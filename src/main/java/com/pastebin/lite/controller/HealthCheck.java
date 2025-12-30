package com.pastebin.lite.controller;

import com.pastebin.lite.repository.PastesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class HealthCheck
{
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheck.class);
    private final PastesRepository pasteRepository;
    public HealthCheck(PastesRepository pasteRepository)
    {
        this.pasteRepository = pasteRepository;
    }

    @GetMapping(value = "/healthz")
    public ResponseEntity<Object> apiHealthCheck()
    {
        try
        {
            LOG.info("Enter into HealthCheckController :: /api/healthz");
            LOG.info("Exit from HealthCheckController :: /api/healthz");
            pasteRepository.count();
            return ResponseEntity.ok(Map.of("ok", true));
        }
        catch (Exception e)
        {
            LOG.error("Exception in HealthCheckController :: healthcheck()",  e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false));
        }
    }
}
