package vk.api;


import okhttp3.OkHttpClient;
import utils.TestingQueueEntry;
import vk.api.methods.board.BoardGetTopics;
import vk.api.methods.database.DatabaseGetCities;
import vk.api.methods.friends.FriendsGet;
import vk.api.methods.groups.GroupsGetById;
import vk.api.methods.likes.LikesGetList;
import vk.api.methods.users.UserGetFollowers;
import vk.api.methods.users.UsersGet;
import vk.api.methods.utils.UtilsGetServerTime;
import vk.api.methods.wall.WallGet;
import vk.api.methods.wall.WallSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodsSingleton  {

    private static volatile MethodsSingleton sharedInstance;


    private final Map<String, TestingQueueEntry> currentQueue;

    private MethodsSingleton() {
        client = new OkHttpClient();
        currentQueue = new HashMap<>();
    }

    public void startTesting(String name, String uid, Long startTime, Long duration) {
        synchronized (currentQueue) {
            currentQueue.put(uid, new TestingQueueEntry(name, startTime, duration));
        }
    }

    public void stopTesting(String uid) {
        synchronized (currentQueue) {
            currentQueue.remove(uid);
        }
    }

    public synchronized List<TestingQueueEntry> getQueue() {
        List<TestingQueueEntry> testsInQueue = new ArrayList<>();
        for (Map.Entry<String, TestingQueueEntry> entry: currentQueue.entrySet()) {
            testsInQueue.add(entry.getValue());
        }
        return testsInQueue;
    }


    public static MethodsSingleton getSharedInstance() {
        if (sharedInstance == null) {
            synchronized (MethodsSingleton.class) {
                sharedInstance = new MethodsSingleton();
            }
        }
        return sharedInstance;
    }

    private List<Class<? extends APIMethod>> methods;
    private OkHttpClient client;


    {
        methods = new ArrayList<>();

        methods.add(UsersGet.class);
        methods.add(UserGetFollowers.class);
        methods.add(FriendsGet.class);
        methods.add(GroupsGetById.class);
        methods.add(WallGet.class);
        methods.add(WallSearch.class);
        methods.add(DatabaseGetCities.class);
        methods.add(LikesGetList.class);
        methods.add(BoardGetTopics.class);
        methods.add(UtilsGetServerTime.class);
    }



    public List<Class<? extends APIMethod>> getMethods() {
        return methods;
    }

    public OkHttpClient getClient() {
        return client;
    }
}
