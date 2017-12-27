package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Address;
import model.CardSecure;
import model.CartItem;
import model.CartItemDAO;
import model.InvCartBook;
import model.Order;
import model.OrderDAO;
import model.User;
import reports.Printer;
import util.AsciiBanner;
import util.TableBuilder;

/**
 * This controls the information entered by the user
 * regarding their shopping cart of items.
 * @author jdowd
 *
 */
public class CustCartController {
	// ---------------------Out messages -------------------------
	/**
	 * the list of messages passed back to main.
	 */
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>(); 
	private AsciiBanner banner = new AsciiBanner();
	private Printer printer;
	/**
	 * an instance of the user class.
	 */
	private User currentUser;

	
	/**
	 *used to see if the user is done with a context menu. 
	 */
	private boolean backToCartMenu = true;
	
	private double totalCost = 0;
	
	private int ccvAtempts = 0;
	
	/**
	 * stores the current context menu being used.
	 */
	private String currentCartCommand = "";

	
// unimplemented shipping data	
//	/**
//	 * the price to ship an item per pound.
//	 */
//	private static final double SHIPPING_PRICE_PER_POUND = 0.40;
//	
//	/**
//	 * the minimum cost for shipping.
//	 */
//	private static final double BASE_SHIPPING_FEE = 4;
//	
	/**
	 * the value of a ten percent discount.
	 */
	private static final double TEN_PERCENT_DISCOUNT = 0.10;
	
	/**
	 * the value of a twenty percent discount.
	 */
	private static final double TWENTY_PERCENT_DISCOUNT = 0.20;
	
	/**
	 * the dollar value where a ten percent discount should apply.
	 */
	private static final double TEN_PERCENT_DISCOUNT_AMOUNT = 75;
	
	/**
	 * the dollar value where a twenty percent discount should apply.
	 */
	private static final double TWENTY_PERCENT_DISCOUNT_AMOUNT = 300;
	
	/**
	 * store the current state of a context menu.
	 */
	private int currentCartCommandState = 1;
	
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
	
	/**
	 * used to format money output.
	 */
	private NumberFormat formatCurrency = NumberFormat.getCurrencyInstance();
	
	// DAO
	
	/**
	 * the DAO for cart items.
	 */
	private CartItemDAO cartDAO;
	
	
	/**
	 * the DAO for orders.
	 */
	private OrderDAO orderDAO;
	
	// Commands
	/**
	 * command for help.
	 */
	private static final String HELP = "help";
	/**
	 * command for update.
	 */
	private static final String UPDATE = "update";
	/**
	 * command for show.
	 */
	private static final String SHOW = "show";
	/**
	 * command for remove.
	 */
	private static final String REMOVE = "remove";
	/**
	 * command for cart.
	 */
	private static final String CART = "cart";
	/**
	 * command for order.
	 */
	private static final String ORDER = "order";

	/**
	 * instance of cart item.
	 */
	private CartItem cart = new CartItem();
	
	/**
	 * list of several cart items.
	 */
	private List<InvCartBook> invCarts = new ArrayList<InvCartBook>();
	
	/**
	 *  SQL Exception numbers.
	 */
	private static final String KNOWN_SQL_ERROR_PREFIX = "45";

	/**
	 *  Messages.
	 */
	private static final String SQL_EXCEPTION_MESSAGE = "A connection error occurred. Check your internet connection and try again.";

	
	/**  */
	private CustAddressController addressController;
	private List<CardSecure> cards;
	private CardSecure card;
	private CustCardController cardController;
	private List<Address> addresses;
	private Address address;
	private Order order = new Order();

