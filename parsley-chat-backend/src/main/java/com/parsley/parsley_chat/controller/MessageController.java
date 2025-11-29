package com.parsley.parsley_chat.controller;

import com.parsley.parsley_chat.model.requestEntity.MessageRequest;
import com.parsley.parsley_chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/message/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, @Payload MessageRequest messageRequest) {
        // Save and send message
        messageService.saveAndSendMessage(roomId, messageRequest);

    }

    @MessageMapping("/join/{roomId}")
    public void getMessageHistory(@DestinationVariable Long roomId) {
        // Get chat history by room id
        messageService.getChatHistoryByRoomId(roomId);

    }


}
