package database;

import database.object.representations.PlotPointDB;
import database.object.representations.SingleColumnDB;
import database.object.representations.TestDB;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

public class MyDAO {

    private static volatile MyDAO sharedInstance;

    private Sql2o sql2o;

    private boolean failure;

    private MyDAO() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String dbName = System.getProperty("RDS_DB_NAME");
        String userName = System.getProperty("RDS_USERNAME");
        String password = System.getProperty("RDS_PASSWORD");
        String hostname = System.getProperty("RDS_HOSTNAME");
        String port = System.getProperty("RDS_PORT");
        String url = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName;
        sql2o = new Sql2o(url, userName, password);
        createTablesIfNotExist();
    }

    private void createTablesIfNotExist() {
        failure = false;
        String createSessionsTableSql = "CREATE TABLE IF NOT EXISTS tests (" +
                "id int NOT NULL," +
                "leftPoint int NOT NULL," +
                "rightPoint int NOT NULL," +
                "measureType int NOT NULL," +
                "methodName varchar(20) NOT NULL," +
                "PRIMARY KEY (id));";
        String createRecordsTableSql = "CREATE TABLE IF NOT EXISTS points (" +
                "id int NOT NULL," +
                "fullTime real NOT NULL," +
                "processingTime real NOT NULL," +
                "networkTime real NOT NULL," +
                "PRIMARY KEY (id));";
        createTable(createSessionsTableSql);
        createTable(createRecordsTableSql);
    }

    public List<PlotPointDB> getPointWithIdsInRange(Integer minimalId, Integer maximalId) {
        String sql = "SELECT id, fullTime, processingTime, networkTime " +
                "FROM points " +
                "WHERE id >= :minId AND id <= :maxId;";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("minId", minimalId)
                    .addParameter("maxId", maximalId)
                    .executeAndFetch(PlotPointDB.class);
        } catch (Exception e) {
            System.out.println("GET POINT IN RANGE ERROR!");
            System.out.println(e.getMessage());
            e.printStackTrace();
            failure = true;
        } finally {
            return null;
        }
    }

    public TestDB getTestWhereIdEqualsTo(Integer targetId) {
        String sql = "SELECT id, leftPoint, rightPoint, measureType, methodName " +
                "FROM tests " +
                "WHERE id = :targetId;";
        List<TestDB> queryResultList = null;
        try (Connection connection = sql2o.open()) {
            queryResultList = connection.createQuery(sql)
                    .addParameter("targetId", targetId)
                    .executeAndFetch(TestDB.class);
        } catch (Exception e) {
            System.out.println("GET TEST WITH TARGET ID ERROR!");
            System.out.println(e.getMessage());
            e.printStackTrace();
            failure = true;
        } finally {
            if (queryResultList != null && queryResultList.size() == 1) {
                return queryResultList.get(0);
            } else {
                return null;
            }
        }
    }

    public void clearTable(String tableName) {
        String sql = "DELETE FROM " + tableName + ";";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql).executeUpdate();
        } catch (Exception e) {
            System.out.println("GET ERROR WHEN TRIED TO DELETE TABLE '" + tableName + "'!");
            System.out.println(e.getMessage());
            e.printStackTrace();
            failure = true;
        }
    }

    private Integer getSingleColumnValue(String sql) {
        SingleColumnDB singleColumnDB = null;
        try (Connection connection = sql2o.open()) {
             singleColumnDB = connection.createQuery(sql).executeAndFetchFirst(SingleColumnDB.class);
        } catch (Exception e) {
            System.out.println("ERROR WHILE GETTING " + sql + " IN GET SINGLE COLUMN DB VALUE!");
            System.out.println(e.getMessage());
            e.printStackTrace();
            failure = true;
        }
        if (singleColumnDB == null) {
            return 0;
        } else {
            return singleColumnDB.getSingleField();
        }
    }

    public void addPointsForTest(List<PlotPointDB> points, TestDB test) {

        System.out.println("I AM HERE!");

        String addPointsSql = "INSERT INTO points(id, fullTime, processingTime, networkTime) " +
                "values (:idParam, :fullTimeParam, :processingTimeParam, :networkTimeParam);";
        String selectMaxIdSqlFromPoints = "SELECT coalesce(MAX(id), 0) AS singleField FROM points;";

        int lastId = 0;
        Integer maxId = getSingleColumnValue(selectMaxIdSqlFromPoints);
        if (maxId != null) {
            lastId = maxId.intValue();
        }

        int minimalId = lastId + 1;

        for (int i = 0; i < points.size(); i++) {
            PlotPointDB point = points.get(i);
            try (Connection connection = sql2o.open()) {
                lastId++;
                connection.createQuery(addPointsSql)
                        .addParameter("idParam", lastId)
                        .addParameter("fullTimeParam", point.getFullTime())
                        .addParameter("processingTimeParam", point.getProcessingTime())
                        .addParameter("networkTimeParam", point.getNetworkTime())
                        .executeUpdate();
                System.out.println("HEY! ADDED NEW POINT WITH ID = " + lastId);
            } catch (Exception e) {
                System.out.println("ADD POINTS FOR TEST, GOT ERROR WHILE ADDING POINT!");
                System.out.println(e.getMessage());
                e.printStackTrace();
                failure = true;
            }
        }

        int maximalId = lastId;

        String selectMaxIdSqlFromTests = "SELECT coalesce(MAX(id), 0) AS singleField FROM tests;";

        int lastTestId = 0;
        Integer maxTestId = getSingleColumnValue(selectMaxIdSqlFromTests);
        if (maxTestId != null) {
            lastTestId = maxTestId.intValue();
        }

        String addTestSql = "INSERT INTO tests(id, leftPoint, rightPoint, measureType, methodName) " +
                "values (:idParam, :leftPointParam, :rightPointParam, :measureTypeParam, :methodNameParam);";

        test.setLeftPoint(minimalId);
        test.setRightPoint(maximalId);

        try (Connection connection = sql2o.open()) {
            connection.createQuery(addTestSql)
                    .addParameter("idParam", lastTestId + 1)
                    .addParameter("leftPointParam", test.getLeftPoint())
                    .addParameter("rightPointParam", test.getRightPoint())
                    .addParameter("measureTypeParam", test.getMeasureType())
                    .addParameter("methodNameParam", test.getMethodName())
                    .executeUpdate();
            System.out.println("YO! ADDED NEW TEST WITH ID = " + (lastTestId + 1) + " left: " + test.getLeftPoint()
                    + " right: " + test.getRightPoint() + " type: " + test.getMeasureType());
        } catch (Exception e) {
            System.out.println("ERROR HAPPENED WHILE ADDING NEW TEST INTO tests!");
            System.out.println(e.getMessage());
            e.printStackTrace();
            failure = true;
        }

    }

    public List<TestDB> getAllTests() {
        List<TestDB> result = null;
        String sql = "SELECT * FROM tests;";
        try (Connection connection = sql2o.open()) {
            result = connection.createQuery(sql).executeAndFetch(TestDB.class);
        }
        if (result == null) {
            System.out.println("GOT NULL WHEN GETTING TESTS!!!!");
            result = new ArrayList<>();
        }
        return result;
    }

    public List<PlotPointDB> getAllPoints() {
        List<PlotPointDB> result = null;
        String sql = "SELECT * FROM points;";
        try (Connection connection = sql2o.open()) {
            result = connection.createQuery(sql).executeAndFetch(PlotPointDB.class);
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    private void createTable(String sql) {
        try (Connection connection = sql2o.beginTransaction()) {
            connection.createQuery(sql).executeUpdate();
            connection.commit();
        } catch (Exception e) {
            failure = true;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }



    public boolean hasFailed() {
        return failure;
    }

    public static MyDAO getSharedInstance() {
        if (sharedInstance == null) {
            synchronized (MyDAO.class) {
                sharedInstance = new MyDAO();
            }
        }
        return sharedInstance;
    }

}
