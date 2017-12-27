package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Address;
import model.AddressDAO;
import model.AddressType;
import model.User;
import util.AsciiBanner;
import util.TableBuilder;

/**
 * controls customer address information.
 * @author jdowd
 *
 */
public class CustAddressController {
	// ---------------------Out messages -------------------------
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();

	private AsciiBanner banner = new AsciiBanner();
	private User currentUser;
	
	private List<Address> addresses = null;
	private Address address = new Address();

	private boolean backToAddressMenu = true;
	
	private String currentAddressCommand = "";
	
	private int currentAddressCommandState = 1;
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int FOUR = 4;
	private static final int FIVE = 5;
	private static final int SIX = 6;
	private static final int SEVEN = 7;
	private static final int EIGHT = 8;
	
	// DAO
	private AddressDAO addressDAO;

	// Commands
	private static final String ADD = "add";
	private static final String HELP = "help";
	private static final String UPDATE = "update";
	private static final String SHOW = "show";
	private static final String DELETE = "delete";
	private static final String ADDRESS = "address";

	// SQL Exception numbers
	private static final String KNOWN_SQL_ERROR_PREFIX = "45";

	// Messages
	private static final String SQL_EXCEPTION_MESSAGE = "A connection error occurred. Check your internet connection and try again.";
	
	/**
	 * this constructor sets up the connection for
	 * the DAO.
	 * @param c the connection to the database
	 * @param myUser the active user
	 */
	public CustAddressController(final Connection c, final User myUser) {
		currentUser = myUser;
		
		// DAO
		try {
			addressDAO = new AddressDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
		}

	}

	/**
	 * This receives the input, processes it, and returns a list of generated
	 * TableBuilders
	 * 
	 * @param cmd
	 *            the command to execute
	 * @param args
	 *            the options arguments to apply to the command
	 * @param myAddress 
	 * @return the generated output
	 */
	public List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		
		Address myAddress = new Address();
		
		//run this only if the address has completed a context menu
		if (backToAddressMenu) {
			currentAddressCommand = cmd;
			
		}


