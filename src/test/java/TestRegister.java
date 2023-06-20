import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Register;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestRegister {
    private String token = "токен";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void checkRegisterSuccess() {
        Register reg  = new Register("mmm@xxxx.ru", "1234", "register111");
        token = given()
                        .header("Content-type", "application/json")
                        .body(reg)
                        .when()
                        .post("/api/auth/register")
                        .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .extract().jsonPath().get("accessToken");
    }

    @Test
    public void checkRegisterDuplicateError() {
        Register reg  = new Register("double_login@xxxx.ru", "1234", "double_login");
        token = given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    public void checkRegisterWithoutEmailError() {
        Register reg  = new Register("", "noemail", "noemail");
        given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void checkRegisterWithoutPassError() {
        Register reg  = new Register("nopass@pass.ru", "", "nopass");
        given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void checkRegisterWithoutNameError() {
        Register reg  = new Register("noname@vvv.ru", "noname", "");
        given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        given()
                .header("Authorization", token)
                .when()
                .delete("/api/auth/user");
    }

}
