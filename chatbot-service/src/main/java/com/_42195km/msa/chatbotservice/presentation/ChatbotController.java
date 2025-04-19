package com._42195km.msa.chatbotservice.presentation;



import com._42195km.msa.chatbotservice.application.dto.response.SearchConversationResponseAppDto;
import com._42195km.msa.chatbotservice.application.service.ChatModelService;
import com._42195km.msa.chatbotservice.application.service.ConversationService;
import com._42195km.msa.chatbotservice.application.service.EmbeddingService;
import com._42195km.msa.chatbotservice.application.service.SseService;
import com._42195km.msa.chatbotservice.presentation.dto.request.QuestionRequestDto;
import com._42195km.msa.chatbotservice.presentation.dto.request.SearchConversationRequestDto;
import com._42195km.msa.chatbotservice.presentation.dto.response.SearchConversationResponseDto;
import com._42195km.msa.chatbotservice.presentation.mapper.ChatbotMapper;
import com._42195km.msa.chatbotservice.presentation.mapper.ConversationMapper;
import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.common.code.CommonServiceCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbots")
public class ChatbotController {

    private final ChatModelService aiService;
    private final SseService sseService;
    private final EmbeddingService embeddingService;
    private final ConversationService conversationService;


    @PostMapping
    public SseEmitter sendQuestion(@RequestBody QuestionRequestDto questionRequestDto) {
        SseEmitter sseEmitter = sseService.subscribe(questionRequestDto.getUserId());
        aiService.sendQuestion(ChatbotMapper.toQuestionRequestAppDto(questionRequestDto));
        return sseEmitter;

    }


    @GetMapping("/conversations/search")
    public ResponseEntity<ApiResponse<Page<SearchConversationResponseDto>>> search(@RequestBody SearchConversationRequestDto searchConversationDto,
                                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc){
        Page<SearchConversationResponseAppDto> search = conversationService.search(ConversationMapper.toAppDto(searchConversationDto), page-1, size, sortBy, isAsc);
        return ResponseEntity.ok(ApiResponse.success(search.map(ConversationMapper::toDto)));
    }


    @PostMapping("/embedding")
    public ResponseEntity<?> saveEmbeddingInfo(@RequestBody String embeddingRequest){
        embeddingService.saveEmbeddingInfo(embeddingRequest);
        return ResponseEntity.ok(ApiResponse.success(CommonServiceCode.SUCCESS));

    }


}
