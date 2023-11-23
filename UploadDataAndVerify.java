import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.lang.Assert;

public class UploadDataAndVerify {
  WebDriver driver;
  private static final String url = "https://testpages.herokuapp.com/styled/tag/dynamic-table.html";
  private static final String summaryLocator = "//div[@class='centered']//summary";
  private static final String textAreaId = "jsondata";
  private static final String refreshButtonId = "refreshtable";
  private static final String tableLocator = "//table[@id='dynamictable']";
  private static final String  data = "[\n" +
    "    {\n" +
    "        \"name\": \"Bob\",\n" +
    "        \"age\": 20,\n" +
    "        \"gender\": \"male\"\n" +
    "    },\n" +
    "    {\n" +
    "        \"name\": \"George\",\n" +
    "        \"age\": 42,\n" +
    "        \"gender\": \"male\"\n" +
    "    },\n" +
    "    {\n" +
    "        \"name\": \"Sara\",\n" +
    "        \"age\": 42,\n" +
    "        \"gender\": \"female\"\n" +
    "    },\n" +
    "    {\n" +
    "        \"name\": \"Conor\",\n" +
    "        \"age\": 40,\n" +
    "        \"gender\": \"male\"\n" +
    "    },\n" +
    "    {\n" +
    "        \"name\": \"Jennifer\",\n" +
    "        \"age\": 42,\n" +
    "        \"gender\": \"female\"\n" +
    "    }\n" +
    "]";

  @BeforeMethod
  public void testSetUp() {
//    System.setProperty("webdriver.chrome.driver","C:\\Users\\msi26\\Downloads\\chromedriver_win32\\chromedriver.exe");
    driver = new ChromeDriver();
    driver.manage().window().maximize();

  }

  @Test(description = "Verify that given data is equal to actual data.")
  public void verifyUploadedData(){
      driver.get(url);
      WebElement summaryElement = driver.findElement(By.xpath(summaryLocator));
      click(summaryElement, 10);

      WebElement textAreaElement = driver.findElement(By.id(textAreaId));
      textAreaElement.clear();

      JSONArray expectedData = new JSONArray(data);
      textAreaElement.sendKeys(expectedData.toString());

      WebElement refreshElement = driver.findElement(By.id(refreshButtonId));
      click(refreshElement, 10);

      WebElement tableElement = driver.findElement(By.xpath(tableLocator));
      JSONArray actualData = extractTableData(tableElement);

    Assert.isTrue((compareJSONArrays(expectedData, actualData)));

  }

  @AfterMethod
  public void testTearDown(){
    driver.quit();
  }

  public String click(WebElement locator, int timeOut) {
    String errorMsg = null;
    try {
      waitUntilClickable(timeOut, locator);
      locator.click();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return errorMsg;
  }

  public Object waitUntilClickable(int timeOutInSeconds, WebElement webElement) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.elementToBeClickable(webElement));
    return this;
  }


  private static JSONArray extractTableData(WebElement table) {
    JSONArray jsonObject = new JSONArray();
    for (WebElement row : table.findElements(By.xpath(".//tr"))) {
      // Skip the header row (assumes the first row is the header)
      if (row.findElements(By.xpath(".//th")).size() > 0) {
        continue;
      }

      String gender = row.findElement(By.xpath(".//td[1]")).getText();
      String name = row.findElement(By.xpath(".//td[2]")).getText();
      String age = row.findElement(By.xpath(".//td[3]")).getText();

      JSONObject rowData = new JSONObject();
      rowData.put("name", name);
      rowData.put("age", Integer.parseInt(age));
      rowData.put("gender", gender);

      jsonObject.put(rowData);
    }
    return jsonObject;
  }



  private static List<JSONObject> toList(JSONArray array) {
    List<JSONObject> list = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      list.add(array.getJSONObject(i));
    }
    return list;
  }

  private static boolean compareJSONArrays(JSONArray arr1, JSONArray arr2) {
    if (arr1.length() != arr2.length()) {
      return false;
    }

    for (int i = 0; i < arr1.length(); i++) {
      try {
        JSONObject obj1 = arr1.getJSONObject(i);
        JSONObject obj2 = arr2.getJSONObject(i);

        if (!compareJSONObjects(obj1, obj2)) {
          return false;
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return true;
  }

  private static boolean compareJSONObjects(JSONObject obj1, JSONObject obj2) {
    // Compare keys and values of JSON objects
    return obj1.getString("gender").equalsIgnoreCase(obj2.getString("gender")) && obj1.getString("name").equalsIgnoreCase(obj2.getString("name")) && obj1.get("age").equals(obj2.get("age"));
  }
}
