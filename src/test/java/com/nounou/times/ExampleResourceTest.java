package com.nounou.times;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HealthCheckTest {

    @Test
    void testNounouEndpointIsAvailable() {
        given()
                .when().get("/api/nounous")
                .then()
                .statusCode(200);
    }
}
