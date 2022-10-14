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

public class CourierCreationTest {
    Faker faker;
    String login;
    String password;
    String firstName;

    @Before
    @Step("Базовый URL и генерируемые данные")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        faker = new Faker();
        login = faker.name().firstName();
        password = faker.name().firstName();
        firstName = faker.name().firstName();
    }

    @Test
    @DisplayName("Курьера можно создать")
    public void createNew() {
        Courier courier = new Courier(login, password, firstName);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void createTwoIdentical() {
        Courier courier = new Courier(login, password, firstName);
        Response responseCreate = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        responseCreate.then()
                .statusCode(201);
        Response responseSecond = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        responseSecond.then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void createWithoutRequiredField() {
        Courier courier = new Courier(login, password);
        courier.setPassword(null);
        courier.setFirstName(firstName);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Запрос возвращает правильный код ответа")
    public void checkStatusCode() {
        Courier courier = new Courier(login, password, firstName);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    public void checkBodyResponse() {
        Courier courier = new Courier(login, password, firstName);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then()
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Если одного из полей нет, запрос возвращает ошибку")
    public void createWithoutRequiredFieldAndCheckBodyResponse() {
        Courier courier = new Courier(login, password);
        courier.setPassword(null);
        courier.setFirstName(firstName);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then()
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void createTwoIdenticalAndCheckBodyResponse() {
        Courier courier = new Courier(login, password, firstName);
        Response responseCreate = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        responseCreate.then()
                .statusCode(201);
        Response responseSecond = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        responseSecond.then()
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
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