package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * the DAO for orders.
 * @author jdowd
 *
 */
public class OrderDAO implements DAO<Order> {


	/**
	 * This is the connection to the database
	 */
	private Connection c;

	/**
	 * @param connect connection to the database
	 * @throws SQLException an exception from the database
	 */
	public OrderDAO(final Connection connect) throws SQLException {
		
		c = connect;
	}
	
	// must reflect changes to table
	/* (non-Javadoc)
	 * @see model.DAO#update(java.lang.Object)
	 */
	@Override
	public void update(final Order objectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspUpdateOrder(?,?,?,?,?)}");
		stmt.setInt(++n, objectToUpdate.getOrderID());
		stmt.setString(++n, objectToUpdate.getOrderStatus());
		stmt.setInt(++n, objectToUpdate.getCardID());
		stmt.setInt(++n, objectToUpdate.getAddressID());
		stmt.setDouble(++n, objectToUpdate.getDiscountPercent());
		stmt.executeQuery();
	}

	/**
	 * update an order item.
	 * @param b the order item to update
	 * @throws SQLException an exception from the database
	 */
	public void updateOrderItem(final OrderItemBook b) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspUpdateOrderItem(?,?,?,?,?)");
		stmt.setInt(++n, b.getOrderItemID());
		stmt.setInt(++n, b.getOrderID());
		stmt.setInt(++n, b.getBookID());
		stmt.setDouble(++n, b.getRetailPrice());
		stmt.setInt(++n, b.getQuantity());
		stmt.executeQuery();
	}

	/* (non-Javadoc)
	 * @see model.DAO#create(java.lang.Object)
	 */
	@Override
	public void create(final Order objectToCreate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspCreateOrder(?,?,?,?,?)}");
		stmt.setInt(++n, objectToCreate.getUserID());
		stmt.setTimestamp(++n, objectToCreate.getCreationDateAsTimestamp());
		stmt.setString(++n, objectToCreate.getOrderStatus());
		stmt.setInt(++n, objectToCreate.getCardID());
		stmt.setInt(++n, objectToCreate.getAddressID());
		stmt.setDouble(++n, objectToCreate.getDiscountPercent());
		
		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			objectToCreate.setOrderID(r.getInt("orderID"));
		}

	}

	/**
	 * @param b the order item to create
	 * @throws SQLException an exception from the database
	 */
	public void createOrderItem(final OrderItemBook b) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("call uspCreateOrderItem(?,?,?)");
		stmt.setInt(++n, b.getOrderID());
		stmt.setInt(++n, b.getBookID());
		stmt.setInt(++n, b.getQuantity());

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			b.setOrderItemID(rs.getInt("orderItemID"));
		}
	}

	/**
	 * @param objectToCreate order to create
	 * @throws SQLException an exception from the database
	 */
	public void createOrderFromCart(final Order objectToCreate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspCreateOrderFromCart(?,?,?,?,?,?)}");
		stmt.setInt(++n, objectToCreate.getUserID());
		stmt.setTimestamp(++n, objectToCreate.getCreationDateAsTimestamp());
		stmt.setString(++n, objectToCreate.getOrderStatus());
		stmt.setInt(++n, objectToCreate.getCardID());
		stmt.setInt(++n, objectToCreate.getAddressID());
		stmt.setDouble(++n, objectToCreate.getDiscountPercent());
		
		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			objectToCreate.setOrderID(r.getInt("orderID"));
		}
	}

	/* (non-Javadoc)
	 * @see model.DAO#getAll()
	 */
	@Override
	public List<Order> getAll() throws SQLException {
		List<Order> myOrders = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetAllOrders()}");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Order o = new Order();
			OrderItemBook i = new OrderItemBook();
			o.setOrderID(rs.getInt("orderID"));
			o.setUserID(rs.getInt("userID"));
			o.setCreationDate(rs.getTimestamp("creationDate"));
			o.setOrderStatus(rs.getString("orderStatus"));
			o.setDiscountPercent(rs.getDouble("discountPercent"));
			o.getAddress().setAddressID(rs.getInt("shippingAddressID"));
			o.getAddress().setCity(rs.getString("shippingAddressCity"));
			o.getAddress().setLine1(rs.getString("shippingAddressLine1"));
			o.getAddress().setLine2(rs.getString("shippingAddressLine2"));
			o.getAddress().setOwnerID(o.getUserID());
			o.getAddress().setState(rs.getString("shippingAddressState"));
			o.getAddress().setType(AddressType.SHIPPING);
			o.getAddress().setZip(rs.getString("shippingAddressZipcode"));
			o.getCard().setCardHolderName(rs.getString("cardHolderName"));
			o.getCard().setCardID(rs.getInt("cardID"));
			o.getCard().setUserID(rs.getInt("userID"));
			o.getCard().setExpirationDate(rs.getString("expirationDate"));
			o.getCard().setFirstTwelveDigits(rs.getString("firstTwelveDigits"));
			o.getCard().setLastFourDigits(rs.getString("lastFourDigits"));
			o.getCard().getAddress().setAddressID(rs.getInt("billingAddressID"));
			o.getCard().getAddress().setCity(rs.getString("billingAddressCity"));
			o.getCard().getAddress().setLine1(rs.getString("billingAddressLine1"));
			o.getCard().getAddress().setLine2(rs.getString("billingAddressLine2"));
			o.getCard().getAddress().setOwnerID(o.getCard().getCardID());
			o.getCard().getAddress().setState(rs.getString("billingAddressState"));
			o.getCard().getAddress().setType(AddressType.CARD);
			o.getCard().getAddress().setZip(rs.getString("billingAddressZipcode"));
			i.setAuthor(rs.getString("author"));
			i.setBookID(rs.getInt("bookID"));
			i.setGenre(rs.getString("genre"));
			i.setIsbn10(rs.getString("ISBN10"));
			i.setIsbn13(rs.getString("ISBN13"));
			i.setPublisherID(rs.getInt("publisherID"));
			i.setRetailPrice(rs.getDouble("retailPrice"));
			i.setSalePrice(rs.getDouble("salePrice"));
			i.setShippingWeight(rs.getDouble("shippingWeight"));
			i.setTitle(rs.getString("title"));
			i.setOrderID(o.getOrderID());
			i.setQuantity(rs.getInt("quantity"));
			i.setOrderItemID(rs.getInt("orderItemID"));
			o.getItems().add(i);
			if (!myOrders.isEmpty()) {
				if (!myOrders.get(myOrders.size() - 1).equals(o)) {
					myOrders.add(o);
				} else {
					myOrders.get(myOrders.size() - 1).getItems().add(i);
				}
			} else {
				myOrders.add(o);
			}
		}
		return myOrders;
	}

	/**
	 * @param userID the user to get orders for
	 * @return the orders for that user
	 * @throws SQLException an exception from the database
	 */
	public List<Order> getByUser(final int userID) throws SQLException {
		List<Order> myOrders = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetOrdersWithItemsAndBooksByUser(?)}");
		int n = 0;
		stmt.setInt(++n, userID);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Order o = new Order();
			OrderItemBook i = new OrderItemBook();
			o.setOrderID(rs.getInt("orderID"));
			o.setUserID(rs.getInt("userID"));
			o.setCreationDate(rs.getTimestamp("creationDate"));
			o.setOrderStatus(rs.getString("orderStatus"));
			o.setDiscountPercent(rs.getDouble("discountPercent"));
			o.getAddress().setAddressID(rs.getInt("shippingAddressID"));
			o.getAddress().setCity(rs.getString("shippingAddressCity"));
			o.getAddress().setLine1(rs.getString("shippingAddressLine1"));
			o.getAddress().setLine2(rs.getString("shippingAddressLine2"));
			o.getAddress().setOwnerID(o.getUserID());
			o.getAddress().setState(rs.getString("shippingAddressState"));
			o.getAddress().setType(AddressType.SHIPPING);
			o.getAddress().setZip(rs.getString("shippingAddressZipcode"));
			o.getCard().setCardHolderName(rs.getString("cardHolderName"));
			o.getCard().setCardID(rs.getInt("cardID"));
			o.getCard().setUserID(rs.getInt("userID"));
			o.getCard().setExpirationDate(rs.getString("expirationDate"));
			o.getCard().setFirstTwelveDigits(rs.getString("firstTwelveDigits"));
			o.getCard().setLastFourDigits(rs.getString("lastFourDigits"));
			o.getCard().getAddress().setAddressID(rs.getInt("billingAddressID"));
			o.getCard().getAddress().setCity(rs.getString("billingAddressCity"));
			o.getCard().getAddress().setLine1(rs.getString("billingAddressLine1"));
			o.getCard().getAddress().setLine2(rs.getString("billingAddressLine2"));
			o.getCard().getAddress().setOwnerID(o.getCard().getCardID());
			o.getCard().getAddress().setState(rs.getString("billingAddressState"));
			o.getCard().getAddress().setType(AddressType.CARD);
			o.getCard().getAddress().setZip(rs.getString("billingAddressZipcode"));
			i.setAuthor(rs.getString("author"));
			i.setBookID(rs.getInt("bookID"));
			i.setGenre(rs.getString("genre"));
			i.setIsbn10(rs.getString("ISBN10"));
			i.setIsbn13(rs.getString("ISBN13"));
			i.setPublisherID(rs.getInt("publisherID"));
			i.setRetailPrice(rs.getDouble("retailPrice"));
			i.setSalePrice(rs.getDouble("salePrice"));
			i.setShippingWeight(rs.getDouble("shippingWeight"));
			i.setTitle(rs.getString("title"));
			i.setOrderID(o.getOrderID());
			i.setQuantity(rs.getInt("quantity"));
			i.setOrderItemID(rs.getInt("orderItemID"));
			o.getItems().add(i);
			if (!myOrders.isEmpty()) {
				if (!myOrders.get(myOrders.size() - 1).equals(o)) {
					myOrders.add(o);
				} else {
					myOrders.get(myOrders.size() - 1).getItems().add(i);
				}
			} else {
				myOrders.add(o);
			}
		}

		return myOrders;
	}

	// change to reflect updates in table structure
	/* (non-Javadoc)
	 * @see model.DAO#get(int)
	 */
	@Override
	public Order get(final int id) throws SQLException {
		Order o = new Order();
		CallableStatement stmt = c.prepareCall("{call uspGetOrdersWithItemsAndBooksByOrder(?)}");
		int n = 0;
		stmt.setInt(++n, id);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			OrderItemBook i = new OrderItemBook();
			o.setOrderID(rs.getInt("orderID"));
			o.setUserID(rs.getInt("userID"));
			o.setCreationDate(rs.getTimestamp("creationDate"));
			o.setOrderStatus(rs.getString("orderStatus"));
			o.setAddressID(rs.getInt("shippingAddressID"));
			o.setDiscountPercent(rs.getDouble("discountPercent"));
			o.setCardID(rs.getInt("cardID"));
			o.getAddress().setAddressID(rs.getInt("shippingAddressID"));
			o.getAddress().setCity(rs.getString("shippingAddressCity"));
			o.getAddress().setLine1(rs.getString("shippingAddressLine1"));
			o.getAddress().setLine2(rs.getString("shippingAddressLine2"));
			o.getAddress().setOwnerID(o.getUserID());
			o.getAddress().setState(rs.getString("shippingAddressState"));
			o.getAddress().setType(AddressType.SHIPPING);
			o.getAddress().setZip(rs.getString("shippingAddressZipcode"));
			o.getCard().setCardHolderName(rs.getString("cardHolderName"));
			o.getCard().setCardID(rs.getInt("cardID"));
			o.getCard().setUserID(rs.getInt("userID"));
			o.getCard().setExpirationDate(rs.getString("expirationDate"));
			o.getCard().setFirstTwelveDigits(rs.getString("firstTwelveDigits"));
			o.getCard().setLastFourDigits(rs.getString("lastFourDigits"));
			o.getCard().getAddress().setAddressID(rs.getInt("billingAddressID"));
			o.getCard().getAddress().setCity(rs.getString("billingAddressCity"));
			o.getCard().getAddress().setLine1(rs.getString("billingAddressLine1"));
			o.getCard().getAddress().setLine2(rs.getString("billingAddressLine2"));
			o.getCard().getAddress().setOwnerID(o.getCard().getCardID());
			o.getCard().getAddress().setState(rs.getString("billingAddressState"));
			o.getCard().getAddress().setType(AddressType.CARD);
			o.getCard().getAddress().setZip(rs.getString("billingAddressZipcode"));
			i.setAuthor(rs.getString("author"));
			i.setBookID(rs.getInt("bookID"));
			i.setGenre(rs.getString("genre"));
			i.setIsbn10(rs.getString("ISBN10"));
			i.setIsbn13(rs.getString("ISBN13"));
			i.setPublisherID(rs.getInt("publisherID"));
			i.setRetailPrice(rs.getDouble("retailPrice"));
			i.setSalePrice(rs.getDouble("salePrice"));
			i.setShippingWeight(rs.getDouble("shippingWeight"));
			i.setTitle(rs.getString("title"));
			i.setOrderID(o.getOrderID());
			i.setQuantity(rs.getInt("quantity"));
			i.setOrderItemID(rs.getInt("orderItemID"));
			o.getItems().add(i);
			o.setAddressID(o.getAddress().getAddressID());
			o.setCardID(o.getCard().getCardID());
		}
		return o;
	}

	/* (non-Javadoc)
	 * @see model.DAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(final Order objectToDelete) throws SQLException {
		CallableStatement stmt = c.prepareCall("{call uspDeleteOrder(?)}");
		int n = 0;
		stmt.setInt(++n, objectToDelete.getOrderID());
		stmt.executeQuery();
	}

	/**
	 * @param pOrderItemID the id of the order item to delete
	 * @throws SQLException an exception from the database
	 */
	public void deleteItemFromOrder(final int pOrderItemID) throws SQLException {
		CallableStatement stmt = c.prepareCall("call uspDeleteOrderItem(?)");
		int n = 0;
		stmt.setInt(++n, pOrderItemID);
		stmt.executeQuery();
	}
	
	/**
	 * @param orderID the order to cancel
	 * @throws SQLException an exception from the database
	 */
	public void cancelOrder(final int orderID) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspUpdateOrderStatus(?,?)}");
		stmt.setInt(++n, orderID);
		stmt.setString(++n, "Canceled");
		stmt.executeQuery();
	}
	
	/**
	 * @param objectToDelete the orders to delete by user
	 * @throws SQLException an exception from the database
	 */
	public void deleteByUser(final User objectToDelete) throws SQLException {
		CallableStatement stmt = c.prepareCall("{call uspDeleteOrdersByUser(?)}");
		int n = 0;
		stmt.setInt(++n, objectToDelete.getUserID());
		stmt.executeQuery();
	}

	/**
	 * @param user the user to get the orders for
	 * @return the orders from that user
	 * @throws SQLException an exception from the database
	 */
	public List<Order> getByUserWithTotals(final User user) throws SQLException {
		List<Order> myOrders = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetOrdersWithTotalsByUser(?)}");
		int n = 0;
		stmt.setInt(++n, user.getUserID());
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Order tempOrder = new Order();
			tempOrder.setOrderID(rs.getInt("orderID"));
			tempOrder.setUserID(rs.getInt("userID"));
			tempOrder.setCreationDate(rs.getTimestamp("creationDate"));
			tempOrder.setOrderStatus(rs.getString("orderStatus"));
			tempOrder.setCardID(rs.getInt("cardID"));
			tempOrder.setAddressID(rs.getInt("addressID"));
			tempOrder.setTotalCost(rs.getDouble("totalCost"));
			tempOrder.setDiscountPercent(rs.getDouble("discountPercent"));
			myOrders.add(tempOrder);
		}
		return myOrders;
	}

	/**
	 * @param userID the user to get the items for
	 * @return the order items for a user
	 * @throws SQLException an exception from the database
	 */
	public List <OrderBookItem> getWithBooksByUser(final int userID) throws SQLException {
		List<OrderBookItem> myItems = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetOrdersWithItemsAndBooksByUser(?)}");
		int n = 0;
		stmt.setInt(++n, userID);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			OrderBookItem tempItem = new OrderBookItem();
			tempItem.setAuthor(rs.getString("author"));
			tempItem.setBookID(rs.getInt("bookID"));
			tempItem.setGenre(rs.getString("genre"));
			tempItem.setIsbn10(rs.getString("ISBN10"));
			tempItem.setIsbn13(rs.getString("ISBN13"));
			tempItem.setRetailPrice(rs.getDouble("retailPrice"));
			tempItem.setShippingWeight(rs.getDouble("shippingWeight"));
			tempItem.setTitle(rs.getString("title"));
			tempItem.setQuantity(rs.getInt("quantity"));
			tempItem.setSalePrice(rs.getDouble("salePrice"));
			tempItem.setOrderID(rs.getInt("orderID"));
			tempItem.setOrderItemID(rs.getInt("orderItemID"));
			tempItem.setUserID(rs.getInt("userID"));
			tempItem.setCreationDate(rs.getTimestamp("creationDate"));
			tempItem.setOrderStatus(rs.getString("orderStatus"));
			tempItem.setCardID(rs.getInt("cardID"));
			tempItem.setAddressID(rs.getInt("shippingAddressID"));
			tempItem.setAddressLine1(rs.getString("shippingAddressLine1"));
			tempItem.setDiscountPercent(rs.getDouble("discountPercent"));
			myItems.add(tempItem);
		}
		return myItems;
	}
	
	/**
	 * @param orderID the order to get detail for
	 * @return the items from that order
	 * @throws SQLException an exception from the database
	 */
	public List <OrderBookItem> getWithBooksByOrder(final int orderID) throws SQLException {
		List<OrderBookItem> myItems = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetOrdersWithItemsAndBooksByOrder(?)}");
		int n = 0;
		stmt.setInt(++n, orderID);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			OrderBookItem tempItem = new OrderBookItem();
			tempItem.setAuthor(rs.getString("author"));
			tempItem.setBookID(rs.getInt("bookID"));
			tempItem.setGenre(rs.getString("genre"));
			tempItem.setIsbn10(rs.getString("ISBN10"));
			tempItem.setIsbn13(rs.getString("ISBN13"));
			tempItem.setRetailPrice(rs.getDouble("retailPrice"));
			tempItem.setShippingWeight(rs.getDouble("shippingWeight"));
			tempItem.setTitle(rs.getString("title"));
			tempItem.setQuantity(rs.getInt("quantity"));
			tempItem.setSalePrice(rs.getDouble("salePrice"));
			tempItem.setOrderID(rs.getInt("orderID"));
			tempItem.setOrderItemID(rs.getInt("orderItemID"));
			tempItem.setUserID(rs.getInt("userID"));
			tempItem.setCreationDate(rs.getTimestamp("creationDate"));
			tempItem.setOrderStatus(rs.getString("orderStatus"));
			tempItem.setCardID(rs.getInt("cardID"));
			tempItem.setAddressID(rs.getInt("shippingAddressID"));
			tempItem.setAddressLine1(rs.getString("shippingAddressLine1"));
			tempItem.setDiscountPercent(rs.getDouble("discountPercent"));
			myItems.add(tempItem);
		}
		return myItems;
	}
	
	/**
	 * @param orderID the order to get detail for
	 * @param currentUser the user to get detail for
	 * @return the items from that order
	 * @throws SQLException an exception from the database
	 */
	public List <OrderBookItem> getWithBooksByOrderAndUser(final int orderID, final User currentUser) throws SQLException {
		List<OrderBookItem> myItems = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetOrdersWithItemsAndBooksByOrderAndUser(?,?)}");
		int n = 0;
		stmt.setInt(++n, orderID);
		stmt.setInt(++n, currentUser.getUserID());
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			OrderBookItem tempItem = new OrderBookItem();
			tempItem.setAuthor(rs.getString("author"));
			tempItem.setBookID(rs.getInt("bookID"));
			tempItem.setGenre(rs.getString("genre"));
			tempItem.setIsbn10(rs.getString("ISBN10"));
			tempItem.setIsbn13(rs.getString("ISBN13"));
			tempItem.setRetailPrice(rs.getDouble("retailPrice"));
			tempItem.setShippingWeight(rs.getDouble("shippingWeight"));
			tempItem.setTitle(rs.getString("title"));
			tempItem.setQuantity(rs.getInt("quantity"));
			tempItem.setSalePrice(rs.getDouble("salePrice"));
			tempItem.setOrderID(rs.getInt("orderID"));
			tempItem.setOrderItemID(rs.getInt("orderItemID"));
			tempItem.setUserID(rs.getInt("userID"));
			tempItem.setCreationDate(rs.getTimestamp("creationDate"));
			tempItem.setOrderStatus(rs.getString("orderStatus"));
			tempItem.setCardID(rs.getInt("cardID"));
			tempItem.setAddressID(rs.getInt("shippingAddressID"));
			tempItem.setAddressLine1(rs.getString("shippingAddressLine1"));
			tempItem.setDiscountPercent(rs.getDouble("discountPercent"));
			myItems.add(tempItem);
		}
		return myItems;
	}
	/**
	 * @param orderID the order to get detail for
	 * @param currentUser the user to get detail for
	 * @return the items from that order
	 * @throws SQLException an exception from the database
	 */
	public Order getWithBooksByOrderAndUser(final int orderID, final int currentUser) throws SQLException {
		Order o = new Order();
		CallableStatement stmt = c.prepareCall("{call uspGetOrdersWithItemsAndBooksByOrderAndUser(?,?)}");
		int n = 0;
		stmt.setInt(++n, orderID);
		stmt.setInt(++n, currentUser);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			OrderItemBook i = new OrderItemBook();
			o.setOrderID(rs.getInt("orderID"));
			o.setUserID(rs.getInt("userID"));
			o.setCreationDate(rs.getTimestamp("creationDate"));
			o.setOrderStatus(rs.getString("orderStatus"));
			o.setAddressID(rs.getInt("shippingAddressID"));
			o.setDiscountPercent(rs.getDouble("discountPercent"));
			o.setCardID(rs.getInt("cardID"));
			o.getAddress().setAddressID(rs.getInt("shippingAddressID"));
			o.getAddress().setCity(rs.getString("shippingAddressCity"));
			o.getAddress().setLine1(rs.getString("shippingAddressLine1"));
			o.getAddress().setLine2(rs.getString("shippingAddressLine2"));
			o.getAddress().setOwnerID(o.getUserID());
			o.getAddress().setState(rs.getString("shippingAddressState"));
			o.getAddress().setType(AddressType.SHIPPING);
			o.getAddress().setZip(rs.getString("shippingAddressZipcode"));
			o.getCard().setCardHolderName(rs.getString("cardHolderName"));
			o.getCard().setCardID(rs.getInt("cardID"));
			o.getCard().setUserID(rs.getInt("userID"));
			o.getCard().setExpirationDate(rs.getString("expirationDate"));
			o.getCard().setFirstTwelveDigits(rs.getString("firstTwelveDigits"));
			o.getCard().setLastFourDigits(rs.getString("lastFourDigits"));
			o.getCard().getAddress().setAddressID(rs.getInt("billingAddressID"));
			o.getCard().getAddress().setCity(rs.getString("billingAddressCity"));
			o.getCard().getAddress().setLine1(rs.getString("billingAddressLine1"));
			o.getCard().getAddress().setLine2(rs.getString("billingAddressLine2"));
			o.getCard().getAddress().setOwnerID(o.getCard().getCardID());
			o.getCard().getAddress().setState(rs.getString("billingAddressState"));
			o.getCard().getAddress().setType(AddressType.CARD);
			o.getCard().getAddress().setZip(rs.getString("billingAddressZipcode"));
			i.setAuthor(rs.getString("author"));
			i.setBookID(rs.getInt("bookID"));
			i.setGenre(rs.getString("genre"));
			i.setIsbn10(rs.getString("ISBN10"));
			i.setIsbn13(rs.getString("ISBN13"));
			i.setPublisherID(rs.getInt("publisherID"));
			i.setRetailPrice(rs.getDouble("retailPrice"));
			i.setSalePrice(rs.getDouble("salePrice"));
			i.setShippingWeight(rs.getDouble("shippingWeight"));
			i.setTitle(rs.getString("title"));
			i.setOrderID(o.getOrderID());
			i.setQuantity(rs.getInt("quantity"));
			i.setOrderItemID(rs.getInt("orderItemID"));
			o.getItems().add(i);
			o.setAddressID(o.getAddress().getAddressID());
			o.setCardID(o.getCard().getCardID());
		}
		return o;
	}
}
