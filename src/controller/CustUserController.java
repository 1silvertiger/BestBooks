package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;

import model.User;
import model.UserDAO;
import util.AsciiBanner;
import util.TableBuilder;

/**
 * controls customer user information.
 * @author jdowd
 *
 */
public class CustUserController {
	// ---------------------Out messages -------------------------
	/**
	 * The messages returned to the view.
	 */
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();

	private AsciiBanner banner = new AsciiBanner();
	
	/**
	 * an instance of the user.
	 */
	private User currentUser;
	
	/**
	 * an instance of the user's update preferences.
	 */
	private User updateUser = new User(); 
	
	/**
	 * keep a user in their menu.
	 */
	private boolean backToUserMenu = true;

	/**
	 * the user command being processed.
	 */
	private String currentUserCommand = "";

	/**
	 * store the current state of a context menu.
	 */
	private int currentUserCommandState = 1;
	
	/** context menu state one. */
	private static final int ONE = 1;
	
	/** context menu state two. */
	private static final int TWO = 2;
	
	/** context menu state three. */
	private static final int THREE = 3;
	
	/** context menu state four. */
	private static final int FOUR = 4;
	
	/** context menu state five. */
	private static final int FIVE = 5;
	
	/** context menu state six. */
	private static final int SIX = 6;
	
	/** context menu state seven. */
	private static final int SEVEN = 7;
	
	/** context menu state eight. */
	private static final int EIGHT = 8;
	
	/** context menu state nine. */
	private static final int NINE = 9;

	// DAO
	private UserDAO userDAO;

	private CustCardController cardControl;

	// Commands
	private static final String HELP = "help";
	private static final String UPDATE = "update";
	private static final String SHOW = "show";
	private static final String DELETE = "delete";
	private static final String ADDRESS = "address";
	private static final String CARD = "card";

	private boolean logout;

	private CustAddressController addressControl;

	private Connection connection;

	private static final String USER = "user";

	// SQL Exception numbers
	private static final String KNOWN_SQL_ERROR_PREFIX = "45";

	// Messages
	private static final String SQL_EXCEPTION_MESSAGE = 
			"A connection error occurred. "
			+ "Check your internet connection and try again.";

	/**
	 * the constructor sets up the DB connection for 
	 * the DAO and sets the current user instance to a field.
	 * @param c the connection to the database.
	 * @param myUser the current user session.
	 */
	public CustUserController(final Connection c, final User myUser) {
		currentUser = myUser;
		connection = c;

		cardControl = new CustCardController(c, myUser);
		addressControl = new CustAddressController(c, myUser);

		logout = false;

		// DAO
		try {
			userDAO = new UserDAO(c);
		} catch (SQLException e) {
			outMessages.add(
					new TableBuilder(SQL_EXCEPTION_MESSAGE));
		}

	}

	/**
	 * This receives the input, processes it, 
	 * 	and returns a list of generated TableBuilders
	 * 
	 * @param cmd
	 *            the command to execute
	 * @param args
	 *            the options arguments to apply to the command
	 * @param myUser
	 * @return the generated output
	 */
	public List<TableBuilder> parseCommand(
			final String cmd, final String[] args) {

		if (cmd.toLowerCase().equals(USER)) {
			backToUserMenu = true;
		}

		// run this only if the user has 
		//completed a context menu or entered
		// user
		if (backToUserMenu) {
			currentUserCommand = cmd;

		}

		// We switch the command
		switch (currentUserCommand) {
		case HELP:
			showHelp();
			break;
		case UPDATE:
			backToUserMenu = false;
			updateUser(cmd);
			break;
		case SHOW:
			getUser();
			break;
		case DELETE:
			backToUserMenu = false;
			deleteUser(cmd);
			break;
		case ADDRESS:
			backToUserMenu = false;

			// EDIT ADDRESS -- pass to another controller
			outMessages = addressControl.parseCommand(cmd, args);

			break;
		case CARD:
			backToUserMenu = false;
			// edit card
			outMessages = cardControl.parseCommand(cmd, args);
			break;
		case USER:
			try {
				outMessages.addAll(banner.getBanner("USER"));
			} catch (IOException e) {
				outMessages.add(
						new TableBuilder("User information"
								+ " section!"));
			}

			showHelp();
			reset();
			break;
		default:
			// notify the user that the command is invalid
			outMessages.add(new TableBuilder(
					"Invalid command:"
					+ " '"
					+ currentUserCommand
					+ "'"
					+ ", enter <help> to "
					+ "see valid commands"));
		}
		return outMessages;
	}

