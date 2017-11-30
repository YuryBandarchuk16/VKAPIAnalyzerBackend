package vk.api.methods.groups;

import utils.ThreadPool;
import vk.api.APIMethod;

import java.util.Random;

public class GroupsGetById extends APIMethod {

    private static final String SEPARATOR = ",";
    private static final String NAME = "groups.getById";

    private static final int RANDOM_BOUND = 500000000;
    private static final int GROUPS_AMOUNT_TO_TEST = 20;

    private final String userIds;

    public GroupsGetById() {
        super(NAME);

        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < GROUPS_AMOUNT_TO_TEST; i++) {
            stringBuilder.append(random.nextInt(RANDOM_BOUND) + 1);
            if (i + 1 != GROUPS_AMOUNT_TO_TEST) {
                stringBuilder.append(SEPARATOR);
            }
        }
        userIds = stringBuilder.toString();
        addHeader("group_ids", userIds);
    }

    @Override
    protected void prepareAndMakeRequest() {
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}

