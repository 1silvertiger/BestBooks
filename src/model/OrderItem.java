package model;

/**
 * an object with date from orders
 * order items
 * @author jdowd
 *
 */
public class OrderItem {
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
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bookID;
		result = prime * result + orderID;
		result = prime * result + orderItemID;
		result = prime * result + quantity;
		long temp;
		temp = Double.doubleToLongBits(salePrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OrderItem other = (OrderItem) obj;
		if (bookID != other.bookID) {
			return false;
		}
		if (orderID != other.orderID) {
			return false;
		}
		if (orderItemID != other.orderItemID) {
			return false;
		}
		if (quantity != other.quantity) {
			return false;
		}
		if (Double.doubleToLongBits(salePrice) != Double.doubleToLongBits(other.salePrice)) {
			return false;
		}
		return true;
	}
	
}
