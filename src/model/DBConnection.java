package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * the connection to the database.
 * @author jdowd
 *
 */
public class DBConnection {
	
	/** the object's server. */
	private String server = "joshuadowd.info";
	/** the object's database. */
	private String database = "BookStore";
	/** the object's username. */
	private String username = "app";
	/** the object's password. */
	private String password = "test";
	/** the object's connection. */
	private Connection c;
	
	
	/**
	 * sets of the database connection from
	 * default values.
	 * @throws SQLException an exception from the database.
	 */
	public DBConnection() throws SQLException {
		c = DriverManager.getConnection(
				"jdbc:mysql://" 
				+ server
				+ "/"
				+ database
				+ "?noAccessToProcedureBodies=true"
				+ "&useSSL=false", username, password);
	}
	
	/**
	 * set the connection based on current data.
	 * @throws SQLException an exception from the database
	 */
	public void setDBConnection () throws SQLException {
		c = DriverManager.getConnection(
				"jdbc:mysql://" 
				+ server
				+ "/"
				+ database
				+ "?noAccessToProcedureBodies=true"
				+ "&useSSL=false", username, password);
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(final String server) {
		this.server = server;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(final String database) {
		this.database = database;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return c;
	}

	/**
	 * @param c the connection to set
	 */
	public void setConnection(final Connection c) {
		this.c = c;
	}


}
