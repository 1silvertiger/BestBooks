package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.Order;
import model.OrderBookItem;
import model.OrderDAO;
import model.User;
import reports.Printer;
import util.AsciiBanner;
import util.TableBuilder;

/**
 * controls customer order information.
 * @author jdowd
 *
 */
public class CustOrderController {
	// ---------------------Out messages -------------------------
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();
	private AsciiBanner banner = new AsciiBanner();
	private User currentUser;
	private Printer printer;
	//money formatter
	private NumberFormat formatCurrency = NumberFormat.getCurrencyInstance();
	
	//date formatter
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	
	private List<Order> orders = null;
	private List<OrderBookItem> orderItems = null;

	private boolean backToOrderMenu = true;
	
	private String currentOrderCommand = "";
	
	private int currentOrderCommandState = 1;
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	
	// DAO
	private OrderDAO orderDAO;

	// Commands
	private static final String HELP = "help";
	private static final String SHOW = "show";
	private static final String RECENT = "recent";
	private static final String DETAIL = "detail";
	private static final String INVOICE = "invoice";
	private static final String CANCEL = "cancel";
	private static final String HISTORY = "history";

	// SQL Exception numbers
	private static final String KNOWN_SQL_ERROR_PREFIX = "45";

	// Messages
	private static final String SQL_EXCEPTION_MESSAGE = "A connection error occurred. Check your internet connection and try again.";
	
