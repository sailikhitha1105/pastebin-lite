package com.pastebin.lite.controller;

import com.pastebin.lite.constants.Constants;
import com.pastebin.lite.dto.PasteResponseDto;
import com.pastebin.lite.dto.PastesDTO;
import com.pastebin.lite.exception.BadRequestException;
import com.pastebin.lite.exception.NotFoundException;
import com.pastebin.lite.exception.ResourceExpiredException;
import com.pastebin.lite.service.PastesService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

@RestController
public class PasteController
{
    private static final Logger LOG = LoggerFactory.getLogger(PasteController.class);

    private final PastesService service;

    public PasteController(PastesService service) {
        this.service = service;
    }

    @PostMapping(value = "/api/pastes")
    public ResponseEntity<Object> createPastes(@RequestBody(required = true) PastesDTO pastesDTO, HttpServletRequest request)
    {
        long startTimeMillis = System.currentTimeMillis();
        LOG.info("API CALL Started /api/pastes");
        Map<String, Object> response;
        try
        {
            response= service.createPastes(pastesDTO,request);
            LOG.info("API CALL Ended /api/pastes:: perfLog :{} ms", System.currentTimeMillis() - startTimeMillis);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BadRequestException bre)
        {
            LOG.warn("BadRequest in /api/pastes :: {}", bre.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(Constants.ERROR, bre.getMessage()));
        }
        catch (Exception e)
        {
            LOG.error("Error in /api/pastes :: {}", e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(Constants.ERROR, "unable to create record"));
        }
    }

    @GetMapping(value = "/api/pastes/{id}")
    public ResponseEntity<Object> getPaste(@PathVariable String id,HttpServletRequest request)
    {
        long startTimeMillis = System.currentTimeMillis();
        LOG.info("API CALL Started /api/pastes/{}",id);
        try
        {
            PasteResponseDto resp = service.fetchPastes(id,request);
            LOG.info("API CALL Ended /api/pastes/{}:: perfLog :{} ms", id,System.currentTimeMillis() - startTimeMillis);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
        catch (BadRequestException bre)
        {
            LOG.warn("BadRequest in /api/pastes/{} :: {}", id, bre.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(Constants.ERROR, bre.getMessage()));
        }

        catch (NotFoundException nfe)
        {
            LOG.warn("NotFound in /api/pastes/{} :: {}", id, nfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(Constants.ERROR, nfe.getMessage()));
        }
        catch (ResourceExpiredException ree)
        {
            LOG.info("Expired in /api/pastes/{} :: {}", id, ree.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(Constants.ERROR, ree.getMessage()));
        }
        catch (Exception e)
        {
            LOG.error("Error in /api/pastes/{} :: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(Constants.ERROR, "unable to fetch record"));
        }
    }

    @GetMapping(value = "/p/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> viewPasteHtml(@PathVariable String id,HttpServletRequest request)
    {
        try
        {
            PasteResponseDto pasteResponseDto = service.fetchPastes(id, request);
            String safeContent = HtmlUtils.htmlEscape(pasteResponseDto.getContent());

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head><title>Paste</title></head>
                    <body>
                        <pre>%s</pre>
                    </body>
                    </html>
                    """.formatted(safeContent);

            return ResponseEntity.ok(html);
        }
        catch (ResourceExpiredException | NotFoundException | BadRequestException e)
        {
            String html = """
                <!DOCTYPE html>
                <html>
                <head><title>Content Not Available</title></head>
                <body>
                    <h2>Content not available</h2>
                    <p>This Content has expired or does not exist.</p>
                </body>
                </html>
                """;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        }
    }

}
