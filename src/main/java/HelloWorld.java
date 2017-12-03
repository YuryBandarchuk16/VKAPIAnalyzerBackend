import com.google.gson.Gson;
import database.MyDAO;
import database.object.representations.PlotPointDB;
import database.object.representations.TestDB;

import spark.servlet.SparkApplication;
import utils.ThreadPool;
import vk.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.TestingQueueEntry;

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

		List<Class<? extends APIMethod>> methodsClasses = MethodsSingleton.getSharedInstance().getMethods();
		List<APIMethodTestable> methods = new ArrayList<>();
		for (Class<? extends  APIMethod> someMethodClass: methodsClasses) {
			try {
				methods.add(someMethodClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				System.out.println("ERROR WHILE DOING CASTS WITH REFLECTION API!");
				e.printStackTrace();
			}
		}

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

		get("/queue", (req, res) -> {
			List<TestingQueueEntry> queueEntries = MethodsSingleton.getSharedInstance()
				.getQueue()
				.stream()
				.map(TestingQueueEntry::getEnrtyWithCurrentTime)
				.collect(Collectors.toList());
			return new Gson().toJson(queueEntries);
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
			APIMethodTestable methodToRun = null;
			int methodIndex = -1;
			for (int i = 0; i < methods.size(); i++) {
				APIMethodTestable method = methods.get(i);
				if (method.getName().equals(methodName)) {
					methodIndex = i;
					methodToRun = method;
					break;
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
			final APIMethodTestable finalMethod = methodsClasses.get(methodIndex).newInstance();
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
