package vk.api;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import vk.api.test.TestResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class APIMethod implements APIMethodTestable {

    /*

    // Will be sent only in case isReady() == false
    void sendRequest() throws IOException;
    boolean wasRequestSent();

    boolean isReady();
    void prepareForNewRequest();

    Double getFullTime();
    Double getProcessingTime();
    Double getNetworkTime();

    Gson getResponse();

    void addHeader(String name, String value);
     */

    private String name;
    private Map<String, String> headers;
    private List<TestResult> results;
    private AtomicLong timeBeforeLastRequest;

    public APIMethod(String name) {
        this.name = name;
        this.headers = new HashMap<>();
        this.timeBeforeLastRequest = new AtomicLong(0L);
    }

    /*private Request buildRequest() {
        Request.Builder requestBuilder = new Request.Builder().url(Constants.VK_API_URL + name + "?");
        for (Map.Entry<String, String> entry: headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        requestBuilder.addHeader(Constants.PROCESSING_TIME_HEADER_REQUEST, "1");
        requestBuilder.addHeader(Constants.ACCESS_TOKEN_HEADER, Constants.ACCESS_TOKEN);
        return requestBuilder.build();
    }*/

    private void addKeyValuePair(StringBuilder builder, String key, String value, String ending) {
        builder.append(key);
        builder.append('=');
        builder.append(value);
        builder.append(ending);
    }

    private Request buildRequest() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Constants.VK_API_URL + name + "?");
        for (Map.Entry<String, String> entry: headers.entrySet()) {
            addKeyValuePair(urlBuilder, entry.getKey(), entry.getValue(), "&");
        }
        //addKeyValuePair(urlBuilder, Constants.PROCESSING_TIME_HEADER_REQUEST, "1", "&");
        addKeyValuePair(urlBuilder, Constants.ACCESS_TOKEN_HEADER, Constants.ACCESS_TOKEN, "&v=V");
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .addHeader(Constants.PROCESSING_TIME_HEADER_REQUEST, "1")
                .build();
        return request;
    }

    public void sendRequest()  {
        OkHttpClient client = new OkHttpClient();
        Request request = buildRequest();
        System.out.println(request.url().toString());
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            response = null;
        }

        if (response == null || response.code() >= 400) {
            synchronized (results) {
                results.notify();
                return;
            }
        }

        Gson gson = new Gson();

        try {
            String responseBody = response.body().string();
            String json = gson.toJson(responseBody);
            System.out.println(json);
            System.out.println(response.headers().toString());
            // do something with time here
            results.add(new TestResult.Builder().build());
        } catch (Exception e) {
            // some clever code to sort out this situation
        } finally {
            synchronized (results) {
                results.notify();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    protected abstract void prepareAndMakeRequest();

    public void test(long duration) {
        results = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        while (true) {
            timeBeforeLastRequest.set(System.currentTimeMillis());
            prepareAndMakeRequest();
            synchronized (results) {
                try {
                    results.wait();
                } catch (InterruptedException e) {
                    break;
                }
            }
            long timePassed = System.currentTimeMillis() - startTime;
            if (timePassed >= duration) {
                break;
            }
        }
        System.out.println(results.size() + " added!");
    }

}
