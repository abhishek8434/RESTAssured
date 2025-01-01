package test;

import org.testng.annotations.Test;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class HeadersDemo {
	
	
			//@Test(priority=1)
			void getHeader() {
		
			given()
		
		
			.when()
				.get("https://www.google.com/")
		
			.then()
				.header("Content-Type","text/html; charset=ISO-8859-1")
				.and()
				.header("Content-Encoding", "gzip")
				.and()
				.header("Server", "gws");
		
			}	

			@Test(priority=2)
			void testHeader() {
				
				Response res = given()
				
				
				.when()
					.get("https://www.google.com/");
				
				//get single header info
				String header_value = res.getHeader("Content-Type");
				System.out.println("The value of content type header is: "+ header_value);
				
				//get all header info
				
				Headers myheaders = res.getHeaders();
				
				for(Header hd:myheaders) {
					System.out.println(hd.getName()+"           "+hd.getValue());
				}
				
			}
			
		
}
