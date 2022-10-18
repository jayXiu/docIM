package com.junlin.vo;

import lombok.Data;

@Data
public class FriendVO {

    private Long chatRoomId;

    private Long userId;

    private String name;

    private Integer unread;

}
