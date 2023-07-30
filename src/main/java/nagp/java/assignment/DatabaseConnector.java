package nagp.java.assignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseConnector  {
	private ApplicationProperties properties;
	private String dbName;
	private String dbUserName;
	private String dbPassword;

	DatabaseConnector() {
		this.properties = new ApplicationProperties();
		this.dbName = properties.readProperty("dbname");
		this.dbUserName = properties.readProperty("dbusername");
		this.dbPassword = properties.readProperty("dbpassword");
	}

	public Connection createDatabaseConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, dbUserName, dbPassword);
		} catch (Exception e) {
			System.out.println("Error in connecting to the database" + e.getMessage());
			e.printStackTrace();
		}
		return connection;

	}

	/*
	 * Method will read all the files present in the directory and will update the record if id is already present
	 * else it will insert new record
	 */
	public void InsertRecords() {
		Connection connection = createDatabaseConnection();
		PreparedStatement prepareStatement = null;
		if (connection != null) {
			String insertsql = "insert into BrandInfo values(?,?,?,?,?,?,?,?)";
			String updatesql = "update BrandInfo set ModelName=?,Colour=?,GENDER_RECOMMENDATION=?,"
					+ "SIZE=?,PRICE=?,RATING=?,AVAILABILITY=? where ID=?";
			try {
				FileReading fileReader = new FileReading();
				List<BrandInfo> tableRecords = getRecords(connection);
				List<BrandInfo> fileBrandData = fileReader.readAllFiles();
				
				for(BrandInfo fileRow:fileBrandData) {					
					boolean existingRecord = tableRecords.stream()
							.filter(tableRowData -> tableRowData.getId().equalsIgnoreCase(fileRow.getId()))
							.collect(Collectors.toList()).size() > 0;
					try {

						if (!existingRecord) {
							prepareStatement = connection.prepareStatement(insertsql);
							prepareStatement.setString(1, fileRow.getId());
							prepareStatement.setString(2, fileRow.getName());
							prepareStatement.setString(3, fileRow.getColour());
							prepareStatement.setString(4, fileRow.getGenderRecommendation());
							prepareStatement.setString(5, fileRow.getSize());
							prepareStatement.setDouble(6, fileRow.getPrice());
							prepareStatement.setDouble(7, fileRow.getRating());
							prepareStatement.setString(8, fileRow.getAvailability());
							prepareStatement.executeUpdate();
						} else {
							prepareStatement = connection.prepareStatement(updatesql);
							prepareStatement.setString(1, fileRow.getName());
							prepareStatement.setString(2, fileRow.getColour());
							prepareStatement.setString(3, fileRow.getGenderRecommendation());
							prepareStatement.setString(4, fileRow.getSize());
							prepareStatement.setDouble(5, fileRow.getPrice());
							prepareStatement.setDouble(6, fileRow.getRating());
							prepareStatement.setString(7, fileRow.getAvailability());
							prepareStatement.setString(8, fileRow.getId());
							prepareStatement.executeUpdate();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					connection.close();
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Method will fetch all the records from the database table
	 */
	public List<BrandInfo> getRecords(Connection connection) {
		List<BrandInfo> finalResult = new ArrayList<BrandInfo>();
		PreparedStatement statement = null;
		String selectQuery = "select * from BrandInfo";

		try {
			statement = connection.prepareStatement(selectQuery);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				BrandInfo shirt = new BrandInfo();
				shirt.setId(resultSet.getString(1));
				shirt.setName(resultSet.getString(2));
				shirt.setColour(resultSet.getString(3));
				shirt.setGenderRecommendation(resultSet.getString(4));
				shirt.setSize(resultSet.getString(5));
				shirt.setPrice(resultSet.getDouble(6));
				shirt.setRating(resultSet.getDouble(7));
				shirt.setAvailability(resultSet.getString(8));
				finalResult.add(shirt);
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return finalResult;

	}

	/*
	 * Method will fetch the record from the database based on user preference and availability of the item
	 */
	public List<BrandInfo> getRecordsWithInput(String colour, String size, String gender, String sortingPreference) {
		Connection connection = createDatabaseConnection();
		List<BrandInfo> finalResult = new ArrayList<BrandInfo>();
		PreparedStatement statement = null;
		if (connection != null) {
			String selectQuery = "select * from BrandInfo where (GENDER_RECOMMENDATION=? or GENDER_RECOMMENDATION=?) and  colour=? and size=?  and AVAILABILITY=?  ";

			try {
				statement = connection.prepareStatement(selectQuery);
				statement.setString(3, colour);
				statement.setString(4, size);
				statement.setString(1, gender);
				statement.setString(2, "U");
				statement.setString(5, "Y");

				ResultSet resultSet = statement.executeQuery();
				boolean resultFound = false;
				while (resultSet.next()) {
					resultFound = true;
					BrandInfo shirt = new BrandInfo();
					shirt.setId(resultSet.getString(1));
					shirt.setName(resultSet.getString(2));
					shirt.setColour(resultSet.getString(3));
					shirt.setGenderRecommendation(resultSet.getString(4));
					shirt.setSize(resultSet.getString(5));
					shirt.setPrice(resultSet.getDouble(6));
					shirt.setRating(resultSet.getDouble(7));
					shirt.setAvailability(resultSet.getString(8));
					finalResult.add(shirt);
				}
				if (!resultFound) {
					System.out.println(
							"NO Tshirt found for Colour:" + colour + ":size:" + size + " and gender:" + gender);
				} else {
					sortRecord(finalResult, sortingPreference);
				}
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					connection.close();
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return finalResult;
	}

	/*
	 * Will sort the record based on user preference.
	 * if user select price as sorting then it will sort item based on price in ascending  order and if price of 
	 * two item is same then will sort the record based on rating in descending order 
	 * if user select rating as sorting then it will sort item based on rating in descending  order and if rating of 
	 * two item is same then will sort the record based on price in ascending order 
	 */
	public void sortRecord(List<BrandInfo> records, String sorting) {
		Comparator<BrandInfo> price = Comparator.comparing(BrandInfo::getPrice);
		Comparator<BrandInfo> rating = Comparator.comparing(BrandInfo::getRating);

		if (sorting.equalsIgnoreCase("price")) {
			System.out.println(
					"ID::" + "Name::" + "Colour::" + "Gender::" + "Size::" + "Price::" + "Rating::" + "Availability");
			List<BrandInfo> list = records.stream().sorted(price.thenComparing(rating.reversed()))
					.collect(Collectors.toList());
			list.stream().forEach(cust -> System.out.println(cust.toString()));
		} else if (sorting.equalsIgnoreCase("rating")) {
			System.out.println(
					"ID::" + "Name::" + "Colour::" + "Gender::" + "Size::" + "Price::" + "Rating::" + "Availability");
			List<BrandInfo> list = records.stream().sorted(rating.reversed().thenComparing(price))
					.collect(Collectors.toList());
			list.stream().forEach(cust -> System.out.println(cust.toString()));
		} else {
			System.out.println("Sorting preference was not from price or rating");
		}
	}

}
