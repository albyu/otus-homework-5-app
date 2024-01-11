package ru.boldyrev.otus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import ru.boldyrev.otus.exception.NotFoundException;
import ru.boldyrev.otus.exception.SessionNotFoundException;
import ru.boldyrev.otus.model.AppUser;
import ru.boldyrev.otus.service.AppUserService;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AppController {

    private final String authUrl;
    private final AppUserService appUserService;

    public AppController(@Value("${application.auth-url}") String authUrl, @Autowired AppUserService appUserService) {
        this.authUrl = authUrl;
        this.appUserService = appUserService;
    }

    @GetMapping("/users/me")
    public ResponseEntity<Map<String, String>> getMe(@CookieValue(value = "session_id", required = false) String sessionId) throws SessionNotFoundException, NotFoundException {
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorReason", "Not authorized"));
        }

        Map<String, String> responseMap = checkAuth(sessionId);
        //получить данные из репо
        String username = responseMap.get("X-User-Name");
        AppUser appUser = appUserService.getUserById(username);

        return ResponseEntity.status(HttpStatus.OK).body(appUser.toMap()); //вернуть данные из БД

    }

    @PutMapping("/users/me")
    public ResponseEntity<Map<String, String>> updateMe(@CookieValue(value = "session_id", required = false) String sessionId, @RequestBody AppUser updatedUser) throws SessionNotFoundException, NotFoundException {
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorReason", "Not authorized"));
        }

        Map<String, String> responseMap = checkAuth(sessionId);
        //обновить данные в репо
        String username = responseMap.get("X-User-Name");

        AppUser appUser = appUserService.updateUser(username, updatedUser);

        return ResponseEntity.status(HttpStatus.OK).body(appUser.toMap()); //вернуть данные из БД


    }

    Map<String, String> checkAuth(String sessionId) throws SessionNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "session_id=" + sessionId);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(authUrl, HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, String>>() {
        });
        if (response.getStatusCode() != HttpStatus.OK)
            throw new SessionNotFoundException("Session not found");

        return response.getHeaders().entrySet().stream().filter(e -> e.getKey().startsWith("X-")).collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("errorReason", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorReason", ex.getMessage()));
    }


}
