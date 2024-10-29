import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.RegisterRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Story("Создание пользователя")
public class RegisterTest extends AbstractApiTest {
    private String name;
    private String password;
    private String email;

    @Before
    public void setUp() {
        RestAssured.baseURI = URL;

        name = "sprhero" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        password = RandomStringUtils.randomNumeric(5);
        email = name + "@mailme.ru";
    }

    @Test
    @DisplayName("создать уникального пользователя")
    public void registerUniqSuccess() {

        RegisterRequest registerRequest = new RegisterRequest(name, password, email);
        Response response = ApiHelper.postRegister(registerRequest);
        ApiHelper.checkResponseCode(response,200);

    }

    @Test
    @DisplayName("создать пользователя, который уже зарегистрирован")
    public void registerNotUniqError() {

        RegisterRequest registerRequest = new RegisterRequest(name, password, email);

        //создаем первый раз
        Response response = ApiHelper.postRegister(registerRequest);
        ApiHelper.checkResponseCode(response,200);

        //создаем второй раз
        Response responseTwo = ApiHelper.postRegister(registerRequest);
        ApiHelper.checkResponseCode(responseTwo,403);
        ApiHelper.checkResponseMessage(responseTwo, "User already exists");
    }

    @After
    public void deleteUser() {
        ApiHelper.deleteUser(password, email);

    }

}
