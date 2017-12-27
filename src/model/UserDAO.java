package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.berry.BCrypt;

/**
 * the DAO for users.
 * @author jdowd
 *
 */
public class UserDAO implements DAO<User>{
	
	private static final int WORKLOAD = 12;
	
	/**
	 * This is the connection to the database
	 */
	private Connection c;

	/**
	 * @param connect the connection to the database
	 * @throws SQLException an exception from the database
	 */
	public UserDAO(Connection connect) throws SQLException {
		c = connect;
	}
	
	/* (non-Javadoc)
	 * @see model.DAO#update(java.lang.Object)
	 */
	@Override
	public void update(User objectToUpdate) throws SQLException {
		String salt = BCrypt.gensalt(WORKLOAD);
		String hashed_password = BCrypt.hashpw(objectToUpdate.getUserPassword(), salt);
		
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspUpdateUser(?,?,?,?,?,?,?)}");
		stmt.setInt(++n, objectToUpdate.getUserID());
		stmt.setString(++n, hashed_password);
		stmt.setString(++n, objectToUpdate.getFirstName());
		stmt.setString(++n, objectToUpdate.getLastName());
		stmt.setString(++n, objectToUpdate.getEmailAddress());
		stmt.setString(++n, objectToUpdate.getHomePhone());
		stmt.setString(++n, objectToUpdate.getCellPhone());
		stmt.executeQuery();
	}

	/* (non-Javadoc)
	 * @see model.DAO#create(java.lang.Object)
	 */
	@Override
	public void create(User objectToCreate) throws SQLException {
		String salt = BCrypt.gensalt(WORKLOAD);
		String hashed_password = BCrypt.hashpw(objectToCreate.getUserPassword(), salt);
		
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspCreateUser(?,?,?,?,?,?)}");
		stmt.setString(++n, hashed_password);
		stmt.setString(++n, objectToCreate.getFirstName());
		stmt.setString(++n, objectToCreate.getLastName());
		stmt.setString(++n, objectToCreate.getEmailAddress());
		stmt.setString(++n, objectToCreate.getHomePhone());
		stmt.setString(++n, objectToCreate.getCellPhone());

		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			objectToCreate.setUserID(r.getInt("userID"));
		}
	}

	/* (non-Javadoc)
	 * @see model.DAO#getAll()
	 */
	@Override
	public List<User> getAll() throws SQLException {
		List<User> myUsers = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetAllUsers()}");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			User tempUser = new User();
			tempUser.setUserID(rs.getInt("userID"));
			tempUser.setEmailAddress(rs.getString("emailAddress"));
			tempUser.setFirstName(rs.getString("firstName"));
			tempUser.setLastName(rs.getString("lastName"));
			tempUser.setAdmin(rs.getInt("isAdmin"));
			tempUser.setHomePhone(rs.getString("homePhone"));
			tempUser.setCellPhone(rs.getString("cellPhone"));
			tempUser.setActive(rs.getInt("isInactive") == 0 ? true : false);
			myUsers.add(tempUser);
		}
		return myUsers;
	}

	/* (non-Javadoc)
	 * @see model.DAO#get(int)
	 */
	@Override
	public User get(int id) throws SQLException {

		User myUser = new User();
		CallableStatement stmt = c.prepareCall("{call uspGetUser(?)}");
		int n = 0;
		stmt.setInt(++n, id);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			myUser.setUserID(rs.getInt("userID"));
			myUser.setEmailAddress(rs.getString("emailAddress"));
			myUser.setFirstName(rs.getString("firstName"));
			myUser.setLastName(rs.getString("lastName"));
			myUser.setAdmin(rs.getInt("isAdmin"));
			myUser.setHomePhone(rs.getString("homePhone"));
			myUser.setCellPhone(rs.getString("cellPhone"));
			myUser.setActive(rs.getInt("isInactive") == 0 ? true : false);
		}
		return myUser;
	}

	/* (non-Javadoc)
	 * @see model.DAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(User objectToDelete) throws SQLException {

		CallableStatement stmt = c.prepareCall("{call uspDeleteUser(?)}");
		int n = 0;
		stmt.setInt(++n, objectToDelete.getUserID());
		stmt.executeQuery();
	}
	
	/**
	 * Allows for the deletion of a user that is 
	 * not the active user.
	 * @param objectToDelete the user to delete.
	 * @param current the current logged in user.
	 * @throws SQLException an exception from the database
	 */
	public void safeDelete(User objectToDelete, User current) throws SQLException {

		CallableStatement stmt = c.prepareCall("{call uspSafeDeleteUser(?, ?)}");
		int n = 0;
		stmt.setInt(++n, objectToDelete.getUserID());
		stmt.setInt(++n, current.getUserID());
		stmt.executeQuery();
	}

	/**
	 * gives administration privileges to a given user
	 * id.
	 * @param id the id to add administration privileges for
	 * @throws SQLException an exception from the database
	 */
	public void setUserAdmin(int id) throws SQLException {
		CallableStatement stmt = c.prepareCall("{call uspSetUserAdmin(?)}");
		int n = 0;
		stmt.setInt(++n, id);
		stmt.executeQuery();
	}
	
	/**
	 * revokes administration privileges for a given user
	 * id.
	 * @param id the id to revoke administration privileges for
	 * @param current the logged in user
	 * @throws SQLException an exception from the database
	 */
	public void revokeUserAdmin(int id, User current) throws SQLException {		
		CallableStatement stmt = c.prepareCall("{call uspRevokeUserAdmin(?, ?)}");
		int n = 0;
		stmt.setInt(++n, id);
		stmt.setInt(++n, current.getUserID());
		stmt.executeQuery();
	}
	
	/**
	 * @param email the user's email address
	 * @return the user's login info
	 * @throws SQLException an exception from the database
	 */
	public User login(String email) throws SQLException{
		User myUser = new User();
		CallableStatement stmt = c.prepareCall("{call uspLoginUser(?)}");
		int n = 0;
		stmt.setString(++n, email);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			myUser.setUserPassword(rs.getString("userPassword"));
			myUser.setUserID(rs.getInt("userID"));
		}
		return myUser;
	}
}
