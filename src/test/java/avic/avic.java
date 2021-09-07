package avic;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class AvicTests {
    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }
    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver(); //создание экземпляра хром драйвера
        driver.manage().window().maximize(); //открытие брайзера на весь экран
        driver.get("https://avic.ua/"); //открытие сайта
    }

    @Test (priority = 1)
    public void checkUrlContainsSearchWord() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("Apple iMac 27"); //ввод в поиск Apple iMac 27
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        assertTrue(driver.getCurrentUrl().contains("query=Apple iMac 27")); //проверка, что урл содержит запрос
    }

    @Test (priority = 2)
    public void checkElementsAmountOnSearchPage() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("Apple iMac 27"); //ввод в поиск Apple iMac 27
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS); //неявное ожидание 30 сек
        driver.findElement(xpath("//a[@class='btn-see-more js_show_more']")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElement(xpath("//a[@class='btn-see-more js_show_more']")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        List<WebElement> elementsList = driver.findElements(xpath("//div[@class='prod-cart__descr']")); //сбор элементов поиска в лист
        int actualElementsSize = elementsList.size(); // определение элементов в списке
        assertEquals(actualElementsSize, 34);
    }

    @Test (priority = 3)
    public void checkDisplayPriceOnCategoryPage() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click(); //дропдаун лист - каталог товаров
        driver.findElement(xpath("//span[text()='Apple Store']")).click(); //меню Apple store
        driver.findElement(xpath("//span[text()='IPhone']")).click(); //меню IPhone
        List<WebElement> elementsList = driver.findElements(xpath("//div[@class='prod-cart__descr']")); //сбор элементов поиска в лист
        for (WebElement webElement : elementsList) {
            assertTrue(webElement.getText().contains("грн"));
        }
    }

    @Test (priority = 4)
    public void checkDeleteItemFromCart() {
        driver.findElement(xpath("//div[@class='partner-box height']//a[@href='/brand-apple']")).click(); // кнопка Apple Store
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();//iphone
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("Complete")); // ожидание загрузки страницы
        driver.findElement(xpath("//a[@class='prod-cart__buy'][contains (@data-ecomm-cart,'244654')]")); // добавление в корзину
        WebDriverWait wait = new WebDriverWait(driver, 30);// ожидание пока не отобразится попап с товаром добавленным в корзину
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));
        driver.findElement(xpath("//div[@class='btns-cart-holder']//a[contains(@class,'btn--orange')]")).click();//продолжить покупки
        driver.findElement(xpath("//i[@class='icon icon-close js-btn-close']")).click(); //удаление добавленного товара из корзины
        driver.findElement(xpath("//div[@class='btns-cart-holder']//a[contains(@class,'btn--orange')]")).click();//продолжить покупки
        String actualProductsCountInCart =
                driver.findElement(xpath("//a[@class='mobile-cart mobile-item js-btn-open']//div[@class='active-cart-item js_cart_count']"))
                        .getText();//получили 0 который в корзине (0 продуктов)
        assertEquals(actualProductsCountInCart, "0");
    }

    @AfterMethod
    public void tearDown() {
        driver.close(); //закрытие драйвера
    }
}
