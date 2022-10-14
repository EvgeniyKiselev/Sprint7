import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jsons.Answer;
import jsons.Courier;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    Faker faker;
    String login;
    String password;
    String firstName;
    Courier courier;

    @Before
    @Step("Базовый URL и создание курьера")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        faker = new Faker();
        login = faker.name().firstName();
        password = faker.name().firstName();
        firstName = faker.name().firstName();
        courier = new Courier(login, password, firstName);
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void loginCourier() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
        response.then().statusCode(200);
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля")
    public void loginCourierWithRequiredField() {
        Courier courier = new Courier(login, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
        response.then().statusCode(200);
    }

    @Test
    @DisplayName("Система вернёт ошибку, если неправильно указать логин или пароль")
    public void loginCourierWithWrongPass() {
        courier.setPassword(password + login);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
        response.then().statusCode(404);
    }

    @Test
    @DisplayName("Если какого-то поля нет, запрос возвращает ошибку")
    public void loginCourierWithoutRequiredField() {
        courier.setLogin(null);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
        response.then().statusCode(400);
    }

    @Test
    @DisplayName("Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void loginNonexistentCourier() {
        courier.setLogin(login + login);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
        response.then().body("message", is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    public void loginCourierCheckBody() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login");
        response.then().body("id", notNullValue());
    }

    @After
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