package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * an object with values from
 * orders, order items, inventory,
 * and books.
 * @author jdowd
 *
 */
public class OrderBookItem {
	/** the object's order item id. */
	private int orderItemID;
	/** the object's order id. */
	private int orderID;
	/** the object's book id. */
	private int bookID;
	/** the object's sale price. */
	private double salePrice;
	/** the object's quantity. */
	private int quantity;
	/** the object's title. */
	private String title;
	/** the object's author. */
	private String author;
	/** the object's isbn 13. */
	private String isbn13;
	/** the object's isbn 10. */
	private String isbn10;
	/** the object's shipping weight. */
	private double shippingWeight;
	/** the object's genre. */
	private String genre;
	/** the object's retail price. */
	private double retailPrice;
	/** the object's user id. */
	private int userID;
	/** the object's creation date. */
	private LocalDateTime creationDate;
	/** the object's order status. */
	private String orderStatus;
	/** the object's card id. */
	private int cardID;
	/** the object's address id. */
	private int addressID;
	/** the object's address line 1. */
	private String addressLine1;
	/** the object's discount percentage. */
	private double discountPercent;
	
	/**
	 * @return the orderItemID
	 */
	public int getOrderItemID() {
		return orderItemID;
	}
	/**
	 * @param orderItemID the orderItemID to set
	 */
	public void setOrderItemID(final int orderItemID) {
		this.orderItemID = orderItemID;
	}
	/**
	 * @return the orderID
	 */
	public int getOrderID() {
		return orderID;
	}
	/**
	 * @param orderID the orderID to set
	 */
	public void setOrderID(final int orderID) {
		this.orderID = orderID;
	}
	/**
	 * @return the bookID
	 */
	public int getBookID() {
		return bookID;
	}
	/**
	 * @param bookID the bookID to set
	 */
	public void setBookID(final int bookID) {
		this.bookID = bookID;
	}
	/**
	 * @return the salePrice
	 */
	public double getSalePrice() {
		return salePrice;
	}
	/**
	 * @param salePrice the salePrice to set
	 */
	public void setSalePrice(final double salePrice) {
		this.salePrice = salePrice;
	}
	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(final String author) {
		this.author = author;
	}
	/**
	 * @return the isbn13
	 */
	public String getIsbn13() {
		return isbn13;
	}
	/**
	 * @param isbn13 the isbn13 to set
	 */
	public void setIsbn13(final String isbn13) {
		this.isbn13 = isbn13;
	}
	/**
	 * @return the isbn10
	 */
	public String getIsbn10() {
		return isbn10;
	}
	/**
	 * @param isbn10 the isbn10 to set
	 */
	public void setIsbn10(final String isbn10) {
		this.isbn10 = isbn10;
	}
	/**
	 * @return the shippingWeight
	 */
	public double getShippingWeight() {
		return shippingWeight;
	}
	/**
	 * @param shippingWeight the shippingWeight to set
	 */
	public void setShippingWeight(final double shippingWeight) {
		this.shippingWeight = shippingWeight;
	}
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(final String genre) {
		this.genre = genre;
	}
	/**
	 * @return the retailPrice
	 */
	public double getRetailPrice() {
		return retailPrice;
	}
	/**
	 * @param retailPrice the retailPrice to set
	 */
	public void setRetailPrice(final double retailPrice) {
		this.retailPrice = retailPrice;
	}
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(final int userID) {
		this.userID = userID;
	}
	/**
	 * @return the creationDate
	 */
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	/**
	 * @return the creationDate as timestamp
	 */
	public Timestamp getCreationDateAsTimestamp() {
		return java.sql.Timestamp.valueOf(creationDate);
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(final LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(final Timestamp creationDate) {
		this.creationDate = creationDate.toLocalDateTime();
	}
	/**
	 * @return the orderStatus
	 */
	public String getOrderStatus() {
		return orderStatus;
	}
	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(final String orderStatus) {
		this.orderStatus = orderStatus;
	}
	/**
	 * @return the addressID
	 */
	public int getAddressID() {
		return addressID;
	}
	/**
	 * @param addressID the addressID to set
	 */
	public void setAddressID(final int addressID) {
		this.addressID = addressID;
	}
	/**
	 * @return the cardID
	 */
	public int getCardID() {
		return cardID;
	}
	/**
	 * @param cardID the cardID to set
	 */
	public void setCardID(final int cardID) {
		this.cardID = cardID;
	}
	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}
	/**
	 * @param addressLine1 the addressLine1 to set
	 */
	public void setAddressLine1(final String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	/**
	 * @return the discountPercent
	 */
	public double getDiscountPercent() {
		return discountPercent;
	}
	/**
	 * @param discountPercent the discountPercent to set
	 */
	public void setDiscountPercent(final double discountPercent) {
		this.discountPercent = discountPercent;
	}

}
