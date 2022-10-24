import api.client.OrdersClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationParametrizedTest {
    private final String black;
    private final String grey;

    public OrderCreationParametrizedTest(String black, String grey) {
        this.black = black;
        this.grey = grey;
    }

    @Before
    @Step("Базовый URL")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters(name = "Тестовые данные:{0},{1}")
    public static Object[][] getCredentials() {
        return new Object[][]{
                {"Black", null},
                {null, "Gray"},
                {"Black", "Gray"},
                {null, null}
        };
    }

    @Test
    @DisplayName("Можно указать один из цветов — BLACK или GREY или оба")
    public void createOrderWithColor() {
        OrdersClient ordersClient = new OrdersClient();
        Response response = ordersClient.createOrder();
        response.then().body("track", notNullValue());
    }

    @After
    public void deleteData() {
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.cancelOrder();
    }
}