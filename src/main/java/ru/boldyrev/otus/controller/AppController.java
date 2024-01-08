package ru.boldyrev.otus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AppController {

    private final String authUrl;

    public AppController(@Value("${application.auth-url}") String authUrl) {
        this.authUrl = authUrl;
    }

    @GetMapping("/get/me")
    public ResponseEntity<Map<String, String>> getMe(@CookieValue(value = "session_id", required = false) String sessionId) {
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorReason", "Not authorized"));
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "session_id=" + sessionId);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(authUrl, HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, String>>() {});

        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorReason", "Session not found"));
        }

        else {
            // Если авторизация успешна, вернуть данные из заголовков ответа
            Map<String,String> responseMap = response.getHeaders().entrySet().stream().filter(e-> e.getKey().startsWith("X-")).collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
            //responseMap.put("sessionId", sessionId);
            //responseMap.put("status", "OK");
            return ResponseEntity.ok(responseMap);
        }
    }

    @ExceptionHandler(Exception.class)
    public  ResponseEntity<Map<String, String>> handleUnauthorizedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorReason", ex.getMessage()));
    }


}
