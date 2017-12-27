package model;

/**
 * the object with user information.
 * @author jdowd
 *
 */
public class User {
	/**the user's user id. */
	private int userID;
	/**the user's user password. */
	private String userPassword;
	/**the user's plain password. */
	private String plainPassword;
	/**the user's first name. */
	private String firstName;
	/**the user's last name. */
	private String lastName;
	/**the user's email address. */
	private String emailAddress;
	/**the user's admin state. */
	private boolean isAdmin;
	/**the user's home phone number. */
	private String homePhone = "";
	/**the user's cell phone number. */
	private String cellPhone = "";
	/**
	 * if the user is active
	 */
	private boolean active = true;

	
	/**
	 * this constructor allows for the creation of a new
	 * user instance based off another user.
	 * @param pUser the user to copy
	 */
	public User(User pUser) {

		 userID = pUser.getUserID();

		 userPassword = pUser.getUserPassword();

		 firstName = pUser.getFirstName();

		 lastName = pUser.getLastName();

		 emailAddress = pUser.getEmailAddress();

		 isAdmin = pUser.isAdmin();

		 homePhone = pUser.getHomePhone();

		 cellPhone = pUser.getCellPhone();
		 
		 setPlainPassword(pUser.getPlainPassword());
	}
	
	/**
	 * Default constructor.
	 */
	public User() {	
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
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}
	/**
	 * @param userPassword the userPassword to set
	 */
	public void setUserPassword(final String userPassword) {
		this.userPassword = userPassword;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress.toLowerCase();
	}
	/**
	 * @return the homePhone
	 */
	public String getHomePhone() {
		return homePhone;
	}
	/**
	 * @param homePhone the homePhone to set
	 */
	public void setHomePhone(final String homePhone) {
		if (homePhone != null) {
			this.homePhone = homePhone;
		}
	}
	/**
	 * @return the cellPhone
	 */
	public String getCellPhone() {
		return cellPhone;
	}
	/**
	 * @param cellPhone the cellPhone to set
	 */
	public void setCellPhone(final String cellPhone) {
		if (cellPhone != null) {
			this.cellPhone = cellPhone;
		}
	}
	/**
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}
	public final int isAdminInt() {
		if (isAdmin){
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * @param isAdmin the state of administration privileges
	 */
	public void setAdmin(final boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	/**
	 * @param isAdmin the state of administration privileges
	 */
	public void setAdmin(final int pIsAdmin) {
		if (pIsAdmin == 1) {
			isAdmin = true;
		} else {
			isAdmin = false;
		}
		
	}

	/**
	 * @return the plainPassword
	 */
	public String getPlainPassword() {
		return plainPassword;
	}

	/**
	 * @param plainPassword the plainPassword to set
	 */
	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param pActive the active to set
	 */
	public void setActive(final boolean pActive) {
		active = pActive;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [userID=" + userID + ", userPassword=" + userPassword + ", plainPassword=" + plainPassword
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", emailAddress=" + emailAddress
				+ ", isAdmin=" + isAdmin + ", homePhone=" + homePhone + ", cellPhone=" + cellPhone + "]";
	}


}
