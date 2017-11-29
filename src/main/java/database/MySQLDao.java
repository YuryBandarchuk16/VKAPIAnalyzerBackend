package database;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class MySQLDao {

    private static volatile MySQLDao sharedInstance;

    private Sql2o sql2o;

    private boolean failure;

    private MySQLDao() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String dbName = System.getProperty("RDS_DB_NAME");
        String userName = System.getProperty("RDS_USERNAME");
        String password = System.getProperty("RDS_PASSWORD");
        String hostname = System.getProperty("RDS_HOSTNAME");
        String port = System.getProperty("RDS_PORT");
        String url = "jdbc:mysql://" + hostname + ":" +
                port + "/" + dbName;
        sql2o = new Sql2o(url, userName, password);
        createTablesIfNotExist();
    }

    private void createTablesIfNotExist() {
        failure = false;
        String createSessionsTableSql = "CREATE TABLE IF NOT EXISTS tests (" +
                "id int NOT NULL AUTO_INCREMENT," +
                "leftPoint int NOT NULL," +
                "rightPoint int NOT NULL," +
                "measureType int NOT NULL," +
                "PRIMARY KEY (id));";
        String createRecordsTableSql = "CREATE TABLE IF NOT EXISTS points (" +
                "id int NOT NULL AUTO_INCREMENT," +
                "fullTime double NOT NULL," +
                "processingTime double NOT NULL," +
                "networkTime double NOT NULL," +
                "PRIMARY KEY (id));";
        createTable(createSessionsTableSql);
        createTable(createRecordsTableSql);
    }

    public List<PlotPointDB> getPointWithIdsInRange(Integer minimalId, Integer maximalId) {
        String sql = "SELECT id, fullTime, processingTime, networkTime " +
                "FROM points " +
                "WHERE id >= :minId AND id <= :maxId";
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
        String sql = "SELECT id, leftPoint, rightPoint, measureType " +
                "FROM tests " +
                "WHERE id = :targetId";
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

    public static MySQLDao getSharedInstance() {
        if (sharedInstance == null) {
            synchronized (MySQLDao.class) {
                sharedInstance = new MySQLDao();
            }
        }
        return sharedInstance;
    }

}
