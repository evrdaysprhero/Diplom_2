import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Assert;
import pojo.*;

import static io.restassured.RestAssured.given;

public class ApiHelper {

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

    @Step("Вызов /api/orders с авторизацией")
    public static Response getOrders(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .get("/api/orders");
    }

    @Step("Вызов /api/orders без авторизации")
    public static Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .get("/api/orders");
    }

    @Step("Обновление данных. Вызов /api/auth/user")
    public static Response patchUser(User user, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .body(user)
                .patch("/api/auth/user");
    }

    @Step("Получение данных. Вызов /api/auth/user")
    public static Response getUser(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .get("/api/auth/user");
    }

    @Step("Вызов /api/auth/register")
    public static Response postRegister(RegisterRequest registerRequest) {
        return given()
                .header("Content-type", "application/json")
                .body(registerRequest)
                .post("/api/auth/register");
    }

    @Step("Проверка кода ответа")
    public static void checkResponseCode(Response response, Integer expCode) {
        response.then().assertThat()
                .statusCode(expCode);
    }

    @Step("Проверка сообщения об ошибке")
    public static void checkResponseMessage(Response response, String expMsg) {
        RegisterResponse registerResponse = response
                .body()
                .as(RegisterResponse.class);
        Assert.assertEquals(expMsg, registerResponse.getMessage());
    }
}
