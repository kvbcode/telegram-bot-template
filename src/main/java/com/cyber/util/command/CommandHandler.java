package com.cyber.util.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHandler<T> {
    private final Map<Object,Command<T>> commandMap = new ConcurrentHashMap<>();
    private Command<T> defaultCommand = (data) -> {};

    public void invoke(Object commandId, T data) throws Exception{
        Command<T> cmd = commandMap.getOrDefault(commandId, defaultCommand);
        cmd.execute(data);
    }

    public void register(Object commandId, Command<T> command){
        commandMap.put(commandId, command);
    }

    public void setDefaultCommand(Command<T> defaultCommand){
        this.defaultCommand = defaultCommand;
    }

    public void remove(Object commandId){
        commandMap.remove(commandId);
    }
}
