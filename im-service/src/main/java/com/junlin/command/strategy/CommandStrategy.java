package com.junlin.command.strategy;

public interface CommandStrategy {


    void executeCommand(String message, String command);

    /**
     * 策略key
     * @return
     */
    String getCommand();

}
