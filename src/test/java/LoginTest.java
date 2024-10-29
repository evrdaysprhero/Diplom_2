import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.LoginRequest;
import pojo.RegisterRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

@Story("Логин пользователя")
public class LoginTest {

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

    @Step("Вызов /api/auth/register")
    public static Response postLogin(LoginRequest loginRequest) {
        return given()
                .header("Content-type", "application/json")
                .body(loginRequest)
                .post("/api/auth/login");
    }

    @Test
    @DisplayName("логин под существующим пользователем")
    public void loginSuccess() {

        LoginRequest loginRequest = new LoginRequest(password, email);
        Response response = postLogin(loginRequest);
        ApiHelper.checkResponseCode(response,200);

    }

    @Test
    @DisplayName("логин с неверным логином")
    public void loginWrongEmailError() {

        LoginRequest loginRequest = new LoginRequest(password, email + "text");
        Response response = postLogin(loginRequest);
        ApiHelper.checkResponseCode(response,401);
        ApiHelper.checkResponseMessage(response, "email or password are incorrect");

    }

    @Test
    @DisplayName("логин с неверным логином")
    public void loginWrongPasswordError() {

        LoginRequest loginRequest = new LoginRequest(password + "text", email);
        Response response = postLogin(loginRequest);
        ApiHelper.checkResponseCode(response,401);
        ApiHelper.checkResponseMessage(response, "email or password are incorrect");

    }

    @Test
    @DisplayName("логин с неверным логином и паролем")
    public void loginWrongEmailAndPasswordError() {

        LoginRequest loginRequest = new LoginRequest(password + "text", email + "text");
        Response response = postLogin(loginRequest);
        ApiHelper.checkResponseCode(response,401);
        ApiHelper.checkResponseMessage(response, "email or password are incorrect");

    }

    @After
    public void deleteUser() {
        String accessToken = ApiHelper.authUser(password, email);
        given()
                .header("authorization", accessToken)
                .delete("/api/auth/user");
    }
}
