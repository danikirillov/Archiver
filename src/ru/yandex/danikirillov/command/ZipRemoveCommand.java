package ru.yandex.danikirillov.command;

import ru.yandex.danikirillov.ConsoleHelper;
import ru.yandex.danikirillov.ZipFileManager;

import java.nio.file.Paths;

public class ZipRemoveCommand extends ZipCommand {

    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Удаление файла из архива.");
        ZipFileManager zipFileManager = getZipFileManager();

        ConsoleHelper.writeMessage("Введите полный путь файла в архиве:");
        zipFileManager.removeFile(Paths.get(ConsoleHelper.readString()));

        ConsoleHelper.writeMessage("Удаление из архива завершено.");
    }
}