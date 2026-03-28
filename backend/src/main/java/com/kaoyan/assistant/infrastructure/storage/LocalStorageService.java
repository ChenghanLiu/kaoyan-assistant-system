package com.kaoyan.assistant.infrastructure.storage;

import com.kaoyan.assistant.common.config.AppProperties;
import com.kaoyan.assistant.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageService.class);

    private final AppProperties appProperties;
    private Path rootPath;

    public LocalStorageService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        try {
            this.rootPath = resolveStorageRoot(appProperties.getStorage().getLocation());
            Files.createDirectories(rootPath);
        } catch (IOException exception) {
            log.error("storage init failed: location={}", appProperties.getStorage().getLocation(), exception);
            throw new IllegalStateException("storage init failed", exception);
        }
    }

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.invalidInput("file is required");
        }
        if (file.getSize() > appProperties.getStorage().getMaxFileSizeMb() * 1024L * 1024L) {
            throw BusinessException.invalidInput("file size exceeds limit");
        }

        String originalName = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "file"));
        if (!StringUtils.hasText(originalName) || originalName.contains("..")) {
            throw BusinessException.invalidInput("file name is invalid");
        }
        String extension = "";
        int index = originalName.lastIndexOf('.');
        if (index > -1) {
            extension = originalName.substring(index);
        }

        String storedFileName = UUID.randomUUID() + extension;
        try {
            Files.copy(file.getInputStream(), rootPath.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException exception) {
            log.error("file save failed: originalName={}, storedName={}", originalName, storedFileName, exception);
            throw new BusinessException("file save failed");
        }
    }

    public Resource load(String storedFileName) {
        try {
            Path filePath = rootPath.resolve(storedFileName).normalize();
            if (!filePath.startsWith(rootPath)) {
                throw BusinessException.invalidInput("file path is invalid");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw BusinessException.notFound("file not found");
            }
            return resource;
        } catch (IOException exception) {
            log.error("file load failed: storedFileName={}", storedFileName, exception);
            throw new BusinessException("file load failed");
        }
    }

    public void delete(String storedFileName) {
        if (!StringUtils.hasText(storedFileName)) {
            return;
        }
        try {
            Path filePath = rootPath.resolve(storedFileName).normalize();
            if (!filePath.startsWith(rootPath)) {
                throw BusinessException.invalidInput("file path is invalid");
            }
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            log.error("file delete failed: storedFileName={}", storedFileName, exception);
            throw new BusinessException("file delete failed");
        }
    }

    private Path resolveStorageRoot(String location) {
        Path configuredPath = Paths.get(location);
        if (configuredPath.isAbsolute()) {
            return configuredPath.normalize();
        }

        Path currentPath = Paths.get("").toAbsolutePath().normalize();
        Path backendPath = currentPath.resolve("backend");
        Path basePath = Files.exists(backendPath.resolve("pom.xml")) ? backendPath : currentPath;
        return basePath.resolve(configuredPath).normalize().toAbsolutePath();
    }
}
