import api.client.CourierClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    CourierClient courierClient;

    @Before
    @Step("Базовый URL и создание курьера")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        courierClient = new CourierClient();
        courierClient.getCorrectCreditsResponse();
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void loginCourier() {
        Response response = courierClient.authCourier();
        response.then().statusCode(200).and().body("id", notNullValue());
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля")
    public void loginCourierWithRequiredField() {
        Response response = courierClient.authCourier();
        response.then().statusCode(200);
    }

    @Test
    @DisplayName("Система вернёт ошибку, если неправильно указать логин или пароль")
    public void loginCourierWithWrongPass() {
        Response response = courierClient.authCourierWithWrongPassword();
        response.then().statusCode(404);
    }

    @Test
    @DisplayName("Если какого-то поля нет, запрос возвращает ошибку")
    public void loginCourierWithoutRequiredField() {
        Response response = courierClient.authCourierWithEmptyRequiredField();
        response.then().statusCode(400);
    }

    @Test
    @DisplayName("Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void loginNonexistentCourier() {
        Response response = courierClient.authNonNonexistentCourier();
        response.then().body("message", is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    public void loginCourierCheckBody() {
        Response response = courierClient.authCourier();
        response.then().body("id", notNullValue());
    }

    @After
    public void setDown() {
        CourierClient courierClient = new CourierClient();
        courierClient.deleteData();
    }
}