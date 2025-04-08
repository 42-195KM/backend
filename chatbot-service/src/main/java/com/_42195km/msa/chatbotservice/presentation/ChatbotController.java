package com._42195km.msa.chatbotservice.presentation;



import com._42195km.msa.chatbotservice.application.service.AiService;
import com._42195km.msa.chatbotservice.application.service.SseService;
import com._42195km.msa.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class ChatbotController {

    private final AiService aiService;
    private final SseService sseService;

    @GetMapping(value = "/api/v1/chatbots/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable Long userId){

//        return ResponseEntity.ok(ApiResponse.success(sseService.subscribe(userId)));
        return sseService.subscribe(userId);
    }

    @PostMapping("/api/v1/chatbots")
    public ResponseEntity<ApiResponse<SseEmitter>>  sendQuestion(@RequestParam("userId") Long userId, @RequestBody String question) {
        aiService.sendQuestion(userId, question);
        return ResponseEntity.ok(ApiResponse.success(sseService.subscribe(userId)));
    }



}
