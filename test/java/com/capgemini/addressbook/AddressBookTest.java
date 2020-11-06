package com.capgemini.addressbook;

import org.junit.Before;
import com.google.gson.Gson;
import java.util.logging.Logger;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.capgemini.addressbook.Address_Book_Service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
		Assert.assertEquals(4, entries);
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
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenNewListOfContacts_WhenAdded_ShouldMatch() {
		Address_Book_Service addressBookService;
		Address_Book_Data[] arrayOfContacts = getAddressBookList();
		addressBookService = new Address_Book_Service(Arrays.asList(arrayOfContacts));
		Address_Book_Data[] addressArrays = {
				 new Address_Book_Data("Charan", "durgam", "ssr", LocalDate.now(), "Karimnagar", "Telangana",
							500015, 779984874, "charan@gmail.com", "Family"),
				 new Address_Book_Data("Rahul", "kk", "ssr", LocalDate.now(), "Warangal", "Telangana",
							500255, 889984874, "rahuln@gmail.com", "Family"),
				 new Address_Book_Data("Nithin", "jadi", "dskr", LocalDate.now(), "Hyderabad", "Telangana",
							500315, 779984874, "nithin@gmail.com", "Family")};
		for (Address_Book_Data addressData :addressArrays) {
			Response response = addContactToJsonServer(addressData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			addressData = new Gson().fromJson(response.asString(),Address_Book_Data.class);
			addressBookService.addContactToAddressBook(addressData, IOService.REST_IO);
		}
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(6, entries);
	}
	@Test
	public void givenAddressBookInDB_WhenRetrieved_ShouldMatchAddressBookCount() throws SQLException {
		Address_Book_Service addressBookService = new Address_Book_Service();
		List<Address_Book_Data> addressBookData = addressBookService.readData();
		System.out.println(addressBookData);
		Assert.assertEquals(4, addressBookData.size());
	}

	@Test
	public void givenNewCityForPerson_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB() throws SQLException {
		Address_Book_Service addressBookService = new Address_Book_Service();
		List<Address_Book_Data> addressBookData = addressBookService.readData();
		addressBookService.update("Ashu", "hyderabad");
		boolean result = addressBookService.checkContactInSyncWithDB("Ashu");
		Assert.assertTrue(result);
		System.out.println(addressBookData);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
		Address_Book_Service addressBookService = new Address_Book_Service();
		addressBookService.readData();
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<Address_Book_Data> addressBookData = addressBookService.readContactDataForDateRange(startDate, endDate);
		Assert.assertEquals(3, addressBookData.size());
		System.out.println(addressBookData);
	}

	@Test
	public void givenAddressBook_RetrieveNumberOfContacts_ByCityOrState() throws SQLException {
		Address_Book_Service addressBookService = new Address_Book_Service();
		addressBookService.readData();
		Map<String, Integer> addressByCityMap = addressBookService.getAddressByCityOrState();
		Integer count = 1;
		Assert.assertEquals(count, addressByCityMap.get("Warangal"));
		System.out.println(addressByCityMap);
	}

	@Test
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() throws SQLException {
		Address_Book_Service addressBookService = new Address_Book_Service();
		addressBookService.readData();
		LocalDate date = LocalDate.now();
		addressBookService.addContactToAddressBook("sneha", " A ", "ssr", date, "Adilabad", "Telangana", 5001015,
				885699227, "sneha@gmail", "Family");
		boolean result = addressBookService.checkContactInSyncWithDB("sneha");
		Assert.assertTrue(result);
	}

}
