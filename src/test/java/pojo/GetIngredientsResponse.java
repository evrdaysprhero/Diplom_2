package pojo;

import java.util.List;

public class GetIngredientsResponse {
    private boolean success;
    private List<Ingredients> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Ingredients> getData() {
        return data;
    }
}
