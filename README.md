# REST Assured API Testing with Maven on Eclipse

This guide provides step-by-step instructions to set up and run REST Assured API tests using Maven on Eclipse.

## Prerequisites

Before you begin, ensure the following tools are installed on your system:

1. **Eclipse IDE for Java Developers**
2. **Java Development Kit (JDK)** (version 8 or higher)
3. **Maven Integration for Eclipse (m2e)** plugin (comes pre-installed in most Eclipse versions).

## Project Setup in Eclipse

### Step 1: Clone the Repository

1. Open your terminal or command prompt.
2. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-folder>
   ```

### Step 2: Import the Project into Eclipse

1. Open Eclipse.
2. Go to `File > Import`.
3. Select `Maven > Existing Maven Projects` and click **Next**.
4. Browse to the folder containing the cloned repository and select the root directory (where `pom.xml` is located).
5. Click **Finish** to import the project.

### Step 3: Resolve Maven Dependencies

1. Right-click on the project in the **Project Explorer**.
2. Select `Maven > Update Project`.
3. Check the option `Force Update of Snapshots/Releases` and click **OK**.
4. Wait for Eclipse to download and resolve all dependencies.

## Writing a Basic Test

Here is an example of a simple GET request test using REST Assured:

```java
import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BasicApiTest {

    @Test
    public void validateGetRequest() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        given()
            .when()
            .get("/posts/1")
            .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", notNullValue());
    }
}
```

## Running Tests

### Using Eclipse

1. Right-click on the test file (e.g., `BasicApiTest.java`) or the `src/test/java` folder.
2. Select `Run As > TestNG Test` or `Run As > Maven Test`.
3. View the results in the **Console** or **TestNG Results** tab.

### Using Maven from the Command Line

1. Open a terminal and navigate to the project folder.
2. Run the following command to execute tests:
   ```bash
   mvn test
   ```

## Test Reporting

### Generate Allure Reports (Optional)

1. Add the Allure dependencies and plugins in your `pom.xml`.
2. Run tests and generate reports:
   ```bash
   mvn test
   mvn allure:serve
   ```
3. Open the generated report in your browser.

## Best Practices

- **Organize Test Cases**: Group tests logically by API endpoints or features.
- **Centralize Configurations**: Store common properties like base URLs and headers in a config file or constants class.
- **Data-Driven Testing**: Use TestNG's `@DataProvider` for dynamic test inputs.
- **Response Validations**: Always validate status codes and response bodies thoroughly.

## Troubleshooting

1. **Dependency Issues**: If Maven dependencies fail to load, run `Maven > Update Project` again with the `Force Update` option enabled.
2. **Test Execution Problems**: Ensure you have the correct TestNG or Maven TestNG plugin installed.

## Support

For assistance, raise an issue in the repository or consult the REST Assured and Eclipse documentation.


