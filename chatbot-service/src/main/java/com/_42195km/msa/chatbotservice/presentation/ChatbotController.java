package com._42195km.msa.chatbotservice.presentation;



import com._42195km.msa.chatbotservice.application.service.AiService;
import com._42195km.msa.chatbotservice.application.service.ConversationService;
import com._42195km.msa.chatbotservice.application.service.SseService;
import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import com._42195km.msa.chatbotservice.presentation.dto.request.QuestionRequestDto;
import com._42195km.msa.chatbotservice.presentation.dto.request.SearchConversationDto;
import com._42195km.msa.chatbotservice.presentation.mapper.ChatbotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChatbotController {

    private final AiService aiService;
    private final SseService sseService;
    private final ConversationService conversationService;

    @GetMapping(value = "/api/v1/chatbots/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable UUID userId){
        return sseService.subscribe(userId);
    }

    @PostMapping("/api/v1/chatbots")
    public ResponseEntity<?> sendQuestion(@RequestBody QuestionRequestDto questionRequestDto) {
        aiService.sendQuestion(ChatbotMapper.toQuestionRequestAppDto(questionRequestDto));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/v1/chatbots/conversations/search")
    public ResponseEntity<Page<Conversation>> search(@RequestBody SearchConversationDto searchConversationDto,
                                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc){
        Page<Conversation> search = conversationService.search(ChatbotMapper.toSearchConversationAppDto(searchConversationDto), page-1, size, sortBy, isAsc);
        return ResponseEntity.ok(search);
    }


}
