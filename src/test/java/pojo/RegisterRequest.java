package pojo;

public class RegisterRequest {
    private String name;
    private String password;
    private String email;

    public RegisterRequest(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public RegisterRequest(){}
}
