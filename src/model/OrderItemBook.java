package model;

/**
 * an object with information from
 * order items and books
 * @author Dakota
 *
 */
public class OrderItemBook extends Book {
	/** the object's order id. */
	private int orderID = 0;
	/** the object's quantity. */
	private int quantity = 0;
	/** the object's order item id. */
	private int orderItemID = 0;
	/** the object's sale price. */
	private double salePrice = 0;
	/**
	 * @return the orderID
	 */
	public int getOrderID() {
		return orderID;
	}

	/**
	 * @param pOrderID
	 *            the orderID to set
	 */
	public void setOrderID(final int pOrderID) {
		orderID = pOrderID;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param pQuantity
	 *            the quantity to set
	 */
	public void setQuantity(final int pQuantity) {
		quantity = pQuantity;
	}

	/**
	 * @return the orderItemID
	 */
	public int getOrderItemID() {
		return orderItemID;
	}

	/**
	 * @param pOrderItemID
	 *            the orderItemID to set
	 */
	public void setOrderItemID(final int pOrderItemID) {
		orderItemID = pOrderItemID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + orderID;
		result = prime * result + orderItemID;
		result = prime * result + quantity;
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OrderItemBook other = (OrderItemBook) obj;
		if (orderID != other.orderID) {
			return false;
		}
		if (orderItemID != other.orderItemID) {
			return false;
		}
		if (quantity != other.quantity) {
			return false;
		}
		return true;
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
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}



}
