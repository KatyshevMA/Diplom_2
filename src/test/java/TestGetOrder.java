import io.restassured.RestAssured;
import org.example.Register;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestGetOrder {
    private String token = "токен";
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void checkGetOrderSuccess() {
        Register reg  = new Register("getorder1@xxxx.ru", "1234", "getorder1");
        token = given()
                .header("Content-type", "application/json")
                .body(reg)
                .when()
                .post("/api/auth/register")
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

        given()
                .header("Authorization", token)
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    public void checkGetOrderWithoutAuthorizationUnauthorized() {
        given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/orders")
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        given()
                .header("Authorization", token)
                .when()
                .delete("/api/auth/user");
    }
}
