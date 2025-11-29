package com.parsley.parsley_chat.model.mybatisEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private Long id;

    private String name;

    private Long creator_id;

}