	/**
	 * updates the user information
	 * @param cmd the command in use
	 */
	private void updateUser(final String cmd) {
		String phoneNumber = "";

		
		// switch is used to determine how far into the menu the user is
		switch (currentUserCommandState) {
		//start case
		case ONE:
			updateUser = new User(currentUser);
			
			outMessages.add(
					new TableBuilder("Are you sure you want"
							+ " to edit "
							+ "your information?"));
			outMessages.add(
					new TableBuilder("enter (1) for yes"
							+ ", enter (2)"
							+ " for no."));
			currentUserCommandState = TWO;
			break;
		//confirm update case
		case TWO:
			if (cmd.equals("1") || cmd.equals("yes")) {
				outMessages.add(
						new TableBuilder("current first name: " 
								+ updateUser.getFirstName()));
				outMessages.add(
						new TableBuilder("Enter a new first name"
								+ ", or enter nothing to make no changes"));
				currentUserCommandState = THREE;
			} else {
				outMessages.add(new TableBuilder("Update cancelled..."));
				endMenu();
			}
			break;
		//get the users first name and ask for last name
		case THREE:
			if (!cmd.equals("")) {
				updateUser.setFirstName(cmd);
			}
			outMessages.add(
					new TableBuilder(
							"current last name: " 
					+ updateUser.getLastName()));
			outMessages.add(
					new TableBuilder(
							"Enter a new last name,"
							+ " or enter nothing to make no changes"));
			currentUserCommandState = FOUR;
			break;
		//accept last name and ask for home phone
		case FOUR:
			if (!cmd.equals("")) {
				updateUser.setLastName(cmd);
			}
			outMessages.add(new TableBuilder("Enter a home phone number, or enter nothing to make no changes"));
			currentUserCommandState = FIVE;
			break;
		//accept home phone and ask for cell phone
		case FIVE:
			//get rid of all the unneeded characters
			phoneNumber = cmd;
			phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
			
			//see if the cmd was empty
			if (!cmd.equals("")) {
				//check the length
				if (phoneNumber.length() == 10) {
					updateUser.setHomePhone(phoneNumber);
					outMessages.add(new TableBuilder("Enter a cell phone number, or enter nothing to make no changes"));
					currentUserCommandState = SIX;
				//this runs if the entered phone number is a bad length
				} else {
					outMessages.add(new TableBuilder("please make sure you enter only a 10 digit phone number and try again"));
					outMessages.add(new TableBuilder("Enter a home phone number, or enter nothing to make no changes"));
				}
			} else {
				outMessages.add(new TableBuilder("Enter a cell phone number, or enter nothing to make no changes"));
				currentUserCommandState = SIX;
			}
			break;
		//accept cell phone and ask for password
		case SIX:
			//get rid of all the unneeded characters
			phoneNumber = cmd;
			phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
			
			//see if the cmd was empty
			if (!cmd.equals("")) {
				//check the length
				if (phoneNumber.length() == 10) {
					updateUser.setCellPhone(phoneNumber);
					outMessages.add(new TableBuilder("Enter a password, or enter nothing to make no changes"));
					currentUserCommandState = SEVEN;
				//this runs if the entered phone number is a bad length
				} else {
					outMessages.add(new TableBuilder("please make sure you enter only a 10 digit phone number and try again"));
					outMessages.add(new TableBuilder("Enter a cell phone number, or enter nothing to make no changes"));
				}
			} else {
				outMessages.add(new TableBuilder("Enter a password, or enter nothing to make no changes"));
				currentUserCommandState = SEVEN;
			}

			break;
		//accept password and ask for email
		case SEVEN:
			if (!cmd.equals("")) {
				updateUser.setUserPassword(cmd);
			} else {
				updateUser.setUserPassword(updateUser.getPlainPassword());				
			}
			outMessages.add(new TableBuilder("current email: " + updateUser.getEmailAddress()));
			outMessages.add(new TableBuilder("Enter a new email, or enter nothing to make no changes"));
			currentUserCommandState = EIGHT;
			break;
		//accept email and ask for confirmation of update
		case EIGHT:
			EmailValidator validator = EmailValidator.getInstance();
			if (cmd.equals("")) {
				outMessages.add(
						new TableBuilder("new name: " + updateUser.getFirstName() + " " + updateUser.getLastName()));
				if (updateUser.getUserPassword() != null) {
					outMessages.add(new TableBuilder("new password: " + updateUser.getUserPassword()));
				}
				if (!updateUser.getHomePhone().equals("")) {
					outMessages.add(new TableBuilder("home phone number: " + updateUser.getHomePhone()));
				}
				if (!updateUser.getCellPhone().equals("")) {
					outMessages.add(new TableBuilder("cell phone number: " + updateUser.getCellPhone()));
				}
				outMessages.add(new TableBuilder("new email: " + updateUser.getEmailAddress()));
				outMessages.add(new TableBuilder("Enter (1) to confirm changes. Enter (2) to cancel."));
				currentUserCommandState = NINE;
			} else if (validator.isValid(cmd)) {
				updateUser.setEmailAddress(cmd);
				outMessages.add(
						new TableBuilder("new name: " + updateUser.getFirstName() + " " + updateUser.getLastName()));
				if (updateUser.getUserPassword() != null) {
					outMessages.add(new TableBuilder("new password: " + updateUser.getUserPassword()));
				}
				if (!updateUser.getHomePhone().equals("")) {
					outMessages.add(new TableBuilder("home phone number: " + updateUser.getHomePhone()));
				}
				if (!updateUser.getCellPhone().equals("")) {
					outMessages.add(new TableBuilder("cell phone number: " + updateUser.getCellPhone()));
				}
				outMessages.add(new TableBuilder("new email: " + updateUser.getEmailAddress()));
				outMessages.add(new TableBuilder("Enter (1) to confirm changes. Enter (2) to cancel."));
				currentUserCommandState = NINE;
			} else {
				outMessages.add(new TableBuilder("invalid email address"));
				outMessages.add(new TableBuilder("Enter a new email, or enter nothing to make no changes"));
			}
			break;
		//accept confirmation and update
		case NINE:
			if (cmd.equals("1") || cmd.equals("yes")) {
				try {
					
					userDAO.update(updateUser);
					outMessages.add(new TableBuilder("user updated"));
					currentUser = updateUser;
					endMenu();
				} catch (SQLException e) {
					outMessages.add(new TableBuilder("update could not be completed"));
					sqlExceptionMessage(e);
				}
			} else {
				outMessages.add(new TableBuilder("update has been canceled..."));
			}

			// end use of the current menu
			endMenu();

			break;
		default:
			break;
		}

	}

