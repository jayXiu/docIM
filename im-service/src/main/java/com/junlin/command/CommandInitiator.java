package com.junlin.command;

import com.junlin.command.strategy.CommandStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CommandInitiator implements InitializingBean {

    @Resource
    private List<CommandStrategy> strategyList;
    private static Map<String, CommandStrategy> strategys = new HashMap<>();

    //命令集合
    private static List<String> commands = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        strategys = strategyList.stream().collect(Collectors.toMap(CommandStrategy::getCommand, Function.identity()));
        commands = strategyList.stream().map(CommandStrategy::getCommand).collect(Collectors.toList());
    }

    /**
     * 选择策略
     *
     * @return
     */
    public void execute(String message, String command) {

        if (command != null) {
            CommandStrategy strategy = strategys.get(command);
            if (strategy != null) {
                strategy.executeCommand(message, command);
                return;
            }
        }


    }
}
