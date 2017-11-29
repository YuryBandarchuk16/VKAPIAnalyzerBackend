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

    private String name;
    private Map<String, String> headers;
    private List<TestResult> results;
    private AtomicLong timeBeforeLastRequest;

    public APIMethod(String name) {
        this.name = name;
        this.headers = new HashMap<>();
        this.timeBeforeLastRequest = new AtomicLong(0L);
    }

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

        // The URL of the current request could be tracked here

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

            /*** Calculating all required time statistics ***/
            Double processingTime = Double.parseDouble(response.headers().
                    toMultimap().
                    get(Constants.PROCESSING_TIME_HEADER_RESPONSE).
                    get(0));
            long fullTimeMillis = System.currentTimeMillis() - timeBeforeLastRequest.get();
            Double fullTime = 1.0 * fullTimeMillis / 1000.0;
            Double networkTime = fullTime - processingTime;
            TestResult currentResult = new TestResult.Builder()
                    .setFullTime(fullTime)
                    .setNetworkTime(networkTime)
                    .setProcessingTime(processingTime)
                    .build();

            synchronized (results) {
                results.add(currentResult);
            }
        } catch (Exception ignored) {
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
        for (TestResult result: results) {
            System.out.println(result.toString());
        }
    }

}