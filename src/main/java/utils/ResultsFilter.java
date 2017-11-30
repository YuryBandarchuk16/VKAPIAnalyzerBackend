package utils;

import database.object.representations.PlotPointDB;
import vk.api.test.TestResult;

import java.util.*;
import java.util.stream.Collectors;

public class ResultsFilter {

    private static final int MAX_POINTS_ON_GRAPH = 300;
    private static final int NOISE_POINTS = 20;

    public static List<PlotPointDB> removeNoise(List<TestResult> results) {
        int amount = results.size();
        int oneBlockSize = Math.max(1, (amount / MAX_POINTS_ON_GRAPH));
        List<PlotPointDB> answer = new ArrayList<>();
        List<TestResult> currentBlock = new ArrayList<>();
        Iterator<TestResult> iterator = results.iterator();
        while (iterator.hasNext()) {
            if (currentBlock.size() == oneBlockSize) {
                answer.add(filterBlock(currentBlock));
                currentBlock = new ArrayList<>();
            }
            currentBlock.add(iterator.next());
        }
        if (currentBlock.size() > 0) {
            answer.add(filterBlock(currentBlock));
        }
        return answer;
    }

    private static PlotPointDB filterBlock(List<TestResult> block) {
        block.sort(Comparator.comparing(TestResult::getProcessingTime));
        List<TestResult> updatedBlock = block.stream().limit(Math.max(2, block.size() - NOISE_POINTS)).collect(Collectors.toList());

        double fullTime = 0.0;
        double processingTime = 0.0;
        double networkTime = 0.0;

        for (TestResult t: updatedBlock) {
            fullTime += t.getFullTime();
            processingTime += t.getProcessingTime();
            networkTime += t.getNetworkTime();
        }

        double count = 1.0 * updatedBlock.size();

        return new PlotPointDB(new TestResult.Builder()
                .setFullTime(fullTime / count)
                .setNetworkTime(networkTime / count)
                .setProcessingTime(processingTime / count)
                .build());
    }
}
