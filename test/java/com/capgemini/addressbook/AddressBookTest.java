package com.capgemini.addressbook;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;
import java.util.logging.Logger;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.capgemini.addressbook.Address_Book_Service.*;
public class AddressBookTest {
	static Logger log=Logger.getLogger(AddressBookTest.class.getName());
	@Before
	public void sertup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public Address_Book_Data[] getContactList() {
		Response response = RestAssured.get("/contacts");
		log.info("Contact entries in JSON Server :\n" + response.asString());
		Address_Book_Data[] arrayOfContacts = new Gson().fromJson(response.asString(),Address_Book_Data[].class);
		return arrayOfContacts;
	}
}
