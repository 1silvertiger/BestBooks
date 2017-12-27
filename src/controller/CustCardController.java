package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.Address;
import model.AddressDAO;
import model.AddressType;
import model.CardSecure;
import model.CardSecureDAO;
import model.User;
import util.AsciiBanner;
import util.TableBuilder;

/**
 * controls customer card information.
 * @author jdowd
 *
 */
public class CustCardController {
	// ---------------------Out messages -------------------------
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();
	private AsciiBanner banner = new AsciiBanner();

	private User currentUser;
	private Address address;
	private List<Address> addresses; 
	private CardSecure card = new CardSecure();
	private List<CardSecure> cards;
	private CustAddressController addressController;
	private boolean hasAddresses = false;
	private boolean makingAddress = false;
	private AddressDAO addressDAO;
	private boolean backToCardMenu = true;
	
	
	/**
	 * stores the current context menu being used.
	 */
	private String currentCardCommand = "";
	
	/**
	 * store the current state of a context menu.
	 */
	private int currentCardCommandState = 1;
	
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
	
	// DAO
	private CardSecureDAO cardDAO;

	// Commands
	private static final String ADD = "add";
	private static final String HELP = "help";
	private static final String UPDATE = "update";
	private static final String SHOW = "show";
	private static final String DELETE = "delete";
	private static final String CARD = "card";
	
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
	public CustCardController(final Connection c, final User myUser) {
		currentUser = myUser;
		
		addressController = new CustAddressController(c, myUser);
		
		// DAO
		try {
			addressDAO = new AddressDAO(c);
			cardDAO = new CardSecureDAO(c);
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
	 * @param myCard 
	 * @return the generated output
	 */
	public List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		
		
		
		//run this only if the card has completed a context menu
		if (backToCardMenu) {
			currentCardCommand = cmd;
			
		}

		// We switch the command
		switch (currentCardCommand) {
		case HELP:
			showHelp();
			break;
		case UPDATE:
				backToCardMenu = false;
				updateCard(cmd, args);
			break;
		case ADD:
			backToCardMenu = false;
			addCard(cmd, args);
			break;
		case SHOW:
				getCards();
			break;
		case DELETE:
				backToCardMenu = false;
				deleteCard(cmd);
			break;
		case CARD:
			try {
				outMessages.addAll(banner.getBanner("CARDS"));
			} catch (IOException e) {
				outMessages.add(new TableBuilder("Card information section!"));
			}
		
			showHelp();
			break;
		default:
			outMessages.add(new TableBuilder(
					"Invalid command:" + " '" + currentCardCommand + "'" + ", enter <help> to see valid commands"));
		}
		return outMessages;
	}


	/**
	 * add a credit card to the user account.
	 * @param cmd the command in use
	 * @param args the arguments in use
	 */
	private void addCard(final String cmd, final String[] args) {
		
		card.setUserID(currentUser.getUserID());

		switch (currentCardCommandState) {
		
		//initial message
		case ONE:
			outMessages.add(new TableBuilder("adding a new card!"));
			outMessages.add(new TableBuilder("enter the name on the card."));
			currentCardCommandState = TWO;
			break;
			//add the name to the card
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave name blank."));
				outMessages.add(new TableBuilder("enter the name on the card."));
			} else {
				//concatenate the argurments to get the full name
				StringBuilder fullName = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullName.append(" " + s);
				}
				card.setCardHolderName(fullName.toString());
				
				outMessages.add(new TableBuilder("enter the number on the card."));
				currentCardCommandState = THREE;
			}
			break;
			
			//add the number to the card
		case THREE:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave the number blank."));
				outMessages.add(new TableBuilder("enter the number on the card."));
			} else if (cmd.length() != 16) {
				outMessages.add(new TableBuilder("please ensure that exactly 16 digits are entered."));
				outMessages.add(new TableBuilder("enter the number on the card."));
			} else if (isInteger(cmd)) {
				card.setCardNumber(cmd);
				
				outMessages.add(new TableBuilder("enter the ccv number."));
				currentCardCommandState = FOUR;
			} else {
				outMessages.add(new TableBuilder("please ensure that an integer number is entered"));
				outMessages.add(new TableBuilder("enter the card number"));
			}
			break;
			
