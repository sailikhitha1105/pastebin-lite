package com.pastebin.lite.service;

import com.pastebin.lite.dto.PasteResponseDto;
import com.pastebin.lite.dto.PastesDTO;
import com.pastebin.lite.entity.Pastes;
import com.pastebin.lite.exception.BadRequestException;
import com.pastebin.lite.exception.NotFoundException;
import com.pastebin.lite.exception.ResourceExpiredException;
import com.pastebin.lite.repository.PastesRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
public class PastesService
{
    @Value("${app.domain}")
    private String domain;

    private static final Logger LOG = LoggerFactory.getLogger(PastesService.class);

    private final PastesRepository repository;

    public PastesService(PastesRepository repository) {
        this.repository = repository;
    }

    public Map<String,Object> createPastes(PastesDTO pastesDTO,HttpServletRequest request)
    {
        LOG.info("createPastes called");
        if (pastesDTO.getContent() == null || pastesDTO.getContent().trim().isEmpty())
        {
            LOG.warn("createPastes - content is missing or empty");
            throw new BadRequestException("Content must not be null or empty");
        }
        if (pastesDTO.getTtl_seconds() != null && pastesDTO.getTtl_seconds() < 1) {
            throw new BadRequestException("ttl_seconds must be >= 1");
        }

        if (pastesDTO.getMax_views() != null && pastesDTO.getMax_views() < 1) {
            throw new BadRequestException("max_views must be >= 1");
        }

        Map<String,Object> response = new HashMap<>();

        Pastes paste = new Pastes();
        paste.setContent(pastesDTO.getContent());
        paste.setCreatedDate(LocalDateTime.now());
        paste.setActive(true);
        paste.setViewCount(0);
        paste.setMaxViews(pastesDTO.getMax_views());
        if (pastesDTO.getTtl_seconds() != null)
        {
            paste.setExpiresAt(now(request).plusSeconds(pastesDTO.getTtl_seconds()));
        }
        Pastes pasteRecord = repository.save(paste);
        LOG.info("createPastes - saved paste id={}, expiresAt={}, maxViews={}",
                pasteRecord.getId(), pasteRecord.getExpiresAt(), pasteRecord.getMaxViews());
        response.put("id",pasteRecord.getId());
        response.put("url",domain+"/p/" + paste.getId());
        return response;
    }

    public PasteResponseDto fetchPastes(String id,HttpServletRequest request)
    {
        LOG.info("fetchPastes called for id={}", id);
        if (id == null || id.trim().isEmpty())
        {
            LOG.error("fetchPastes - id is null or empty");
            throw new BadRequestException("Id must be provided");
        }
        Pastes pasteRecord = repository.findById(id);
        if(pasteRecord ==null)
        {
            LOG.error("fetchPastes - record not found for id={}", id);
            throw new NotFoundException("Record not found");
        }
        LocalDateTime expiresAt = pasteRecord.getExpiresAt();
        if (expiresAt != null && expiresAt.isBefore(now(request)))
        {
            LOG.info("fetchPastes - record expired by time for id={}", id);
            throw new ResourceExpiredException("Record expired");
        }
        Integer maxViews = pasteRecord.getMaxViews();
        int currentViews = pasteRecord.getViewCount();
        if (maxViews != null && currentViews >= maxViews)
        {
            LOG.info("fetchPastes - record expired by views for id={}, viewCount={}, maxViews={}",
                    id, pasteRecord.getViewCount(), maxViews);
            throw new ResourceExpiredException("Record expired due to max views");
        }
        pasteRecord.setViewCount(currentViews + 1);
        LOG.info("fetchPastes - incremented viewCount for id={} to {}", id, pasteRecord.getViewCount());
        repository.save(pasteRecord);

        Integer remaining = maxViews == null ? null : maxViews - (currentViews + 1);

        PasteResponseDto pasteResponseDto = new PasteResponseDto();
        pasteResponseDto.setContent(pasteRecord.getContent());
        pasteResponseDto.setRemaining_views(remaining);
        pasteResponseDto.setExpires_at(expiresAt);
        LOG.info("fetchPastes - returning content for id={}, remaining_views={}", id, remaining);
        return pasteResponseDto;
    }

    private LocalDateTime now(HttpServletRequest request)
    {
        if ("1".equals(System.getenv("TEST_MODE")))
        {
            String header = request.getHeader("x-test-now-ms");
            if (header != null)
            {
                return Instant.ofEpochMilli(Long.parseLong(header))
                        .atZone(ZoneOffset.UTC)
                        .toLocalDateTime();
            }
        }
        return LocalDateTime.now();
    }

}

