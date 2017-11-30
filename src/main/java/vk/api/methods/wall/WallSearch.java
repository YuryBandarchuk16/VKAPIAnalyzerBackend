package vk.api.methods.wall;

import utils.ThreadPool;
import vk.api.APIMethod;

public class WallSearch extends APIMethod {

    private static final String NAME = "wall.search";

    public WallSearch() {
        super(NAME);

        addHeader("query", "пр");
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}