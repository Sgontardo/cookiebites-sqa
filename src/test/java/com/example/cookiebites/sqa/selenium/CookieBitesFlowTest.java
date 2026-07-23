package com.example.cookiebites.sqa.selenium;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CookieBitesFlowTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Inicializamos el navegador una sola vez para toda la clase
    @BeforeAll
    void setupAll() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Cada prueba abre su propia pestaña
    @BeforeEach
    void setUp() {
        driver.switchTo().newWindow(WindowType.TAB);
    }

    // Al finalizar la prueba, cerramos la pestaña de forma SEGURA
    @AfterEach
    void tearDown() {
        if (driver.getWindowHandles().size() > 1) {
            try {
                driver.close();
            } catch (Exception e) {
                System.out.println("No se pudo cerrar la pestaña, forzando cambio de foco. Error: " + e.getMessage());
            } finally {
                // Siempre intentamos regresar el foco a la pestaña principal, incluso si falló el cierre
                driver.switchTo().window(driver.getWindowHandles().iterator().next());
            }
        }
    }

    // Finalmente, cerramos el navegador al terminar todas las pruebas
    @AfterAll
    void tearDownAll() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error al cerrar el navegador: " + e.getMessage());
            }
        }
    }

    private void pausa() {
        try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void iniciarSesion(String usuario, String clave) {
        driver.get("http://localhost:8080/LogIn.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario"))).sendKeys(usuario);
        driver.findElement(By.id("contrasena")).sendKeys(clave);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    @ParameterizedTest(name = "Ejecución {index} - Probando usuario: {1}")
    @CsvFileSource(resources = "/dataDrivenSeleniumCookiesBites.csv", numLinesToSkip = 1)
    void testRegistro(String nombre, String usuario, String clave, String correo) {
        driver.get("http://localhost:8080/CrearPerfil.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("nombre"))).sendKeys(nombre);
        driver.findElement(By.name("usuario")).sendKeys(usuario);
        driver.findElement(By.name("correo")).sendKeys(correo);
        driver.findElement(By.id("contrasena")).sendKeys(clave);
        driver.findElement(By.id("confirmar")).sendKeys(clave);
        pausa();
        driver.findElement(By.id("formita")).click();
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    @ParameterizedTest(name = "Ejecución {index} - Probando usuario: {1}")
    @CsvFileSource(resources = "/dataDrivenSeleniumCookiesBites.csv", numLinesToSkip = 1)
    void testModificarUsuario(String nombre, String usuario, String clave, String correo, String usuarioEditado, String contraseñaEditada) {
        iniciarSesion(usuario, clave);
        wait.until(ExpectedConditions.elementToBeClickable(By.className("usuario"))).click();
        pausa();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("editar-btn"))).click();
        WebElement campoNombre = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-nombreusuario")));
        campoNombre.clear();
        campoNombre.sendKeys(usuarioEditado);
        WebElement campoPass = driver.findElement(By.id("edit-passworduser"));
        campoPass.clear();
        campoPass.sendKeys(contraseñaEditada);
        pausa();
        driver.findElement(By.id("guardar-btn")).click();
        driver.findElement(By.id("boton-volver")).click();
    }

    @ParameterizedTest(name = "Ejecución {index} - Probando usuario: {1}")
    @CsvFileSource(resources = "/dataDrivenSeleniumCookiesBites.csv", numLinesToSkip = 1)
    void testEliminarProducto(String nombre, String usuario, String clave, String correo, String uE, String pE, String productoEliminar) {
        iniciarSesion(uE, pE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto")));
        driver.findElements(By.className("producto")).get(Integer.parseInt(productoEliminar)).click();
        driver.findElement(By.id("boton-agrergar")).click();
        pausa();
        WebElement btnEliminar = driver.findElement(By.className("eliminar-btn"));
        btnEliminar.click();
        wait.until(ExpectedConditions.stalenessOf(btnEliminar));
        pausa();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("boton-volver"))).click();
    }

    @ParameterizedTest(name = "Ejecución {index} - Probando usuario: {1}")
    @CsvFileSource(resources = "/dataDrivenSeleniumCookiesBites.csv", numLinesToSkip = 1)
    void testRealizarCompra(String n, String user, String pass, String c, String uE, String pE, String pEl, String prodComprar, String cantidad, String cliente, String fecha, String zona, String desc, String titular, String banco, String tel, String cedula, String ref) {
        iniciarSesion(uE, pE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto")));
        driver.findElements(By.className("producto")).get(Integer.parseInt(prodComprar)).click();
        WebElement inputCantidad = wait.until(ExpectedConditions.elementToBeClickable(By.id("cantidadGalletas")));
        inputCantidad.clear();
        inputCantidad.sendKeys(cantidad);
        driver.findElement(By.id("boton-agrergar")).click();
        pausa();
        driver.findElement(By.id("boton-crearpedido")).click();
        
        // Formulario de pedido
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cliente"))).sendKeys(cliente);
        WebElement campoFecha = driver.findElement(By.id("fecha"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + fecha + "';", campoFecha);
        
        // Forzar evento 'change' en la fecha por si la página necesita validarlo
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", campoFecha);
        
        Select dropdownZona = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("zona"))));
        dropdownZona.selectByValue(zona);
        driver.findElement(By.id("detalles")).sendKeys(desc);
        pausa();
        
        // Buscar el botón siguiente, hacer scroll para asegurar que sea visible, y hacer click
        WebElement btnSiguiente = wait.until(ExpectedConditions.elementToBeClickable(By.id("boton-siguiente")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnSiguiente);
        pausa();
        
        try {
            btnSiguiente.click();
        } catch (Exception e) {
            // Si el click normal falla por superposición, forzamos el click con Javascript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSiguiente);
        }
        
        // Esperamos 2 segundos extras para asegurar que la pantalla cambie
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        // Formulario de pago
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titular"))).sendKeys(titular);
        Select dropdownBanco = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("banco"))));
        dropdownBanco.selectByValue(banco);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("celular"))).sendKeys(tel);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cedula"))).sendKeys(cedula);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("referencia"))).sendKeys(ref);
        pausa();
        wait.until(ExpectedConditions.elementToBeClickable(By.className("boton-enviar"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("botonvisu"))).click();
        pausa();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("botondownload"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("boton-volver"))).click();
    }
}