	/**
	 * the constructor sets up the DB connection for 
	 * the DAO and sets the current user instance to a field.
	 * @param c the connection to the database.
	 * @param myUser the current user session.
	 */
	public CustCartController(final Connection c, final User myUser) {
		currentUser = myUser;
		
		// the controllers need the user insance passed to them
		addressController = new CustAddressController(c, myUser);
		cardController = new CustCardController(c, myUser);
		
		// DAO and Printer connections
		try {
			printer = new Printer(c);
			cartDAO = new CartItemDAO(c);
			orderDAO = new OrderDAO(c);
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
	 * @return the generated output
	 */
	public List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		
		
		
		//run this only if the cart has completed a context menu
		if (backToCartMenu) {
			currentCartCommand = cmd;
			
		}

		// We switch the command
		switch (currentCartCommand) {
		case HELP:
			showHelp();
			break;
		case UPDATE:
				//update only allows for a change of quantities 
				backToCartMenu = false;
				updateCart(cmd);
			break;
			//displays current cart items
		case SHOW:
				getCarts();
			break;
			//drops an item from the cart
		case REMOVE:
				backToCartMenu = false;
				deleteCartItem(cmd);
			break;
			//place an order
		case ORDER:
			backToCartMenu = false;
			tryOrder(cmd);
			break;
		case CART:
			try {
				outMessages.addAll(banner.getBanner("CART"));
			} catch (IOException e) {
				outMessages.add(new TableBuilder("Shopping Cart!"));
			}
	

			showHelp();
			break;
		default:
			outMessages.add(new TableBuilder(
					"Invalid command:" + " '" + currentCartCommand + "'" + ", enter <help> to see valid commands"));
		}
		return outMessages;
	}

	/**
	 * Receives input and walks the user through a menu
	 * to create their order.
	 * @param cmd the command being used for the context menu.
	 */
	private void tryOrder(final String cmd) {
		//check how far into the menu the user is
		switch (currentCartCommandState) {
		
		//display initial statement
		case ONE:
			//get the users address and card information from those controllers
			addresses = addressController.getAddresses();
			cards = cardController.getCards();
			
			//display the cart items to the user
			getCarts();
			//check if the user has added any items to their cart
			if (invCarts.isEmpty()){
				outMessages.add(new TableBuilder("Please add items to cart before making an order"));
				outMessages.add(new TableBuilder("Enter <menu> to go back to the menu, then enter <find> to find books to add to cart"));
				endMenu();
				
			//only run if the user has addresses entered
			} else if (addresses != null && cards != null && !addresses.isEmpty() && !cards.isEmpty()) {
				//calculate total cost and add shipping
				
			//	double shippingCost = BASE_SHIPPING_FEE;
				int numberOfItems = 0;
				
				//generate the number of items from the cart items
				for (InvCartBook c : invCarts) {
					//unused shipping cost
				//	shippingCost = shippingCost + (c.getWeight() * SHIPPING_PRICE_PER_POUND);
					numberOfItems = numberOfItems + c.getQuantity();
				}
				
				//ask the user if they want to make the order
				outMessages.add(new TableBuilder("Would you like to order these books?"));
				
				String[] headers = new String[2];
				String[] row = new String[2];
				
				//display the cost of the books
				headers[0] = "cost of these " + numberOfItems + " books: ";
				headers[1] = formatCurrency.format(totalCost);
				TableBuilder table = new TableBuilder(headers);
				
				
//				unused shipping display
//				row[0] ="shipping costs: ";
//				row[1] = formatCurrency.format(shippingCost);
//				table.addRow(row);
//				row = new String[2];
				
				double discount = 0;
				//set the discount for the order
				if (totalCost > TWENTY_PERCENT_DISCOUNT_AMOUNT) {
					//display the discount as twenty percent
					row[0] ="discount: ";
					row[1] = formatCurrency.format(-(totalCost * (TWENTY_PERCENT_DISCOUNT)));
					table.addRow(row);
					row = new String[2];
					
					discount = totalCost * (TWENTY_PERCENT_DISCOUNT);
					
				} else if (totalCost > TEN_PERCENT_DISCOUNT_AMOUNT) {
					// dIscount at ten percent
					row[0] ="discount: ";
					row[1] = formatCurrency.format(-(totalCost * (TEN_PERCENT_DISCOUNT)));
					table.addRow(row);
					row = new String[2];
					
					discount = totalCost * (TEN_PERCENT_DISCOUNT);
				}
				
				//display the total with discount
				row[0] ="total cost: ";
				row[1] = formatCurrency.format((totalCost - discount));
				table.addRow(row);
				row = new String[2];
				

				table.rightJustified();
				outMessages.add(table);
				
				//ask the user to confirm
				outMessages.add(new TableBuilder("Enter (1) to place order, enter (2) to cancel"));
				currentCartCommandState = TWO;
				
			//if the user does not have any addresses on file
			} else if (addresses == null || addresses.isEmpty()) {
				outMessages.add(new TableBuilder("Please make sure to add an address before placing an order"));
				outMessages.add(new TableBuilder("Enter <menu> to go back to the menu, then enter <user> to edit user info, then enter <address> to edit address info"));
				endMenu();
				
			//if the user does not have any cards on file
			} else if (cards == null || cards.isEmpty()) {
				outMessages.add(new TableBuilder("Please make sure to add a credit card before placing an order"));
				outMessages.add(new TableBuilder("Enter <menu> to go back to the menu, then enter <user> to edit user info, then enter <card> to edit credit card info"));
				endMenu();
				
			//if something goes bump
			} else {
				outMessages.add(new TableBuilder("An error has occured"));
				outMessages.add(new TableBuilder("order cancelled"));
				endMenu();
			}
			break;
		
		//this case evaluate the confirm, and checks for credit cards and addresses
		case TWO:
			if (cmd.equals("1") || cmd.equals("yes") || cmd.equals("order")) {
				// if there is more than one address, have the user pick it and move to state 3
				if (addresses.size() > 1) {
					outMessages.addAll(addressController.getOutMessages());
					outMessages.add(new TableBuilder("enter the number of an address above to select it"));
					currentCartCommandState = THREE;
					break;
				} else {
					// if there is only one address just use it and check cards
					address = addresses.get(0);
		
					// if there is more than one card, make the user pick
					if (cards.size() > 1) {
						outMessages.addAll(cardController.getOutMessages());
						outMessages.add(new TableBuilder("enter the number of credit card above to select it"));
						currentCartCommandState = FOUR;
						break;
					} else {
						//just set the card to the only one available and move to the final confirmation case
						card = cards.get(0);
						currentCartCommandState = SEVEN;
						outMessages.add(new TableBuilder("enter the ccv number for credit card " + card.getCardNumber()));
						break;
					}
				}
				
			} else {
				outMessages.add(new TableBuilder("order cancelled"));
				endMenu();
				break;
			}
		
		//this part gets the selected address and asks for the credit card
		case THREE:
			if (isInteger(cmd)) {
				int addressSelection = Integer.parseInt(cmd) - 1;
				//see if the selection is in a valid range
				if (addressSelection >= 0 && addressSelection < addresses.size()) {
					address = addresses.get(addressSelection);
					if (cards.size() > 1) {
						outMessages.addAll(cardController.getOutMessages());
						outMessages.add(new TableBuilder("enter the number of credit card above to select it"));
						currentCartCommandState = FOUR;
						break;
					} else {
						//have the user enter the ccv number
						card = cards.get(0);
						outMessages.add(new TableBuilder("enter the ccv number for card " + card.getCardNumber()));
						currentCartCommandState = SEVEN;
						break;
					}
			// error messages
				} else {
					outMessages.add(new TableBuilder("please enter a number from 1 to " + addresses.size()));
				}
			} else {
				outMessages.add(new TableBuilder("please enter only a number"));
			}
			
		//this part checks for a selected card
		case FOUR:
			if (isInteger(cmd)) {
				int cardSelection = (Integer.parseInt(cmd) - 1);
				
				//check if the selection was in a valid range
				if (cardSelection >= 0 && cardSelection < cards.size()) {
					card = cards.get(cardSelection);
					outMessages.add(new TableBuilder("enter the ccv number for card " + card.getCardNumber()));
					currentCartCommandState = SEVEN;
					break;
				} else {
					outMessages.add(new TableBuilder("please enter a number from 1 to " + cards.size()));
				}
			} else {
				outMessages.add(new TableBuilder("please enter only a number"));
			}
			break;
			
		//this number is a little out of order, but it checks the ccv
		//and decrypts the card
		case SEVEN:
			ccvAtempts++;
			if (isInteger(cmd) && cmd.length() == 3) {
				CardSecure tempCard = new CardSecure(card);
				tempCard.setCvc(cmd);
				tempCard.decryptFirstTwelveDigits();
				String firstTwelve = tempCard.getFirstTwelveDigits();
				if (firstTwelve != null) {
					currentCartCommandState = FIVE;
					card.setFirstTwelveDigits(tempCard.getFirstTwelveDigits());
				} else {
					outMessages.add(new TableBuilder("incorrect ccv number, please try again"));
					if (ccvAtempts >= 3) {
						outMessages.add(new TableBuilder("incorrect ccv number, order cancelled"));
						endMenu();
					}
					break;
				}

			} else {
				outMessages.add(new TableBuilder("please enter only a three digit number"));
				if (ccvAtempts >= 3) {
					outMessages.add(new TableBuilder("incorrect ccv number, order cancelled"));
					endMenu();
				}
				break;
			}

			
		//display confirmation options to the user
		case FIVE:
			outMessages.add(new TableBuilder("\ndoes this look good?"));
			outMessages.add(new TableBuilder("Shipping to:"));
			outMessages.add(new TableBuilder(currentUser.getFirstName() + " " + currentUser.getLastName()));
			outMessages.add(new TableBuilder("" + address.getLine1()));
			if (address.getLine2() != null && !address.getLine2().equals("") && !address.getLine2().equals(" ")) {
				outMessages.add(new TableBuilder("" + address.getLine2()));
			}
			outMessages.add(new TableBuilder(address.getCity() + ", " + address.getState() + " " + address.getZip()));
			outMessages.add(new TableBuilder(""));
			outMessages.add(new TableBuilder("with credit card: " + card.getFirstTwelveDigits() + card.getLastFourDigits()));
			outMessages.add(new TableBuilder(""));
			outMessages.add(new TableBuilder("enter <confirm> to confirm purchase, enter <cancel> to cancel"));
			currentCartCommandState = SIX;
			break;
			
		//check the users answer to the confirmation question
		case SIX:
			if (cmd.equals("confirm")) {
				//assign the additional info to the order
				order.setAddressID(address.getAddressID());
				order.setCardID(card.getCardID());
				order.setCreationDate(LocalDateTime.now());
				order.setUserID(currentUser.getUserID());
				
				//set the discount for the order
				if (totalCost > TWENTY_PERCENT_DISCOUNT_AMOUNT) {
					order.setDiscountPercent(TWENTY_PERCENT_DISCOUNT);
				} else if (totalCost > TEN_PERCENT_DISCOUNT_AMOUNT) {
					order.setDiscountPercent(TEN_PERCENT_DISCOUNT);
				} else {
					order.setDiscountPercent(0);
				}
				//try to save the order
				try {
					orderDAO.createOrderFromCart(order);
					outMessages.add(new TableBuilder("order placed (to view past orders, enter <menu>, then enter <history> to view order history)"));
					outMessages.add(new TableBuilder("order number: " + order.getOrderID()));
					//GENERATE INVOICE AND DISPLAY SAVE NAME
					String invoiceLocation;
					try {
						invoiceLocation = printer.printCustomerInvoice(order.getOrderID(), currentUser.getUserID());
						outMessages.add(new TableBuilder("invoice generated as: " + invoiceLocation));
					} catch (FileNotFoundException e) {
						//do nothing
					}

					
				} catch (SQLException e) {
					sqlExceptionMessage(e);
				}
			} else {
				outMessages.add(new TableBuilder("order cancelled"));
			}
			endMenu();
			break;
		default:
			break;
		}
		
		
	}

	
	/**
	 * 
	 * Updates the quantities of an item in the cart.
	 * @param cmd the command entered by the user.
	 */
	private void updateCart(final String cmd) {	
	//all this needs to do is update quantities in the cart
		int selection = 0;
		
		switch (currentCartCommandState) {
		
		//gets the items in the cart, determine if there are any or not, and asks
		//which of them needs to be updated
		case ONE:
		getCarts();
		if (invCarts.isEmpty()) {
			endMenu();
			break;
		} else {
			outMessages.add(new TableBuilder("Which cart item would you like to update?"));
			outMessages.add(new TableBuilder("enter the number of the cart item you would like to change the quantity of, or enter <cancel> to cancel."));
			currentCartCommandState = TWO;
		}

		break;
		
		//checks if the user entered nothing, cancel, or a number
		//takes the number and checks if it is in a valid range
		//asks for the new quantity of that item
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave selection blank."));
				outMessages.add(new TableBuilder("enter the number of the cart item you would like to change the quantity of, or enter <cancel> to cancel."));
	
				//check if the user canceled the operation
			} else if (cmd.equals("cancel")) {
				outMessages.add(new TableBuilder("update canceled."));
				endMenu();
				break;
				//check if the command is an integer
			} else if (isInteger(cmd)){
				selection = Integer.parseInt(cmd) - 1;
				//in range
				if (selection < 0 || selection >= invCarts.size()) {
					outMessages.add(new TableBuilder("#" + cmd + " is not valid, enter a number between 1 and " + invCarts.size()));
					outMessages.add(new TableBuilder("enter the number of the cart you would like to delete, or enter <cancel> to cancel."));
				} else {
					//ask for new quant
					outMessages.add(new TableBuilder("enter the new quantity for that cart item"));
					currentCartCommandState = THREE;
				}
				
		
			//they did not enter an int
			} else {
				outMessages.add(new TableBuilder("please enter only an integer."));
				outMessages.add(new TableBuilder("enter the number of the cart you would like to edit, or enter <cancel> to cancel."));
			}
			break;
		
		//checks the quantity answer, makes sure they did not enter nothing
		//makes sure the input was an integer, if it is make sure that
		//that is in a valid range with the quantity in inventory
		case THREE:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave selection blank."));
				outMessages.add(new TableBuilder("enter the new quantity for that cart item"));
				
				//check if the command is an integer
			} else if (isInteger(cmd)){
				if (invCarts.get(selection).getQuantityOnHand() < Integer.parseInt(cmd) || Integer.parseInt(cmd) < 1) {
					outMessages.add(new TableBuilder(cmd + " is outside of inventory range, enter a number between 1 and " + invCarts.get(selection).getQuantityOnHand()));
					outMessages.add(new TableBuilder("enter the new quantity for that cart item"));
				} else {
					cart.setCartItemID(invCarts.get(selection).getCartID());
					cart.setQuantity(Integer.parseInt(cmd));
					
					try {
						cartDAO.updateQuantity(cart);
						outMessages.add(new TableBuilder("quantity updated"));
					} catch (SQLException e) {
						sqlExceptionMessage(e);
					}

					
					endMenu();
					break;
				}
				
		
			//they did not enter an int
			} else {
				outMessages.add(new TableBuilder("please enter only an integer."));
				outMessages.add(new TableBuilder("enter the new quantity for the cart item"));
			}
			break;
		default:
			break;
		}
	}

	/**
	 * display the cart items under the user's account.
	 */
	private void getCarts() {
		User user = currentUser;
		try {
			invCarts  = cartDAO.getCartByUserWithBook(user.getUserID());
			
			//check if the cart is null or empty, 
			if (invCarts == null || invCarts.isEmpty()){
				outMessages.add(new TableBuilder("no items in cart"));
			} else {
				
				// all the different parts of the cart to display
				List<String> titles = new ArrayList<String>();
				List<String> authors = new ArrayList<String>();
				List<String> quantities = new ArrayList<String>();
				List<String> costs = new ArrayList<String>();
				List<String> list = new ArrayList<String>();

				totalCost = 0;
				int counter = 1;
				//set the values for display and total up the cost of the order.
				for (InvCartBook c : invCarts) {
					titles.add(c.getTitle());
					authors.add(c.getAuthor());
					quantities.add(String.valueOf(c.getQuantity()));
					costs.add(String.valueOf(formatCurrency.format(c.getCost())));
					list.add("#" + counter++);
					totalCost = totalCost + (c.getCost() * c.getQuantity());
				}
				
				TableBuilder table = new TableBuilder();
				table.addColumn("cart item: ", list);
				table.addColumn("title: ", titles);
				table.addColumn("author: " , authors);
				table.addColumn("quantiy in cart: ", quantities);
				table.addColumn("price: ", costs);

				outMessages.add(table);
				
			}
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}

	}

	/**
	 * 
	 * asks the user which cart item they would like to remove.
	 * @param cmd the command being processed.
	 */
	private void deleteCartItem(final String cmd) {
//		//switch is used to determine how far into the menu the user is
		switch (currentCartCommandState) {
		
		//show the current items, and ask the user if they want to delete one of them
		case ONE:
		
		getCarts();
		if (invCarts.isEmpty()) {
			endMenu();
		} else {
			outMessages.add(new TableBuilder("Which cart item would you like to delete?"));
			outMessages.add(new TableBuilder("enter the number of the cart item you would like to remove, or enter <cancel> to cancel."));
			currentCartCommandState = TWO;
		}

		break;
		
		//process the user choice for deletion
		case TWO:
			if (cmd.equals("")) {
				outMessages.add(new TableBuilder("please do not leave selection blank."));
				outMessages.add(new TableBuilder("enter the number of the cart item you would like to remove, or enter <cancel> to cancel."));
			
				//check if the user canceled the operation
			} else if (cmd.equals("cancel")) {
				outMessages.add(new TableBuilder("delete canceled."));
				endMenu();
				
				//check if the command is an integer
			} else if (isInteger(cmd)){
				int selection = Integer.parseInt(cmd) - 1;
				if (selection < 0 || selection >= invCarts.size()) {
					outMessages.add(new TableBuilder("#" + cmd + " is not valid, enter a number between 1 and " + invCarts.size()));
					outMessages.add(new TableBuilder("enter the number of the cart item you would like to delete, or enter <cancel> to cancel."));
				} else {
					CartItem cartDelete = new CartItem();
					cartDelete.setCartItemID(invCarts.get(selection).getCartID());
					
					try {
						cartDAO.delete(cartDelete);
						outMessages.add(new TableBuilder("cart item deleted"));
					} catch (SQLException e) {
						sqlExceptionMessage(e);
					}

					endMenu();
				}
				
		
			//they did not enter an int
			} else {
				outMessages.add(new TableBuilder("please enter only an integer."));
				outMessages.add(new TableBuilder("enter the number of the cart item you would like to edit, or enter <cancel> to cancel."));
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
		
		row[0] ="<show>";
		row[1] ="to display current cart information";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<remove>";
		row[1] ="to remove a cart item";
		table.addRow(row);
		row = new String[2];

		row[0] ="<update>";
		row[1] ="to alter the quantity of an item in cart";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<order>";
		row[1] ="to place your order";
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
		currentCartCommandState = ONE;
		backToCartMenu = true;
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
}
