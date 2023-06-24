import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Register;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestRefresh {
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Метод PATCH/auth/user. Успешное изменение Email")
    public void checkRefreshEmailSuccess() {
        Register reg  = new Register("refreshemail@xxxx.ru", "1234", "refreshemail");
        token = given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"email\": \"refreshemail2@xxxx.ru\"}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("user.email", equalTo("refreshemail2@xxxx.ru"));
    }

    @Test
    @DisplayName("Метод PATCH/auth/user. Успешное изменение Password")
    public void checkRefreshNameSuccess() {
        Register reg  = new Register("refreshname@xxxx.ru", "1234", "refreshname");
        token = given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"name\": \"refreshname2\"}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("user.name", equalTo("refreshname2"));
    }

    @Test
    @DisplayName("Метод PATCH/auth/user. Успешное изменение Email и Password")
    public void checkRefreshAllSuccess() {
        Register reg  = new Register("refreshall@xxxx.ru", "1234", "refreshall");
        token = given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"email\": \"refreshall2@xxxx.ru\", \"name\": \"refreshall2\"}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("user.email", equalTo("refreshall2@xxxx.ru"))
                .body("user.name", equalTo("refreshall2"));
    }

    @Test
    @DisplayName("Метод PATCH/auth/user. Успешное изменение данных на введенные ранее")
    public void checkRefreshNothingSuccess() {
        Register reg  = new Register("refreshnothing@xxxx.ru", "1234", "refreshnothing");
        token = given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"email\": \"refreshnothing@xxxx.ru\", \"name\": \"refreshnothing\"}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("user.email", equalTo("refreshnothing@xxxx.ru"))
                .body("user.name", equalTo("refreshnothing"));
    }

    @Test
    @DisplayName("Метод PATCH/auth/user. Ошибка изменения данных. Email уже занят")
    public void checkRefreshEmailAlreadyExistsForbidden() {
        Register reg  = new Register("refreshalready@xxxx.ru", "1234", "refreshalready");
        token = given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"email\": \"refreshalready1@xxxx.ru\"}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(403)
                .body("message", equalTo("User with such email already exists"));
    }

    @Test
    @DisplayName("Метод PATCH/auth/user. Ошибка изменения данных. Пользователь не авторизован")
    public void checkRefreshWithoutAuthorizationUnauthorized() {
        String json = "{\"email\": \"noauthorization@xxxx.ru\"}";
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            given()
                    .header("Authorization", token)
                    .when()
                    .delete("/api/auth/user");
        }
    }
}
