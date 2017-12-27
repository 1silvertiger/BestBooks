package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * the DAO for address.
 * @author jdowd
 *
 */
public class AddressDAO implements DAO<Address> {

	/**
	 * This is the connection to the database
	 */
	private Connection c;

	/**
	 * @param connect the connection to the database
	 * @throws SQLException an exception from the database
	 */
	public AddressDAO(final Connection connect) throws SQLException {
		c = connect;
	}

	/* (non-Javadoc)
	 * @see model.DAO#create(java.lang.Object)
	 */
	@Override
	public void create(final Address pObjectToCreate) throws SQLException {
		int n = 0;
		CallableStatement stmt = null;

		switch (pObjectToCreate.getType()) {
		case SHIPPING:
			stmt = c.prepareCall("call uspCreateAddressShipping(?,?,?,?,?,?)");
			break;
		case CARD:
			stmt = c.prepareCall("call uspCreateAddressBilling(?,?,?,?,?,?)");
			break;
		case PUBLISHER:
			stmt = c.prepareCall("call uspCreateAddressPublisher(?,?,?,?,?,?)");
			break;
		default:
			break;
		}

		if (stmt != null) {
			stmt.setString(++n, pObjectToCreate.getLine1());
			stmt.setString(++n, pObjectToCreate.getLine2());
			stmt.setString(++n, pObjectToCreate.getCity());
			stmt.setString(++n, pObjectToCreate.getState());
			stmt.setString(++n, pObjectToCreate.getZip());
			stmt.setInt(++n, pObjectToCreate.getOwnerID());

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				pObjectToCreate.setAddressID(rs.getInt("addressId"));
			}
		}
	}

	/* (non-Javadoc)
	 * @see model.DAO#getAll()
	 */
	@Override
	public List<Address> getAll() throws SQLException {
		List<Address> addresses = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("call uspGetAllAddresses()");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Address a = new Address();
			a.setAddressID(rs.getInt("addressID"));
			a.setLine1(rs.getString("addressLine1"));
			a.setLine2(rs.getString("addressLine2"));
			a.setCity(rs.getString("city"));
			a.setState(rs.getString("state"));
			a.setZip(rs.getString("zipcode"));
			if (rs.getInt("userID") != 0) {
				a.setOwnerID(rs.getInt("userID"));
				a.setType(AddressType.SHIPPING);
			} else if (rs.getInt("cardID") != 0) {
				a.setOwnerID(rs.getInt("cardID"));
				a.setType(AddressType.CARD);
			} else if (rs.getInt("publisherID") != 0) {
				a.setOwnerID(rs.getInt("publisherID"));
				a.setType(AddressType.PUBLISHER);
			} 
			addresses.add(a);
		}
		return addresses;
	}
	/**
	 * @param user the user to the get the addresses of
	 * @return the user's addresses
	 * @throws SQLException an exception from the database
	 */
	public List<Address> getByUser(final User user) throws SQLException {
		List<Address> addresses = new ArrayList<>();
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspGetShippingAddressesByUser(?)");
		
		stmt.setInt(++n, user.getUserID());
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Address a = new Address();
			a.setAddressID(rs.getInt("addressID"));
			a.setLine1(rs.getString("addressLine1"));
			a.setLine2(rs.getString("addressLine2"));
			a.setCity(rs.getString("city"));
			a.setState(rs.getString("state"));
			a.setZip(rs.getString("zipcode"));
			a.setOwnerID(rs.getInt("userID"));
			a.setType(AddressType.SHIPPING);

			addresses.add(a);
		}
		return addresses;
	}

	/* (non-Javadoc)
	 * @see model.DAO#get(int)
	 */
	@Override
	public Address get(final int pId) throws SQLException {
		Address a = new Address();
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspGetAddress(?)");
		stmt.setInt(++n, pId);
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) {
			a.setAddressID(rs.getInt("addressID"));
			a.setLine1(rs.getString("addressLine1"));
			a.setLine2(rs.getString("addressLine2"));
			a.setCity(rs.getString("city"));
			a.setState(rs.getString("state"));
			a.setZip(rs.getString("zipcode"));
			if (rs.getInt("userID") != 0) {
				a.setOwnerID(rs.getInt("userID"));
				a.setType(AddressType.SHIPPING);
			} else if (rs.getInt("cardID") != 0) {
				a.setOwnerID(rs.getInt("cardID"));
				a.setType(AddressType.CARD);
			} else if (rs.getInt("publisherID") != 0) {
				a.setOwnerID(rs.getInt("publisherID"));
				a.setType(AddressType.PUBLISHER);
			}
		}
		return a;
	}
	
	/**
	 * @param pId the id of the billing address
	 * @return the billing address
	 * @throws SQLException an exception from the database
	 */
	public Address getBilling(final int pId) throws SQLException {
		Address a = new Address();
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspGetBillingAddress(?)");
		stmt.setInt(++n, pId);
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) {
			a.setAddressID(rs.getInt("addressID"));
			a.setLine1(rs.getString("addressLine1"));
			a.setLine2(rs.getString("addressLine2"));
			a.setCity(rs.getString("city"));
			a.setState(rs.getString("state"));
			a.setZip(rs.getString("zipcode"));
			a.setOwnerID(rs.getInt("cardID"));
			a.setType(AddressType.CARD);
		}
		return a;
	}

	/* (non-Javadoc)
	 * @see model.DAO#update(java.lang.Object)
	 */
	@Override
	public void update(final Address pObjectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspUpdateAddress(?,?,?,?,?,?)");
		
		stmt.setInt(++n, pObjectToUpdate.getAddressID());
		stmt.setString(++n, pObjectToUpdate.getLine1());
		stmt.setString(++n, pObjectToUpdate.getLine2());
		stmt.setString(++n, pObjectToUpdate.getCity());
		stmt.setString(++n, pObjectToUpdate.getState());
		stmt.setString(++n, pObjectToUpdate.getZip());
		
		stmt.executeQuery();
	}

	/* (non-Javadoc)
	 * @see model.DAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(final Address pObjectToDelete) throws SQLException {
		CallableStatement stmt = c.prepareCall("call uspDeleteAddress(?)");
		int n = 0;
		stmt.setInt(++n, pObjectToDelete.getAddressID());
		stmt.executeQuery();
	}

	/**
	 * @param pObjectToUpdate the card to update
	 * @throws SQLException an exception from the database
	 */
	public void updateByCard(final Address pObjectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspUpdateAddressByCard(?,?,?,?,?,?)");
		
		stmt.setInt(++n, pObjectToUpdate.getOwnerID());
		stmt.setString(++n, pObjectToUpdate.getLine1());
		stmt.setString(++n, pObjectToUpdate.getLine2());
		stmt.setString(++n, pObjectToUpdate.getCity());
		stmt.setString(++n, pObjectToUpdate.getState());
		stmt.setString(++n, pObjectToUpdate.getZip());
		
		stmt.executeQuery();
	}

}
