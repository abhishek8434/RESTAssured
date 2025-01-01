package test;

import org.testng.annotations.Test;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;


import java.util.Map;

public class CookieDemo {
		//@Test(priority=1)
		void testCookie() {
			
			given()
			
			
			.when()
				.get("https://www.google.com/")
			
			.then()
				.cookie("AEC","AZ6Zc-XU4HkbZGmLtVOkvbK7bqF9iqJ938YC5_dsGAbfBXOQXRtYbD7uIA")
				.log().all();
			
		}
		@Test(priority=2)
		void getCookieInfo() {
			
			Response res = given()
			
			.when()
				.get("https://www.google.com/");
			
			
			//get single cookie info
			
			//String cookie_value = res.getCookie("AEC");
			//System.out.println("Value of cookie is===>"+cookie_value);
			
			//get all cookie info 
			Map<String, String> cookie_value = res.getCookies();
			
			//System.out.println(cookie_value.keySet());
			
			for(String k:cookie_value.keySet()) {
				String cookies_value = res.getCookie(k);
				System.out.println(k+"         "+cookies_value);
			}
			
		}
}
