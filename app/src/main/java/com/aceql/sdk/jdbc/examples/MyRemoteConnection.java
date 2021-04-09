/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.                                 
 * Copyright (C) 2017,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.aceql.sdk.jdbc.examples;


import org.kawanfw.test.util.MessageDisplayer;
import com.aceql.jdbc.driver.free.AceQLDriver;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * 
 * This example:
 * <ul>
 * <li>Inserts a Customer and an Order on a remote database.</li>
 * <li>Displays the inserted raws on the console with two SELECT executed on the
 * remote database.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 */
public class MyRemoteConnection {

    /** The JDBC Connection to the remote AceQL Server */
    Connection connection = null;

    /**
     * Remote Connection Quick Start client example. 
     * Creates a Connection to a
     * remote database.
     * 
     * @return the Connection to the remote database
     * @throws SQLException
     *             if a database access error occurs
     */

	public static Connection remoteConnectionBuilder() throws SQLException, ClassNotFoundException {

		// The URL of the AceQL Server servlet
		// Port number is the port number used to start the Web Server:
		String url = "http://www.runsafester.net:8081/aceql";

		// The remote database to use:
		String database = "sampledb";

		// (username, password) for authentication on server side.
		// No authentication will be done for our Quick Start:
		String username = "MyUsername";
		String password = "MySecret";

		// Attempts to establish a connection to the remote database:
		DriverManager.registerDriver(new AceQLDriver());
		Class.forName(AceQLDriver.class.getName());

		Properties info = new Properties();
		info.put("user", username);
		info.put("password", password);
		info.put("database", database);

		Connection connection = DriverManager.getConnection(url, info);
		return connection;
	}
    /**
     * Main
     * 
     * @param args
     *            not used
     */
    public static void main(String[] args) throws Exception {

	aceqlCodeToRun();
    }

    public static void aceqlCodeToRun() throws SQLException, IOException, ClassNotFoundException {
	int customerId = 1;
	int itemId = 1;

	Connection connection = MyRemoteConnection.remoteConnectionBuilder();
	MyRemoteConnection myRemoteConnection = new MyRemoteConnection(
		connection);

	// Delete previous instances, so that user can recall class
	MessageDisplayer.display("deleting customer...");
	myRemoteConnection.deleteCustomer(customerId);
	MessageDisplayer.display("deleting orderlog...");
	myRemoteConnection.deleteOrderlog(customerId, itemId);

	// Normal insert and select
	myRemoteConnection.insertCustomerAndOrderLog(customerId, itemId);
	myRemoteConnection.selectCustomerAndOrderLog(customerId, itemId);

	// Now run our Blob insert
	myRemoteConnection.deleteOrderlog(customerId, itemId);
	BlobExample blobExample = new BlobExample(connection);

	// On Android is: System.getProperty("java.io.tmpdir") + "kawansoft-user-home"
	//String userHome = FrameworkFileUtil.getUserHome();
        String userHome = "/storage/emulated/0/Pictures";

	File imageFile = new File(userHome + File.separator + "koala.jpg");
	File imageFileDownloaded = new File(userHome + File.separator + "koala_DOWNLOADED.jpg");
	
	if (imageFile.exists()) {
	    blobExample.insertOrderWithImage(1, 1, "description",
		    new BigDecimal("99.99"), imageFile);

	    blobExample.selectOrdersForCustomerWithImage(1, 1, imageFileDownloaded);
	}
	else {
	    MessageDisplayer.display();
	    MessageDisplayer.display("No BLOB test because file does not exist: " + imageFile);
	}

	connection.close();
    }
    
    /**
     * Constructor
     * 
     * @param connection
     *            the AwakeConnection to use for this session
     */
    private MyRemoteConnection(Connection connection) {
	this.connection = connection;
    }

