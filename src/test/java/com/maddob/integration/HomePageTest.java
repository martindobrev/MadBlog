package com.maddob.integration;

import com.maddob.data.Article;
import com.maddob.data.InMemoryArticleProvider;
import com.maddob.server.MadWebServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * HomePage integration test
 *
 * Tests that the page has correctly loaded
 *
 * Created by martindobrev on 12/01/17.
 */
@RunWith(VertxUnitRunner.class)
public class HomePageTest {

    private final static Integer TEST_SERVER_PORT = 25303;
    private final static String BASE_URL = "http://localhost:" + TEST_SERVER_PORT;
    private final static String TEST_DB_NAME = "MadArticles_TEST";

    private WebDriver webDriver;
    private InMemoryArticleProvider testArticleProvider;
    private Vertx vertx;

    /**
     * Prepares the test environment
     *
     * Before each test a new test vertx instance is created. Test data is added
     * before deploying the MadWebServerVerticle
     *
     * @param context
     */
    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();

        // Prepare the database
        LocalMap<String, Article> testDb = vertx.sharedData().getLocalMap(TEST_DB_NAME);
        testArticleProvider = new InMemoryArticleProvider(testDb);
        addTestArticles();

        // Prepare the deployment configs
        DeploymentOptions testDeploymentOptions = new DeploymentOptions();
        testDeploymentOptions.setConfig(new JsonObject());
        testDeploymentOptions.getConfig().put(MadWebServerVerticle.CONFIG_DB, TEST_DB_NAME);
        testDeploymentOptions.getConfig().put(MadWebServerVerticle.CONFIG_HTTP_PORT, TEST_SERVER_PORT);

        // Deploy the verticle to be tested
        vertx.deployVerticle(MadWebServerVerticle.class.getName(), testDeploymentOptions, context.asyncAssertSuccess());
        webDriver = new FirefoxDriver();
        webDriver.get(BASE_URL);
    }

    @After
    public void tearDown(TestContext context) {
        webDriver.close();
        webDriver.quit();
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testThatCssLoadedCorrectly(TestContext context) {
        String title = webDriver.getTitle();
        assertEquals("Title shall be '" + "MADDOB | Home" + "', but was '" + title, "MADDOB | Home", title);
        WebElement mainMenu = webDriver.findElement(By.id("menu-main"));
        assertTrue("Main menu shall be in the middle of the page, otherwise CSS was not correctly loaded",
                mainMenu.getRect().getX() > 200);
    }

    @Test
    public void testThatLatestFourArticlesAreShownOnThePage() {
        IntStream.range(1, 5).forEach(intValue -> {
            String title = "Test Article " + intValue;
            Article article = testArticleProvider.getArticleByTitle("Test Article " + intValue);
            assertNotNull("The test article with a title '" + title + "' was not found in the test database!");
            WebElement element = webDriver.findElement(By.id(article.getId().toString()));
                assertNotNull("Article '" + article.getTitle() + "', with uuid: " + article.getId().toString()
                        + " was not found on the main page, but should be there!", element);
        });
    }

    @Test
    public void testThatLatestFifthArticleIsNotShownOnThePage() {
        Article fifthArticle = testArticleProvider.getLatestArticles(5).get(4);
        try {
            WebElement element = webDriver.findElement(By.id(fifthArticle.getId().toString()));
            fail("The fifth article shall not be displayed on the home page");
        } catch (NoSuchElementException exception) {
            // do nothing, the test will succeed automatically
        }
    }

    /***************************************** HELPER METHODS *********************************/

    /**
     * Fills the test database with some dummy articles
     */
    private void addTestArticles() {
        IntStream.range(1, 10).forEach(intValue -> {
            Article article = new Article();
            article.setId(UUID.randomUUID());
            article.setCreated(LocalDateTime.now().minusDays(intValue));
            article.setTitle("Test Article " + intValue);
            article.setPublished(true);
            article.setContent("<p id=\"" + article.getId().toString()
                    + "\">Just a test content of article number " + intValue + "</p>");
            testArticleProvider.addArticle(article);
        });
    }
}