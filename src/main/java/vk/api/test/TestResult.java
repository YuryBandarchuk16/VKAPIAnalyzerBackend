package vk.api.test;

import java.io.Serializable;

public class TestResult implements Serializable {

    private final Double fullTime;
    private final Double processingTime;
    private final Double networkTime;

    @Deprecated
    public TestResult(Double fullTime, Double processingTime, Double networkTime) {
        this.fullTime = fullTime;
        this.processingTime = processingTime;
        this.networkTime = networkTime;
    }

    @Override
    public String toString() {
        return "[FullTime = " + fullTime
                + ", ProcessingTime = "
                + processingTime + ", NetworkTime = "
                + networkTime + "]";
    }

    public Double getFullTime() {
        return fullTime;
    }

    public Double getProcessingTime() {
        return processingTime;
    }

    public Double getNetworkTime() {
        return networkTime;
    }

    public static class Builder {

        private Double fullTime;
        private Double processingTime;
        private Double networkTime;

        public TestResult build() {
            return new TestResult(fullTime, processingTime, networkTime);
        }

        public Builder setFullTime(Double fullTime) {
            this.fullTime = fullTime;
            return this;
        }

        public Builder setProcessingTime(Double processingTime) {
            this.processingTime = processingTime;
            return this;
        }

        public Builder setNetworkTime(Double networkTime) {
            this.networkTime = networkTime;
            return this;
        }

    }

}
