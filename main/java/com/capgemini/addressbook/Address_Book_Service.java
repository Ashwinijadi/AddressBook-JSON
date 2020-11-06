package com.capgemini.addressbook;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class Address_Book_Service {
	private static Logger log = Logger.getLogger(Address_Book_Service.class.getName());
	private List<Address_Book_Data> addressBookList;
	private AddressBook_DBService addressBookDBService;
	private Map<String, Integer> addressByCity;

	public enum IOService {
		DB_IO, REST_IO
	}

	public Address_Book_Service(List<Address_Book_Data> addressBookList) {
		this.addressBookList = addressBookList;
	}

	public Address_Book_Service() {
		addressBookDBService = addressBookDBService.getInstance();
	}

	public List<Address_Book_Data> readData() throws SQLException {
		this.addressBookList = addressBookDBService.readData();
		return addressBookList;

	}

	public void update(String name, String address) {
		int result = addressBookDBService.updateAddressBook(name, address);
		if (result == 0)
			return;
		Address_Book_Data addressbookData = this.readData(name);
		if (addressbookData != null)
			addressbookData.address = address;
	}

	public void updateContactJsonServer(String firstName, String address, IOService ioService) {
		if (ioService.equals(IOService.REST_IO)) {
			Address_Book_Data addressbookData = this.readData(firstName);
			if (addressbookData != null)
				addressbookData.address = address;
		}
	}

	public Address_Book_Data readData(String name) {
		return this.addressBookList.stream().filter(contact -> contact.firstName.equals(name)).findFirst().orElse(null);
	}

	public void deleteContact(String firstName, IOService ioService) {
		if (ioService.equals(IOService.REST_IO)) {
			Address_Book_Data addressbookData = this.readData(firstName);
			addressBookList.remove(addressbookData);
		}
	}

	public long countEntries(IOService ioService) {
		return addressBookList.size();
	}

	public boolean checkContactInSyncWithDB(String name) {
		List<Address_Book_Data> addressBookList = addressBookDBService.getAddressbookDataByName(name);
		return addressBookList.get(0).equals(readData(name));
	}

	public List<Address_Book_Data> readContactDataForDateRange(LocalDate startDate, LocalDate endDate) {
		this.addressBookList = addressBookDBService.getDetailsForDateRange(startDate, endDate);
		return addressBookList;
	}

	public Map<String, Integer> getAddressByCityOrState() {
		this.addressByCity = addressBookDBService.getAddressByCity();
		return addressByCity;
	}

	public void addContactToAddressBook(String firstName, String lastName, String address, LocalDate date_added,
			String city, String state, long zip, long phoneNumber, String email, String Type) {
		addressBookList.add(addressBookDBService.addContact(firstName, lastName, address, date_added, city, state, zip,
				phoneNumber, email, Type));

	}

	public void addContact(List<Address_Book_Data> addressList) {
		addressList.forEach(address -> {
			System.out.println("address being added : " + address.firstName);
			this.addContactToAddress(address.firstName, address.lastName, address.address, address.date_added,
					address.city, address.state, address.zip, address.phoneNumber, address.email, address.Type);
			System.out.println("address added : " + address.firstName);
		});
		System.out.println("" + this.addressBookList);
	}

	public void addEmployeeToPayrollWithThreads(List<Address_Book_Data> addressList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		addressList.forEach(address -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(address.hashCode(), false);
				System.out.println(" being added : " + Thread.currentThread().getName());
				this.addContactToAddress(address.firstName, address.lastName, address.address, address.date_added,
						address.city, address.state, address.zip, address.phoneNumber, address.email, address.Type);
				employeeAdditionStatus.put(address.hashCode(), true);
				System.out.println("address added : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, address.firstName);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("" + this.addressBookList);
	}

	private void addContactToAddress(String firstName, String lastName, String address, LocalDate date_added,
			String city, String state, long zip, long phoneNumber, String email, String Type) {
		addressBookList.add(addressBookDBService.addContact(firstName, lastName, address, date_added, city, state, zip,
				phoneNumber, email, Type));
	}

	public void addContactToAddressBook(Address_Book_Data addressBookData, IOService ioService) {
		if (ioService.equals(IOService.REST_IO))
			addressBookList.add(addressBookData);
	}

	public void addContactToJSONServer(Address_Book_Data addressBookData, IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.addContactToAddress(addressBookData.firstName, addressBookData.lastName, addressBookData.address,
					addressBookData.date_added, addressBookData.city, addressBookData.state, addressBookData.zip,
					addressBookData.phoneNumber, addressBookData.email, addressBookData.Type);
	}
}