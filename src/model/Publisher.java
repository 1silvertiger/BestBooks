package model;

import java.util.ArrayList;

/**
 * the publisher object.
 * @author jdowd
 *
 */
public class Publisher {
	/** the object's publisher id. */
	private int publisherID;
	/** the object's publisher name. */
	private String publisherName;
	/** the object's phone number. */
	private String phoneNumber;
	/** the object's sales representative name. */
	private String salesRepName;
	
	/** the object's addresses. */
	private ArrayList<Address> addresses = new ArrayList<>();
	
	/** the object's state of existence. */
	private boolean exists = false;
	


	/**
	 * @return the publisherID
	 */
	public int getPublisherID() {
		return publisherID;
	}

	/**
	 * @param publisherID the publisherID to set
	 */
	public void setPublisherID(final int publisherID) {
		this.publisherID = publisherID;
	}

	/**
	 * @return the publisherName
	 */
	public String getPublisherName() {
		return publisherName;
	}

	/**
	 * @param publisherName the publisherName to set
	 */
	public void setPublisherName(final String publisherName) {
		this.publisherName = publisherName;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the salesRepName
	 */
	public String getSalesRepName() {
		return salesRepName;
	}

	/**
	 * @param salesRepName the salesRepName to set
	 */
	public void setSalesRepName(final String salesRepName) {
		this.salesRepName = salesRepName;
	}

	/**
	 * @return the exists
	 */
	public boolean isExists() {
		return exists;
	}

	/**
	 * @param exists the exists to set
	 */
	public void setExists(final boolean exists) {
		this.exists = exists;
	}

	/**
	 * @return the addresses
	 */
	public ArrayList<Address> getAddresses() {
		return addresses;
	}

	/**
	 * @param pAddresses the addresses to set
	 */
	public void setAddresses(final ArrayList<Address> pAddresses) {
		addresses = pAddresses;
	}
	/**
	 * @return the existence state
	 */
	public boolean exists() {
		return exists;
	}
}
