package pojo;

public class MakeOrderResponse {
    private String name;
    private OrderNumber order;
    private boolean success;
    private String message;

    public String getName() {
        return name;
    }

    public OrderNumber getOrder() {
        return order;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
