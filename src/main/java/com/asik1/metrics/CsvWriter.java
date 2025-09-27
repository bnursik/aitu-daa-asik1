package com.asik1.metrics;

import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Appends Metrics rows to a CSV file. Writes header once when the file is empty/new.
 */
public final class CsvWriter {
    private final Path path;

    public CsvWriter(Path path) {
        this.path = path;
    }

    /** Append one metrics row; ensure parent directories and header. */
    public void append(Metrics row) {
        try {
            ensureParent();
            boolean writeHeader = Files.notExists(path) || Files.size(path) == 0;
            try (BufferedWriter w = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                if (writeHeader) {
                    w.write(Metrics.csvHeader());
                    w.newLine();
                }
                w.write(row.toCsvRow());
                w.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV to " + path + ": " + e.getMessage(), e);
        }
    }

    private void ensureParent() throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}
