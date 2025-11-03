import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntergrationTest {

    @BeforeAll
    static void setUp() {
        RestAssured .baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOKWithValidToken() {
        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin() {
        String loginPayload = """
                {
                    "email": "invalid_user@test.com",
                    "password": "invalid_password"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void shouldReturnRegistredPatientEmail() {
        String uniqueEmail = "test_user_" + System.currentTimeMillis() + "@test.com";

        String registerPayload = String.format("""
                {
                    "email": "%s",
                    "password": "password123"
                }
                """, uniqueEmail);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(200)
                .body("email", notNullValue())
                .extract()
                .response();

        System.out.println("Generated Email: " + response.jsonPath().getString("email"));
    }

    @Test
    public void shouldReturnConflictWhenEmailAlreadyExists() {
        String existingEmail = "testuser@test.com";

        String registerPayload = String.format("""
                {
                    "email": "%s",
                    "password": "password123"
                }
                """, existingEmail);

        given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(409);
    }
}
