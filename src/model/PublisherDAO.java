package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * the DAO for the publisher.
 * @author jdowd
 *
 */
public class PublisherDAO implements DAO<Publisher> {

	/**
	 * This is the connection to the database
	 */
	private Connection c;

	/**
	 * @param connect the connection to the database
	 * @throws SQLException an exception from the database
	 */
	public PublisherDAO(final Connection connect) throws SQLException {
		c = connect;
	}

	/* (non-Javadoc)
	 * @see model.DAO#update(java.lang.Object)
	 */
	@Override
	public void update(final Publisher pObjectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspUpdatePublisher(?,?,?,?)");
		stmt.setInt(++n, pObjectToUpdate.getPublisherID());
		stmt.setString(++n, pObjectToUpdate.getPublisherName());
		stmt.setString(++n, pObjectToUpdate.getPhoneNumber());
		stmt.setString(++n, pObjectToUpdate.getSalesRepName());

		stmt.executeQuery();
	}

	/* (non-Javadoc)
	 * @see model.DAO#create(java.lang.Object)
	 */
	@Override
	public void create(final Publisher pObjectToCreate) throws SQLException {
		// TODO Auto-generated method stub
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspCreatePublisher(?,?,?)}");
		stmt.setString(++n, pObjectToCreate.getPublisherName());
		stmt.setString(++n, pObjectToCreate.getPhoneNumber());
		stmt.setString(++n, pObjectToCreate.getSalesRepName());

		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			pObjectToCreate.setPublisherID(r.getInt("publisherID"));
		}
	}

	/* (non-Javadoc)
	 * @see model.DAO#getAll()
	 */
	@Override
	public List<Publisher> getAll() throws SQLException {
		// TODO Auto-generated method stub
		List<Publisher> myPublishers = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetAllPublishers()}");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Publisher tempPublisher = new Publisher();
			tempPublisher.setPhoneNumber(rs.getString("phoneNumber"));
			tempPublisher.setPublisherID(rs.getInt("publisherID"));
			tempPublisher.setPublisherName(rs.getString("publisherName"));
			tempPublisher.setSalesRepName(rs.getString("salesRepName"));

			myPublishers.add(tempPublisher);
		}
		return myPublishers;
	}

	/* (non-Javadoc)
	 * @see model.DAO#get(int)
	 */
	@Override
	public Publisher get(final int pId) throws SQLException {
		// TODO Auto-generated method stub
		Publisher myPublisher = new Publisher();
		CallableStatement stmt = c.prepareCall("{call uspGetPublisherWithAddress(?)}");
		int n = 0;
		stmt.setInt(++n, pId);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			myPublisher.setPhoneNumber(rs.getString("phoneNumber"));
			myPublisher.setPublisherID(rs.getInt("publisherID"));
			myPublisher.setPublisherName(rs.getString("publisherName"));
			myPublisher.setSalesRepName(rs.getString("salesRepName"));
			myPublisher.setExists(true);
			Address a = new Address();
			a.setAddressID(rs.getInt("addressID"));
			a.setCity(rs.getString("city"));
			a.setLine1(rs.getString("addressLine1"));
			a.setLine2(rs.getString("addressLine2"));
			a.setOwnerID(myPublisher.getPublisherID());
			a.setState(rs.getString("state"));
			a.setZip(rs.getString("zipcode"));
			a.setType(AddressType.PUBLISHER);
			myPublisher.getAddresses().add(a);
		}
		System.out.println("dao " + myPublisher.getAddresses().size());
		return myPublisher;
	}

	/* (non-Javadoc)
	 * @see model.DAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(final Publisher pObjectToDelete) throws SQLException {
		Publisher p = get(pObjectToDelete.getPublisherID());
		CallableStatement stmt = c.prepareCall("{call uspDeletePublisher(?)}");
		int n = 0;
		stmt.setInt(++n, p.getPublisherID());
		stmt.executeQuery();
	}

}
