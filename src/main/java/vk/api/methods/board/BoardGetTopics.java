package vk.api.methods.board;

import utils.ThreadPool;
import vk.api.APIMethod;

public class BoardGetTopics extends APIMethod {

    private static final String NAME = "board.getTopics";

    public BoardGetTopics() {
        super(NAME);

        addHeader("group_id", "22558194");
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}
