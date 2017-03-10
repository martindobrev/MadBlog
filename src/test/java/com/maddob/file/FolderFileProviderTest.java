package com.maddob.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for the FolderFileProvider
 *
 * Test the basic methods of the FolderFileProvider
 *
 * Created by martindobrev on 10/03/17.
 */
public class FolderFileProviderTest {

    /** Name of the test folder that is to be created */
    private final static String TEST_DATA_FOLDER = "TEST_DATA";

    /** file provider instance that is to be tested */
    private FolderFileProvider fileProvider;

    /**
     * Creates a new folder to use for the tests
     *
     * @throws IOException in case test folder cannot be created
     */
    @Before
    public void createTestData() throws IOException {
        File f = new File(TEST_DATA_FOLDER);
        f.delete();

        if (!f.mkdir()) {
            throw new IOException("Cannot setup test directory");
        }

        fileProvider = new FolderFileProvider(TEST_DATA_FOLDER);
    }

    @After
    public void clearTestData() throws IOException {
        Files.delete(Paths.get(TEST_DATA_FOLDER));
    }

    @Test
    public void testGetFiles() {
        List<File> files = fileProvider.getAvailableFiles();
        assertNotNull("Rusult shall not be null", files);
        assertEquals(0, files.size());
    }
}
