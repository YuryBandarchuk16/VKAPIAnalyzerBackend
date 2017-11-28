package vk.api.test;

public class TestResult {

    private final Integer fullTime;
    private final Integer processingTime;
    private final Integer networkTime;

    @Deprecated
    public TestResult(Integer fullTime, Integer processingTime, Integer networkTime) {
        this.fullTime = fullTime;
        this.processingTime = processingTime;
        this.networkTime = networkTime;
    }

    public static class Builder {

        private Integer fullTime;
        private Integer processingTime;
        private Integer networkTime;

        public TestResult build() {
            return new TestResult(fullTime, processingTime, networkTime);
        }

        public Builder setFullTime(Integer fullTime) {
            this.fullTime = fullTime;
            return this;
        }

        public Builder setProcessingTime(Integer processingTime) {
            this.processingTime = processingTime;
            return this;
        }

        public Builder setNetworkTime(Integer networkTime) {
            this.networkTime = networkTime;
            return this;
        }

    }

}
