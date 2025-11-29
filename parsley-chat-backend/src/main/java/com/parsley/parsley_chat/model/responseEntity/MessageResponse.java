package com.parsley.parsley_chat.model.responseEntity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {

  private String senderName;
  private String message;
  private String date;
  private long roomId;

}
