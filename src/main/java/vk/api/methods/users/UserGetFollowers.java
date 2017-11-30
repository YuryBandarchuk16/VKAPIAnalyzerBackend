package vk.api.methods.users;

import utils.ThreadPool;
import vk.api.APIMethod;

public class UserGetFollowers extends APIMethod {

    private static final String NAME = "users.getFollowers";

    public UserGetFollowers() {
        super(NAME);
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}
