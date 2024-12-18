import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.RestAssured.given;

@Story("Создание заказа")
public class MakeOrderTest {
    private String password;
    private String email;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

        String name = "sprhero" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        password = RandomStringUtils.randomNumeric(5);
        email = name + "@mailme.ru";

        //создать пользователя
        RegisterRequest registerRequest = new RegisterRequest(name, password, email);
        RegisterTest.postRegister(registerRequest);

    }

    @Step("Авторизоваться")
    public static String authUser(String password, String email) {
        LoginRequest loginRequest = new LoginRequest(password, email);

        RegisterResponse registerResponse =  LoginTest
                .postLogin(loginRequest)
                .body()
                .as(RegisterResponse.class);

        return registerResponse.getAccessToken();
    }

    @Step("Вызов /api/orders с авторизацией")
    public static Response postOrders(MakeOrderRequest order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .body(order)
                .post("/api/orders");
    }

    @Step("Вызов /api/orders без авторизации")
    public static Response postOrders(MakeOrderRequest order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/orders");
    }

    @Step("Проверка кода ответа")
    public static void checkResponseCode(Response response, Integer expCode) {
        response.then().assertThat()
                .statusCode(expCode);
    }

    @Test
    @DisplayName("Заказ с авторизацией")
    public void makeOrderWithAuthSuccess() {

        //авторизация
        String accessToken = authUser(password, email);

        //создание заказа
        MakeOrderRequest order = new MakeOrderRequest(List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c"));
        Response response = postOrders(order, accessToken);
        checkResponseCode(response, 200);

        //проверка
        MakeOrderResponse orderResponse = response
                .body()
                .as(MakeOrderResponse.class);
        Assert.assertTrue("Заказ не создан", orderResponse.isSuccess());

    }

    @Test
    @DisplayName("Заказ без авторизации")
    public void makeOrderNoAuthSuccess() {

        //создание заказа
        MakeOrderRequest order = new MakeOrderRequest(List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c"));
        Response response = postOrders(order);
        checkResponseCode(response, 200);

        //проверка
        MakeOrderResponse orderResponse = response
                .body()
                .as(MakeOrderResponse.class);
        Assert.assertTrue("Заказ не создан", orderResponse.isSuccess());

    }

    @Test
    @DisplayName("Заказ без ингридиентов")
    public void makeOrderNoIngredintsFail() {

        //создание заказа
        MakeOrderRequest order = new MakeOrderRequest(null);
        Response response = postOrders(order);
        checkResponseCode(response, 400);

        //проверка
        MakeOrderResponse orderResponse = response
                .body()
                .as(MakeOrderResponse.class);
        Assert.assertEquals("Ingredient ids must be provided", orderResponse.getMessage());

    }

    @Test
    @DisplayName("Заказ с неверным хешем ингредиентов")
    public void makeOrderWrongIngredintsFail() {

        //создание заказа
        MakeOrderRequest order = new MakeOrderRequest(List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa6e", "0000"));
        Response response = postOrders(order);
        checkResponseCode(response, 500);

    }

    @After
    public void deleteUser() {
        String accessToken = MakeOrderTest.authUser(password, email);
        given()
                .header("authorization", accessToken)
                .delete("/api/auth/user");
    }
}
