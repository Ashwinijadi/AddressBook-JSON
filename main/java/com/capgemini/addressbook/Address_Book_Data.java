package com.capgemini.addressbook;

import java.time.LocalDate;
import java.util.Objects;

public class Address_Book_Data {
	String firstName;
	String lastName;
	String address;
	String city;
	String state;
	LocalDate date_added;
	long zip;
	long phoneNumber;
	String email;
	String Type;
	String addressBookName;

	public Address_Book_Data(String firstName, String lastName, String address, String city, String state, long zip,
			long phoneNumber, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public Address_Book_Data(String firstName, String lastName, String address, String city, String state, long zip,
			long phoneNumber, String email, String Type, String addressBookName) {
		this(firstName, lastName, address, city, state, zip, phoneNumber, email);
		this.Type = Type;
		this.addressBookName = addressBookName;
	}

	public Address_Book_Data(String firstName, String lastName, String address, LocalDate date_added, String city,
			String state, long zip, long phoneNumber, String email, String Type) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.date_added = date_added;
		this.zip = zip;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.Type = Type;
	}

	public Address_Book_Data() {

	}

	@Override
	public String toString() {
		return "Address_Book_Data [firstName=" + firstName + ", lastName=" + lastName + ", address=" + address
				+ ", city=" + city + ", state=" + state + ", zip=" + zip + ", phoneNumber=" + phoneNumber + ", email="
				+ email + ", type=" + Type + ", addressBookName=" + addressBookName + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, address, city, state, zip, phoneNumber, email, addressBookName,
				date_added);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Address_Book_Data that = (Address_Book_Data) obj;
		return firstName.equals(that.firstName) && address.equals(that.address);
	}
}
