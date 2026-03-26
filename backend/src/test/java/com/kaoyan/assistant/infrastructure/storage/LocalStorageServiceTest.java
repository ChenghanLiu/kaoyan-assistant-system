package com.kaoyan.assistant.infrastructure.storage;

import com.kaoyan.assistant.common.config.AppProperties;
import com.kaoyan.assistant.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadFromSameStorageRoot() throws IOException {
        LocalStorageService storageService = createStorageService(tempDir.toString(), 5);
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain", "study notes".getBytes()
        );

        String storedFileName = storageService.save(file);
        Resource resource = storageService.load(storedFileName);

        assertEquals(tempDir.resolve(storedFileName), Path.of(resource.getFile().getAbsolutePath()));
        assertArrayEquals("study notes".getBytes(), Files.readAllBytes(Path.of(resource.getFile().getAbsolutePath())));
    }

    @Test
    void shouldRejectFileWhenSizeExceedsConfiguredLimit() {
        LocalStorageService storageService = createStorageService(tempDir.toString(), 1);
        byte[] content = new byte[1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile("file", "big.bin", "application/octet-stream", content);

        BusinessException exception = assertThrows(BusinessException.class, () -> storageService.save(file));

        assertEquals(4000, exception.getCode());
        assertEquals("file size exceeds limit", exception.getMessage());
    }

    private LocalStorageService createStorageService(String location, long maxFileSizeMb) {
        AppProperties properties = new AppProperties();
        properties.getStorage().setLocation(location);
        properties.getStorage().setMaxFileSizeMb(maxFileSizeMb);

        LocalStorageService storageService = new LocalStorageService(properties);
        storageService.init();
        return storageService;
    }
}
