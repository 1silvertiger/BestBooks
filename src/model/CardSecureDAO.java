package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * the DAO for credit cards.
 * @author jdowd
 *
 */
public class CardSecureDAO implements DAO<CardSecure> {

	/**
	 * This is the connection to the database.
	 */
	private Connection c;

	/**
	 * @param connect the connection to the database
	 * @throws SQLException an exception from the database
	 */
	public CardSecureDAO(final Connection connect) throws SQLException {
		c = connect;
	}

	/* (non-Javadoc)
	 * @see model.DAO#update(java.lang.Object)
	 */
	@Override
	public void update(final CardSecure pObjectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspUpdateCreditCard(?,?,?,?,?,?)");
		stmt.setInt(++n, pObjectToUpdate.getCardID());
		stmt.setInt(++n, pObjectToUpdate.getUserID());
		stmt.setString(++n, pObjectToUpdate.getFirstTwelveDigits());
		stmt.setString(++n, pObjectToUpdate.getLastFourDigits());
		stmt.setString(++n, pObjectToUpdate.getExpirationDate());
		stmt.setString(++n, pObjectToUpdate.getCardHolderName());

		stmt.executeQuery();
	}

	/* (non-Javadoc)
	 * @see model.DAO#create(java.lang.Object)
	 */
	@Override
	public void create(final CardSecure pObjectToCreate) throws SQLException {
		int n = 0;
		CallableStatement stmt = null;
		stmt = c.prepareCall("{call uspCreateCreditCard(?,?,?,?,?)}");
		stmt.setInt(++n, pObjectToCreate.getUserID());
		pObjectToCreate.encryptFirstTwelveDigits();
		stmt.setString(++n, pObjectToCreate.getFirstTwelveDigits());
		stmt.setString(++n, pObjectToCreate.getLastFourDigits());
		stmt.setString(++n, pObjectToCreate.getExpirationDate());
		stmt.setString(++n, pObjectToCreate.getCardHolderName());
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			pObjectToCreate.setCardID(rs.getInt("cardID"));
			if (pObjectToCreate.getAddress().getAddressID() != 0) {
				AddressDAO tempAddressDAO = new AddressDAO(c);
				Address a = tempAddressDAO.get(pObjectToCreate.getAddress().getAddressID());
				a.setOwnerID(pObjectToCreate.getCardID());
				tempAddressDAO.create(a);
				pObjectToCreate.setAddress(a);
			}
		}
	}

	/* (non-Javadoc)
	 * @see model.DAO#getAll()
	 */
	@Override
	public List<CardSecure> getAll() throws SQLException {

		List<CardSecure> myCards = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetAllCreditCardsWithAddresses()}");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			CardSecure tempCard = new CardSecure();
			Address a = new Address();
			tempCard.setCardID(rs.getInt("cardID"));
			tempCard.setUserID(rs.getInt("userID"));
			tempCard.setFirstTwelveDigits(rs.getString("firstTwelveDigits"));
			tempCard.setLastFourDigits(rs.getString("lastFourDigits"));
			tempCard.setExpirationDate(rs.getString("expirationDate"));
			tempCard.setCardHolderName(rs.getString("cardHolderName"));
			a.setAddressID(rs.getInt("addressID"));
			a.setCity(rs.getString("city"));
			a.setLine1(rs.getString("addressLine1"));
			a.setLine2(rs.getString("addressLine2"));
			a.setOwnerID(rs.getInt("cardID"));
			a.setState(rs.getString("state"));
			a.setType(AddressType.CARD);
			a.setZip(rs.getString("zipcode"));
			tempCard.setAddress(a);

			myCards.add(tempCard);
		}
		return myCards;
	}

	/* (non-Javadoc)
	 * @see model.DAO#get(int)
	 */
	@Override
	public CardSecure get(final int pId) throws SQLException {
		CardSecure myCard = new CardSecure();
		Address a = new Address();
		CallableStatement stmt = c.prepareCall("{call uspGetCreditCardWithAddress(?)}");
		int n = 0;
		stmt.setInt(++n, pId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			myCard.setCardID(rs.getInt("cardID"));
			myCard.setUserID(rs.getInt("userID"));
			myCard.setFirstTwelveDigits(rs.getString("firstTwelveDigits"));
			myCard.setLastFourDigits(rs.getString("lastFourDigits"));
			myCard.setExpirationDate(rs.getString("expirationDate"));
			myCard.setCardHolderName(rs.getString("cardHolderName"));
			a.setAddressID(rs.getInt("addressID"));
			a.setCity(rs.getString("city"));
			a.setLine1(rs.getString("addressLine1"));
			a.setLine2(rs.getString("addressLine2"));
			a.setOwnerID(rs.getInt("cardID"));
			a.setState(rs.getString("state"));
			a.setType(AddressType.CARD);
			a.setZip(rs.getString("zipcode"));
			myCard.setAddress(a);
		}
		return myCard;
	}

	/* (non-Javadoc)
	 * @see model.DAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(final CardSecure pObjectToDelete) throws SQLException {
		CallableStatement stmt = c.prepareCall("{call uspDeleteCreditCard(?)}");
		int n = 0;
		stmt.setInt(++n, pObjectToDelete.getCardID());
		stmt.executeQuery();
	}

	/**
	 * @param user the user to get cards for
	 * @return the user's cards
	 * @throws SQLException an exception from the database
	 */
	public List<CardSecure> getCardsByUserID(final User user) throws SQLException {

		List<CardSecure> myCards = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspGetCreditCardsByUser(?)}");
		int n = 0;
		stmt.setInt(++n, user.getUserID());

		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			CardSecure tempCard = new CardSecure();
			tempCard.setCardID(rs.getInt("cardID"));
			tempCard.setUserID(rs.getInt("userID"));
			tempCard.setFirstTwelveDigits(rs.getString("firstTwelveDigits"));
			tempCard.setLastFourDigits(rs.getString("lastFourDigits"));
			tempCard.setExpirationDate(rs.getString("expirationDate"));
			tempCard.setCardHolderName(rs.getString("cardHolderName"));

			myCards.add(tempCard);
		}
		return myCards;
	}

}
