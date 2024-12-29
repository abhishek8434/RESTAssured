package test;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static io.restassured.response.Response.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MultiplewaysToCreate {
	
	//Using Hash Map()
	//@Test(priority=1)
	void testPostUsingHashMap() {
		
		HashMap data = new HashMap();
		String grades[] = {"5.0","4.5","3.6"};
		
		data.put("name", "priyansh");
		data.put("active", "true");
		data.put("grades", grades);
		data.put("yearsOld", "15");
		data.put("color", "pink");
		
		given()
			.contentType("application/json")
			.body(data)
		
		.when()
			.post("http://localhost:3000/students")
		
		.then()
			.statusCode(201)
			.body("name", equalTo("priyansh"))
			.body("active", equalTo("true"))
			.body("grades[0]", equalTo("5.0"))
			.body("grades[1]", equalTo("4.5"))
			.body("grades[2]", equalTo("3.6"))
			.body("yearsOld", equalTo("15"))
			.body("color", equalTo("pink"))
			.header("content-type", "application/json")
			.log().all();
		
		}
	//Delete
	 //@Test(priority=2)
	 void deleteUser() {
	
		 
		 given()
		 	
		 
		 .when()
		 	.put("http://localhost:3000/students/7857")
		 	
		 .then()
		 	.statusCode(200);		 	
	 }
	 
		//Using json.org ()
		//@Test(priority=1)
		void testPostUsingJsonLibrary() {
			
			JSONObject data = new JSONObject();
			String grades[] = {"5.0","4.5","3.6"};
			
			data.put("name", "priyansh1");
			data.put("active", "true");
			data.put("grades", grades);
			data.put("yearsOld", "15");
			data.put("color", "pink");
			
			given()
				.contentType("application/json")
				.body(data.toString())
			
			.when()
				.post("http://localhost:3000/students")
			
			.then()
				.statusCode(201)
				.body("name", equalTo("priyansh1"))
				.body("active", equalTo("true"))
				.body("grades[0]", equalTo("5.0"))
				.body("grades[1]", equalTo("4.5"))
				.body("grades[2]", equalTo("3.6"))
				.body("yearsOld", equalTo("15"))
				.body("color", equalTo("pink"))
				.header("content-type", "application/json")
				.log().all();
			
			}
		
		//Delete
		 //@Test(priority=2)
		 void deleteUserJSON() {
		
			 
			 given()
			 	
			 
			 .when()
			 	.put("http://localhost:3000/students/207a")
			 	
			 .then()
			 	.statusCode(200);		 	
		 }
		 
		//Using POJO ()
			//@Test(priority=1)
			void POJORequest() {
				
				POJO_PostRequest data = new POJO_PostRequest();
				
				data.setName("priyansh1");
				data.setActive("true");
				data.setYearsOld("15");
				data.setColor("pink");
				String gradesarr[] = {"5.0","4.5","3.6"};
				data.setGrades(gradesarr);
				
				
				given()
					.contentType("application/json")
					.body(data)
				
				.when()
					.post("http://localhost:3000/students")
				
				.then()
					.statusCode(201)
					.body("name", equalTo("priyansh1"))
					.body("active", equalTo("true"))
					.body("grades[0]", equalTo("5.0"))
					.body("grades[1]", equalTo("4.5"))
					.body("grades[2]", equalTo("3.6"))
					.body("yearsOld", equalTo("15"))
					.body("color", equalTo("pink"))
					.header("content-type", "application/json")
					.log().all();
				
				}
			
			//Delete
			 //@Test(priority=2)
			 void deleteUser1() {
			
				 
				 given()
				 	
				 
				 .when()
				 	.put("http://localhost:3000/students/207a")
				 	
				 .then()
				 	.statusCode(200);		 	
			 }
			 
			 //Using External JSON
			 @Test(priority=1)
				void ExternalJSON() throws FileNotFoundException {
					
					File f = new File(".\\body.json");
					FileReader fr = new FileReader(f);
					
					JSONTokener jt = new JSONTokener(fr);
					JSONObject data = new JSONObject(jt);
					
					given()
						.contentType("application/json")
						.body(data.toString())
					
					.when()
						.post("http://localhost:3000/students")
					
					.then()
						.statusCode(201)
						.body("name", equalTo("priyansh1"))
						.body("active", equalTo("true"))
						.body("grades[0]", equalTo("5.0"))
						.body("grades[1]", equalTo("4.5"))
						.body("grades[2]", equalTo("3.6"))
						.body("yearsOld", equalTo("15"))
						.body("color", equalTo("pink"))
						.header("content-type", "application/json")
						.log().all();
					
					}
				
				//Delete
				 //@Test(priority=2)
				 void deleteUserJSON1() {
				
					 
					 given()
					 	
					 
					 .when()
					 	.put("http://localhost:3000/students/207a")
					 	
					 .then()
					 	.statusCode(200);		 	
				 }
}
