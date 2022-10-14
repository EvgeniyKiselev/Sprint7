import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jsons.Order;
import jsons.TrackNumber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationParametrizedTest {
    private final String[] color;

    public OrderCreationParametrizedTest(String[] color) {
        this.color = color;
    }

    @Before
    @Step("Базовый URL")
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters
    public static Object[][] getColor() {
        return new Object[][]{
                {new String[]{"Black"}},
                {new String[]{"Grey"}},
                {new String[]{"Black", "Grey"}},
                {new String[]{}},
        };
    }

    @Test
    @DisplayName("Можно указать один из цветов — BLACK или GREY или оба")
    public void createOrderWithColor() {
        Order order = new Order(color);
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