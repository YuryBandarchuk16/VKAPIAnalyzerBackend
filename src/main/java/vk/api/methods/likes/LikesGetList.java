package vk.api.methods.likes;

import utils.ThreadPool;
import vk.api.APIMethod;

public class LikesGetList extends APIMethod {

    private static final String NAME = "likes.getList";

    public LikesGetList() {
        super(NAME);

        addHeader("type", "photo");
        addHeader("owner_id", "1");
        addHeader("item_id", "456264771");
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}