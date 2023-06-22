import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Login;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestLogin {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Метод POST/auth/register. Успешная авторизация")
    public void checkLoginSuccess() {
        Login login  = new Login("successlogin@email.ru", "success");
        given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Метод POST/auth/register. Ошибка авторизации с несуществующим логином")
    public void checkLoginIncorrectEmailUnauthorized() {
        Login login  = new Login("incorrectemail@vvv.ru", "incorrect");
        given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Метод POST/auth/register. Ошибка авторизации с невалидным паролем")
    public void checkLoginIncorrectPasswordUnauthorized() {
        Login login  = new Login("successlogin@email.ru", "incorrect");
        given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Метод POST/auth/register. Ошибка авторизации с пустым Email")
    public void checkLoginWithoutEmailUnauthorized() {
        Login login  = new Login("", "noname");
        given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Метод POST/auth/register. Ошибка авторизации с пустым Password")
    public void checkLoginWithoutPasswordUnauthorized() {
        Login login  = new Login("successlogin@email.ru", "");
        given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
