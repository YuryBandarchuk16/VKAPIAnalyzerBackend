package vk.api;


import java.util.ArrayList;
import java.util.List;

public class MethodsSingleton  {

    private List<APIMethod> methods;

    {
        methods = new ArrayList<>();
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

}
