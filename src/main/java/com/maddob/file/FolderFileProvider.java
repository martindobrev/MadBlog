package com.maddob.file;

import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import io.vertx.ext.web.FileUpload;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Simple file provider to access and write files to a local directory
 *
 * Created by martindobrev on 10/03/17.
 */
public class FolderFileProvider implements FileProvider {

    private String path;


    public FolderFileProvider(String path) throws IOException {

        if (null == path) {
            throw new IllegalArgumentException("A valid file path shall be provided");
        }

        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        if (file.isFile()) {
            throw new IllegalArgumentException("The provided path shall be a folder, not a file");
        }

        this.path = path;
    }

    @Override
    public List<File> getAvailableFiles() {
        return null;
    }

    @Override
    public File getFileByName(String filename) {
        return null;
    }

    @Override
    public void saveFile(FileUpload fileUpload) {
        if (null != fileUpload) {
            String filename = fileUpload.uploadedFileName();
            File uploadedFile = new File(filename);
            if (uploadedFile.isFile()) {
                Path source = Paths.get(fileUpload.uploadedFileName());
                Path destination = Paths.get(this.path + "/" + fileUpload.fileName());
                try {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
