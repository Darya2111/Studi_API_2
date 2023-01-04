package api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTestGetNew {
    private final static String URL = "https://reqres.in/";
    // get запрос, со спецификацией
    @JsonCreator
    @JsonProperty
    @Test
    public void checkAvatarAndIdTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200OK());
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        // сравниваем, что наш id совпадает с автаром
        //users.forEach(x->Assertions.assertTrue(x.getAvatar().contains(x.getId().toString())));
        // сравниваем, что email оканчивается на reqres.in
        //Assertions.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        // проверяем аватары и id
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> id = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());

        // теперь делаем проверку. По очереди достаем аватары и id
        for (int i = 0; i<avatars.size(); i++){
            Assertions.assertTrue(avatars.get(i).contains(id.get(i)));
        }

    }

}
