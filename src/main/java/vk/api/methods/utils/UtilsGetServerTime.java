package vk.api.methods.utils;

import utils.ThreadPool;
import vk.api.APIMethod;

public class UtilsGetServerTime extends APIMethod {

    private static final String NAME = "utils.getServerTime";

    public UtilsGetServerTime() {
        super(NAME);
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}
