package com.maddob.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertEquals;

/**
 * HomePage integration test
 *
 * Tests that the page has correctly loaded
 *
 * Created by martindobrev on 12/01/17.
 */
public class HomePageTest {

    private final static String SERVER_HOST = System.getProperty("server.host", "localhost");
    private final static String SERVER_PORT = System.getProperty("server.port", "8080");
    private final static String BASE_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT;

    private WebDriver webDriver;

    @Before
    public void openBrowser() {
        webDriver = new FirefoxDriver();
        webDriver.get(BASE_URL);
    }

    @After
    public void closeBrowser() {
        webDriver.close();
        webDriver.quit();
    }

    @Test
    public void testHomePageParameters() {
        String title = webDriver.getTitle();
        assertEquals("Title shall be 'Hello mad guys!', but was '" + title + "'",
                "Hello mad guys", title);
    }
}
