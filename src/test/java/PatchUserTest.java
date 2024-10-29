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

import static io.restassured.RestAssured.given;

@Story("Изменение данных пользователя")
public class PatchUserTest {

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
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void loginSuccess() {

        String newEmail = email + "text";
        String newName = "New Name";

        //авторизация
        LoginRequest loginRequest = new LoginRequest(password, email);
        Response responseLogin = LoginTest.postLogin(loginRequest);

        RegisterResponse registerResponse = responseLogin
                .body()
                .as(RegisterResponse.class);
        String accessToken = registerResponse.getAccessToken();

        //обновляем данные
        User patchRequest = new User(newEmail, newName);
        ApiHelper.patchUser(patchRequest, accessToken)
                .then()
                .assertThat()
                .statusCode(200);

        //проверяем, что данные обновились
        GetUserResponse getUserResponse = ApiHelper.getUser(accessToken)
                .body()
                .as(GetUserResponse.class);
        Assert.assertEquals("Email не обновился", newEmail, getUserResponse.getUser().getEmail());
        Assert.assertEquals("Name не обновился", newName, getUserResponse.getUser().getName());

    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void loginNoAuthFail() {

        //обновляем данные
        User patchRequest = new User(email + "text", "New Name");
        given()
                .header("Content-type", "application/json")
                .body(patchRequest)
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(401);

    }

    @After
    public void deleteUser() {
        String accessToken = ApiHelper.authUser(password, email);
        if(accessToken!=null) {
            given()
                    .header("authorization", accessToken)
                    .delete("/api/auth/user");
        } else {
            given()
                    .header("authorization", ApiHelper.authUser(password, email + "text"))
                    .delete("/api/auth/user");
        }

    }
}