		// We switch the command
		switch (currentAddressCommand) {
		case HELP:
			showHelp();
			break;
		case UPDATE:
			backToAddressMenu = false;
			myAddress = updateAddress(cmd, args);
			if (myAddress != null) {
				myAddress.setOwnerID(currentUser.getUserID());
				myAddress.setType(AddressType.SHIPPING);
				try {
					addressDAO.update(myAddress);
					outMessages.add(new TableBuilder("address updated"));
				} catch (SQLException e1) {
					sqlExceptionMessage(e1);
				}
			}
			break;
		case ADD:
			backToAddressMenu = false;
			myAddress = addAddress(cmd, args);
	
			if (myAddress != null) {
				myAddress.setOwnerID(currentUser.getUserID());
				myAddress.setType(AddressType.SHIPPING);
				try {
					addressDAO.create(myAddress);
					outMessages.add(new TableBuilder("address created"));
				} catch (SQLException e) {
					sqlExceptionMessage(e);
				}
			}
			break;
		case SHOW:
				getAddresses();
			break;
		case DELETE:
				backToAddressMenu = false;
				deleteAddress(cmd);
			break;
		case ADDRESS:
			try {
				outMessages.addAll(banner.getBanner("ADDRESSES"));
			} catch (IOException e) {
				outMessages.add(new TableBuilder("Address information section!"));
			}

			showHelp();
			break;
		default:
				outMessages.add(new TableBuilder(
						"Invalid command:" + " '" + currentAddressCommand + "'" + ", enter <help> to see valid commands"));
		}
		return outMessages;
	}


	/**
	 * allows a user to create a new address.
	 * @param cmd the command in use
	 * @param args the arguments in use
	 * @return the address created
	 */
	public final Address addAddress(final String cmd, final String[] args) {
		outMessages.clear();
		StringBuilder fullLine;
		switch (currentAddressCommandState) {
		
		//initial message
		case ONE:
			outMessages.add(new TableBuilder("adding a new address!"));
			outMessages.add(new TableBuilder("enter enter first line of the address (the house number, street name, etc). of the address."));
			currentAddressCommandState = TWO;
			break;
			//add the first line to the address
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave first line blank."));
				outMessages.add(new TableBuilder("enter first line of the address (the house number, street name, etc)."));
			} else {
				//concatenate the arguments to get the full line
				fullLine = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullLine.append(" " + s);
				}
				address.setLine1(fullLine.toString());
				
				outMessages.add(new TableBuilder("enter the second line of the address (PO box, etc), or enter nothing if there is no second line."));
				currentAddressCommandState = THREE;
			}
			break;
			
			//add the second line of the address
		case THREE:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("no second line."));
				outMessages.add(new TableBuilder("enter the city on the address."));
				currentAddressCommandState = FOUR;
			} else {
				//concatenate the arguments to get the full line
				fullLine = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullLine.append(" " + s);
				}
				address.setLine2(fullLine.toString());
				
				outMessages.add(new TableBuilder("enter the city on the address."));
				currentAddressCommandState = FOUR;
			}
			break;
			
			//enter the CVC to the address
		case FOUR:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave the city blank."));
				outMessages.add(new TableBuilder("enter the city on the address."));
			} else {
				//concatenate the argurments to get the full line
				fullLine = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullLine.append(" " + s);
				}
				address.setCity(fullLine.toString());
				outMessages.add(new TableBuilder("enter the zip code"));
				currentAddressCommandState = FIVE;
			}
			break;
			
			//enter zip code for the address
		case FIVE:
			
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave the zip code blank."));
				outMessages.add(new TableBuilder("enter the zip code"));
			} else if (cmd.length() != 10 && cmd.length() != 9 && cmd.length() != 5) {
				outMessages.add(new TableBuilder("invalid zip code, please try again."));
			} else if (!isInteger(cmd)) {
				outMessages.add(new TableBuilder("please make sure the zip code is only composed of integers."));
				outMessages.add(new TableBuilder("enter the zip code"));
			} else {
				address.setZip(cmd);
				outMessages.add(new TableBuilder("enter the state postal code."));
				currentAddressCommandState = SIX;
			}
			break;
		case SIX:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave the state blank."));
				outMessages.add(new TableBuilder("enter the state postal code."));
			} else if (cmd.length() != 2) {
				outMessages.add(new TableBuilder("Enter only the state postal code."));
			} else {
				address.setState(cmd);
				outMessages.add(new TableBuilder("your address: "));
				outMessages.add(new TableBuilder(currentUser.getFirstName() + " " + currentUser.getLastName()));
				outMessages.add(new TableBuilder("" + address.getLine1()));
				if (address.getLine2() != null 
						&& !address.getLine2().equals("") 
						&& !address.getLine2().equals(" "))  {
					outMessages.add(new TableBuilder("" + address.getLine2()));
				}
				outMessages.add(new TableBuilder(address.getCity() + ", " + address.getState() + " " + address.getZip()));
				outMessages.add(new TableBuilder("enter (1) to confirm address creation, enter (2) to cancel"));
				
				currentAddressCommandState = SEVEN;
			}

			break;
		case SEVEN:
			if (cmd.equals("1") || cmd.equals("confirm")) {
				endMenu();
				return address;
			} else {
				outMessages.add(new TableBuilder("address creation has been canceled..."));
			}
			endMenu();
			break;
		default:
			break;
		}		



		return null;
	}

	/**
	 * allows a customer to create an address
	 * @param cmd the command in use
	 * @param args the arguments in use
	 * @return the updated address
	 */
	public final Address updateAddress(final String cmd, final String[] args) {
		StringBuilder fullLine;

		
		switch (currentAddressCommandState) {
		
		//initial message
		case ONE:
			
			getAddresses();
			if (addresses.isEmpty()) {
				outMessages.add(new TableBuilder("please create an address before updating"));
				endMenu();
			} else {
				outMessages.add(new TableBuilder("Which address would you like to update?"));
				outMessages.add(new TableBuilder("enter the number of the address you would like to update, or enter <cancel> to cancel."));
				currentAddressCommandState = TWO;
			}

		break;
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave selection blank."));
				outMessages.add(new TableBuilder("enter the number of the address you would like to update, or enter <cancel> to cancel."));
			
				//check if the user canceled the operation
			} else if (cmd.equals("cancel")) {
				outMessages.add(new TableBuilder("update canceled."));
				endMenu();
				
				//check if the command is an integer
			} else if (isInteger(cmd)){
				int selection = Integer.parseInt(cmd) - 1;
				if (selection < 0 || selection >= addresses.size()) {
					outMessages.add(new TableBuilder("#" + cmd + " is not valid, enter a number between 1 and " + addresses.size()));
					outMessages.add(new TableBuilder("enter the number of the card you would like to delete, or enter <cancel> to cancel."));
				} else {
					address = addresses.get(selection);
					outMessages.add(new TableBuilder("enter first line of the address, or enter nothing to make no changes."));
				}
				
		
			//they did not enter an int
			} else {
				outMessages.add(new TableBuilder("please enter only an integer."));
				outMessages.add(new TableBuilder("enter the number of the address you would like to delete, or enter <cancel> to cancel."));
			}
			break;
			//add the first line to the address
		case THREE:
			if (cmd.equals("")) {
				currentAddressCommandState = FOUR;
				outMessages.add(new TableBuilder("enter the second line of the address (PO box, etc), or enter nothing to make no changes."));
			} else {
				//concatenate the argurments to get the full line
				fullLine = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullLine.append(" " + s);
				}
				address.setLine1(fullLine.toString());
				
				outMessages.add(new TableBuilder("enter the second line of the address (PO box, etc), or enter nothing to make no changes."));
				currentAddressCommandState = FOUR;
			}
			break;
			
			//add the second line of the address
		case FOUR:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("enter the city on the address, or enter nothing to make no changes."));
				currentAddressCommandState = FIVE;
			} else {
				//concatenate the argurments to get the full line
				fullLine = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullLine.append(" " + s);
				}
				address.setLine2(fullLine.toString());
				
				outMessages.add(new TableBuilder("enter the city on the address, or enter nothing to make no changes."));
				currentAddressCommandState = FIVE;
			}
			break;
			
		case FIVE:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("enter the zip code, or enter nothing to make no changes"));
				currentAddressCommandState = SIX;
			} else {
				//concatenate the argurments to get the full line
				fullLine = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullLine.append(" " + s);
				}
				address.setCity(fullLine.toString());
				outMessages.add(new TableBuilder("enter the zip code, or enter nothing to make no changes"));
				currentAddressCommandState = SIX;
			}
			break;
			
		case SIX:
			
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("enter the state postal code, or enter nothing to make no changes."));
				currentAddressCommandState = SEVEN;
			} else if (cmd.length() != 10 && cmd.length() != 9 && cmd.length() != 5) {
				outMessages.add(new TableBuilder("invalid zip code, please try again."));
			} else if (!isInteger(cmd)) {
				outMessages.add(new TableBuilder("please make sure the zip code is only composed of integers."));
				outMessages.add(new TableBuilder("enter the zip code"));
			} else {
				address.setZip(cmd);
				outMessages.add(new TableBuilder("enter the state postal code, or enter nothing to make no changes."));
				currentAddressCommandState = SEVEN;
			}
			break;
		case SEVEN:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("your address: "));
				outMessages.add(new TableBuilder(currentUser.getFirstName() + " " + currentUser.getLastName()));
				outMessages.add(new TableBuilder("" + address.getLine1()));
				if (address.getLine2() != null) {
					outMessages.add(new TableBuilder("" + address.getLine2()));
				}
				outMessages.add(new TableBuilder(address.getCity() + ", " + address.getState() + " " + address.getZip()));
				outMessages.add(new TableBuilder("enter (1) to confirm address creation, enter (2) to cancel"));
				
				currentAddressCommandState = EIGHT;
			} else if (cmd.length() != 2) {
				outMessages.add(new TableBuilder("Enter only the state postal code."));
			} else {
				address.setState(cmd);
				outMessages.add(new TableBuilder("your address: "));
				outMessages.add(new TableBuilder(currentUser.getFirstName() + " " + currentUser.getLastName()));
				outMessages.add(new TableBuilder("" + address.getLine1()));
				if (address.getLine2() != null 
						&& !address.getLine2().equals("") 
						&& !address.getLine2().equals(" "))  {
					outMessages.add(new TableBuilder("" + address.getLine2()));
				}
				outMessages.add(new TableBuilder(address.getCity() + ", " + address.getState() + " " + address.getZip()));
				outMessages.add(new TableBuilder("enter (1) to confirm address creation, enter (2) to cancel"));
				
				currentAddressCommandState = EIGHT;
			}

			break;
		case EIGHT:
			if (cmd.equals("1") || cmd.equals("confirm")) {
				endMenu();
				return address;
			} else {
				outMessages.add(new TableBuilder("address update has been canceled..."));
			}
			endMenu();
			break;
		default:
			break;
		}		

		return null;
	}
	
	/**
	 * shows the user's addresses.
	 * @return a list of user addresses
	 */
	public final List<Address> getAddresses() {
		boolean hasSecondLine = false;
		outMessages.clear();
		try {
			addresses = addressDAO.getByUser(currentUser);
			
			if (addresses.isEmpty()){
				outMessages.add(new TableBuilder("no addresses to show"));
			} else {
				List<String> line1s = new ArrayList<String>();
				List<String> line2s = new ArrayList<String>();
				List<String> cities = new ArrayList<String>();
				List<String> zips = new ArrayList<String>();
				List<String> states = new ArrayList<String>();
				List<String> list = new ArrayList<String>();
				
				int counter = 1;
				for (Address a : addresses) {
					line1s.add(a.getLine1());
					if (a.getLine2() != null 
							&& !a.getLine2().equals("") 
							&& !a.getLine2().equals(" ")) {
						line2s.add(a.getLine2());
						hasSecondLine = true;
					} else {
						line2s.add("");
					}
					cities.add(a.getCity());
					zips.add(a.getZip());
					states.add(a.getState());
					list.add("#" + counter++);
				}
				
				TableBuilder table = new TableBuilder();
				table.addColumn("address: ", list);
				table.addColumn("line 1: ", line1s);
				if (hasSecondLine) {
					table.addColumn("line 2: " , line2s);
				}
				table.addColumn("city: ", cities);
				table.addColumn("state: ", states);
				table.addColumn("zip code: " , zips);

				outMessages.add(table);
			}
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}
		return addresses;
	}

	

	/**
	 * allows a user to delete one of their
	 * addresses.
	 * @param cmd the command in use
	 */
	private void deleteAddress(final String cmd) {
//		//switch is used to determine how far into the menu the user is
		switch (currentAddressCommandState) {
		case ONE:
		
			getAddresses();
			if (addresses.isEmpty()) {
				outMessages.add(new TableBuilder("please create an address before deleting"));
				endMenu();
			} else {
				outMessages.add(new TableBuilder("Which address would you like to delete?"));
				outMessages.add(new TableBuilder("enter the number of the address you would like to delete, or enter <cancel> to cancel."));
				currentAddressCommandState = TWO;
			}

		break;
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave selection blank."));
				outMessages.add(new TableBuilder("enter the number of the address you would like to delete, or enter <cancel> to cancel."));
			
				//check if the user canceled the operation
			} else if (cmd.equals("cancel")) {
				outMessages.add(new TableBuilder("delete canceled."));
				endMenu();
				
				//check if the command is an integer
			} else if (isInteger(cmd)){
				int selection = Integer.parseInt(cmd) - 1;
				if (selection < 0 || selection >= addresses.size()) {
					outMessages.add(new TableBuilder("#" + cmd + " is not valid, enter a number between 1 and " + addresses.size()));
					outMessages.add(new TableBuilder("enter the number of the card you would like to delete, or enter <cancel> to cancel."));
				} else {
					Address address = addresses.get(selection);
					
					try {
						addressDAO.delete(address);
						outMessages.add(new TableBuilder("address deleted"));
					} catch (SQLException e) {
						sqlExceptionMessage(e);
					}

					endMenu();
				}
				
		
			//they did not enter an int
			} else {
				outMessages.add(new TableBuilder("please enter only an integer."));
				outMessages.add(new TableBuilder("enter the number of the address you would like to delete, or enter <cancel> to cancel."));
			}
			break;
		default:
			break;
		}	


	}

	
	/**
	 * shows the billing address for a card.
	 * @param cardID the card id to get
	 */
	public final void getBillingAddress(final int cardID) {
		//unimplemented
		Address a = null;
		
		try {
			a = addressDAO.getBilling(cardID);
			if (a == null){
				outMessages.add(new TableBuilder("no address to show"));
			} else {
				
				TableBuilder table = new TableBuilder();
				table.addColumn("line 1: ", a.getLine1());
				if (a.getLine2() != null 
						&& !a.getLine2().equals("") 
						&& !a.getLine2().equals(" "))  {
					table.addColumn("line 2: " , a.getLine2());
				}
				table.addColumn("city: ", a.getCity());
				table.addColumn("state: ", a.getState());
				table.addColumn("zip code: " , a.getZip());

				outMessages.add(table);
			}
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}
	}
	
	/**
	 * return help information to main.
	 */
	public void showHelp() {
		String[] headers = new String[2];
		String[] row = new String[2];
		headers[0] = "command: ";
		headers[1] = "action: ";
		TableBuilder table = new TableBuilder(headers);
		
		row[0] ="<help>";
		row[1] ="to show this dialogue";
		table.addRow(row);
		row = new String[2];
		
		row[0] = "<menu>";
		row[1] = "to go back to the main menu";
		table.addRow(row);
		row = new String[2];
		
		row[0] = "<user>";
		row[1] = "to go back to the user info section";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<show>";
		row[1] ="to display current address information";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<add>";
		row[1] ="to add a new address";
		table.addRow(row);
		row = new String[2];

		row[0] ="<delete>";
		row[1] ="to delete an address";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<update>";
		row[1] ="to update address information";
		table.addRow(row);
		row = new String[2];
		
		outMessages.add(table);
	}
	
	
	/**
	 * set the menu progress
	 * back to its initial
	 * state.
	 */
	private void endMenu(){
		//set the progress back to one
		currentAddressCommandState = ONE;
		backToAddressMenu = true;
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
			outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
					+ e.getSQLState() + " please notify your database administrator."));
		}
		
	}
	
	/**
	 * checks if a string is only integers.
	 * @param s the string to check
	 * @return whether it is a string or not.
	 */
	private boolean isInteger(final String s) {
	    return isInteger(s,10);
	}

	/**
	 * checks if a string contains integers.
	 * @param s the string to check
	 * @param radix 
	 * @return whether it is a string or not.
	 */
	private boolean isInteger(final String s, final int radix) {
	    if(s.isEmpty()) {
			return false;
		}
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) {
					return false;
				} else {
					continue;
				}
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) {
				return false;
			}
	    }
	    return true;
	}
	
	/**
	 * @return the current out messages
	 */
	public List<TableBuilder> getOutMessages (){
		return outMessages;
	}
}
