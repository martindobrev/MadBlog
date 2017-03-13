package com.maddob.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for the FolderFileProvider
 *
 * Test the basic methods of the FolderFileProvider
 *
 * Test resources are located in the test/resources/test_uploads folder.
 * The contents of that folder will be copied to a folder that will be
 * created before each test case and deleted after the test case completed.
 * This folder will be named by the variable TEST_DATA_FOLDER.
 * If a relative path is used (as in this case), the folder will be created
 * in the root folder of the current project.
 *
 * TODO: add additional description after some test cases are added
 *
 * Created by martindobrev on 10/03/17.
 */
public class FolderFileProviderTest {

    /** Name of the test folder that is to be created */
    private final static String TEST_DATA_FOLDER = "TEST_DATA";

    /** file provider instance that is to be tested */
    private FolderFileProvider fileProvider;

    /**
     * Reference of the test data folder that is to be deleted after each test
     */
    private Path testDataFolder;

    /**
     * Creates a new folder to use for the tests
     *
     * @throws IOException in case test folder cannot be created
     */
    @Before
    public void createTestData() throws IOException {
        testDataFolder = Paths.get(TEST_DATA_FOLDER);
        Files.deleteIfExists(testDataFolder);
        Files.createDirectory(testDataFolder);
        String testUploadsPath = this.getClass().getResource("/test_uploads").getFile();
        Files.list(Paths.get(testUploadsPath)).forEach(file -> {
            try {
                Files.copy(file, Paths.get(TEST_DATA_FOLDER, file.getName(file.getNameCount() - 1).toString()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Logger.getAnonymousLogger().info("Path is: " + testUploadsPath);
        fileProvider = new FolderFileProvider(TEST_DATA_FOLDER);
    }


    @After
    public void clearTestData() throws IOException {
        if (null != testDataFolder) {
            Files.list(testDataFolder).forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Files.delete(testDataFolder);
        }
    }

    @Test
    public void testGetFiles() {
        List<File> files = fileProvider.getAvailableFiles();
        assertNotNull("Rusult shall not be null", files);
        assertEquals(0, files.size());
    }
}
