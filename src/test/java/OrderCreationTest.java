import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jsons.Order;
import jsons.TrackNumber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTest {
    Order order;

    @Before
    @Step("Базовый URL")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        order = new Order();
    }

    @Test
    @DisplayName("Можно совсем не указывать цвет")
    public void createOrderWithoutColor() {
        order.setColor(null);
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");
        response.then().statusCode(201);
    }

    @Test
    @DisplayName("Тело ответа содержит track")
    public void createOrderCheckBody() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");
        response.then().body("track", notNullValue());
    }

    @After
    @Step("Отмена заказа")
    public void deleteData() {
        Order order = new Order();
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");
        TrackNumber trackNumber = response.body()
                .as(TrackNumber.class);

        given().header("Content-type", "application/json")
                .body(trackNumber.getTrack())
                .put("/api/v1/orders/cancel" );
    }
}