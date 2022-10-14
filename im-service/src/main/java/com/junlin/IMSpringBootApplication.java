package com.junlin;

import com.junlin.netty.IMServer;
import com.junlin.netty.IMServerHandler;
import com.junlin.utils.RedisUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.junlin.repository.mapper")
@SpringBootApplication
public class IMSpringBootApplication implements CommandLineRunner {

    @Autowired
    private IMServer imServer;

    public static void main(String[] args) {
        SpringApplication.run(IMSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        imServer.run();;
    }
}
