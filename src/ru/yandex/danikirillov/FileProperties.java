package ru.yandex.danikirillov;

public class FileProperties {
    private final String name;
    private final long size;
    private final long compressedSize;
    private final int compressionMethod;

    public FileProperties(String name, long size, long compressedSize, int compressionMethod) {
        this.name = name;
        this.size = size;
        this.compressedSize = compressedSize;
        this.compressionMethod = compressionMethod;
    }


    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public long getCompressedSize() {
        return compressedSize;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    public long getCompressionRatio() {
        return 100 - ((compressedSize * 100) / size);
    }

    @Override
    public String toString() {
        StringBuilder fileProps = new StringBuilder();
        fileProps.append(name);
        if (size > 0)
            fileProps.append("\t")
            .append(size)
            .append(" b (")
            .append(compressedSize)
            .append(" b) сжатие: ")
            .append(getCompressionRatio())
            .append("%");

        return fileProps.toString();
    }
}
