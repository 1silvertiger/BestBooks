package model;

/**
 * an object which contains values
 * from inventory, cart, and book.
 * @author jdowd
 *
 */
public class InvCartBook {
	/** the object's cart id. */
	private int cartID;
	/** the object's title. */
	private String title;
	/** the object's author. */
	private String author;
	/** the object's quantity in cart. */
	private int quantity;
	/** the object's quantity on hand. */
	private int quantityOnHand;
	/** the object's cost. */
	private double cost;
	/** the object's weight. */
	private double weight;
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
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}
	/**
	 * @param cost the cost to set
	 */
	public void setCost(final double cost) {
		this.cost = cost;
	}
	/**
	 * @return the cartID
	 */
	public int getCartID() {
		return cartID;
	}
	/**
	 * @param cartID the cartID to set
	 */
	public void setCartID(final int cartID) {
		this.cartID = cartID;
	}
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(final double weight) {
		this.weight = weight;
	}
	/**
	 * @return the quantityOnHand
	 */
	public int getQuantityOnHand() {
		return quantityOnHand;
	}
	/**
	 * @param quantityOnHand the quantityOnHand to set
	 */
	public void setQuantityOnHand(final int quantityOnHand) {
		this.quantityOnHand = quantityOnHand;
	}
	
}
