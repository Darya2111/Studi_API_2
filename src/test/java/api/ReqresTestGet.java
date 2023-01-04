package api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTestGet {
    private final static String URL = "https://reqres.in/";
    // get запрос, без спецификации
    @JsonCreator
    @JsonProperty
    @Test
    public void checkAvatarAndIdTest(){
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL+"api/users?page=2")
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
