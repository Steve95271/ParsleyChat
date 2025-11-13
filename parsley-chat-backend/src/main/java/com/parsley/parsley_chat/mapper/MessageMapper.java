package com.parsley.parsley_chat.mapper;

import com.parsley.parsley_chat.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    void saveMessage(Message message);

    List<Message> getChatHistoryByRoomId(Long roomId);
}
