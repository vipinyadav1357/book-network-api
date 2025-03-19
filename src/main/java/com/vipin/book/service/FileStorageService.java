package com.vipin.book.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${spring.application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@Nonnull MultipartFile sourcFile, @Nonnull Integer userId) {
        final String fileUploadSubPath = "users" + File.separator + userId;
        return uploadFile(sourcFile, fileUploadSubPath);
    }

    private @Nullable String uploadFile(@Nonnull MultipartFile sourcFile, @Nonnull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create sub folder");
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourcFile.getOriginalFilename());
        if (fileExtension.isEmpty())
            return "";
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
        System.out.println("*****************" + targetFilePath + "******************");
        Path targetPath = Path.of(targetFilePath);
        try {
            Files.write(targetPath, sourcFile.getBytes());
            log.info("file saved to the " + targetFilePath);
        } catch (Exception e) {
            log.error("file was not saved", e);
        }
        return targetFilePath;
    }

    private @NotNull String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1)
            return "";
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

}
