package com.parsley.parsley_chat.model.mybatisEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Long id;

    private Long roomId;

    private Long senderId;

    private String senderName;

    private String content;

    private LocalDateTime timestamp;

}
