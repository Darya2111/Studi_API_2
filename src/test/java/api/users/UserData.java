package api.users;


// с сайта https://json2csharp.com/code-converters/json-to-pojo   перевели Json в Pojo
// Эта штука нужна, чтобы красиво выводило полученную информацию
public class UserData {
    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;

    // создали конструктор со веми переменными
    public UserData(Integer id, String email, String first_name, String last_name, String avatar) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.avatar = avatar;
    }


   // создали пустой конструктор
    public UserData() {}

    public Integer getId() {
        return id;
    }

    // добавили getter всех переменных
    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getAvatar() {
        return avatar;
    }

}
