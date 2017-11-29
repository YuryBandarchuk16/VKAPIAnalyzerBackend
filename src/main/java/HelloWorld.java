import com.google.gson.Gson;
import database.MySQLDao;
import database.PlotPointDB;
import database.TestDB;
import spark.servlet.SparkApplication;
import vk.api.APIMethodTestable;
import vk.api.Constants;
import vk.api.MethodsSingleton;
import vk.api.methods.UsersGet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.halt;

public class HelloWorld implements SparkApplication {

	public static void main(String[] args) {
		new HelloWorld().init();
	}

	private MySQLDao sqlDao;

	@Override
	public void init() {

		List<APIMethodTestable> methods = MethodsSingleton.getSharedInstsance().getMethods();

		sqlDao = MySQLDao.getSharedInstance();

		final String response;

		if (sqlDao.hasFailed()) {
			response = "FAIL! :(";
		} else {
			response = "Everything is OK!";
		}

		get("/test", (req, res) -> response);

		get("/getPlot/:id", (req, res) -> {
			Map<String, String> params = req.params();
			String idValue = params.get("id");
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

		// type == 0 --> minute
		// type == 1 --> hour
		// type == 2 --> day

		get("/testMethod/:name/:type", (req, res) -> {
			Map<String, String> params = req.params();
			String methodName = params.get("name");
			APIMethodTestable methodToRun = null;
			for (APIMethodTestable method: methods) {
				if (method.getName().equals(methodName)) {
					methodToRun = method;
				}
			}
			if (methodToRun == null) {
				halt(401);
			}
			String typeString = params.get("type");
			Long duration = Constants.ONE_MINUTE_DURATION;
			if (typeString != null) {
				Integer type = Integer.parseInt(typeString);
				if (type.intValue() == 1) {
					duration = Constants.ONE_HOUR_DURATION;
				} else if (type.intValue() == 2) {
					duration = Constants.ONE_DAY_DURATION;
				}
			}
			methodToRun.test(duration);
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
