package com.rupeek.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import com.rupeek.context.Context;
import com.rupeek.utils.GetPropertiesData;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import static org.hamcrest.Matchers.*;

public class TestApiRequest {
	public static Map<String, String> listOfUsers = new HashMap<String, String>();

	@Test(priority = 1)
	public void authenicateUser() throws Throwable {
		RestAssured.baseURI = GetPropertiesData.getPropertyValue("baseURI");
		Context.tempValues.put("authToken", new JsonPath(RestAssured.given().contentType(ContentType.JSON)
				.body(new FileInputStream(new File(
						System.getProperty("user.dir") + GetPropertiesData.getPropertyValue("loginPayloadPath"))))
				.when().post("/authenticate").then().assertThat().statusCode(200).and().contentType(ContentType.JSON)
				.extract().response().asString()).get("token").toString());
	}

	@Test(priority = 2)
	public void getListOfUsers() throws Throwable {
		RestAssured.baseURI = GetPropertiesData.getPropertyValue("baseURI");
		String response = RestAssured.given().headers("Authorization", "Bearer " + Context.tempValues.get("authToken"))
				.when().get("/api/v1/users").then().statusCode(200).and().contentType(ContentType.JSON).extract()
				.response().asString();
		JsonPath jp = new JsonPath(response);
		int listOfPhoneNumbers = jp.get("last_name.size()");
		for (int i = 0; i < listOfPhoneNumbers; i++) {
			listOfUsers.put(jp.get("phone[" + i + "]").toString(), jp.get("first_name[" + i + "]").toString());
		}
	}

	@Test(priority = 3)
	public void searchUserByPhoneNumber() throws Throwable {
		for (Entry<String,String> e : listOfUsers.entrySet()) {
			RestAssured.baseURI = GetPropertiesData.getPropertyValue("baseURI");
			RestAssured.given().headers("Authorization", "Bearer " + Context.tempValues.get("authToken")).when()
					.get("/api/v1/users/" + e.getKey()).then().assertThat().body("first_name", equalTo(e.getValue()));
		}

	}
}
