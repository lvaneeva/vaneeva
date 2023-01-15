import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class LoginTest extends AbstractTest {
    static String session_id;
    static String myToken;

    @BeforeAll
    static void postLoginTest() {

        myToken =
                given()
                        .contentType("application/x-www-form-urlencoded")
                        //.contentType("application/form-data")
                        .formParam("username", "GB202301271f401")
                        .formParam("password", "a27fbe220e")
                        .when()

                        //.formParams("username","autotest","password","4956318935")
                        .post(getBaseUrl() + "/gateway/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .get("token")
                        .toString();
    }

    @Test
    void postLoginNegTest() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", "")
                .formParam("password", "4956318935")
                .when()
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .statusCode(401);

    }

    @Test
    void postLoginNegativeTest() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", "avtotest")
                .formParam("password", "4956318935")
                .when()
                .post(getBaseUrl() + "/gateway/login")
                .then()
                .assertThat()
                .statusCode(401);

    }

    @Test
    void getNotMyPostTest() {
        given()
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts?owner=notMe&sort=createdAt&order=ASC&page=1")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void getNotMyPostPage0Test() {
        given()
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts?owner=notMe&sort=createdAt&order=ASC&page=0")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void getNotMyPostOrderAllTest() {
        given()
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts?owner=notMe&sort=createdAt&order=ALL&page=1")
                .then()
                .assertThat()
                .statusCode(500);
    }

    @Test
    void getNotMyPostPage50000Test3() {
        given()
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts?owner=notMe&sort=createdAt&order=ASC&page=50000")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void getNotMyPostNoOrderTest() {
        given()
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts?owner=notMe&sort=createdAt&page=1")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void getMyPostTest() {
        given()
                .queryParam("sort", "createdAt")
                .queryParam("order", "DESC")
                .queryParam("page", "1")
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .body("data[1].id", equalTo(10760))
                .body("data[1].description", equalTo("testDescr"))
                .statusCode(200);
    }
    @Test
    void getMyPostAscTest() {
        given()
                .queryParam("sort", "createdAt")
                .queryParam("order", "ASC")
                .queryParam("page", "1")
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .body("data[1].id", equalTo(10761))
                .body("data[1].description", equalTo("descr"))
                .statusCode(200);
    }
    @Test
    void getMyPostNoPageTest() {
        given()
                .queryParam("sort", "createdAt")
                .queryParam("order", "DESC")
                .queryParam("page", "")
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .body("data[1].id", equalTo(10760))
                .body("data[1].description", equalTo("testDescr"))
                .statusCode(200);
    }
    @Test
    void getMyPostNoOrderTest() {
        given()
                .queryParam("sort", "createdAt")
                .queryParam("page", "1")
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .body("data[1].id", equalTo(10760))
                .body("data[1].description", equalTo("testDescr"))
                .statusCode(200);
    }
    @Test
    void getMyPostNoExistPageTest() {
        given()
                .queryParam("sort", "createdAt")
                .queryParam("order", "DESC")
                .queryParam("page", "100000")
                .headers("X-Auth-Token", myToken)
                .when()
                .get(getBaseUrl() + "/api/posts")
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .body("data[1].id", equalTo(null))
                .statusCode(200);
    }
}