	/**
	 * displays current user information.
	 */
	private void getUser() {
		User user = currentUser;

		TableBuilder table = new TableBuilder();
		table.addColumn("Email: ", user.getEmailAddress());
		table.addColumn("first name: ", user.getFirstName());
		table.addColumn("last name: ", user.getLastName());
		if (!user.getHomePhone().equals("")) {
			table.addColumn("home phone number: ", user.getHomePhone());
		}
		if (!user.getCellPhone().equals("")) {
			table.addColumn("cell phone number: ", user.getCellPhone());
		}
		outMessages.add(table);
	}

	/**
	 * sets the current user's account to inactive
	 * and log the user out.
	 * @param cmd the user's command.
	 */
	private void deleteUser(final String cmd) {
		switch (currentUserCommandState) {
		case ONE:
			outMessages.add(new TableBuilder("Are you sure you want to delete your user?"));
			outMessages.add(new TableBuilder("enter (1) for yes, enter (2) for no."));
			currentUserCommandState = TWO;
			break;
		case TWO:
			if (cmd.equals("1") || cmd.equals("yes")) {
				try {
					userDAO.delete(currentUser);
					outMessages.add(new TableBuilder("user deleted, logging out..."));
					logout = true;
				} catch (SQLException e) {
					sqlExceptionMessage(e);
				}
			} else {
				outMessages.add(new TableBuilder("deletion has been canceled..."));
			}

			// end use of the current menu
			endMenu();

			break;
		default:
			break;
		}

	}

	/**
	 * show help information.
	 */
	public final void showHelp() {
		String[] headers = new String[2];
		String[] row = new String[2];
		headers[0] = "command: ";
		headers[1] = "action: ";
		TableBuilder table = new TableBuilder(headers);
		
		row[0] = "<help>";
		row[1] = "to show this dialogue";
		table.addRow(row);
		row = new String[2];

		row[0] = "<menu>";
		row[1] = "to go back to the main menu";
		table.addRow(row);
		row = new String[2];
		
		row[0] = "<show>";
		row[1] = "to display current user information";
		table.addRow(row);
		row = new String[2];

		row[0] = "<delete>";
		row[1] = "to delete the current user account";
		table.addRow(row);
		row = new String[2];

		row[0] = "<update>";
		row[1] = "to update current user information";
		table.addRow(row);
		row = new String[2];

		row[0] = "<card>";
		row[1] = "to edit credit card information";
		table.addRow(row);
		row = new String[2];

		row[0] = "<address>";
		row[1] = "to edit address information";
		table.addRow(row);
		row = new String[2];

		outMessages.add(table);
	}

	/**
	 * @return the logout state
	 */
	public final boolean getLogout() {
		return logout;
	}

	/**
	 * restarts the sub controllers.
	 */
	private void reset() {
		cardControl = new CustCardController(connection, currentUser);
		addressControl = new 
				CustAddressController(connection, currentUser);
	}

	/**
	 * set the menu progress
	 * back to its initial
	 * state.
	 */
	private void endMenu() {
		// set the progress back to one
		currentUserCommandState = ONE;
		backToUserMenu = true;
	}

	/**
	 * gets a sql exception and adds the
	 * proper message to the output.
	 * @param e the error message
	 */
	private void sqlExceptionMessage(final SQLException e) {
		if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
			outMessages.add(new TableBuilder(e.getMessage()));
		} else {
			outMessages.add(new TableBuilder(
			"Error code "
			+ e.getErrorCode()
			+ " occurred with SQL state "
			+ e.getSQLState()
			+ " please notify your database administrator."));
		}

	}
}
