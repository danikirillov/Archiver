package ru.yandex.danikirillov.command;

import ru.yandex.danikirillov.ConsoleHelper;
import ru.yandex.danikirillov.ZipFileManager;
import ru.yandex.danikirillov.exception.PathIsNotFoundException;

import java.nio.file.Paths;

public class ZipExtractCommand extends ZipCommand {

    @Override
    public void execute() throws Exception {
        try {
            ConsoleHelper.writeMessage("Распаковка архива.");
            ZipFileManager zipFileManager = getZipFileManager();

            ConsoleHelper.writeMessage("Введите путь для распаковки:");
            zipFileManager.extractAll(Paths.get(ConsoleHelper.readString()));

            ConsoleHelper.writeMessage("Архив был распакован.");
        } catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Неверный путь для распаковки.");
        }
    }
}
