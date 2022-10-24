import api.client.CourierClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class CourierCreationTest {
    @Before
    @Step("Базовый URL и генерируемые данные")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Курьера можно создать")
    public void createNew() {
        CourierClient courierClient = new CourierClient();
        Response correctResponse = courierClient.getCorrectCreditsResponse();
        correctResponse.then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void createTwoIdentical() {
        CourierClient courierClient = new CourierClient();
        courierClient.getCorrectCreditsResponse();
        Response response = courierClient.getCorrectCreditsResponse();
        response.then().statusCode(409);
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void createWithoutRequiredField() {
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseWithEmptyRequiredField();
        response.then().statusCode(400);
    }

    @Test
    @DisplayName("Запрос возвращает правильный код ответа")
    public void checkStatusCode() {
        CourierClient courierClient = new CourierClient();
        Response correctResponse = courierClient.getCorrectCreditsResponse();
        correctResponse.then().statusCode(201);
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    public void checkBodyResponse() {
        CourierClient courierClient = new CourierClient();
        Response correctResponse = courierClient.getCorrectCreditsResponse();
        correctResponse.then().body("ok", is(true));
    }

    @Test
    @DisplayName("Если одного из полей нет, запрос возвращает ошибку")
    public void createWithoutRequiredFieldAndCheckBodyResponse() {
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseWithEmptyRequiredField();
        response.then()
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void createTwoIdenticalAndCheckBodyResponse() {
        CourierClient courierClient = new CourierClient();
        courierClient.getCorrectCreditsResponse();
        Response response = courierClient.getCorrectCreditsResponse();
        response.then()
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    @After
    public void setDown() {
        CourierClient courierClient = new CourierClient();
        courierClient.deleteData();
    }
}