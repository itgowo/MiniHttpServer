package com.itgowo.httpserver;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class FileManager {
    public File tempDir;
    public File fileDir;
    public File webDir;

    public FileManager(String webDir) {
        this.webDir = new File(webDir);
        if (!this.webDir.exists()) {
            this.webDir.mkdirs();
        }
        this.tempDir = new File(this.webDir, "temp");
        if (this.tempDir.exists()) {
            deleteFile(this.tempDir);
        }
        this.tempDir.mkdirs();

        this.fileDir = new File(this.webDir, "file");
        if (!this.fileDir.exists()) {
            this.fileDir.mkdirs();
        }

    }

    /**
     * 如果fileName为空则创建临时文件，不为空则按名字创建
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public File createTempFile(String filename) throws IOException {
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        if (filename == null || filename.trim().length() == 0) {
            return File.createTempFile("itgowo_MiniServer_", ".tmp", tempDir);
        } else {
            File file = new File(tempDir, filename);
            if (file.exists()) {
                file.delete();
            }
            return file;
        }
    }

    public File createFile(String filename) throws IOException {
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        if (filename == null || filename.trim().length() == 0) {
            return File.createTempFile("itgowo_MiniServer_", ".tmp", fileDir);
        } else {
            File file = new File(fileDir, filename);
            if (file.exists()) {
                file.delete();
            }
            return file;
        }
    }

    /**
     * 获取临时文件随机读取流
     *
     * @return
     */
    public RandomAccessFile getTempRandomAccessFile() {
        try {
            File tempFile = createTempFile(null);
            return new RandomAccessFile(tempFile, "rw");
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public File getFile(String filename) {
        File file = new File(webDir, filename);
        return file;
    }

    public void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
            file.delete();
        }
    }
}
