package controlm.qrcodegenerator.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    public Resource loadAsResource(String path) throws MalformedURLException {
        Path file = Paths.get(path);

        if (!Files.exists(file)) {
            throw new RuntimeException("Файл не найден: " + path);
        }

        return new UrlResource(file.toUri());
    }
}
