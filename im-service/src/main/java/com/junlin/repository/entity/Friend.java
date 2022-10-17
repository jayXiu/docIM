package com.junlin.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junlin.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 好友
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_friend")
public class Friend extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 好友用户ID
     */
    private Long friendUserId;

    private String friendName;
}
