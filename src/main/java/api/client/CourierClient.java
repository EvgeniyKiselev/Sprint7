package api.client;

import api.model.Answer;
import api.model.Courier;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import net.datafaker.Faker;

import static io.restassured.RestAssured.given;

public class CourierClient {
    Faker faker = new Faker();
    String login = faker.name().firstName();
    String password = faker.name().firstName();
    String firstName = faker.name().firstName();
    Courier courier;

    @Step("Создание курьера")
    public Response getCorrectCreditsResponse() {
        courier = new Courier(login, password, firstName);
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Создание курьера с пустым паролем")
    public Response getResponseWithEmptyRequiredField() {
        Courier courier = new Courier(login, password);
        courier.setPassword(null);
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизация курьера")
    public Response authCourier() {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
    }

    @Step("Попытка авторизации с неправильным паролем")
    public Response authCourierWithWrongPassword() {
        courier.setPassword(password + login);
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
    }

    @Step("Попытка авторизации с пустым обязательным полем")
    public Response authCourierWithEmptyRequiredField() {
        courier.setLogin(null);
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
    }

    @Step("Попытка авторизации несуществующего курьера")
    public Response authNonNonexistentCourier() {
        courier.setLogin(login + login);
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера")
    public void deleteData() {
        Courier courier = new Courier(login, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
        Answer answer = response.body()
                .as(Answer.class);

        given().header("Content-type", "application/json")
                .delete("/api/v1/courier/" + answer.getId());
    }
}