package database.object.representations;

import vk.api.test.TestResult;

public class PlotPointDB {

    private Integer id;
    private Double fullTime;
    private Double processingTime;
    private Double networkTime;

    public PlotPointDB(TestResult t) {
        this.id = -1;
        this.fullTime = t.getFullTime();
        this.processingTime = t.getProcessingTime();
        this.networkTime = t.getNetworkTime();
    }


    public Double getNetworkTime() {
        return networkTime;
    }

    public void setNetworkTime(Double networkTime) {
        this.networkTime = networkTime;
    }

    public Double getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Double processingTime) {
        this.processingTime = processingTime;
    }

    public Double getFullTime() {
        return fullTime;
    }

    public void setFullTime(Double fullTime) {
        this.fullTime = fullTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
