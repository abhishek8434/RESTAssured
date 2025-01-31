package clymb;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class EncryptionAndDecryption{
	

// Declare class-level variables to store tokens
    private String loginToken;
    private String authToken;
    private String authTokenRefresh;
    private String baseURI = "Enter Your Url Here";
    private String keyStr = "Your Key";   // 16-byte key
    private String ivStr = "Your Key";    // 16-byte IV
    
    @Test(priority = 1)
    public void testLogin() throws Exception {
        // Base URI
        RestAssured.baseURI = baseURI;

        // Constructing the request body dynamically using JSONObject
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "sampleuser@yopmail.com");
        requestBody.put("password", "Test@123");

        String encryptedData = encryptAES(requestBody.toString(), keyStr, ivStr);

        // Send the encrypted data directly as the request body
        Response response = given()
                .header("Content-Type", "application/json")
                .body(encryptedData)  // Direct encrypted body
                .when()
                .post("/login")
                .then()
                .extract().response();

        // Debugging: Print status code and response body
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        // Print headers for further debugging
        System.out.println("Response Headers: " + response.getHeaders());

        // Extract token from Authorization header
        loginToken = response.getHeader("Authorization");

        // Verify that token is present
        Assert.assertNotNull(loginToken, "Authorization token is missing in the response headers.");

        // Optionally, if needed, you can store the token for further use
        System.out.println("Extracted Token from Login: " + loginToken);
    }

   @Test(priority = 2)
    public void testGetUserAuthDetails() throws Exception {
        // Base URI (same as before)
        RestAssured.baseURI = baseURI;

        // Ensure token is not null or empty before proceeding
        Assert.assertNotNull(loginToken, "Token is not available. Please ensure that testLogin() runs successfully.");

        // Use the token to make another GET request to fetch user auth details
        Response getUserAuthResponse = given()
                .header("Authorization", loginToken)  // Use the token from previous response
                .when()
                .get("/api/auth/getUserAuthDetails")
                .then()
                .extract().response();

        // Debugging: Print status code and response body
        System.out.println("GET Response Status Code: " + getUserAuthResponse.getStatusCode());
        System.out.println("GET Response Body: " + getUserAuthResponse.getBody().asString());

        // Verify that the GET request was successful
        Assert.assertEquals(getUserAuthResponse.getStatusCode(), 200, "Get User Auth Details request failed with status code: " + getUserAuthResponse.getStatusCode());

        // Extract authorization token and refresh token from response headers
        String authToken = getUserAuthResponse.getHeader("Authorization");
        String authTokenRefresh = getUserAuthResponse.getHeader("Authorization_Refresh_Token");

        // Debugging: Print tokens
        System.out.println("auth header -> " + authToken);
        System.out.println("auth header refresh token -> " + authTokenRefresh);

        // Store the tokens for further use in the test (e.g., in variables or test context)
        Assert.assertNotNull(authToken, "Authorization token is missing in the response headers.");
        Assert.assertNotNull(authTokenRefresh, "Authorization refresh token is missing in the response headers.");

        // Optionally, if you want to store the tokens as environment variables (using some external mechanism), do that here
        // For example, store in a global or class-level variable for later use
        this.authToken = authToken;
        this.authTokenRefresh = authTokenRefresh;
        System.out.println("Stored Tokens -> authToken: " + this.authToken + ", authTokenRefresh: " + this.authTokenRefresh);
    }

    @Test(priority = 3)
    public void testGetData() throws Exception {
        // Base URI (same as before)
        RestAssured.baseURI = baseURI;

        // Ensure token from user auth is not null or empty before proceeding
        Assert.assertNotNull(authToken, "Authorization token is not available. Please ensure that testGetUserAuthDetails() runs successfully.");

        // Use the auth token to make another GET request to fetch data from /students/getData endpoint
        Response getDataResponse = given()
                .header("Authorization", authToken)  // Use the auth token from previous response
                .when()
                .get("api/auth/get/data")
                .then()
                .extract().response();

        // Debugging: Print status code and response body
        System.out.println("GET /students/getData Response Status Code: " + getDataResponse.getStatusCode());
        System.out.println("GET /students/getData Response Body: " + getDataResponse.getBody().asString());

        // Verify that the GET request was successful
        Assert.assertEquals(getDataResponse.getStatusCode(), 200, "Get Data request failed with status code: " + getDataResponse.getStatusCode());

        // If the response body is encrypted, decrypt it
        String encryptedResponseBody = getDataResponse.getBody().asString();

        // Decrypt the response
        String decryptedResponseBody = decryptAES(encryptedResponseBody, keyStr, ivStr);

        // Debugging: Print decrypted response
        System.out.println("Decrypted Response Body: " + decryptedResponseBody);

        // Parse the decrypted JSON response as a JSONArray, since the response is an array
        JSONArray jsonResponse = new JSONArray(decryptedResponseBody);

        // Assuming the first element of the array is the student object
        JSONObject studentData = jsonResponse.getJSONObject(0); // Get the first element from the array

        // Extract details from the student data
        JSONObject studentInfo = studentData.getJSONObject("s");  // Access the student "s" object

        // Change getString() to getInt() for student_id since it's an integer
        int studentId = studentInfo.getInt("student_id"); // Extract student ID (as int)
        String studentName = studentInfo.getString("first_name") + " " + studentInfo.getString("last_name"); // Extract student full name

        // Debugging: Print the student details
        System.out.println("Student ID: " + studentId);
        System.out.println("Student Name: " + studentName);

        // You can also use assertions to verify the presence of these values
        Assert.assertNotNull(studentId, "Student ID is missing in the response.");
        Assert.assertNotNull(studentName, "Student name is missing in the response.");
    }
    
    //@Test(priority = 4)
    public void loadTestAllCases() throws Exception {
        int numberOfThreads = 5;  // Simulate 5 concurrent users

        // Using ExecutorService to manage threads more efficiently
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        testLogin();  // Simulate login
        testGetUserAuthDetails();  // Simulate getting user auth details
        
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    // Perform the login, authentication, and data retrieval in sequence
//                    testLogin();  // Simulate login
//                    testGetUserAuthDetails();  // Simulate getting user auth details
                    testGetData();  // Simulate getting user data
                    System.out.println(Thread.currentThread().getName() + " completed all cases successfully.");
                } catch (Exception e) {
                    System.out.println("Error in thread " + Thread.currentThread().getName() + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();  // Shut down the executor after all tasks are submitted
        while (!executor.isTerminated()) {
            // Wait until all tasks are finished
        }
        System.out.println("All threads have completed.");
    }
    
    // AES Encryption method
    private String encryptAES(String data, String keyStr, String ivStr) throws Exception {
        byte[] key = keyStr.getBytes("UTF-8");
        byte[] iv = ivStr.getBytes("UTF-8");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(key, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(encryptedBytes);  // Base64 encode the encrypted result
    }

    // AES Decryption method 
    private String decryptAES(String encryptedData, String keyStr, String ivStr) throws Exception {
        byte[] key = keyStr.getBytes("UTF-8");
        byte[] iv = ivStr.getBytes("UTF-8");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(key, "AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        return new String(decryptedBytes, "UTF-8");  // Convert the decrypted bytes back to a string
    }
    
}


