package com.junlin.netty.entity;

import lombok.Data;

@Data
public class UserInfo {

    private Long userId;

    private String name;

    private String hashCode;

    private Line line;

    private String listener;


}
