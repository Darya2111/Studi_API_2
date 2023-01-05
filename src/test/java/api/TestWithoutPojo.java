package api;

import api.spec.Specifications;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.restassured.path.json.JsonPath;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestWithoutPojo {
    private final static String URL = "https://reqres.in/";
    @JsonCreator
    @JsonProperty
    // get. получить список пользователей и проверить, что аватар содержит id
    @Test
    public void checkAvatarsNoPojo(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200OK());
        // первый способ. Общий
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        // собираем в отдельные списки email и id
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");

        //проверяем, что в наших аватарах содержатся id
        for(int i =0; i<avatars.size(); i++){
            Assertions.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }

        // проверяем, что почта заканчивается на reqres.in
        Assertions.assertTrue(emails.stream().allMatch(x->x.endsWith("@reqres.in")));
    }

    // post. успешная регистрация
    @Test
    public void successRegNoPojo(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200OK());
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        // если через response, то убираем body внизу, добавляем extract и JsonPath
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
         //       .body("id", is(4))
         //       .body("token", is("QpwL5tke4Pnpja7X4"))
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        // подостаем переменные
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        Assertions.assertEquals(4, id);
        Assertions.assertEquals("QpwL5tke4Pnpja7X4", token);
    }

    // post. успешная регистрация
    @Test
    public void unSuccessRegNoPojo_1(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@fife");
        given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .body("error", is("Missing password"));

    }

    @Test
    public void unSuccessRegNoPojo_2(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@fife");
        Response response = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String error = jsonPath.get("error");
        Assertions.assertEquals("Missing password", error);
    }
}
