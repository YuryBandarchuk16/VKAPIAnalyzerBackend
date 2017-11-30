package vk.api;


import okhttp3.OkHttpClient;
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
import java.util.List;

public class MethodsSingleton  {

    private static volatile MethodsSingleton sharedInstance;

    private MethodsSingleton() {
        client = new OkHttpClient();
    }

    public static MethodsSingleton getSharedInstance() {
        if (sharedInstance == null) {
            synchronized (MethodsSingleton.class) {
                sharedInstance = new MethodsSingleton();
            }
        }
        return sharedInstance;
    }

    private List<APIMethodTestable> methods;
    private OkHttpClient client;


    {
        methods = new ArrayList<>();

        methods.add(new UsersGet());
        methods.add(new UserGetFollowers());
        methods.add(new FriendsGet());
        methods.add(new GroupsGetById());
        methods.add(new WallGet());
        methods.add(new WallSearch());
        methods.add(new DatabaseGetCities());
        methods.add(new LikesGetList());
        methods.add(new BoardGetTopics());
        methods.add(new UtilsGetServerTime());
    }


    public List<APIMethodTestable> getMethods() {
        return methods;
    }

    public OkHttpClient getClient() {
        return client;
    }
}
