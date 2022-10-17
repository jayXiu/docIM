package com.junlin.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junlin.common.mybatis.base.BaseEntity;
import com.junlin.repository.enums.FriendshipStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 好友关系
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_friendship")
public class Friendship extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 发起用户添加关系用户ID
     */
    private Long aUserId;

    private String name;

    /**
     * 受邀用户ID
     */
    private Long bUserId;

    private String friendName;

    /**
     * ADD添加 AGREE同意 DISAGREE不同意 REMOVE移除
     */
    private FriendshipStatus status;


}
