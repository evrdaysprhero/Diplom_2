import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Assert;
import pojo.*;

import static io.restassured.RestAssured.given;

public class ApiHelper {

    public static String API_ORDERS = "/api/orders";
    public static String API_OAUTH_USER = "/api/auth/user";
    public static String API_OAUTH_REGISTER = "/api/auth/register";
    public static String API_OAUTH_LOGIN = "/api/auth/login";
    public static String API_INGREDIENTS = "/api/ingredients";

    @Step("Вызов /api/auth/register")
    public static Response postLogin(LoginRequest loginRequest) {
        return given()
                .header("Content-type", "application/json")
                .body(loginRequest)
                .post(API_OAUTH_LOGIN);
    }

    @Step("Авторизоваться")
    public static String authUser(String password, String email) {
        LoginRequest loginRequest = new LoginRequest(password, email);

        RegisterResponse registerResponse =  ApiHelper
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
                .post(API_ORDERS);
    }

    @Step("Вызов /api/orders без авторизации")
    public static Response postOrders(MakeOrderRequest order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(API_ORDERS);
    }

    @Step("Вызов /api/orders с авторизацией")
    public static Response getOrders(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .get(API_ORDERS);
    }

    @Step("Вызов /api/orders без авторизации")
    public static Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .get(API_ORDERS);
    }

    @Step("Обновление данных. Вызов /api/auth/user")
    public static Response patchUser(User user, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .body(user)
                .patch(API_OAUTH_USER);
    }

    @Step("Получение данных. Вызов /api/auth/user")
    public static Response getUser(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .get(API_OAUTH_USER);
    }

    @Step("Вызов /api/auth/register")
    public static Response postRegister(RegisterRequest registerRequest) {
        return given()
                .header("Content-type", "application/json")
                .body(registerRequest)
                .post(API_OAUTH_REGISTER);
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

    @Step("Вызов /api/ingredients")
    public static Response getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .get(API_INGREDIENTS);
    }

    @Step("Удаление пользователя")
    public static void deleteUser(String password, String email) {
        String accessToken = ApiHelper.authUser(password, email);
        given()
                .header("authorization", accessToken)
                .delete(API_OAUTH_USER);
    }
}
