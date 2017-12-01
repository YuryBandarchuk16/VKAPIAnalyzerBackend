import com.google.gson.Gson;
import database.MyDAO;
import database.object.representations.PlotPointDB;
import database.object.representations.TestDB;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;
import utils.ThreadPool;
import vk.api.APIMethodTestable;
import vk.api.Constants;
import vk.api.CorsFilter;
import vk.api.MethodsSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.halt;

public class HelloWorld implements SparkApplication {

	public static void main(String[] args) {
		new HelloWorld().init();
	}

	private MyDAO sqlDao;

	@Override
	public void init() {

		CorsFilter.apply();

		List<APIMethodTestable> methods = MethodsSingleton.getSharedInstance().getMethods();

		sqlDao = MyDAO.getSharedInstance();

		get("/test", (req, res) -> {
			final String response;
			if (sqlDao.hasFailed()) {
				response = "FAIL! :(";
			} else {
				response = "Everything is OK!";
			}
			return response;
		});

		get("/getPlot/:id", (req, res) -> {
			Map<String, String> params = req.params();
			String idValue = params.get(":id");
			Integer id = Integer.parseInt(idValue);
			TestDB testObject = sqlDao.getTestWhereIdEqualsTo(id);
			if (testObject == null) {
				return "[]";
			} else {
				List<PlotPointDB> points = sqlDao.getPointWithIdsInRange(testObject.getLeftPoint(), testObject.getRightPoint());
				if (points == null) {
					return "[]";
				} else {
					return new Gson().toJson(points);
				}
			}
		});


		/* Method for debug purpose only!!! */
		get("/points", (req, res) -> new Gson().toJson(sqlDao.getAllPoints()));

		/* Method for debug purpose only!!! */
		get("/tests", (req, res) -> new Gson().toJson(sqlDao.getAllTests()));

		/* Clears 'points' table */
		get("/clear/points", (req, res) -> {
			MyDAO.getSharedInstance().clearTable("points");
			return "[\"ok\"]";
		});

		/* Clears 'tests' table */
		get("/clear/tests", (req, res) -> {
			MyDAO.getSharedInstance().clearTable("tests");
			return "[\"ok\"]";
		});

		// type == 0 --> minute
		// type == 1 --> hour
		// type == 2 --> day

		get("/testMethod/:name/:type", (req, res) -> {
			Map<String, String> params = req.params();
			String methodName = params.get(":name");
			System.out.println(methodName);
			APIMethodTestable methodToRun = null;
			for (APIMethodTestable method: methods) {
				if (method.getName().equals(methodName)) {
					methodToRun = method;
				}
			}
			if (methodToRun == null) {
				halt(401);
			}
			String typeString = params.get(":type");
			Long duration = Constants.ONE_MINUTE_DURATION;
			if (typeString != null) {
				Integer type = Integer.parseInt(typeString);
				if (type.intValue() == 1) {
					duration = Constants.ONE_HOUR_DURATION;
				} else if (type.intValue() == 2) {
					duration = Constants.ONE_DAY_DURATION;
				}
			}
			final APIMethodTestable finalMethod = methodToRun;
			final Long finalDuration = duration;
			ThreadPool.getSharedInstance().addTask(() -> finalMethod.test(finalDuration));
			return "[\"ok\"]";
		});

		get("/methods", (req, res) -> {
			List<String> result = new ArrayList<>();
			for (APIMethodTestable method: methods) {
				result.add(method.getName());
			}
			return new Gson().toJson(result);
		});
	}
}
