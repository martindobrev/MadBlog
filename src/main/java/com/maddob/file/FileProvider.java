package com.maddob.file;

import io.vertx.ext.web.FileUpload;

import java.nio.file.Path;
import java.util.List;

/**
 * Basic interface for all file providers
 *
 * The interface is to be modified so that some convenience
 * methods like step-like file download is supported, thumbnail
 * generation etc...
 *
 * Created by martindobrev on 10/03/17.
 */
public interface FileProvider {

    /**
     * Returns a list of all avaiable files
     *
     * @return a list of file instances
     */
    List<Path> getAvailableFiles();

    /**
     * Gets a file by name
     *
     * !!! Maybe not needed !!!
     *
     * @param filename name of the file that is to be retrieved
     * @return file instance or null if not available
     */
    Path getFileByName(String filename);

    /**
     * Stores the file from a FileUpload
     *
     * @param fileUpload fileUpload isntance from the vertx context handler
     */
    void saveFile(FileUpload fileUpload);
}
