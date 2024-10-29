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

@Story("Получение заказов конкретного пользователя")
public class GetOrderTest {
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
        ApiHelper.postRegister(registerRequest);

    }

    @Test
    @DisplayName("Без авторизации")
    public void getOrderNoAuthFail() {

        Response response = ApiHelper.getOrders();
        response.then()
                .assertThat()
                .statusCode(401);

        GetOrderResponse orderResponse = response
                .body()
                .as(GetOrderResponse.class);
        Assert.assertEquals("You should be authorised", orderResponse.getMessage());
        Assert.assertFalse(orderResponse.isSuccess());

    }

    @Test
    @DisplayName("С авторизацией")
    public void getOrderWithAuthSuccess() {

        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c");

        //авторизация
        String accessToken = ApiHelper.authUser(password, email);

        //создать заказ
        MakeOrderRequest order = new MakeOrderRequest(ingredients);
        ApiHelper.postOrders(order, accessToken);

        //получить заказ
        Response response = ApiHelper.getOrders(accessToken);
        response.then()
                .assertThat()
                .statusCode(200);

        GetOrderResponse getOrderResponse = response
                .body()
                .as(GetOrderResponse.class);
        Assert.assertTrue(getOrderResponse.isSuccess());
        Assert.assertEquals(ingredients, getOrderResponse.getOrders().get(0).getIngredients());
    }

    @After
    public void deleteUser() {
        String accessToken = ApiHelper.authUser(password, email);
        given()
                .header("authorization", accessToken)
                .delete("/api/auth/user");
    }
}
