package pojo;

public class RegisterResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
