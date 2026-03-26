package com.kaoyan.assistant.infrastructure.storage;

import com.kaoyan.assistant.common.config.AppProperties;
import com.kaoyan.assistant.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService {

    private final AppProperties appProperties;
    private Path rootPath;

    public LocalStorageService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() throws IOException {
        this.rootPath = Paths.get(appProperties.getStorage().getLocation()).toAbsolutePath().normalize();
        Files.createDirectories(rootPath);
    }

    public String save(MultipartFile file) {
        String extension = "";
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        int index = originalName.lastIndexOf('.');
        if (index > -1) {
            extension = originalName.substring(index);
        }
        String fileName = UUID.randomUUID() + extension;
        try {
            Files.copy(file.getInputStream(), rootPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new BusinessException("File save failed");
        }
    }

    public Resource load(String fileName) {
        try {
            Path path = rootPath.resolve(fileName).normalize();
            return new UrlResource(path.toUri());
        } catch (IOException ex) {
            throw new BusinessException("File load failed");
        }
    }
}
