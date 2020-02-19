package ru.yandex.danikirillov.command;

import ru.yandex.danikirillov.ConsoleHelper;
import ru.yandex.danikirillov.ZipFileManager;
import ru.yandex.danikirillov.exception.PathIsNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipAddCommand extends ZipCommand {

    @Override
    public void execute() throws Exception {
        try {
            ConsoleHelper.writeMessage("Добавление нового файла в архив.");

            ZipFileManager zipFileManager = getZipFileManager();

            ConsoleHelper.writeMessage("Введите полное имя файла для добавления:");
            zipFileManager.addFile(Paths.get(ConsoleHelper.readString()));

            ConsoleHelper.writeMessage("Добавление в архив завершено.");

        } catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Файл не был найден.");
        }
    }
}
