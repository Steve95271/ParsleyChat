package com.parsley.parsley_chat.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Snowflake;
import com.parsley.parsley_chat.entity.Message;
import com.parsley.parsley_chat.mapper.MessageMapper;
import com.parsley.parsley_chat.model.requestEntity.MessageRequest;
import com.parsley.parsley_chat.model.responseEntity.MessageResponse;
import com.parsley.parsley_chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final Snowflake snowflake;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate template;

    @Override
    public void saveAndSendMessage(Long roomId, MessageRequest messageRequest) {
        // Convert message request to message entity
        Message newMessage = new Message(
                snowflake.nextId(),
                messageRequest.getRoomId(),
                1L, // TODO we need to get the sender Id from messageRequest
                messageRequest.getSenderName(),
                messageRequest.getMessage(),
                LocalDateTime.now()
        );

        // Operate save message
        messageMapper.saveMessage(newMessage);

        // send to message queue
        // TODO change the messageRequest object to the message
        template.convertAndSend("/chatroom/" + roomId, messageRequest);

    }

    @Override
    public void getChatHistoryByRoomId(Long roomId) {
        // Query the chat history by room id
        List<Message> chatHistory = messageMapper.getChatHistoryByRoomId(roomId);

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
