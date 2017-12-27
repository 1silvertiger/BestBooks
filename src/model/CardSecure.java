package model;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;

import com.berry.BCrypt;

import crypto.AES;

/**
 * a secure credit card object.
 * @author jdowd
 *
 */
public class CardSecure {
	/** the object's card id. */
	private int cardID = 0;
	/** the object's user id. */
	private int userID = 0;
	/** the object's first twelve digits. */
	private String firstTwelveDigits = null;
	/** the object's last four digits. */
	private String lastFourDigits = null;
	/** the object's cvc. */
	private String cvc = null;
	/** the object's expiration date. */
	private String expirationDate = null;
	/** the object's holder's name. */
	private String cardHolderName = null;
	/** the object's address. */
	private Address address = new Address();
	/** the number of twelve digits. */
	private static final int END_OF_FIRST_TWELVE = 12;
	/** workload for encryption. */
	private static final int WORKLOAD = 12;

	/**
	 * this constructor allows for the creation of a new
	 * card instance based off another user.
	 * @param pUser the user to copy
	 */
	public CardSecure(CardSecure pCard) {

		cardID = pCard.getCardID();
		
		userID = pCard.getUserID();

		firstTwelveDigits = pCard.getFirstTwelveDigits();

		lastFourDigits = pCard.getLastFourDigits();

		cvc = pCard.getCvc();

		expirationDate = pCard.getExpirationDate();

		cardHolderName = pCard.getCardHolderName();

		address = pCard.getAddress();
	}
	
	/**
	 * Default constructor.
	 */
	public CardSecure() {	
	}
	
	/**
	 * 
	 * @return if the object exists or not
	 */
	public boolean exists() {
		return cardID != 0 && userID != 0 && firstTwelveDigits != null && lastFourDigits != null
				&& expirationDate != null && cardHolderName != null && address.exists();
	}

	/**
	 * @return the cardID
	 */
	public int getCardID() {
		return cardID;
	}

	/**
	 * @param cardID
	 *            the cardID to set
	 */
	public void setCardID(final int cardID) {
		this.cardID = cardID;
	}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param userID
	 *            the userID to set
	 */
	public void setUserID(final int userID) {
		this.userID = userID;
	}

	public final String getFirstTwelveDigits() {
		return firstTwelveDigits;
	}

	public final void setFirstTwelveDigits(final String pDigits) {
		firstTwelveDigits = pDigits;
	}

	public final String getLastFourDigits() {
		return lastFourDigits;
	}

	public final void setLastFourDigits(final String pDigits) {
		lastFourDigits = pDigits;
	}

	/**
	 * @return the cvc
	 */
	public String getCvc() {
		return cvc;
	}

	/**
	 * @param cvc
	 *            the cvc to set
	 */
	public void setCvc(final String cvc) {
		this.cvc = cvc;
	}

	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate
	 *            the expirationDate to set
	 */
	public void setExpirationDate(final String expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the cardHolderName
	 */
	public String getCardHolderName() {
		return cardHolderName;
	}

	/**
	 * @param cardHolderName
	 *            the cardHolderName to set
	 */
	public void setCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
	
	/**
	 * encrypts the first twelve digits of the credit
	 * card.
	 */
	public final void encryptFirstTwelveDigits() {
		byte[] bytes = BigInteger.valueOf(Integer.parseInt(cvc + userID)).toByteArray();
		String salt = BCrypt.gensalt(WORKLOAD, new SecureRandom(bytes));
		String secretKey = BCrypt.hashpw(cvc, salt);

		firstTwelveDigits = AES.encrypt(firstTwelveDigits, secretKey);
	}

	
	/**
	 * decrypts the first twelve digits of the credit
	 * card.
	 */
	public final void decryptFirstTwelveDigits() {
		byte[] bytes = BigInteger.valueOf(Integer.parseInt(cvc + userID)).toByteArray();
		String salt = BCrypt.gensalt(WORKLOAD, new SecureRandom(bytes));
		String secretKey = BCrypt.hashpw(cvc, salt);
		
		firstTwelveDigits = AES.decrypt(firstTwelveDigits, secretKey);
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return "**** **** **** " + lastFourDigits;
	}

	/**
	 * @param pCardNumber
	 *            the cardNumber to set
	 */
	public void setCardNumber(final String pCardNumber) {
		String cardNumber = pCardNumber;
		cardNumber = StringUtils.remove(cardNumber, '-');
		cardNumber = StringUtils.remove(cardNumber, " ");
		setFirstTwelveDigits(cardNumber.substring(0, END_OF_FIRST_TWELVE));
		setLastFourDigits(cardNumber.substring(END_OF_FIRST_TWELVE));
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

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CardSecure [cardID=" + cardID + ", userID=" + userID + ", firstTwelveDigits=" + firstTwelveDigits
				+ ", lastFourDigits=" + lastFourDigits + ", cvc=" + cvc + ", expirationDate=" + expirationDate
				+ ", cardHolderName=" + cardHolderName + ", address=" + address + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((cardHolderName == null) ? 0 : cardHolderName.hashCode());
		result = prime * result + cardID;
		result = prime * result + ((cvc == null) ? 0 : cvc.hashCode());
		result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result + ((firstTwelveDigits == null) ? 0 : firstTwelveDigits.hashCode());
		result = prime * result + ((lastFourDigits == null) ? 0 : lastFourDigits.hashCode());
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
		CardSecure other = (CardSecure) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (cardHolderName == null) {
			if (other.cardHolderName != null) {
				return false;
			}
		} else if (!cardHolderName.equals(other.cardHolderName)) {
			return false;
		}
		if (cardID != other.cardID) {
			return false;
		}
		if (cvc == null) {
			if (other.cvc != null) {
				return false;
			}
		} else if (!cvc.equals(other.cvc)) {
			return false;
		}
		if (expirationDate == null) {
			if (other.expirationDate != null) {
				return false;
			}
		} else if (!expirationDate.equals(other.expirationDate)) {
			return false;
		}
		if (firstTwelveDigits == null) {
			if (other.firstTwelveDigits != null) {
				return false;
			}
		} else if (!firstTwelveDigits.equals(other.firstTwelveDigits)) {
			return false;
		}
		if (lastFourDigits == null) {
			if (other.lastFourDigits != null) {
				return false;
			}
		} else if (!lastFourDigits.equals(other.lastFourDigits)) {
			return false;
		}
		if (userID != other.userID) {
			return false;
		}
		return true;
	}

}
