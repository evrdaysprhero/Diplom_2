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

@Story("Получение заказов конкретного пользователя")
public class GetOrderTest extends AbstractApiTest{
    private String password;
    private String email;

    @Before
    public void setUp() {
        RestAssured.baseURI = URL;

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

        GetIngredientsResponse ingredients = ApiHelper.getIngredients()
                .body()
                .as(GetIngredientsResponse.class);

        List<String> ingredientsList = List.of(ingredients.getData().get(0).get_id(),
                ingredients.getData().get(1).get_id());

        //авторизация
        String accessToken = ApiHelper.authUser(password, email);

        //создать заказ
        MakeOrderRequest order = new MakeOrderRequest(ingredientsList);
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
        Assert.assertEquals(ingredientsList, getOrderResponse.getOrders().get(0).getIngredients());
    }

    @After
    public void deleteUser() {
        ApiHelper.deleteUser(password, email);

    }
}
