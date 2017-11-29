package vk.api.methods;

import utils.ThreadPool;
import vk.api.APIMethod;

import java.util.Random;

public class UsersGet extends APIMethod {

    private static final String SEPARATOR = ",";
    private static final String NAME = "users.get";

    private static final int RANDOM_BOUND = 35000000;
    private static final int USERS_AMOUNT_TO_TEST = 1;

    private final String userIds;

    public UsersGet() {
        super(NAME);

        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < USERS_AMOUNT_TO_TEST; i++) {
            stringBuilder.append(random.nextInt(RANDOM_BOUND) + 1);
            if (i + 1 != USERS_AMOUNT_TO_TEST) {
                stringBuilder.append(SEPARATOR);
            }
        }
        userIds = stringBuilder.toString();
        addHeader("user_ids", userIds);
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}