    /**
     * Example of 2 INSERT in the same transaction.
     * 
     * @param customerId
     *            the Customer Id
     * @param itemId
     *            the Item Id
     * 
     * @throws SQLException
     */
    public void insertCustomerAndOrderLog(int customerId, int itemId)
	    throws SQLException {

	connection.setAutoCommit(false);

	try {
	    // Create a Customer
	    String sql = "INSERT INTO CUSTOMER VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
	    PreparedStatement prepStatement = connection.prepareStatement(sql);

	    int i = 1;
	    prepStatement.setInt(i++, customerId);
	    prepStatement.setString(i++, "Sir");
	    prepStatement.setString(i++, "Doe");
	    prepStatement.setString(i++, "John");
	    prepStatement.setString(i++, "1 Madison Ave");
	    prepStatement.setString(i++, "New York");
	    prepStatement.setString(i++, "NY 10010");
	    prepStatement.setString(i, null);

	    prepStatement.executeUpdate();
	    prepStatement.close();

	    // Uncomment following line to test transaction behavior
	    // if (true) throw new SQLException("Exception thrown.");

	    // Create an Order for this Customer
	    sql = "INSERT INTO ORDERLOG VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	    // Create a new Prepared Statement
	    prepStatement = connection.prepareStatement(sql);

	    i = 1;
	    long now = new java.util.Date().getTime();

	    prepStatement.setInt(i++, customerId);
	    prepStatement.setInt(i++, itemId);
	    prepStatement.setString(i++, "Item Description");
	    prepStatement.setBigDecimal(i++, new BigDecimal("99.99"));
	    prepStatement.setDate(i++, new Date(now));
	    prepStatement.setTimestamp(i++, new Timestamp(now));
	    // No Blob in this example.
	    prepStatement.setBinaryStream(i++, null);
	    prepStatement.setInt(i++, 1);
	    prepStatement.setInt(i, 2);

	    prepStatement.executeUpdate();
	    prepStatement.close();

	    MessageDisplayer.display("Insert done!");
	} catch (SQLException e) {
	    e.printStackTrace();
	    connection.rollback();
	    throw e;
	} finally {
	    connection.setAutoCommit(true);
	}
    }

    /**
     * Example of 2 SELECT
     * 
     * @param customerId
     *            the Customer Id
     * @param itemId the Item Id
     * 
     * @throws SQLException
     */
    public void selectCustomerAndOrderLog(int customerId, int itemId)
	    throws SQLException {

	// Display the created Customer:
	String sql = "SELECT CUSTOMER_ID, FNAME, LNAME FROM CUSTOMER "
		+ " WHERE CUSTOMER_ID = ?";
	PreparedStatement prepStatement = connection.prepareStatement(sql);
	prepStatement.setInt(1, customerId);

	ResultSet rs = prepStatement.executeQuery();
	
	while (rs.next()) {
	    
	    int customerId2 = rs.getInt("customer_id");
	    String fname = rs.getString("fname");
	    String lname = rs.getString("lname");

	    MessageDisplayer.display();
	    MessageDisplayer.display("customer_id: " + customerId2);
	    MessageDisplayer.display("fname      : " + fname);
	    MessageDisplayer.display("lname      : " + lname);
	}

	prepStatement.close();
	rs.close();

	connection.setAutoCommit(false);

	// Display the created Order
	sql = "SELECT * FROM ORDERLOG WHERE  customer_id = ? AND item_id = ? ";

	prepStatement = connection.prepareStatement(sql);

	int i = 1;
	prepStatement.setInt(i++, customerId);
	prepStatement.setInt(i, itemId);

	rs = prepStatement.executeQuery();

	MessageDisplayer.display();

	if (rs.next()) {
	    int customerId2 = rs.getInt("customer_id");
	    int itemId2 = rs.getInt("item_id");
	    String description = rs.getString("description");
	    BigDecimal itemCost = rs.getBigDecimal("item_cost");
	    Date datePlaced = rs.getDate("date_placed");
	    Timestamp dateShipped = rs.getTimestamp("date_shipped");
	    // byte[] jpeg_image = rs.getBytes("jpeg_image");
	    boolean is_delivered = (rs.getInt("is_delivered") == 1) ; // (a < b) ? a : b;
	    int quantity = rs.getInt("quantity");

	    MessageDisplayer.display("customer_id : " + customerId2);
	    MessageDisplayer.display("item_id     : " + itemId2);
	    MessageDisplayer.display("description : " + description);
	    MessageDisplayer.display("item_cost   : " + itemCost);
	    MessageDisplayer.display("date_placed : " + datePlaced);
	    MessageDisplayer.display("date_shipped: " + dateShipped);
	    // MessageDisplayer.display("jpeg_image  : " + jpeg_image);
	    MessageDisplayer.display("is_delivered: " + is_delivered);
	    MessageDisplayer.display("quantity    : " + quantity);

	    prepStatement.close();
	    rs.close();
	}
    }

    /**
     * Delete an existing customers
     * 
     * @throws SQLException
     */
    public void deleteCustomer(int customerId) throws SQLException {
	String sql = "delete from customer where customer_id = ?";
	PreparedStatement prepStatement = connection.prepareStatement(sql);
	prepStatement.setInt(1, customerId);

	prepStatement.executeUpdate();
	prepStatement.close();

    }

    /**
     * Delete an existing orderlog
     * 
     * @throws SQLException
     */
    public void deleteOrderlog(int customerId, int idemId) throws SQLException {
	String sql = "delete from orderlog where customer_id = ? and item_id = ? ";
	PreparedStatement prepStatement = connection.prepareStatement(sql);
	prepStatement.setInt(1, customerId);
	prepStatement.setInt(2, idemId);

	prepStatement.executeUpdate();
	prepStatement.close();

    }



}
