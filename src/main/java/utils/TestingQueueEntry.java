package utils;

public class TestingQueueEntry {

    private String name;
    private Long startTime;
    private Long duration;
    private Long currentTime;

    public TestingQueueEntry(String name, Long startTime,  Long duration) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.currentTime = -1L;
    }

    public TestingQueueEntry(String name, Long startTime,  Long duration, Long currentTime) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.currentTime = currentTime;
    }

    public String getName() {
        return name;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public TestingQueueEntry getEnrtyWithCurrentTime() {
        return new TestingQueueEntry(name, startTime, duration, System.currentTimeMillis());
    }
}
