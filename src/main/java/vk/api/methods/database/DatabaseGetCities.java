package vk.api.methods.database;

import utils.ThreadPool;
import vk.api.APIMethod;

import java.util.HashMap;
import java.util.Random;

public class DatabaseGetCities extends APIMethod {

    private static final String NAME = "database.getCities";

    private static final int COUNTRY_ID_BOUND = 224;

    private Random random;

    public DatabaseGetCities() {
        super(NAME);

        random = new Random();
    }

    @Override
    protected void prepareAndMakeRequest() {
        headers = new HashMap<>();
        addHeader("country_id", "" + (random.nextInt(COUNTRY_ID_BOUND) + 1));
        ThreadPool.getSharedInstance().addTask(this::sendRequest);
    }
}
