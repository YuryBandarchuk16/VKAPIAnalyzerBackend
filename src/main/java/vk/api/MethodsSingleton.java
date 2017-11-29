package vk.api;


import vk.api.methods.UsersGet;

import java.util.ArrayList;
import java.util.List;

public class MethodsSingleton  {

    private static volatile MethodsSingleton sharedInstsance;

    private MethodsSingleton() {
    }

    public static MethodsSingleton getSharedInstsance() {
        if (sharedInstsance == null) {
            synchronized (MethodsSingleton.class) {
                sharedInstsance = new MethodsSingleton();
            }
        }
        return sharedInstsance;
    }

    private List<APIMethodTestable> methods;


    {
        methods = new ArrayList<>();
        methods.add(new UsersGet());
        /*methods.put(0, "users.get");
        methods.put(1, "users.getFollowers");
        methods.put(2, "friends.get");
        methods.put(3, "groups.getById");
        methods.put(4, "wall.get");
        methods.put(5, "wall.search");
        methods.put(6, "database.getCities");
        methods.put(7, "likes.getList");
        methods.put(8, "board.getTopics");
        methods.put(9, "utils.getServerTime");
        */
    }


    public List<APIMethodTestable> getMethods() {
        return methods;
    }
}
