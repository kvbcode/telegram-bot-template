package com.cyber.util.command;

@FunctionalInterface
public interface Command<T>{
    void execute(T data) throws Exception;
}