	/**
	 * the constructor sets up the DB connection for 
	 * the DAO and sets the current user instance to a field.
	 * @param c the connection to the database.
	 * @param myUser the current user session.
	 */
	public CustOrderController(final Connection c, final User myUser) {
		currentUser = myUser;
		
		// DAO
		try {
			printer = new Printer(c);
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
	 * @param myOrder 
	 * @return the generated output
	 */
	public List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		//run this only if the order has completed a context menu
		if (backToOrderMenu) {
			currentOrderCommand = cmd;
			
		}


		// We switch the command
		switch (currentOrderCommand) {
		case HELP:
			showHelp();
			break;
		case RECENT:
			getRecentOrders();
			break;
		case SHOW:
			getOrderHeaders();
			break;
		case DETAIL:
			backToOrderMenu = false;
			getDetail(cmd);
			break;
		case INVOICE:
			backToOrderMenu = false;
			getInvoice(cmd);
			break;
		case CANCEL:
			backToOrderMenu = false;
			updateOrder(cmd);
			break;
		case HISTORY:
				try {
					outMessages.addAll(banner.getBanner("HISTORY"));
				} catch (IOException e) {
					outMessages.add(new TableBuilder("Order information section!"));
				}
				showHelp();
			break;
		default:
			outMessages.add(new TableBuilder(
					"Invalid command:" + " '" + currentOrderCommand + "'" + ", enter <help> to see valid commands"));
		}
		return outMessages;
	}


	/**
	 * cancel an order.
	 * @param cmd the current command
	 */
	public void updateOrder(final String cmd) {

		
		//make sure there are orders to look through
		if (!orders.isEmpty()) {
			switch (currentOrderCommandState) {
			case ONE:
				//display all the order headers to make sure their are orders in the array of orders
				getOrderHeaders();
				outMessages.add(new TableBuilder("please enter the order number which you would like to cancel"));
				outMessages.add(new TableBuilder("or enter nothing to make no action"));
				outMessages.add(new TableBuilder("(note that you may only cancel orders which are processing)"));
				currentOrderCommandState = TWO;
				break;
			case TWO:
				if (isInteger(cmd)) {
					currentOrderCommandState = THREE;
				} else if (cmd.equals("")) {
					endMenu();
					break;
				} else {
					outMessages.add(new TableBuilder("please enter only an integer or nothing"));
					break;
				}
			case THREE:
				boolean foundOrder = false;
				boolean statusIsNotProcessing = false;
				for (Order o : orders) {
					if (o.getOrderID() == Integer.parseInt(cmd)) {
						foundOrder = true;
						if (o.getOrderStatus().equals("Processing")) {
							statusIsNotProcessing = true;
						}
					} 
				} 
				if (foundOrder && statusIsNotProcessing) {
					try {
						orderDAO.cancelOrder(Integer.parseInt(cmd));
						outMessages.add(new TableBuilder("order canceled"));
					} catch (SQLException e) {
						sqlExceptionMessage(e);
					}
				} else if (foundOrder) {
					outMessages.add(new TableBuilder("Operation stopped, the order has already shipped, please contact customer support for additional assistance"));
				} else {
					outMessages.add(new TableBuilder("Operation stopped, the selected order number could not be found"));
				}
				endMenu();
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * displays detail about a specified order.
	 * @param cmd the current command
	 */
	public void getDetail(final String cmd) {
		switch (currentOrderCommandState) {
		case ONE:
			outMessages.add(new TableBuilder("please enter the order number which you would like to see detail for"));
			currentOrderCommandState = TWO;
			break;
		//make sure input is good
		case TWO:
			if (isInteger(cmd)) {
				currentOrderCommandState = THREE;
			} else {
				outMessages.add(new TableBuilder("please enter only an integer"));
				outMessages.add(new TableBuilder("please enter the order number which you would like to see detail for"));
				break;
			}
		case THREE:
			int orderListNumber = 1;
			outMessages.clear();
			try {
				orderItems = orderDAO.getWithBooksByOrderAndUser(Integer.parseInt(cmd), currentUser);
				
				if (orderItems.isEmpty()){
					outMessages.add(new TableBuilder("order number " + cmd + " did not appear"));
					outMessages.add(new TableBuilder("enter <detail> if you would like to try again"));
				} else {
					
					List<String> ids = new ArrayList<String>();
					List<String> dates = new ArrayList<String>();
					List<String> status = new ArrayList<String>();
					List<String> spacing = new ArrayList<String>();
					List<String> titles = new ArrayList<String>();
					List<String> prices = new ArrayList<String>();
					List<String> quantities = new ArrayList<String>();
					List<String> addresses = new ArrayList<String>();
					
					List<String> row = new ArrayList<String>();
					
					List<String> totalPrices = new ArrayList<String>();
					double totalPrice = 0;
					String currentOrder = String.valueOf(orderItems.get(0).getOrderID());
					
					//look at all the book info
					for (OrderBookItem i : orderItems) {
						ids.add(String.valueOf(i.getOrderID()));
						dates.add(i.getCreationDate().format(formatter));
						status.add(i.getOrderStatus());
						spacing.add(" ");
						titles.add(i.getTitle());
						prices.add(formatCurrency.format(i.getSalePrice() * (1 - i.getDiscountPercent())));
						quantities.add(String.valueOf(i.getQuantity()));
						addresses.add(i.getAddressLine1());
						if (!currentOrder.equals(String.valueOf(i.getOrderID()))) {
							totalPrices.add(formatCurrency.format(totalPrice));
							totalPrice= 0;
							currentOrder = String.valueOf(i.getOrderID());
						}
						
						totalPrice = totalPrice + (i.getSalePrice() * i.getQuantity() * (1 - i.getDiscountPercent()));
					} 
					//add to the total prices one last time
					totalPrices.add(formatCurrency.format(totalPrice));
					
					//tablebuilder requires values in each row, this is to create blank rows
					String[] blankRow = {"", ""};
					TableBuilder table = new TableBuilder(blankRow);
					currentOrder = "";
					for (int i = 0; i < orderItems.size(); i++){
						
						//add a order header row only if the current order has changed
						if (!currentOrder.equals(ids.get(i))) {
							table = new TableBuilder(blankRow);
							
							//add the values to a tablebuilder
							row.add("Order number:");
							row.add(ids.get(i));
							table.addRow(row);
							row.clear();

							row.add("Order date:");
							row.add(dates.get(i));
							table.addRow(row);
							row.clear();
							
							row.add("Order Status:");
							row.add(status.get(i));
							table.addRow(row);
							row.clear();
							
							row.add("sent to:");
							row.add(addresses.get(i));
							table.addRow(row);
							row.clear();
							
							//needed for total cost
							++orderListNumber;
							
							row.add("items:");
							row.add("");
							table.addRow(row);
							row.clear();
							
							outMessages.add(table);
							table = new TableBuilder();
							
							currentOrder = ids.get(i);
							
						}
						//set up headers
						row.add("Title:");
						row.add("Quantity:");
						row.add("Price:");
						table.setColHeaders(row);
						row.clear();
						
						row.add(titles.get(i));
						row.add(quantities.get(i));
						row.add(prices.get(i));
					
						table.addRow(row);
						row.clear();
						
						//run at the end of an order (check if the next order is different)
						if ((i + 1) >= orderItems.size() || !currentOrder.equals(ids.get(i + 1))){
							//show the total
							row.add("total cost:");
							row.add("");
							row.add(totalPrices.get((orderListNumber-2)));
							table.addRow(row);
							row.clear();
							table.rightJustified();
							outMessages.add(table);
							
						}
						
					}
				}
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
			endMenu();
			break;
		default:
			break;
		}
	}
	
	/**
	 * displays detail about a specified order
	 * and creates an invoice.
	 * @param cmd the current command
	 */
	public void getInvoice(final String cmd) {
		switch (currentOrderCommandState) {
		case ONE:
			outMessages.add(new TableBuilder("please enter the order number which you would like to generate an invoice for"));
			currentOrderCommandState = TWO;
			break;
		//make sure input is good
		case TWO:
			if (isInteger(cmd)) {
				currentOrderCommandState = THREE;
			} else {
				outMessages.add(new TableBuilder("please enter only an integer"));
				outMessages.add(new TableBuilder("please enter the order number which you would like to generate an invoice for"));
				break;
			}
		case THREE:
			int orderListNumber = 1;
			outMessages.clear();
			try {
				orderItems = orderDAO.getWithBooksByOrderAndUser(Integer.parseInt(cmd), currentUser);
				
				if (orderItems.isEmpty()){
					outMessages.add(new TableBuilder("order number " + cmd + " did not appear"));
					outMessages.add(new TableBuilder("enter <invoice> if you would like to try again with a different order number"));
				} else {
					//TODO GENERATE INVOICE AND DISPLAY SAVED NAME
					
					
					List<String> ids = new ArrayList<String>();
					List<String> dates = new ArrayList<String>();
					List<String> status = new ArrayList<String>();
					List<String> spacing = new ArrayList<String>();
					List<String> titles = new ArrayList<String>();
					List<String> prices = new ArrayList<String>();
					List<String> quantities = new ArrayList<String>();
					List<String> addresses = new ArrayList<String>();
					
					List<String> row = new ArrayList<String>();
					
					List<String> totalPrices = new ArrayList<String>();
					double totalPrice = 0;
					String currentOrder = String.valueOf(orderItems.get(0).getOrderID());
					
					//look at all the book info
					for (OrderBookItem i : orderItems) {
						ids.add(String.valueOf(i.getOrderID()));
						dates.add(i.getCreationDate().format(formatter));
						status.add(i.getOrderStatus());
						spacing.add(" ");
						titles.add(i.getTitle());
						prices.add(formatCurrency.format(i.getSalePrice() * (1 - i.getDiscountPercent())));
						quantities.add(String.valueOf(i.getQuantity()));
						addresses.add(i.getAddressLine1());
						if (!currentOrder.equals(String.valueOf(i.getOrderID()))) {
							totalPrices.add(formatCurrency.format(totalPrice));
							totalPrice= 0;
							currentOrder = String.valueOf(i.getOrderID());
						}
						
						totalPrice = totalPrice + (i.getSalePrice() * i.getQuantity() * (1 - i.getDiscountPercent()));
					} 
					//add to the total prices one last time
					totalPrices.add(formatCurrency.format(totalPrice));
					
					//tablebuilder requires values in each row, this is to create blank rows
					String[] blankRow = {"", ""};
					TableBuilder table = new TableBuilder(blankRow);
					currentOrder = "";
					for (int i = 0; i < orderItems.size(); i++){
						
						//add a order header row only if the current order has changed
						if (!currentOrder.equals(ids.get(i))) {
							table = new TableBuilder(blankRow);
							
							//add the values to a tablebuilder
							row.add("Order number:");
							row.add(ids.get(i));
							table.addRow(row);
							row.clear();

							row.add("Order date:");
							row.add(dates.get(i));
							table.addRow(row);
							row.clear();
							
							row.add("Order Status:");
							row.add(status.get(i));
							table.addRow(row);
							row.clear();
							
							row.add("sent to:");
							row.add(addresses.get(i));
							table.addRow(row);
							row.clear();
							
							//needed for total cost
							++orderListNumber;
							
							row.add("items:");
							row.add("");
							table.addRow(row);
							row.clear();
							
							outMessages.add(table);
							table = new TableBuilder();
							
							currentOrder = ids.get(i);
							
						}
						//set up headers
						row.add("Title:");
						row.add("Quantity:");
						row.add("Price:");
						table.setColHeaders(row);
						row.clear();
						
						row.add(titles.get(i));
						row.add(quantities.get(i));
						row.add(prices.get(i));
					
						table.addRow(row);
						row.clear();
						
						//run at the end of an order (check if the next order is different)
						if ((i + 1) >= orderItems.size() || !currentOrder.equals(ids.get(i + 1))){
							//show the total
							row.add("total cost:");
							row.add("");
							row.add(totalPrices.get((orderListNumber-2)));
							table.addRow(row);
							row.clear();
							table.rightJustified();
							outMessages.add(table);
							
						}
						
					}
					
					//display the invoice generation
					String invoiceLocation;
					try {
						invoiceLocation = printer.printCustomerInvoice(Integer.parseInt(cmd), currentUser.getUserID());
						outMessages.add(new TableBuilder("invoice generated as: " + invoiceLocation));
					} catch (FileNotFoundException e) {
						outMessages.add(new TableBuilder("the invoice could not be generated"));
					}
					
					
				}
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
			endMenu();
			break;
		default:
			break;
		}
	}
	
	/**
	 * shows a user's five most recent orders
	 */
	public void getRecentOrders() {

		int orderListNumber = 1;
		outMessages.clear();
		try {
			orderItems = orderDAO.getWithBooksByUser(currentUser.getUserID());
			
			if (orderItems.isEmpty()){
				outMessages.add(new TableBuilder("no orders to show"));
				outMessages.add(new TableBuilder("to add get some orders, first add books from <find>"));
				outMessages.add(new TableBuilder("to your cart, then place an order from <cart>"));
			} else {
				
				List<String> ids = new ArrayList<String>();
				List<String> dates = new ArrayList<String>();
				List<String> status = new ArrayList<String>();
				List<String> spacing = new ArrayList<String>();
				List<String> titles = new ArrayList<String>();
				List<String> prices = new ArrayList<String>();
				List<String> quantities = new ArrayList<String>();
				List<String> addresses = new ArrayList<String>();
				
				List<String> row = new ArrayList<String>();
				
				List<String> totalPrices = new ArrayList<String>();
				double totalPrice = 0;
				String currentOrder = String.valueOf(orderItems.get(0).getOrderID());
				
				for (OrderBookItem i : orderItems) {
					ids.add(String.valueOf(i.getOrderID()));
					dates.add(i.getCreationDate().format(formatter));
					status.add(i.getOrderStatus());
					spacing.add(" ");
					titles.add(i.getTitle());
					prices.add(formatCurrency.format(i.getSalePrice() * (1 - i.getDiscountPercent())));
					quantities.add(String.valueOf(i.getQuantity()));
					addresses.add(i.getAddressLine1());
					if (!currentOrder.equals(String.valueOf(i.getOrderID()))) {
						totalPrices.add(formatCurrency.format(totalPrice));
						totalPrice= 0;
						currentOrder = String.valueOf(i.getOrderID());
					}
					
					totalPrice = totalPrice + (i.getSalePrice() * i.getQuantity() * (1 - i.getDiscountPercent()));
				} 
				totalPrices.add(formatCurrency.format(totalPrice));
				
				//tablebuilder requires values in each row, this is to create blank rows
				String[] blankRow = {"", ""};
				TableBuilder table = new TableBuilder(blankRow);
				currentOrder = "";
				for (int i = 0; i < orderItems.size() && i < 5; i++){
					
					//add a order header row only if the current order has changed
					if (!currentOrder.equals(ids.get(i))) {
						table = new TableBuilder(blankRow);
						
						//add the values to a tablebuilder
						row.add("Order number:");
						row.add(ids.get(i));
						table.addRow(row);
						row.clear();

						row.add("Order date:");
						row.add(dates.get(i));
						table.addRow(row);
						row.clear();
						
						row.add("Order Status:");
						row.add(status.get(i));
						table.addRow(row);
						row.clear();
						
						row.add("sent to:");
						row.add(addresses.get(i));
						table.addRow(row);
						row.clear();
						
						//needed for total cost
						++orderListNumber;
						
						row.add("items:");
						row.add("");
						table.addRow(row);
						row.clear();
						
						outMessages.add(table);
						table = new TableBuilder();
						
						currentOrder = ids.get(i);
						
					}
					//set up headers
					row.add("Title:");
					row.add("Quantity:");
					row.add("Price:");
					table.setColHeaders(row);
					row.clear();
					
					row.add(titles.get(i));
					row.add(quantities.get(i));
					row.add(prices.get(i));
				
					table.addRow(row);
					row.clear();
					
					//run at the end of an order (check if the next order is different)
					if ((i + 1) >= orderItems.size() || !currentOrder.equals(ids.get(i + 1))){
						row.add("total cost:");
						row.add("");
						row.add(totalPrices.get((orderListNumber-2)));
						table.addRow(row);
						row.clear();
						table.rightJustified();
						outMessages.add(table);
						
					}
					
				}
					
			}
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}
	}

	/**
	 * shows all of a customer's order headers.
	 */
	public void getOrderHeaders() {
		
		try {
			orders = orderDAO.getByUserWithTotals(currentUser);
			
			if (!orders.isEmpty()) {
				List<String> ids = new ArrayList<String>();
				List<String> dates = new ArrayList<String>();
				List<String> status = new ArrayList<String>();
				List<String> totals = new ArrayList<String>();
				
				for (Order o : orders) {
					ids.add(String.valueOf(o.getOrderID()));
					dates.add(o.getCreationDate().format(formatter));
					status.add(o.getOrderStatus());
					totals.add(formatCurrency.format(o.getTotalCost() * (1 - o.getDiscountPercent())));
				}
				
				TableBuilder table = new TableBuilder();
				
				table.addColumn("Order Number:", ids);
				table.addColumn("Order Date", dates);
				table.addColumn("Order Status:", status);
				table.addColumn("Total Price:", totals);
				
				outMessages.add(table);
			} else {
				outMessages.add(new TableBuilder("No orders on record"));
			}
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}
	}
	
	/**
	 * show help information.
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
		row[1] ="to display all orders (does not show detail)";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<recent>";
		row[1] ="to display order information for the last 5 orders";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<detail>";
		row[1] ="to display detailed information for a specific order number";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<invoice>";
		row[1] ="to generate an invoice for a specific order number";
		table.addRow(row);
		row = new String[2];
		
		row[0] ="<cancel>";
		row[1] ="to cancel an order";
		table.addRow(row);
		row = new String[2];
		
		outMessages.add(table);
	}
	
	
	/**
	 * set the menu progress
	 * back to its initial
	 * state.
	 */
	private void endMenu() {
		// set the progress back to one
		currentOrderCommandState = ONE;
		backToOrderMenu = true;
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
	
}
