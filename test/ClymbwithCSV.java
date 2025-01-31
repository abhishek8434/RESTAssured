package clymb;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class ClymbwithCSV {

	private String loginToken;
    	private String authToken;
    	private String authTokenRefresh; 
    	final String baseURI = "Enter Your URL here";
    	private final String keyStr = "Your Key";  // 16-byte AES key
    	private final String ivStr = "Your Key";   // 16-byte AES IV

    @DataProvider(name = "userData")
    public Object[][] getUserData() {
        List<Object[]> users = new ArrayList<>();
        String csvFile = "Enter your file path here";  // Adjust path if needed

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true; // Track the first row

            while ((line = br.readLine()) != null) {
                if (isFirstLine) { 
                    isFirstLine = false; // Skip the first row (header)
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 2) {  // Ensure it has exactly 2 columns
                    users.add(new Object[]{data[0].trim(), data[1].trim()});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users.toArray(new Object[0][]);
    }

    @Test(priority = 1, dataProvider = "userData")
    public void testLogin(String username, String password) throws Exception {
        RestAssured.baseURI = baseURI;

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("password", password);

        String encryptedData = encryptAES(requestBody.toString(), keyStr, ivStr);

        Response response = given()
                .header("Content-Type", "application/json")
                .body(encryptedData)
                .when()
                .post("/login")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Login failed!");

        loginToken = response.getHeader("Authorization");
        Assert.assertNotNull(loginToken, "Authorization token is missing in the response headers.");

        System.out.println("Extracted Token from Login: " + loginToken);
    }

    @Test(priority = 2, dependsOnMethods = "testLogin", dataProvider = "userData")
    public void testGetUserAuthDetails(String username, String password) {
        RestAssured.baseURI = baseURI;

        Assert.assertNotNull(loginToken, "Token is not available. Please ensure that testLogin() runs successfully.");

        Response getUserAuthResponse = given()
                .header("Authorization", loginToken)
                .when()
                .get("/api/auth/getUserAuthDetails")
                .then()
                .extract().response();

        Assert.assertEquals(getUserAuthResponse.getStatusCode(), 200, "Get User Auth Details request failed with status code: " + getUserAuthResponse.getStatusCode());

        String authToken = getUserAuthResponse.getHeader("Authorization");
        String authTokenRefresh = getUserAuthResponse.getHeader("Authorization_Refresh_Token");

        System.out.println("auth header -> " + authToken);
        System.out.println("auth header refresh token -> " + authTokenRefresh);

        Assert.assertNotNull(authToken, "Authorization token is missing in the response headers.");
        Assert.assertNotNull(authTokenRefresh, "Authorization refresh token is missing in the response headers.");

        this.authToken = authToken;
        this.authTokenRefresh = authTokenRefresh;
        System.out.println("Stored Tokens -> authToken: " + this.authToken + ", authTokenRefresh: " + this.authTokenRefresh);
    }

    @Test(priority = 3, dependsOnMethods = "testGetUserAuthDetails", dataProvider = "userData")
    public void testGetData(String username, String password) throws Exception {
        RestAssured.baseURI = baseURI;

        Assert.assertNotNull(authToken, "Authorization token is not available. Please ensure that testGetUserAuthDetails() runs successfully.");

        Response getDataResponse = given()
                .header("Authorization", authToken)
                .when()
                .get("api/auth/get/data")
                .then()
                .extract().response();

        System.out.println("GET /students/getData Response Status Code: " + getDataResponse.getStatusCode());
        System.out.println("GET /students/getData Response Body: " + getDataResponse.getBody().asString());

        Assert.assertEquals(getDataResponse.getStatusCode(), 200, "Get Data request failed with status code: " + getDataResponse.getStatusCode());

        String encryptedResponseBody = getDataResponse.getBody().asString();

        String decryptedResponseBody = decryptAES(encryptedResponseBody, keyStr, ivStr);

        System.out.println("Decrypted Response Body: " + decryptedResponseBody);

        JSONArray jsonResponse = new JSONArray(decryptedResponseBody);

        JSONObject studentData = jsonResponse.getJSONObject(0);

        JSONObject studentInfo = studentData.getJSONObject("s");

        int studentId = studentInfo.getInt("student_id");
        String studentName = studentInfo.getString("first_name") + " " + studentInfo.getString("last_name");

        System.out.println("Student ID: " + studentId);
        System.out.println("Student Name: " + studentName);

        Assert.assertNotNull(studentId, "Student ID is missing in the response.");
        Assert.assertNotNull(studentName, "Student name is missing in the response.");
    }
    private String encryptAES(String data, String keyStr, String ivStr) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(ivStr.getBytes("UTF-8"));
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyStr.getBytes("UTF-8"), "AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decryptAES(String encryptedData, String keyStr, String ivStr) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(ivStr.getBytes("UTF-8"));
        SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyStr.getBytes("UTF-8"), "AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        return new String(decryptedBytes, "UTF-8");
    }
	
}
