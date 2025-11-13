package com.parsley.parsley_chat.service;

import com.parsley.parsley_chat.entity.Message;
import com.parsley.parsley_chat.model.requestEntity.MessageRequest;

import java.util.List;

public interface MessageService {

    void saveAndSendMessage(Long roomId, MessageRequest messageRequest);

    void getChatHistoryByRoomId(Long roomId);
}
