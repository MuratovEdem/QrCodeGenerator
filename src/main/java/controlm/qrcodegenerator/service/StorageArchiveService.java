package controlm.qrcodegenerator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class StorageArchiveService {

    @Value("${app.storage.path:/app/storage}")
    private Path storagePath;

    public void archiveStorage(OutputStream outputStream) throws IOException {
        if (!Files.exists(storagePath)) {
            throw new IOException("Storage directory does not exist: " + storagePath);
        }

        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            // zos.setLevel(Deflater.BEST_COMPRESSION);

            Files.walkFileTree(storagePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String entryName = storagePath.relativize(file).toString();
                    entryName = entryName.replace("\\", "/");
                    zos.putNextEntry(new ZipEntry(entryName));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String entryName = storagePath.relativize(dir).toString();
                    if (!entryName.isEmpty()) {
                        entryName = entryName.replace("\\", "/") + "/";
                        zos.putNextEntry(new ZipEntry(entryName));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            zos.finish();
        }
    }
}
