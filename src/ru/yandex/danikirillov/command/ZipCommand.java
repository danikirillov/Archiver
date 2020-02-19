package ru.yandex.danikirillov.command;

import ru.yandex.danikirillov.*;

import java.nio.file.Paths;

public abstract class ZipCommand implements Command {

    public ZipFileManager getZipFileManager() throws Exception{
        ConsoleHelper.writeMessage("Введите полный путь файла архива:");
        return new ZipFileManager(Paths.get(ConsoleHelper.readString()));
    }
}