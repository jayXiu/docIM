package com.junlin.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junlin.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author fwt
 * @since 2022-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user")
public class User extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 密码
     */
    private String password;


}
