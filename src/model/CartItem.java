package model;

public class CartItem {
	/** the object's cart item id. */
	private int cartItemID;
	/** the object's book id. */
	private int bookID;
	/** the object's user id. */
	private int userID;
	/** the object's quantity. */
	private int quantity;
	/**
	 * @return the cartItemID
	 */
	public int getCartItemID() {
		return cartItemID;
	}
	/**
	 * @param cartItemID the cartItemID to set
	 */
	public void setCartItemID(final int cartItemID) {
		this.cartItemID = cartItemID;
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
		result = prime * result + cartItemID;
		result = prime * result + quantity;
		result = prime * result + userID;
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
		CartItem other = (CartItem) obj;
		if (bookID != other.bookID) {
			return false;
		}
		if (cartItemID != other.cartItemID) {
			return false;
		}
		if (quantity != other.quantity) {
			return false;
		}
		if (userID != other.userID) {
			return false;
		}
		return true;
	}
	
}
