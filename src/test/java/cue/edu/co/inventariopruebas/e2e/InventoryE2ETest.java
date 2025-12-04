package cue.edu.co.inventariopruebas.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryE2ETest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("E2E: Should load the main page")
    void shouldLoadMainPage() {
        driver.get(baseUrl);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        WebElement header = driver.findElement(By.tagName("h1"));
        assertTrue(header.getText().contains("Sistema de Gestión de Inventario"));
    }

    @Test
    @Order(2)
    @DisplayName("E2E: Should create a category")
    void shouldCreateCategory() {
        driver.get(baseUrl);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("category-name")));

        // Fill category form
        WebElement categoryNameInput = driver.findElement(By.id("category-name"));
        categoryNameInput.sendKeys("E2E Test Category");

        // Submit form
        WebElement submitButton = driver.findElement(By.cssSelector("#category-form button[type='submit']"));
        submitButton.click();

        // Wait for success notification
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("notification")));

        // Verify category appears in list
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("categories-list")));
        WebElement categoriesList = driver.findElement(By.id("categories-list"));
        assertTrue(categoriesList.getText().contains("E2E Test Category"));
    }

    @Test
    @Order(3)
    @DisplayName("E2E: Should create a product and verify it appears in the list")
    void shouldCreateProductAndVerifyInList() throws InterruptedException {
        driver.get(baseUrl);

        // First create a category
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("category-name")));
        WebElement categoryNameInput = driver.findElement(By.id("category-name"));
        categoryNameInput.sendKeys("Electronics E2E");

        WebElement categorySubmit = driver.findElement(By.cssSelector("#category-form button[type='submit']"));
        categorySubmit.click();

        // Wait for category to be created
        Thread.sleep(1000);

        // Switch to products tab
        WebElement productsTab = driver.findElement(By.cssSelector("[data-tab='products']"));
        productsTab.click();

        // Wait for products tab to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("product-name")));

        // Wait for category select to be populated
        Thread.sleep(1000);

        // Fill product form
        WebElement productName = driver.findElement(By.id("product-name"));
        productName.sendKeys("E2E Test Laptop");

        WebElement productDescription = driver.findElement(By.id("product-description"));
        productDescription.sendKeys("High performance laptop for E2E testing");

        WebElement productPrice = driver.findElement(By.id("product-price"));
        productPrice.sendKeys("1499.99");

        WebElement productStock = driver.findElement(By.id("product-stock"));
        productStock.sendKeys("15");

        // Select category
        WebElement categorySelect = driver.findElement(By.id("product-category"));
        categorySelect.click();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#product-category option"), 1));
        WebElement categoryOption = driver.findElement(By.cssSelector("#product-category option:nth-child(2)"));
        categoryOption.click();

        // Submit product form
        WebElement productSubmit = driver.findElement(By.cssSelector("#product-form button[type='submit']"));
        productSubmit.click();

        // Wait for success notification
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("notification")));

        // Verify product appears in list
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("products-list")));
        Thread.sleep(1000); // Give time for the product to be added to the list

        WebElement productsList = driver.findElement(By.id("products-list"));
        String productsListText = productsList.getText();

        assertTrue(productsListText.contains("E2E Test Laptop"), "Product name should appear in the list");
        assertTrue(productsListText.contains("1499.99"), "Product price should appear in the list");
    }

    @Test
    @Order(4)
    @DisplayName("E2E: Complete flow - Create category, create product, view product")
    void shouldCompleteFullFlow() throws InterruptedException {
        driver.get(baseUrl);

        // Step 1: Create Category
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("category-name")));
        WebElement categoryInput = driver.findElement(By.id("category-name"));
        categoryInput.sendKeys("Complete Flow Category");

        WebElement categoryButton = driver.findElement(By.cssSelector("#category-form button[type='submit']"));
        categoryButton.click();

        // Wait for category creation
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("categories-list"), "Complete Flow Category"));

        // Step 2: Switch to Products Tab
        WebElement productsTab = driver.findElement(By.cssSelector("[data-tab='products']"));
        productsTab.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("product-name")));
        Thread.sleep(1000); // Wait for category dropdown to load

        // Step 3: Create Product
        driver.findElement(By.id("product-name")).sendKeys("Complete Flow Product");
        driver.findElement(By.id("product-description")).sendKeys("Product for complete E2E flow test");
        driver.findElement(By.id("product-price")).sendKeys("299.99");
        driver.findElement(By.id("product-stock")).sendKeys("25");

        // Select the category we just created
        WebElement categorySelect = driver.findElement(By.id("product-category"));
        categorySelect.click();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#product-category option"), 1));
        driver.findElement(By.cssSelector("#product-category option:nth-child(2)")).click();

        driver.findElement(By.cssSelector("#product-form button[type='submit']")).click();

        // Step 4: Verify Product in List
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("notification")));
        Thread.sleep(1500);

        WebElement productsList = driver.findElement(By.id("products-list"));
        String listText = productsList.getText();

        // Verify all product details are visible
        assertTrue(listText.contains("Complete Flow Product"), "Product name should be visible");
        assertTrue(listText.contains("299.99"), "Product price should be visible");
        assertTrue(listText.contains("Complete Flow Category"), "Category name should be visible");
        assertTrue(listText.contains("Stock"), "Stock information should be visible");
    }

    @Test
    @Order(5)
    @DisplayName("E2E: Should be able to switch between tabs")
    void shouldSwitchBetweenTabs() {
        driver.get(baseUrl);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-tab='products']")));

        // Switch to products tab
        WebElement productsTab = driver.findElement(By.cssSelector("[data-tab='products']"));
        productsTab.click();

        // Verify products tab is active
        wait.until(ExpectedConditions.attributeContains(productsTab, "class", "active"));
        assertTrue(productsTab.getAttribute("class").contains("active"));

        // Switch back to categories tab
        WebElement categoriesTab = driver.findElement(By.cssSelector("[data-tab='categories']"));
        categoriesTab.click();

        // Verify categories tab is active
        wait.until(ExpectedConditions.attributeContains(categoriesTab, "class", "active"));
        assertTrue(categoriesTab.getAttribute("class").contains("active"));
    }
}
