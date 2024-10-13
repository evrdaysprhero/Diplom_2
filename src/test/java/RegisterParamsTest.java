import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.RegisterRequest;

@RunWith(Parameterized.class)
@Feature(value = "Не заполнено одно из обязательных полей")
public class RegisterParamsTest {

    private String name;
    private String password;
    private String email;

    public RegisterParamsTest(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    @Parameterized.Parameters
    public static Object[][] getUserData() {
        return new Object[][] {
                { "", "12345", "Eugenia@mail.ru" },
                { null, "12345", "Eugenia@mail.ru" },
                { "sprhero03", "", "Eugenia@mail.ru" },
                { "sprhero03", null, "Eugenia@mail.ru" },
                { "sprhero03", "12345", "" },
                { "sprhero03", "12345", null },
                { null, null, "" },
                { "", null, "" },
                { null, "", null },
                { "", "", null },

        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    public void createNoRequiredFieldFail() {

        RegisterRequest registerRequest = new RegisterRequest(name, password, email);

        Response response = RegisterTest.postRegister(registerRequest);
        RegisterTest.checkResponseCode(response,403);
        RegisterTest.checkResponseMessage(response, "Email, password and name are required fields");

    }

}
