package com.battleiq.service.command;

public interface Command<R> {
    R execute();
}