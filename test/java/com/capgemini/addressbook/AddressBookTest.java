package com.capgemini.addressbook;

import org.junit.Before;
import com.google.gson.Gson;
import java.util.logging.Logger;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.capgemini.addressbook.Address_Book_Service.*;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class AddressBookTest {
	static Logger log = Logger.getLogger(AddressBookTest.class.getName());

	@Before
	public void sertup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public Address_Book_Data[] getAddressBookList() {
		Response response = RestAssured.get("/contacts");
		log.info("Contact entries in JSON Server :\n" + response.asString());
		Address_Book_Data[] arrayOfContacts = new Gson().fromJson(response.asString(), Address_Book_Data[].class);
		return arrayOfContacts;
	}

	public Response addContactToJsonServer(Address_Book_Data Data) {
		String contactJson = new Gson().toJson(Data);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contacts");
	}

	@Test
	public void givenAddressDataInJsonServer_WhenRetrived_ShouldMatchCount() {
		Address_Book_Data[] arrayOfEmps = getAddressBookList();
		Address_Book_Service employeePayrollService;
		employeePayrollService = new Address_Book_Service(Arrays.asList(arrayOfEmps));
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenNewContact_WhenAdded__ShouldMatch() {
		Address_Book_Service addressBookService;
		Address_Book_Data[] arrayOfContacts = getAddressBookList();
		addressBookService = new Address_Book_Service(Arrays.asList(arrayOfContacts));
		Address_Book_Data addressBookData = null;
		addressBookData = new Address_Book_Data("Charan", "durgam", "ssr", LocalDate.now(), "Karimnagar", "Telangana",
				500015, 779984874, "charan@gmail.com", "Family");
		Response response = addContactToJsonServer(addressBookData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		addressBookData = new Gson().fromJson(response.asString(), Address_Book_Data.class);
		addressBookService.addContactToJSONServer(addressBookData, IOService.REST_IO);
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}
}
