package pojo;

import java.util.List;

public class MakeOrderRequest {
    private List<String> ingredients;

    public MakeOrderRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
