import os
import sys
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By


TARGET_URL = os.getenv("TARGET_URL", "http://host.docker.internal:8080/LogIn.html")
REMOTE_URL = os.getenv("SELENIUM_REMOTE_URL", "")


def main() -> int:
    options = Options()
    options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")

    if REMOTE_URL:
        driver = webdriver.Remote(command_executor=REMOTE_URL, options=options)
    else:
        driver = webdriver.Chrome(options=options)

    try:
        driver.get(TARGET_URL)

        title = driver.title
        print(f"URL actual: {driver.current_url}")
        print(f"Titulo: {title}")
        print(driver.page_source[:400])

        login = driver.find_element(By.ID, "usuario")
        login.send_keys("tester-demo")

        print(f"Pagina cargada: {title}")
        print("Interfaz visible y campo de usuario localizado.")

        assert "CookieBites Demo Title Inexistente" in title, (
            "Fallo intencional: el titulo esperado no existe para demostrar el flujo de errores."
        )
    finally:
        driver.quit()

    return 0


if __name__ == "__main__":
    sys.exit(main())