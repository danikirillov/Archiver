package ru.yandex.danikirillov.command;

import ru.yandex.danikirillov.ConsoleHelper;

public class ExitCommand implements Command {
    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("До встречи!");
    }
}
