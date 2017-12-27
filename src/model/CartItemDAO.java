package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * the DAO for cart items.
 * @author jdowd
 *
 */
public class CartItemDAO implements DAO<CartItem> {

	/**
	 * This is the connection to the database.
	 */
	private Connection c;

	/**
	 * @param connect the connection to the database
	 * @throws SQLException an exception from the database
	 */
	public CartItemDAO(final Connection connect) throws SQLException {
		c = connect;
	}

	@Override
	public final void update(
			final CartItem objectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt =
				c.prepareCall(
					"{call uspUpdateCartItem(?,?,?,?)}");
		stmt.setInt(++n, objectToUpdate.getCartItemID());
		stmt.setInt(++n, objectToUpdate.getBookID());
		stmt.setInt(++n, objectToUpdate.getUserID());
		stmt.setInt(++n, objectToUpdate.getQuantity());
		stmt.executeQuery();
	}

	/**
	 * updates only quantity.
	 * @param objectToUpdate the object to update
	 * @throws SQLException an exception from the database
	 */
	public final void updateQuantity(
			final CartItem objectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt =
				c.prepareCall(
				"{call uspUpdateCartItemQuantity(?,?)}");
		stmt.setInt(++n, objectToUpdate.getCartItemID());
		stmt.setInt(++n, objectToUpdate.getQuantity());
		stmt.executeQuery();
	}

	@Override
	public final void create(
			final CartItem objectToCreate) throws SQLException {
		int n = 0;
		CallableStatement stmt =
				c.prepareCall(
					"{call uspCreateCartItem(?,?,?)}");
		stmt.setInt(++n, objectToCreate.getBookID());
		stmt.setInt(++n, objectToCreate.getUserID());
		stmt.setInt(++n, objectToCreate.getQuantity());

		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			objectToCreate.setCartItemID(r.getInt("cartItemID"));
		}
	}

	@Override
	public final List<CartItem> getAll() throws SQLException {
		List<CartItem> myCarts = new ArrayList<>();
		CallableStatement stmt =
				c.prepareCall(
						"{call uspGetAllCartItems()}");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			CartItem tempCart = new CartItem();
			tempCart.setCartItemID(rs.getInt("cartItemID"));
			tempCart.setBookID(rs.getInt("bookID"));
			tempCart.setUserID(rs.getInt("userID"));
			tempCart.setQuantity(rs.getInt("quantity"));
			myCarts.add(tempCart);
		}
		return myCarts;
	}


	/**
	 * @param userID the user id to get
	 * @return the cart items for that user
	 * @throws SQLException an exception from the database
	 */
	public final List<CartItem> getCartByUser(
			final int userID) throws SQLException {
		List<CartItem> myCarts = new ArrayList<>();
		CallableStatement stmt =
				c.prepareCall(
					"{call uspGetCartItemsByUser(?)}");
		int n = 0;
		stmt.setInt(++n, userID);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			CartItem tempCart = new CartItem();
			tempCart.setCartItemID(rs.getInt("cartItemID"));
			tempCart.setBookID(rs.getInt("bookID"));
			tempCart.setUserID(rs.getInt("userID"));
			tempCart.setQuantity(rs.getInt("quantity"));
			myCarts.add(tempCart);
		}
		return myCarts;
	}

	/**
	 * @param userID the user id to get
	 * @return the cart items with book data
	 * 			for that user
	 * @throws SQLException an exception from the database
	 */
	public final List<InvCartBook> getCartByUserWithBook(
			final int userID) throws SQLException {
		List<InvCartBook> myCarts = new ArrayList<>();
		CallableStatement stmt =
				c.prepareCall(
				"{call uspGetCartItemsByUserWithBooks(?)}");
		int n = 0;
		stmt.setInt(++n, userID);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InvCartBook tempCart = new InvCartBook();
			tempCart.setCartID(rs.getInt("cartItemID"));
			tempCart.setTitle(rs.getString("title"));
			tempCart.setAuthor(rs.getString("author"));
			tempCart.setQuantity(rs.getInt("quantity"));
			tempCart.setCost(rs.getDouble("cost"));
			tempCart.setWeight(rs.getDouble("shippingWeight"));
			tempCart.setQuantityOnHand(rs.getInt("quantityOnHand"));
			myCarts.add(tempCart);
		}
		return myCarts;
	}

	@Override
	public final CartItem get(final int id) throws SQLException {
		CartItem myCart = new CartItem();
		CallableStatement stmt = c.prepareCall("{call uspGetCart(?)}");
		int n = 0;
		stmt.setInt(++n, id);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			myCart.setCartItemID(rs.getInt("cartItemID"));
			myCart.setBookID(rs.getInt("bookID"));
			myCart.setUserID(rs.getInt("userID"));
			myCart.setQuantity(rs.getInt("quantity"));
		}
		return myCart;
	}

	@Override
	public final void delete(
			final CartItem objectToDelete) throws SQLException {
		CallableStatement stmt =
				c.prepareCall(
						"{call uspDeleteCartItem(?)}");
		int n = 0;
		stmt.setInt(++n, objectToDelete.getCartItemID());
		stmt.executeQuery();
	}

	/**
	 * deletes all the cart items for the user.
	 * @param objectToDelete the object to delete
	 * @throws SQLException an exception from the database
	 */
	public final void deleteByUser(
			final User objectToDelete) throws SQLException {
		CallableStatement stmt =
				c.prepareCall(
					"{call uspDeleteCartItemsByUser(?)}");
		int n = 0;
		stmt.setInt(++n, objectToDelete.getUserID());
		stmt.executeQuery();
	}

}
