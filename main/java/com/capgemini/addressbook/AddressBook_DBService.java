package com.capgemini.addressbook;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBook_DBService {
	private static AddressBook_DBService addressBookDBService;
	private PreparedStatement AddressBookContactStatement;

	static AddressBook_DBService getInstance() {
		if (addressBookDBService == null) {
			addressBookDBService = new AddressBook_DBService();
		}
		return addressBookDBService;
	}

	private AddressBook_DBService() {
	}

	public List<Address_Book_Data> readData() {
		String sql = "SELECT ab.firstName, ab.lastName, ab.address,ab.city, ab.state,"
				+ "ab.zip,ab.phoneNumber,ab.email,ab.Type,abn.addressBookName from "
				+ "address_book ab inner join address_book_name abn on ab.Type=abn.Type;";
		return this.getContactDetailsUsingSqlQuery(sql);
	}

	private List<Address_Book_Data> getContactDetailsUsingSqlQuery(String sql) {
		List<Address_Book_Data> addressBookList = null;
		try (Connection connection = this.getConnection();) {
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedstatement.executeQuery(sql);
			addressBookList = this.readAddressBookData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressBookList;
	}

	public List<Address_Book_Data> readAddressBookData(ResultSet resultSet) throws SQLException {
		List<Address_Book_Data> addressBookList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("firstName");
				String lastName = resultSet.getString("lastName");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				long zip = resultSet.getLong("zip");
				long phoneNumber = resultSet.getLong("phoneNumber");
				String email = resultSet.getString("email");
				String type = resultSet.getString("type");
				String addressBookName = resultSet.getString("addressBookName");
				addressBookList.add(new Address_Book_Data(firstName, lastName, address, city, state, zip, phoneNumber,
						email, type, addressBookName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressBookList;
	}

	private Connection getConnection() throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/address_book_service", "root",
				"Jashwini@2298");
		System.out.println("Connection successful: " + connection);
		return connection;
	}

	public int updateAddressBook(String name, String address) {
		return this.updateAddressBookUsingPreparedStatement(name, address);
	}

	private int updateAddressBookUsingPreparedStatement(String firstName, String address) {
		try (Connection connection = this.getConnection();) {
			String sql = "update address_book set address = ? Where firstName= ?";
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setString(1, address);
			preparedstatement.setString(2, firstName);
			int update = preparedstatement.executeUpdate();
			return update;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void preparedStatementForContactData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT ab.firstName, ab.lastName, ab.address,ab.city, ab.state,"
					+ "ab.zip,ab.phoneNumber,ab.email,ab.Type,abn.addressBookName from "
					+ "address_book ab inner join address_book_name abn on ab.Type=abn.Type  WHERE firstName=? ;";
			AddressBookContactStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Address_Book_Data> getAddressbookDataByName(String name) {
		List<Address_Book_Data> addressBookList = null;
		if (this.AddressBookContactStatement == null)
			this.preparedStatementForContactData();
		try {
			AddressBookContactStatement.setString(1, name);
			ResultSet resultSet = AddressBookContactStatement.executeQuery();
			addressBookList = this.readAddressBookData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressBookList;
	}

	public List<Address_Book_Data> getDetailsForDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("SELECT ab.firstName, ab.lastName, ab.address,ab.city, ab.state,"
				+ "ab.zip,ab.phoneNumber,ab.email,ab.Type,abn.addressBookName from "
				+ "address_book ab inner join address_book_name abn on ab.Type=abn.Type  WHERE date_added BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getContactDetailsUsingSqlQuery(sql);
	}

	public Map<String, Integer> getAddressByCity() {
		String sql = "SELECT city, count(firstName) as count from address_book group by city; ";
		Map<String, Integer> addreessByCityMap = new HashMap<>();
		try (Connection connection = addressBookDBService.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String city = result.getString("city");
				Integer count = result.getInt("count");
				addreessByCityMap.put(city, count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addreessByCityMap;
	}

	public Address_Book_Data addContact(String firstName, String lastName, String address, LocalDate date_added,
			String city, String state, long zip, long phoneNumber, String email, String type) {
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			Statement statement = connection.createStatement();
			String sql = String.format(
					"insert into address_Book(firstName,lastName,address,date_added,city,state,zip,phoneNumber"
							+ ",email,type) values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
					firstName, lastName, address, date_added, city, state, zip, phoneNumber, email, type);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.printStackTrace();
			}
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return new Address_Book_Data(firstName, lastName, address, date_added, city, state, zip, phoneNumber, email,
				type);
	}
}
