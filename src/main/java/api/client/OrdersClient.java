package api.client;

import api.model.Order;
import api.model.TrackNumber;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrdersClient {
    Order order = new Order();

    @Step("Создание заказа")
    public Response createOrder() {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");
    }

    @Step("Отмена заказа")
    public Response cancelOrder() {
        Order order = new Order();
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");
        TrackNumber trackNumber = response.body()
                .as(TrackNumber.class);

        return given().header("Content-type", "application/json")
                .body(trackNumber.getTrack())
                .put("/api/v1/orders/cancel");
    }
}