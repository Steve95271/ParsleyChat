package com.parsley.parsley_chat.service.impl;

import com.parsley.parsley_chat.model.jpaEntity.Message;
import com.parsley.parsley_chat.model.requestEntity.MessageRequest;
import com.parsley.parsley_chat.model.responseEntity.MessageResponse;
import com.parsley.parsley_chat.repository.MessageRepository;
import com.parsley.parsley_chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate template;

    @Override
    public void saveAndSendMessage(Long roomId, MessageRequest messageRequest) {
        // Convert message request to message entity
        Message newMessage = Message
                .builder()
                .roomId(messageRequest.getRoomId())
                .senderId(1L) // TODO we need to get the sender Id from messageRequest
                .senderName(messageRequest.getSenderName())
                .content(messageRequest.getMessage())
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        // Operate save message
        messageRepository.save(newMessage);

        // send to message queue
        // TODO change the messageRequest object to the message
        template.convertAndSend("/chatroom/" + roomId, messageRequest);

    }

    @Override
    public void getChatHistoryByRoomId(Long roomId) {
        // Query the chat history by room id
        List<Message> chatHistory = messageRepository.findByRoomIdOrderByTimestampAsc(roomId);

        // Convert to response object
        List<MessageResponse> chatHistoryResponse = new ArrayList<>();
        for (Message message : chatHistory) {
            chatHistoryResponse.add(
                    MessageResponse
                            .builder()
                            .senderName(message.getSenderName())
                            .message(message.getContent())
                            .date(message.getTimestamp().toString())
                            .roomId(message.getRoomId())
                            .build()
            );
        }

        // Send to message queue
        template.convertAndSend("/chatroom/" + roomId + "/history", chatHistoryResponse);
    }
}
