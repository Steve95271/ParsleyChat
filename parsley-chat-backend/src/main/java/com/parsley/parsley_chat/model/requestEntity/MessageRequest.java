package com.parsley.parsley_chat.model.requestEntity;

import lombok.Data;

@Data
public class MessageRequest {

  private String senderName;
  private String message;
  private String date;
  private long roomId;


}
