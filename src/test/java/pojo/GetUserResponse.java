package pojo;

public class GetUserResponse {
    private boolean success;
    private User user;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
