package com.parsley.parsley_chat.service;

import com.parsley.parsley_chat.model.requestEntity.MessageRequest;

public interface MessageService {

    void saveAndSendMessage(Long roomId, MessageRequest messageRequest);

    void getChatHistoryByRoomId(Long roomId);
}
