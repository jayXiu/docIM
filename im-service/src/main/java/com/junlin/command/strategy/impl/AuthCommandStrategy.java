package com.junlin.command.strategy.impl;

import com.junlin.command.strategy.CommandStrategy;
import org.springframework.stereotype.Service;

@Service
public class AuthCommandStrategy implements CommandStrategy {
    @Override
    public void executeCommand(String message, String command) {

    }

    @Override
    public String getCommand() {
        return "auth";
    }
}
