package model;

/**
 * 
 * an address object.
 * @author jdowd
 *
 */
public class Address {
	/** the object's address id. */
	private int addressID = 0;
	/** the object's second line. */
	private String line1;
	/** the object's first line. */
	private String line2 = null;
	/** the object's city. */
	private String city;
	/** the object's state. */
	private String state;
	/** the object's zip code. */
	private String zip;
	/** the object's owner if. */
	private int ownerID = 0;
	/** the object's type. */
	private AddressType type;

	/**
	 * @return if the address exists
	 */
	public boolean exists() {
		return addressID != 0 
				&& line1 != null 
				&& city != null 
				&& state != null
				&& zip != null;
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
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}



	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(final String line1) {
		this.line1 = line1;
	}



	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}



	/**
	 * @param city the city to set
	 */
	public void setCity(final String city) {
		this.city = city;
	}



	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}



	/**
	 * @param state the state to set
	 */
	public void setState(final String state) {
		this.state = state.toUpperCase();
	}



	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}



	/**
	 * @param zip the zip to set
	 */
	public void setZip(final String zip) {
		this.zip = zip;
	}



	/**
	 * @return the ownerID
	 */
	public int getOwnerID() {
		return ownerID;
	}



	/**
	 * @param ownerID the ownerID to set
	 */
	public void setOwnerID(final int ownerID) {
		this.ownerID = ownerID;
	}



	/**
	 * @return the type
	 */
	public AddressType getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(final AddressType type) {
		this.type = type;
	}



	/**
	 * @return the second line of the address
	 */
	public String getLine2() {
		return line2 == null ? " " : line2;
	}

	
	/**
	 * @param pLine2 the second line to set
	 */
	public void setLine2(final String pLine2) {
		line2 = pLine2;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + addressID;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((line1 == null) ? 0 : line1.hashCode());
		result = prime * result + ((line2 == null) ? 0 : line2.hashCode());
		result = prime * result + ownerID;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
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
		Address other = (Address) obj;
		if (addressID != other.addressID) {
			return false;
		}
		if (city == null) {
			if (other.city != null) {
				return false;
			}
		} else if (!city.equals(other.city)) {
			return false;
		}
		if (line1 == null) {
			if (other.line1 != null) {
				return false;
			}
		} else if (!line1.equals(other.line1)) {
			return false;
		}
		if (line2 == null) {
			if (other.line2 != null) {
				return false;
			}
		} else if (!line2.equals(other.line2)) {
			return false;
		}
		if (ownerID != other.ownerID) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (zip == null) {
			if (other.zip != null) {
				return false;
			}
		} else if (!zip.equals(other.zip)) {
			return false;
		}
		return true;
	}



}
