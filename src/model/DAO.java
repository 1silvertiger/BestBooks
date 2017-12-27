package model;

import java.sql.SQLException;
import java.util.List;

/**
 * allows for connecting to the database
 * and performing CRUD functions.
 *
 * @author jdowd
 *
 * @param <T> the object for use.
 */
public interface DAO<T> {
	/**
	 * update a row in the database.
	 * @param objectToUpdate the object to update
	 * @throws SQLException an exception from the database
	 */
	void update(T objectToUpdate) throws SQLException;
	/**
	 * create a row in the database.
	 * @param objectToCreate the object to create
	 * @throws SQLException an exception from the database
	 */
	void create(T objectToCreate) throws SQLException;
	/**
	 * @return all the rows in the database
	 * @throws SQLException an exception from the database
	 */
	List<T> getAll() throws SQLException;
	/**
	 * @param id the primary key of the row in the database
	 * @return the row of the database
	 * @throws SQLException an exception from the database
	 */
	T get(int id) throws SQLException;
	/**
	 * delete a row from the database.
	 * @param objectToDelete the object to delete
	 * @throws SQLException an exception from the database
	 */
	void delete(T objectToDelete) throws SQLException;
}
