package com.junlin.common.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Desc: mybatisplus 自动填充创建时间和修改时间
 */
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        if(this.getFieldValByName("createTime",metaObject) == null){
            this.setFieldValByName("createTime", date, metaObject);
        }
        if( this.getFieldValByName("updateTime",metaObject) == null){
            this.setFieldValByName("updateTime", date, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}