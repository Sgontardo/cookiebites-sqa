package com.example.cookiebites.sqa.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.ArrayList;
import org.openqa.selenium.support.ui.Select;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CookieBitesFlowTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    private void pausa() {
        try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Test
    @Order(1)
    void testRegistro() {
        driver.get("http://localhost:8080/CrearPerfil.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("nombre"))).sendKeys("Selenium_Test");
        driver.findElement(By.name("usuario")).sendKeys("Selenium_Test");
        driver.findElement(By.name("correo")).sendKeys("Selenium_Test@gmail.com");
        driver.findElement(By.id("contrasena")).sendKeys("Selenium_Test1234");
        driver.findElement(By.id("confirmar")).sendKeys("Selenium_Test1234");
        pausa();
        driver.findElement(By.id("formita")).click();
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    @Test
    @Order(2)
    void testLoginEnNuevaPestana() {
        ((JavascriptExecutor) driver).executeScript("window.open('about:blank', '_blank');");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        driver.get("http://localhost:8080/LogIn.html");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario"))).sendKeys("Selenium_Test");
        driver.findElement(By.id("contrasena")).sendKeys("Selenium_Test1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.className("usuario"))).click();
        pausa();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("editar-btn"))).click();
        
        WebElement campoNombre = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-nombreusuario")));
        campoNombre.clear();
        campoNombre.sendKeys("Selenium_TestEditado");
        WebElement campoPass = driver.findElement(By.id("edit-passworduser"));
        campoPass.clear();
        campoPass.sendKeys("Selenium_TestEditado123");
        pausa();
        driver.findElement(By.id("guardar-btn")).click();
        driver.findElement(By.id("boton-volver")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto")));
        driver.findElements(By.className("producto")).get(0).click();
        WebElement inputCantidad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cantidadGalletas")));
        inputCantidad.clear();
        inputCantidad.sendKeys("5");
        driver.findElement(By.id("boton-agrergar")).click();
        pausa();

        WebElement btnEliminar = driver.findElement(By.className("eliminar-btn"));
        btnEliminar.click();
        wait.until(ExpectedConditions.alertIsPresent()).accept(); 
        wait.until(ExpectedConditions.stalenessOf(btnEliminar));
        pausa();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.id("boton-volver"))).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto")));
        driver.findElements(By.className("producto")).get(6).click();
        inputCantidad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cantidadGalletas")));
        inputCantidad.clear();
        inputCantidad.sendKeys("12");
        driver.findElement(By.id("boton-agrergar")).click();
        pausa();
        driver.findElement(By.id("boton-crearpedido")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cliente"))).sendKeys("Selenium_TestEditado");
        WebElement campoFecha = driver.findElement(By.id("fecha"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='2026-07-20';", campoFecha);
        Select dropdownZona = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("zona"))));
        dropdownZona.selectByValue("Chacao");
        driver.findElement(By.id("detalles")).sendKeys("Entrega rápida.");
        pausa();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("boton-siguiente"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titular"))).sendKeys("Selenium_TestEditado");
        Select dropdownBanco = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("banco"))));
        dropdownBanco.selectByValue("Banco Exterior");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("celular"))).sendKeys("04121234567");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cedula"))).sendKeys("12345678");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("referencia"))).sendKeys("12345678");
        pausa();
        wait.until(ExpectedConditions.elementToBeClickable(By.className("boton-enviar"))).click();
        
        wait.until(ExpectedConditions.alertIsPresent()).accept(); 
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("botonvisu"))).click();
        pausa();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("botondownload"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("boton-volver"))).click();
    }

    @AfterEach
    void tearDown() {}
}