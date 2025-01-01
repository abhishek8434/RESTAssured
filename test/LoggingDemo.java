package test;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class LoggingDemo {
	
			@Test(priority=1)
			void testLogs() {
		
			given()
		
		
			.when()
				.get("https://reqres.in/api/users?page=2")
		
			.then()
				//.log().body();
				//.log().cookies();
				//.log().headers()
				.log().all();
			}

}
