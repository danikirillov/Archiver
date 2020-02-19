package ru.yandex.danikirillov;

import ru.yandex.danikirillov.exception.PathIsNotFoundException;
import ru.yandex.danikirillov.exception.WrongZipFileException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileManager {
    private final Path zipFile;

    public ZipFileManager(Path zipFile) {
        this.zipFile = zipFile;
    }

    public void createZip(Path source) throws Exception {
        createDirectoryIfNotExist(zipFile.getParent());

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            addFilesToZip(zipOutputStream, source);
        }
    }

    private static void createDirectoryIfNotExist(Path directory) throws IOException {
        if (Files.notExists(directory))
            Files.createDirectories(directory);
    }

    private List<Path> getFilesFromDirectory(Path directory) throws IOException {
        return new FileManager(directory).getFileList();
    }

    private void addFilesToZip(ZipOutputStream zipOutputStream, Path source) throws Exception {
        if (Files.isRegularFile(source))
            addNewZipEntry(zipOutputStream, source.getParent(), source.getFileName());
        else if (Files.isDirectory(source))
            for (Path fileName : getFilesFromDirectory(source))
                addNewZipEntry(zipOutputStream, source, fileName);
        else
            throw new PathIsNotFoundException();
    }

    public void extractAll(Path outputFolder) throws Exception {
        regularFileCheck(zipFile);
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            createDirectoryIfNotExist(outputFolder);
            extractFiles(zipInputStream, outputFolder);
        }
    }

    private void extractFiles(ZipInputStream zipInputStream, Path outputFolder) throws Exception {
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            Path fullFileName = outputFolder.resolve(zipEntry.getName());

            createDirectoryIfNotExist(fullFileName.getParent());

            try (OutputStream outputStream = Files.newOutputStream(fullFileName)) {
                copyData(zipInputStream, outputStream);
            }
        }
    }

    public void removeFile(Path path) throws Exception {
        removeFiles(Collections.singletonList(path));
    }

    public void removeFiles(List<Path> pathList) throws Exception {
        regularFileCheck(zipFile);
        Files.move(deleteFiles(pathList), zipFile, StandardCopyOption.REPLACE_EXISTING);
    }

    private void regularFileCheck(Path zipFile) throws WrongZipFileException {
        if (!Files.isRegularFile(zipFile))
            throw new WrongZipFileException();
    }

    private Path deleteFiles(List<Path> pathList) throws Exception {
        Path tempZipFile = Files.createTempFile(null, null);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
                deleteFiles(zipInputStream, zipOutputStream, pathList);
            }
        }
        return tempZipFile;
    }

    private void deleteFiles(ZipInputStream zipInputStream, ZipOutputStream zipOutputStream, List<Path> pathList) throws Exception {
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            Path archivedFile = Paths.get(zipEntry.getName());

            if (pathList.contains(archivedFile))
                ConsoleHelper.writeMessage(String.format("Файл '%s' удален из архива.", archivedFile.toString()));
            else
                moveZipEntry(zipEntry, zipInputStream, zipOutputStream);
        }

    }

    private void moveZipEntry(ZipEntry zipEntry, ZipInputStream zipInputStream, ZipOutputStream zipOutputStream) throws Exception {
        zipOutputStream.putNextEntry(new ZipEntry(zipEntry.getName()));

        copyData(zipInputStream, zipOutputStream);

        zipOutputStream.closeEntry();
        zipInputStream.closeEntry();
    }

    public void addFile(Path absolutePath) throws Exception {
        addFiles(Collections.singletonList(absolutePath));
    }

    public void addFiles(List<Path> absolutePathList) throws Exception {
        regularFileCheck(zipFile);

        Path tempZipFile = Files.createTempFile(null, null);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {
            List<Path> archiveFiles = copyArchivedFilesToOutputStream(zipOutputStream);
            addFiles(absolutePathList, archiveFiles, zipOutputStream);
        }

        Files.move(tempZipFile, zipFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * @return copied files
     */
    private List<Path> copyArchivedFilesToOutputStream(ZipOutputStream zipOutputStream) throws Exception {
        List<Path> archiveFiles = new ArrayList<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                archiveFiles.add(Paths.get(zipEntry.getName()));
                moveZipEntry(zipEntry, zipInputStream, zipOutputStream);

            }

        }
        return archiveFiles;
    }

    private void addFiles(List<Path> absolutePathList, List<Path> archiveFiles, ZipOutputStream zipOutputStream) throws Exception {
        for (Path file : absolutePathList) {
            if (Files.isRegularFile(file)) {
                if (archiveFiles.contains(file.getFileName()))
                    ConsoleHelper.writeMessage(String.format("Файл '%s' уже существует в архиве.", file.toString()));
                else {
                    addNewZipEntry(zipOutputStream, file.getParent(), file.getFileName());
                    ConsoleHelper.writeMessage(String.format("Файл '%s' добавлен в архиве.", file.toString()));
                }
            } else
                throw new PathIsNotFoundException();
        }
    }

    public List<FileProperties> getFilesList() throws Exception {
        regularFileCheck(zipFile);

        List<FileProperties> files = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

            ZipEntry zipEntry;
            while ((zipEntry= zipInputStream.getNextEntry()) != null) {
                // Поля "размер" и "сжатый размер" не известны, пока элемент не будет прочитан, поэтому читаем его в поток байт
                copyData(zipInputStream, new ByteArrayOutputStream());
                files.add(new FileProperties(zipEntry.getName(), zipEntry.getSize(), zipEntry.getCompressedSize(), zipEntry.getMethod()));
            }

        }

        return files;
    }

    private void addNewZipEntry(ZipOutputStream zipOutputStream, Path filePath, Path fileName) throws Exception {
        try (InputStream inputStream = Files.newInputStream(filePath.resolve(fileName))) {
            zipOutputStream.putNextEntry(new ZipEntry(fileName.toString()));
            copyData(inputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        }
    }

    private void copyData(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0)
            out.write(buffer, 0, len);
    }
}