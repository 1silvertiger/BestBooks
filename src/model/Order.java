package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * the order object.
 * @author jdowd
 *
 */
public class Order {
	/** the object's order id. */
	private int orderID;
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
	/** the object's total cost. */
	private double totalCost;
	/** the object's discount percent. */
	private double discountPercent = 0;
	
	/** the object's shipping address. */
	private Address address = new Address();
	/** the object's credit card. */
	private CardSecure card = new CardSecure();
	/** the items in the order. */
	private ArrayList<OrderItemBook> items = new ArrayList<>();


	
	/**
	 * @return whether the order exists or not
	 */
	public boolean exists() {
		return orderID != 0 && userID != 0 && creationDate != null && orderStatus != null && cardID != 0
				&& addressID != 0 && address != null && card != null;
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
	 * @return the totalCost
	 */
	public double getTotalCost() {
		return totalCost;
	}
	/**
	 * @param totalCost the totalCost to set
	 */
	public void setTotalCost(final double totalCost) {
		this.totalCost = totalCost;
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

	/**
	 * @return the card
	 */
	public CardSecure getCard() {
		return card;
	}

	/**
	 * @param pCard
	 *            the card to set
	 */
	public void setCard(final CardSecure pCard) {
		card = pCard;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param pAddress
	 *            the address to set
	 */
	public void setAddress(final Address pAddress) {
		address = pAddress;
	}


	/**
	 * @param pBooks the books to set
	 */
	public void setBooks(final ArrayList<OrderItemBook> pBooks) {
		items = pBooks;
	}

	/**
	 * @return the order items
	 */
	public ArrayList<OrderItemBook> getItems() {
		return items;
	}


	/**
	 * @return the total cost of an order
	 */
	public double getTotal() {
		double total = 0;
		for (OrderItemBook b : items) {
			total += b.getSalePrice() * b.getQuantity();
		}
		return total * (1 - discountPercent);
	}
	
	/**
	 * @param b the book to check for in the order
	 * @return whether the item appears or not
	 */
	public boolean hasItem(final OrderItemBook b) {
		return items.contains(b);
	}

	/**
	 * @param number the number to get
	 * @return the book with that number
	 */
	public OrderItemBook getItemNumber(final int number) {
		if (hasItemNumber(number)) {
			for (OrderItemBook b : items) {
				if (b.getOrderItemID() == number) {
					return b;
				}
			}
		}
		return null;
	}

	/**
	 * @param number the book number to check
	 * @return whether that item exists or not
	 */
	public boolean hasItemNumber(final int number) {
		for (OrderItemBook b : items) {
			if (b.getOrderItemID() == number) {
				return true;
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Order [orderID=" + orderID + ", userID=" + userID + ", creationDate=" + creationDate + ", orderStatus="
				+ orderStatus + ", cardID=" + cardID + ", addressID=" + addressID + ", totalCost=" + totalCost
				+ ", discountPercent=" + discountPercent + ", address=" + address + ", card=" + card + ", items="
				+ items + "]";
	}

	/**
	 * @param o the order to compare
	 * @return if the orders matched
	 */
	public boolean equals(Order o) {
		return orderID == o.getOrderID() && userID == o.getUserID() && creationDate.equals(o.getCreationDate())
				&& orderStatus.toLowerCase().equals(o.getOrderStatus().toLowerCase()) && totalCost == o.getTotalCost()
				&& discountPercent == o.getDiscountPercent() && address.equals(o.getAddress())
				&& card.equals(o.getCard());
	}

}
