import api.client.OrdersClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTest {

    @Before
    @Step("Базовый URL")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Тело ответа содержит track")
    public void createOrderCheckBody() {
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