package test;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;


public class PathAndQueryParameter {
	
	@Test
	void testQueryandPathParameter() {
		
		
		//https://reqres.in/api/users?page=2&id=5
		given()
			.pathParam("mypath1", "api") //Path Parameter
			.pathParam("mypath2", "users") //Path Parameter
			.queryParam("page", 2) //Query Parameter
			.queryParam("id", 5) //Query Parameter
		
		
		.when()
			.get("https://reqres.in/{mypath1}/{mypath2}")
		
		
		.then()
			.statusCode(200)
			.log().all();
	}

}
