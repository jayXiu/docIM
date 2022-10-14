package com.junlin.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.junlin.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 好友关系日志
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_friendship_log")
public class FriendshipLog extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 好友关系ID
     */
    private Long friendshipId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * ADD添加 AGREE同意 DISAGREE不同意 REMOVE移除
     */
    private String status;

    /**
     * 日志时间
     */
    private Date logTime;


}
