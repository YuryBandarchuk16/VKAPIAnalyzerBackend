package vk.api.methods.friends;

import utils.ThreadPool;
import vk.api.APIMethod;

public class FriendsGet extends APIMethod {

    private static final String NAME = "friends.get";

    public FriendsGet() {
        super(NAME);
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}
