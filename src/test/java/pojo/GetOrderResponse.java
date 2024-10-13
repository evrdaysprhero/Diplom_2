package pojo;

import java.util.List;

public class GetOrderResponse {
    private boolean success;
    private List<Orders> orders;
    private int total;
    private int totalToday;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalToday() {
        return totalToday;
    }

    public String getMessage() {
        return message;
    }
}
