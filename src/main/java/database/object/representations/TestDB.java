package database.object.representations;

public class TestDB {

    private Integer id;
    private Integer leftPoint;
    private Integer rightPoint;
    private Integer measureType;
    private String methodName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLeftPoint() {
        return leftPoint;
    }

    public void setLeftPoint(Integer leftPoint) {
        this.leftPoint = leftPoint;
    }

    public Integer getRightPoint() {
        return rightPoint;
    }

    public void setRightPoint(Integer rightPoint) {
        this.rightPoint = rightPoint;
    }

    public Integer getMeasureType() {
        return measureType;
    }

    public void setMeasureType(Integer measureType) {
        this.measureType = measureType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
