package api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTestNew {
    private final static String URL = "https://reqres.in/";
    // get запрос, со спецификацией. Получаем список пользователей
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
    // Post запросы. Успешная регистрация
    // не работает((
    @Test
    public void successRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200OK());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());

        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());

    }
    // post. неуспешная регистрация
    @Test
    public void unSuccessRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Register user = new Register("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assertions.assertEquals("Missing password", unSuccessReg.getError());
    }

    //get. Сортировка лет по возрастанию
    @Test
    public void sortedYearsTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200OK());
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);
        // получаем список годов
         List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
         // сортируем этот список по возрастанию
         List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        // сравниваем ожидаемый результат с актуальным
        Assertions.assertEquals(sortedYears, years);
        // выведем на консоль что получилось
        System.out.println(years);
        System.out.println(sortedYears);
    }

    // delete. удаляем 2го пользователя и сравниваем статус-код
    @Test
    public void deleteUserTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    //  put. обновляем информацию о пользователе и сравниваем дату обновления с текущей на машине
    @Test
    public void timeTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200OK());
        // создаем юзера и получаем инфу
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        // с помощью регулярных выражений убираем "лишние 5" символы с помощью сайта https://regex101.com/
        String regex = "(.{5})$";

        // получаем время как в ответе на сайте (2023-01-04T21:21:13.428Z), но только последние 5 символов
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");

        //выведем текущее время
        System.out.println(currentTime);

        // сравниваем текущее время с ответом сервера
        Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));

        // выведем полученное время
        System.out.println(response.getUpdatedAt().replaceAll(regex, ""));
    }
}
