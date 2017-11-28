import spark.servlet.SparkApplication;
import vk.api.methods.UsersGet;

import static spark.Spark.get;

public class HelloWorld implements SparkApplication {

	public static void main(String[] args) {
		new HelloWorld().init();
	}

	@Override
	public void init() {

		get("/hello", (req, res) -> {
			new UsersGet().test(5000L);
			return "Hello VK!";
		});
	}
}
