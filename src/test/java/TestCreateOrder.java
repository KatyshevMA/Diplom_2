import io.restassured.RestAssured;
import org.example.Login;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestCreateOrder {
    private String token;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void checkOneIngredientOrderSuccess() {
        Login login  = new Login("loginfororder@email.ru", "1234");
        token = given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\"]}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    public void checkTwoIngredientsOrderSuccess() {
        Login login  = new Login("loginfororder@email.ru", "1234");
        token = given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    public void checkWithoutIngredientOrderBadRequest() {
        Login login  = new Login("loginfororder@email.ru", "1234");
        token = given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"ingredients\": \"\"}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void checkIncorrectIngredientOrderServerError() {
        Login login  = new Login("loginfororder@email.ru", "1234");
        token = given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().jsonPath().get("accessToken");

        String json = "{\"ingredients\": \"dfgdfgdfg\"}";
        given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(json)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(500);
    }

    @Test
    public void checkOrderWithoutAuthorizationBadRequest() {
        String json = "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\"]}";
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

}
