package ru.yandex.danikirillov.command;

import ru.yandex.danikirillov.*;

public class ZipContentCommand extends ZipCommand {

    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Просмотр содержимого архива.");
        ZipFileManager zipFileManager = getZipFileManager();

        ConsoleHelper.writeMessage("Содержимое архива:");
        zipFileManager.getFilesList().forEach(file -> ConsoleHelper.writeMessage(file.toString()));

        ConsoleHelper.writeMessage("Содержимое архива прочитано.");
    }
}
