package com.example.cookiebites.sqa.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class LoginPageTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() throws Exception {
        String remoteUrl = System.getenv("SELENIUM_REMOTE_URL");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");

        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            driver = new RemoteWebDriver(new URL(remoteUrl), options);
        } else {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);
        }
    }

    @Test
    void testLoginPageLoads() {
        String targetUrl = System.getenv().getOrDefault("TARGET_URL", "http://localhost:8080/LogIn.html");
        driver.get(targetUrl);
        String title = driver.getTitle();
        assertTrue(title.contains("CookieBites"), "El titulo debe contener 'CookieBites'");
    }

    @Test
    void testLoginUsernameFieldExists() {
        String targetUrl = System.getenv().getOrDefault("TARGET_URL", "http://localhost:8080/LogIn.html");
        driver.get(targetUrl);
        driver.findElement(By.id("usuario"));
        driver.findElement(By.id("contrasena"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
