package vk.api.methods.wall;


import utils.ThreadPool;
import vk.api.APIMethod;

public class WallGet extends APIMethod {

    private static final String NAME = "wall.get";

    public WallGet() {
        super(NAME);
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}
