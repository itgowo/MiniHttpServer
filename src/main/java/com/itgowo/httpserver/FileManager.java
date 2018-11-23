package com.itgowo.httpserver;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class FileManager {
    public long limitTime = 1000 * 60 * 60 * 24 * 7;
    public long limitSize = 1024 * 1024 * 500;
    public File tempDir;
    public File fileDir;
    public File webDir;

    public void setWebDir(String webDir) {
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

    public FileManager setLimitSize(long limitSize) {
        this.limitSize = limitSize;
        return this;
    }

    public FileManager setLimitTime(long limitTime) {
        this.limitTime = limitTime;
        return this;
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
        File dir = new File(fileDir, UUID.randomUUID().toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (filename == null || filename.trim().length() == 0) {
            return File.createTempFile("itgowo_MiniServer_", ".tmp", dir);
        } else {
            File file = new File(dir, filename);
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

    /**
     * 当使用量超过限制后对超期文件进行清理,循环检查大小，每次删除最远6小时文件，直到小于指定大小
     */
    public void cleanOldFile() {
        if (this.fileDir.exists()) {
            List<File> fileList = getFilesAllAndRemoveEmptyDir(this.fileDir);
            long size = getFileSize(fileList);
            if (size > limitSize) {
                long time = System.currentTimeMillis() - limitTime;
                File temp;
                Iterator<File> iterator = fileList.iterator();
                while (iterator.hasNext()) {
                    temp = iterator.next();
                    if (temp.lastModified() < time) {
                        temp.delete();
                        if (temp.getParentFile().listFiles().length == 0) {
                            temp.getParentFile().delete();
                        }
                        iterator.remove();
                    }
                }
            }

        }

    }

    public long getFileSize(File file) {
        long size = 0;
        if (null == file) {
            return 0;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                size += getFileSize(files[i]);
            }
        } else if (file.isFile()) {
            size += file.length();
        }
        return size;
    }

    public long getFileSize(List<File> files) {
        long size = 0;
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).exists()) {
                size += files.get(i).length();
            }
        }
        return size;
    }

    public List<File> getFilesAllAndRemoveEmptyDir(File file) {
        List<File> fileList = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                file.delete();
            } else {
                for (int i = 0; i < files.length; i++) {
                    fileList.addAll(getFilesAllAndRemoveEmptyDir(files[i]));
                }
            }
        } else if (file.isFile()) {
            fileList.add(file);
        }
        return fileList;
    }
}
