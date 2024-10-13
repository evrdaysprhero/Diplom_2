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
import pojo.RegisterRequest;
import pojo.RegisterResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

@Story("Создание пользователя")
public class RegisterTest {
    private String name;
    private String password;
    private String email;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

        name = "sprhero" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        password = RandomStringUtils.randomNumeric(5);
        email = name + "@mailme.ru";
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

    @Test
    @DisplayName("создать уникального пользователя")
    public void registerUniqSuccess() {

        RegisterRequest registerRequest = new RegisterRequest(name, password, email);
        Response response = postRegister(registerRequest);
        checkResponseCode(response,200);

    }

    @Test
    @DisplayName("создать пользователя, который уже зарегистрирован")
    public void registerNotUniqError() {

        RegisterRequest registerRequest = new RegisterRequest(name, password, email);

        //создаем первый раз
        Response response = postRegister(registerRequest);
        checkResponseCode(response,200);

        //создаем второй раз
        Response responseTwo = postRegister(registerRequest);
        checkResponseCode(responseTwo,403);
        checkResponseMessage(responseTwo, "User already exists");
    }

    @After
    public void deleteUser() {
        String accessToken = MakeOrderTest.authUser(password, email);
        given()
                .header("authorization", accessToken)
                .delete("/api/auth/user");
    }

}
