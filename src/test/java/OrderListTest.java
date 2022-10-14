import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    @Before
    @Step("Базовый URL")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("В тело ответа возвращается список заказов")
    public void checkBody() {
        Response response = given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders");
        response.then().body("orders", notNullValue());
    }
}