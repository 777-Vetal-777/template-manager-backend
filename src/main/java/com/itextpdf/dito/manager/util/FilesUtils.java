package com.itextpdf.dito.manager.util;

import com.itextpdf.dito.manager.exception.resource.UnreadableResourceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.web.multipart.MultipartFile;

import static java.nio.file.Files.createTempDirectory;

public final class FilesUtils {
    public static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    public static final String TEMPLATES_FOLDER = "templates";
    public static final String DATA_FOLDER = "data";


    public static byte[] getFileBytes(final MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new UnreadableResourceException(file.getOriginalFilename());
        }
        return data;
    }

    public static Map<String, Path> createTemplateDirectoryForPreview(final String templateName) throws IOException {
        final Map<String, Path> folders = new HashMap<>();

        final Path rootFolder = createTempDirectory(TEMP_DIRECTORY.toPath() ,templateName);
        folders.put(templateName, rootFolder);
        createFolderInSubDirectory(folders, rootFolder, TEMPLATES_FOLDER);
        createFolderInSubDirectory(folders, rootFolder, DATA_FOLDER);
        return folders;
    }

    private static void createFolderInSubDirectory(final Map<String, Path> folders, final Path rootFolder, final String newFolder) {
        final File dataFolder = new File(rootFolder.toFile().getAbsolutePath(), newFolder);
        dataFolder.mkdir();
        folders.put(newFolder, dataFolder.toPath());
    }

    public static File zipFolder(final Path sourceFolderPath, final Path zipPath) throws IOException {
        final File zipFile = zipPath.toFile();
        final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<>() {
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
        return zipFile;
    }

    private FilesUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}