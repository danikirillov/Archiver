package ru.yandex.danikirillov;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final Path rootPath;
    private final List<Path> fileList;

    public FileManager(Path rootPath) throws IOException {
        this.rootPath = rootPath;
        this.fileList = new ArrayList<>();
        collectFileList(rootPath);
    }

    private void collectFileList(Path path) throws IOException {
        if (Files.isRegularFile(path))
            addRegularFile(path);
        else if (Files.isDirectory(path))
            addDirectory(path);
    }

    private void addRegularFile(Path path) {
        Path relativePath = rootPath.relativize(path);
        fileList.add(relativePath);
    }

    private void addDirectory(Path path) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path file : directoryStream)
                collectFileList(file);
        }
    }

    public List<Path> getFileList() {
        return fileList;
    }
}