			//enter the CVC to the card
		case FOUR:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave the number blank."));
				outMessages.add(new TableBuilder("enter the ccv number on the card."));
			} else if (cmd.length() != 3) {
				outMessages.add(new TableBuilder("please ensure that exactly 3 digits are entered."));
				outMessages.add(new TableBuilder("enter the ccv number on the card."));
			} else if (isInteger(cmd)) {
				card.setCvc(cmd);
				
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
				currentCardCommandState = FIVE;
			} else {
				outMessages.add(new TableBuilder("please ensure that an integer number is entered"));
				outMessages.add(new TableBuilder("enter the ccv number"));
			}
			break;
			
			//enter date for the card
		case FIVE:
			int currentYear = 0;
			int currentMonth = 0;
			int dateYear = 0;
			int dateMonth = 0;
			if(cmd.matches("\\d{2}-\\d{2}")) {
				dateYear = Integer.parseInt(new StringBuilder().append(cmd.charAt(3)).append(cmd.charAt(4)).toString());
				dateMonth = Integer.parseInt(new StringBuilder().append(cmd.charAt(0)).append(cmd.charAt(1)).toString());
				
				DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yy");
				DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
				LocalDateTime dateTime = LocalDateTime.now();
				currentYear = Integer.parseInt(dateTime.format(yearFormat));
				currentMonth = Integer.parseInt(dateTime.format(monthFormat));
			}
			
			
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave the date blank."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
				break;
			} else if (cmd.length() != 5) {
				outMessages.add(new TableBuilder("please make sure to follow the given format."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
				break;
			} else if (!cmd.matches("\\d{2}-\\d{2}")) {
				outMessages.add(new TableBuilder("please make sure to follow the given format."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
				break;
			} else if (dateYear < currentYear) {
				outMessages.add(new TableBuilder("the number you entered is expired."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
				break;
			} else if (dateMonth < currentMonth  && dateYear == currentYear) {
				outMessages.add(new TableBuilder("the number you entered is expired."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
				break;
			} else if (dateMonth > 12 || dateMonth < 1) {
				outMessages.add(new TableBuilder("please make sure the month is correct."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
				break;
			} else {

				card.setExpirationDate(cmd);
			
				addresses = addressController.getAddresses();
				if (!addresses.isEmpty()) {
					outMessages.add(new TableBuilder("select address from the list or create a new address."));
					outMessages.addAll(addressController.getOutMessages());
					outMessages.add(new TableBuilder("enter the number of the address to use existing shipping address."));
					outMessages.add(new TableBuilder("or enter <new> to create a new address."));
					hasAddresses = true;
					currentCardCommandState = SIX;
					break;
				} else {
					outMessages.add(new TableBuilder("create an address for the card."));
					hasAddresses = false;
					makingAddress = true;
				}
				

				
				currentCardCommandState = SIX;

			}
			
		case SIX:
			//see if the user is in the process of making a new address
			if (makingAddress || cmd.equals("new")) {
				address = addressController.addAddress(cmd, args);
				outMessages.addAll(addressController.getOutMessages());
				//see if the address is done or not
				if (address != null) {
					makingAddress = false;
					outMessages.add(new TableBuilder("Name on card: " + card.getCardHolderName()));
					outMessages.add(new TableBuilder("Card Number: " + card.getCardNumber()));
					outMessages.add(new TableBuilder("CVC number: " + card.getCvc()));
					outMessages.add(new TableBuilder("Expiration date: " + card.getExpirationDate()));
					outMessages.add(new TableBuilder("enter (1) to confirm card creation, enter (2) to cancel"));
					currentCardCommandState = SEVEN;
				}
				//if the user wanted to create a new address
				if (cmd.equals("new")) {
					makingAddress = true;
				} 
				hasAddresses = false;
			}
			//check if there were any shipping addresses
			if (hasAddresses) {
				
				//if the user entered and integer or not
				if (isInteger(cmd)) {
					//make sure the number entered was in the range
					int selection = Integer.parseInt(cmd) - 1;
					if (selection < 0 || selection >= addresses.size()) {
						outMessages.add(new TableBuilder("enter a number from 1 to " + addresses.size() + "."));
						//if the user wanted to use an existing address
					} else {
						address = addresses.get(selection);
						outMessages.add(new TableBuilder("Name on card: " + card.getCardHolderName()));
						outMessages.add(new TableBuilder("Card Number: " + card.getCardNumber()));
						outMessages.add(new TableBuilder("CVC number: " + card.getCvc()));
						outMessages.add(new TableBuilder("Expiration date: " + card.getExpirationDate()));
						outMessages.add(new TableBuilder("enter (1) to confirm card creation, enter (2) to cancel"));
						currentCardCommandState = SEVEN;
					}
				} else {
					outMessages.add(new TableBuilder("invalid command, enter a number from the list of addresses or <new> to procede"));
				}
				//if there were no addresses
			} 
		

			
			break;
		case SEVEN:
			if (cmd.equals("1") || cmd.equals("confirm")) {
				try {
					cardDAO.create(card);
					outMessages.add(new TableBuilder("card created"));
					address.setType(AddressType.CARD);
					address.setOwnerID(card.getCardID());
					addressDAO.create(address);
				} catch (SQLException e) {
					sqlExceptionMessage(e);
				}
			} else {
				outMessages.add(new TableBuilder("card creation has been canceled..."));
			}
			endMenu();
			break;
		default:
			break;
		}
	}

	/**
	 * allows a user to update their
	 * card information.
	 * @param cmd the command in use
	 * @param args the arguments in use
	 */
	private void updateCard(final String cmd, final String[] args) {	

//		//switch is used to determine how far into the menu the user is
		switch (currentCardCommandState) {
		case ONE:
		
		getCards();
		if (cards.isEmpty()) {
			outMessages.add(new TableBuilder("please create a card before updating"));
			endMenu();
		} else {
			outMessages.add(new TableBuilder("Which card would you like to edit?"));
			outMessages.add(new TableBuilder("enter the number of the card you would like to edit, or enter <cancel> to cancel."));
			currentCardCommandState = TWO;
		}

		break;
		
		//decide which card to update
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave selection blank."));
				outMessages.add(new TableBuilder("enter the number of the card you would like to edit, or enter <cancel> to cancel."));
			
				//check if the user canceled the operation
			} else if (cmd.equals("cancel")) {
				outMessages.add(new TableBuilder("update canceled."));
				endMenu();
			
				//check if the command is an integer
			} else if (isInteger(cmd)){
				int selection = Integer.parseInt(cmd) - 1;
				if (selection < 0 || selection >= cards.size()) {
					outMessages.add(new TableBuilder("#" + cmd + " is not valid, enter a number between 1 and " + cards.size()));
					outMessages.add(new TableBuilder("enter the number of the card you would like to edit, or enter <cancel> to cancel."));
				} else {
					card = cards.get(selection);
					outMessages.add(new TableBuilder("enter a new name, or enter nothing to make no changes."));
					currentCardCommandState = THREE;
				}
				
				
			//they did not enter an int
			} else {
				outMessages.add(new TableBuilder("please enter only an integer."));
				outMessages.add(new TableBuilder("enter the number of the card you would like to edit, or enter <cancel> to cancel."));
			}
			break;
			
			//enter the name on the card
		case THREE:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("enter the new ccv number, or enter nothing to make no changes."));
				currentCardCommandState = FIVE;
			} else {

				//concatenate the argurments to get the full name
				StringBuilder fullName = new StringBuilder(cmd);
				for (String s : args) {
					//add a space before the arg and add it to the cmd
					fullName.append(" " + s);
				}
				card.setCardHolderName(fullName.toString());
				outMessages.add(new TableBuilder("enter the new ccv number, or enter nothing to make no changes."));
				currentCardCommandState = FIVE;
				
			}
			break;
			
			//enter the card's ccv number
		case FIVE:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave ccv number blank."));
			} else if (cmd.length() != 3) {
				outMessages.add(new TableBuilder("please ensure that exactly 3 digits are entered."));
				outMessages.add(new TableBuilder("enter the new ccv number, or enter nothing to make no changes."));
			} else if (isInteger(cmd)) {
				//if number is numeric
				card.setCvc(cmd);
				
				outMessages.add(new TableBuilder("enter the new expiration date (Format: MM-yy), or enter nothing to make no changes."));
				currentCardCommandState = SIX;
			} else {
				outMessages.add(new TableBuilder("please ensure that an integer number is entered"));
				outMessages.add(new TableBuilder("enter the new ccv number, or enter nothing to make no changes."));
			}
			break;
			
			//enter the date for the card
		case SIX:
			int currentYear = 0;
			int currentMonth = 0;
			int dateYear = 0;
			int dateMonth = 0;
			if(cmd.matches("\\d{2}-\\d{2}")) {
				dateYear = Integer.parseInt(new StringBuilder().append(cmd.charAt(3)).append(cmd.charAt(4)).toString());
				dateMonth = Integer.parseInt(new StringBuilder().append(cmd.charAt(0)).append(cmd.charAt(1)).toString());
				
				DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yy");
				DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM");
				LocalDateTime dateTime = LocalDateTime.now();
				currentYear = Integer.parseInt(dateTime.format(yearFormat));
				currentMonth = Integer.parseInt(dateTime.format(monthFormat));
			}
			
			
			if (cmd.equals("")) {
				currentCardCommandState = SEVEN;
				
				addresses = addressController.getAddresses();
				if (!addresses.isEmpty()) {
					outMessages.add(new TableBuilder("select address from the list or create a new address."));
					outMessages.addAll(addressController.getOutMessages());
					outMessages.add(new TableBuilder(""));
					outMessages.add(new TableBuilder("enter the number of the address to use existing shipping address."));
					outMessages.add(new TableBuilder("enter <new> to create a new address."));
					outMessages.add(new TableBuilder("enter nothing to make no changes."));
					hasAddresses = true;
				} else {
					outMessages.add(new TableBuilder("enter <new> to create a new address for the card, or enter nothing to make no changes."));
					hasAddresses = false;
				}
			} else if (cmd.length() != 5) {
				outMessages.add(new TableBuilder("please make sure to follow the given format."));
				outMessages.add(new TableBuilder("enter the new expiration date (Format: MM-yy), or enter nothing to make no changes."));
			} else if (!cmd.matches("\\d{2}-\\d{2}")) {
				outMessages.add(new TableBuilder("please make sure to follow the given format."));
				outMessages.add(new TableBuilder("enter the new expiration date (Format: MM-yy), or enter nothing to make no changes."));
			} else if (dateYear < currentYear) {
				outMessages.add(new TableBuilder("the number you entered is expired."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
			} else if (dateMonth < currentMonth && dateYear == currentYear) {
				outMessages.add(new TableBuilder("the number you entered is expired."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
			} else if (dateMonth > 12 || dateMonth < 1) {
				outMessages.add(new TableBuilder("please make sure the month is correct."));
				outMessages.add(new TableBuilder("enter the expiration date on the card. (Format: MM-yy)"));
			} else {

				card.setExpirationDate(cmd);
				currentCardCommandState = SEVEN;
				addresses = addressController.getAddresses();
				if (!addresses.isEmpty()) {
					outMessages.add(new TableBuilder("select address from the list or create a new address."));
					outMessages.addAll(addressController.getOutMessages());
					outMessages.add(new TableBuilder("enter the number of the address to use existing shipping address."));
					outMessages.add(new TableBuilder("enter <new> to create a new address."));
					outMessages.add(new TableBuilder("enter nothing to make no changes."));
					hasAddresses = true;
				} else {
					outMessages.add(new TableBuilder("enter <new> to create a new address for the card, or enter nothing to make no changes."));
					hasAddresses = false;
					makingAddress = true;
				}
				
			}
			break;
			
			//enter address info
		case SEVEN:
			//see if the user is in the process of making a new address
			if (makingAddress || cmd.equals("new")) {
				address = addressController.addAddress(cmd, args);
				outMessages.addAll(addressController.getOutMessages());
				//see if the address is done or not
				if (address != null) {
					makingAddress = false;
				}
				//if the user wanted to create a new address
				if (cmd.equals("new")) {
					makingAddress = true;
				} 
			}

			//check if the user wanted to change or not
			else if (!cmd.equals("")) {
				//check if there were any shipping addresses
				if (hasAddresses) {
					//if the user entered and integer or not
					if (isInteger(cmd)) {
						//make sure the number entered w
						int selection = Integer.parseInt(cmd) - 1;
						if (selection < 0 || selection >= addresses.size()) {
							outMessages.add(new TableBuilder("enter a number from 1 to " + addresses.size() + "."));
							//if the user wanted to use an existing address
						} else {
							address = addresses.get(selection);
							currentCardCommandState = EIGHT;
						}
					} else {
						outMessages.add(new TableBuilder("invalid command, enter nothing, a number from the list of addresses, or <new> to procede"));
					}
					//if there were no addresses
				}
	
				
				
				outMessages.add(new TableBuilder("Name on card: " + card.getCardHolderName()));
				outMessages.add(new TableBuilder("Card Number: " + card.getCardNumber()));
				outMessages.add(new TableBuilder("CVC number: " + card.getCvc()));
				outMessages.add(new TableBuilder("Expiration date: " + card.getExpirationDate()));
				outMessages.add(new TableBuilder("enter (1) to confirm card creation, enter (2) to cancel"));
				currentCardCommandState = EIGHT;
				
			} else {
				outMessages.add(new TableBuilder("Name on card: " + card.getCardHolderName()));
				outMessages.add(new TableBuilder("Card Number: " + card.getCardNumber()));
				outMessages.add(new TableBuilder("CVC number: " + card.getCvc()));
				outMessages.add(new TableBuilder("Expiration date: " + card.getExpirationDate()));
				outMessages.add(new TableBuilder("enter (1) to confirm card creation, enter (2) to cancel"));
				currentCardCommandState = EIGHT;
			}
			break;
		case EIGHT:
			if (cmd.equals("1") || cmd.equals("confirm")) {
				try {
					card.decryptFirstTwelveDigits();
					cardDAO.update(card);
					address.setType(AddressType.CARD);
					address.setOwnerID(card.getCardID());
					addressDAO.updateByCard(address);
					outMessages.add(new TableBuilder("card updated"));
				} catch (SQLException e) {
					sqlExceptionMessage(e);
				}
			} else {
				outMessages.add(new TableBuilder("card update has been canceled..."));
			}
			endMenu();
			break;
		default:
			break;
		}

	}

	
	/**
	 * shows the user's card information.
	 * @return the user's cards
	 */
	public final List<CardSecure> getCards() {
		outMessages.clear();
		User user = currentUser;
		try {
			cards = cardDAO.getCardsByUserID(user);
			if (cards.isEmpty()){
				outMessages.add(new TableBuilder("no cards to show"));
			} else {
				List<String> names = new ArrayList<String>();
				List<String> numbers = new ArrayList<String>();
				List<String> dates = new ArrayList<String>();
				List<String> list = new ArrayList<String>();
				
				int counter = 1;
				for (CardSecure c : cards) {
					names.add(c.getCardHolderName());
					numbers.add(c.getCardNumber());
					dates.add(c.getExpirationDate());
					list.add("#" + counter++);
				}
				
				TableBuilder table = new TableBuilder();
				table.addColumn("card: ", list);
				table.addColumn("name on card: ", names);
				table.addColumn("card number: " , numbers);
				table.addColumn("expiration date: ", dates);

				outMessages.add(table);
				
				return cards;
			}
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}
		return null;

	}

	

	/**
	 * allows the user to delete a credit card
	 * @param cmd the command in use
	 */
	private void deleteCard(final String cmd) {
//		//switch is used to determine how far into the menu the user is
		switch (currentCardCommandState) {
		case ONE:
		
		getCards();
		if (cards.isEmpty()) {
			outMessages.add(new TableBuilder("please create a card before deleting"));
			endMenu();
		} else {
			outMessages.add(new TableBuilder("Which card would you like to delete?"));
			outMessages.add(new TableBuilder("enter the number of the card you would like to delete, or enter <cancel> to cancel."));
			currentCardCommandState = TWO;
		}

		break;
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave selection blank."));
				outMessages.add(new TableBuilder("enter the number of the card you would like to delete, or enter <cancel> to cancel."));
			
				//check if the user canceled the operation
			} else if (cmd.equals("cancel")) {
				outMessages.add(new TableBuilder("delete canceled."));
				endMenu();
				
				//check if the command is an integer
			} else if (isInteger(cmd)){
				int selection = Integer.parseInt(cmd) - 1;
				if (selection < 0 || selection >= cards.size()) {
					outMessages.add(new TableBuilder("#" + cmd + " is not valid, enter a number between 1 and " + cards.size()));
					outMessages.add(new TableBuilder("enter the number of the card you would like to delete, or enter <cancel> to cancel."));
				} else {
					card = cards.get(selection);
					
					try {
						cardDAO.delete(card);
						outMessages.add(new TableBuilder("card deleted"));
					} catch (SQLException e) {
						sqlExceptionMessage(e);
					}

					endMenu();
				}
				
		
			//they did not enter an int
			} else {
				outMessages.add(new TableBuilder("please enter only an integer."));
				outMessages.add(new TableBuilder("enter the number of the card you would like to edit, or enter <cancel> to cancel."));
			}
			break;
		default:
			break;
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
		row[1] ="to display current card information";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<add>";
		row[1] ="to add a new credit card";
		table.addRow(row);
		row = new String[2];

		row[0] ="<delete>";
		row[1] ="to delete the current card account";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<update>";
		row[1] ="to update current card information";
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
		currentCardCommandState = ONE;
		backToCardMenu = true;
		makingAddress = false;
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
	 * @return the current out messages
	 */
	public List<TableBuilder> getOutMessages (){
		return outMessages;
	}
}
