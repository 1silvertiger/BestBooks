/**
 * 
 */
package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import model.Order;
import model.OrderDAO;
import model.OrderItemBook;
import util.TableBuilder;

/**
 * this controller lets admins
 * create, read, update, and delete
 * order data.
 * @author Dakota
 *
 */
public class AdminOrderController extends AdminController<Order> {

	private OrderDAO dao;

	// money formatter
	private NumberFormat priceFormatter = NumberFormat.getCurrencyInstance();

	private static final String PROCESSING = "processing";
	private static final String SHIPPING = "shipping";
	private static final String DELIVERED = "delivered";
	private static final String CANCELLED = "cancelled";

	private static final String ADDRESS_ID = "a";
	private static final String ADDRESS_ID_LONG = "address";

	private static final String CARD_ID = "c";
	private static final String CARD_ID_LONG = "card";

	private static final String BOOK_ID = "b";
	private static final String BOOK_ID_LONG = "book";

	private static final String USER_ID = "u";
	private static final String USER_ID_LONG = "user";

	private static final String ITEM_ID = "i";
	private static final String ITEM_ID_LONG = "item";

	private static final String ORDER_ID = "o";
	private static final String ORDER_ID_LONG = "order";

	private static final String QUANTITY = "q";
	private static final String QUANTITY_LONG = "quantity";

	private static final String DISCOUNT = "d";
	private static final String DISCOUNT_LONG = "discount";

	private static final String STATUS = "s";
	private static final String STATUS_LONG = "status";

	private static final int DEFAULT_DISCOUNT = 0;
	private static final int DISCOUNT_COEF = 100;

	private boolean backToMenu = true;

	/**
	 * this controller sets up the connection, options
	 * and commands.
	 * @param pConnection the connection to the database
	 */
	public AdminOrderController(final Connection pConnection) {
		super(pConnection);
		try {
			dao = new OrderDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(e.getMessage()));
		}

		Option requiredBookID = new Option(BOOK_ID, BOOK_ID_LONG, true, "the book ID(s)");
		requiredBookID.setRequired(true);
		requiredBookID.setArgs(UNLIMITED_ARGS);

		Option requiredUserID = new Option(USER_ID, USER_ID_LONG, true, "the user ID");
		requiredUserID.setRequired(true);

		Option requiredOrderItemID = new Option(ITEM_ID, ITEM_ID_LONG, true, "the order  item ID");
		requiredOrderItemID.setRequired(true);

		Option requiredOrderID = new Option(ORDER_ID, ORDER_ID_LONG, true, "the order ID");
		requiredOrderID.setRequired(true);

		Option requiredQuantity = new Option(QUANTITY, QUANTITY_LONG, true, "the quantity");
		requiredQuantity.setRequired(true);

		Option optionalUserID = new Option(USER_ID, USER_ID_LONG, true, "the user ID");
		optionalUserID.setRequired(false);

		Option optionalOrderItemID = new Option(ITEM_ID, ITEM_ID_LONG, true, "the order  item ID");
		optionalOrderItemID.setRequired(false);

		Option optionalOrderID = new Option(ORDER_ID, ORDER_ID_LONG, true, "the order ID");
		optionalOrderID.setRequired(false);

		Option optionalAddress = new Option(ADDRESS_ID, false, "show the address info");
		Option optionalCard = new Option(CARD_ID, false, "show the credit card info");

		Option optionalAddressID = new Option(ADDRESS_ID, ADDRESS_ID_LONG, true, "the shipping address ID");
		optionalAddressID.setRequired(false);

		Option optionalCardID = new Option(CARD_ID, CARD_ID_LONG, true, "the card ID");
		optionalCardID.setRequired(false);

		Option optionalDiscount = new Option(DISCOUNT, DISCOUNT_LONG, true, "the discount");
		optionalDiscount.setRequired(false);

		Option optionalBookID = new Option(BOOK_ID, BOOK_ID_LONG, true, "the book ID");
		optionalBookID.setRequired(false);

		Option optionalQuantity = new Option(QUANTITY, QUANTITY_LONG, true, "the quantity to order");

		Option optionalStatus = new Option(STATUS, STATUS_LONG, false, "the order status");
		optionalStatus.setOptionalArg(true);

		OptionGroup requiredGetChoices = new OptionGroup();
		requiredGetChoices.setRequired(true);
		requiredGetChoices.addOption(optionalOrderID);
		requiredGetChoices.addOption(optionalOrderItemID);
		requiredGetChoices.addOption(optionalUserID);

		addOptions.addOption(requiredBookID);
		addOptions.addOption(requiredQuantity);
		addOptions.addOption(requiredOrderID);

		getOptions.addOption(requiredOrderID);
		getOptions.addOption(optionalAddress);
		getOptions.addOption(optionalCard);

		getAllOptions.addOption(optionalUserID);
		getAllOptions.addOption(optionalAddress);
		getAllOptions.addOption(optionalCard);

		updateOptions.addOption(requiredOrderID);
		updateOptions.addOption(optionalOrderItemID);
		updateOptions.addOption(optionalDiscount);
		updateOptions.addOption(optionalQuantity);
		updateOptions.addOption(optionalBookID);
		updateOptions.addOption(optionalAddressID);
		updateOptions.addOption(optionalCardID);
		updateOptions.addOption(optionalStatus);
		updateOptions.addOption(optionalAddress);
		updateOptions.addOption(optionalCard);

		deleteOptions.addOption(requiredOrderID);
		deleteOptions.addOption(optionalOrderItemID);
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#parseCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		switch (cmd) {
		case HELP:
			try {
				cmdl = parser.parse(helpOptions, args);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
			if (cmdl != null) {
				if (cmdl.getOptions().length == 0) {
					helpFormatter.printHelp(ADD, addOptions);
					helpFormatter.printHelp(GET, getOptions);
					helpFormatter.printHelp(GET_ALL, getAllOptions);
					helpFormatter.printHelp(UPDATE, updateOptions);
					helpFormatter.printHelp(DELETE, deleteOptions);
				} else {
					if (cmdl.hasOption(ADD)) {
						helpFormatter.printHelp(ADD, addOptions);
					}
					if (cmdl.hasOption(GET)) {
						helpFormatter.printHelp(GET, getAllOptions);
					}
					if (cmdl.hasOption(GET_ALL)) {
						helpFormatter.printHelp(GET_ALL, getAllOptions);
					}
					if (cmdl.hasOption(UPDATE)) {
						helpFormatter.printHelp(UPDATE, updateOptions);
					}
					if (cmdl.hasOption(DELETE)) {
						helpFormatter.printHelp(DELETE, deleteOptions);
					}
				}
			}
			break;
		case ADD:
			try {
				cmdl = parser.parse(addOptions, args);
				add(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
			break;
		case GET:
			try {
				cmdl = parser.parse(getOptions, args);
				get(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
			break;
		case GET_ALL:
			try {
				cmdl = parser.parse(getAllOptions, args);
				getAll(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
			break;
		case UPDATE:
			try {
				cmdl = parser.parse(updateOptions, args);
				update(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
			break;
		case DEL:
		case DELETE:
			try {
				cmdl = parser.parse(deleteOptions, args);
				delete(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
			break;
		case SEARCH:
			try {
				cmdl = parser.parse(searchOptions, args);
				search(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
			break;
		default:
			if (backToMenu) {
				outMessages.add(new TableBuilder("Welcome to the order controller!"));
				backToMenu = false;
			} else {
				outMessages.add(new TableBuilder(
						"Unrecognized command " + cmd + ". Enter 'help' to see a list of valid commands"));
			}
		}
		return outMessages;
	}


	/* (non-Javadoc)
	 * @see controller.AdminController#add(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void add(final CommandLine cmdl) {
		Order o = new Order();
		OrderItemBook i = new OrderItemBook();
		if (cmdl.hasOption(ORDER_ID)) {
			try {
				o = dao.get(Integer.parseInt(cmdl.getOptionValue(ORDER_ID)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				outMessages.add(new TableBuilder("did not get"));
				e.printStackTrace();
			}

			if (o.exists()) {
				if (o.getOrderStatus().toLowerCase().equals(PROCESSING)) {
					i.setOrderID(o.getOrderID());
					i.setBookID(Integer.parseInt(cmdl.getOptionValue(BOOK_ID)));
					i.setQuantity(Integer.parseInt(cmdl.getOptionValue(QUANTITY)));
					boolean added = true;
					try {
						dao.createOrderItem(i);
					} catch (SQLException e) {
						added = false;
						if (e.getSQLState()
								.startsWith(KNOWN_SQL_EXCEPTION_PREFIXES[FOREIGN_KEY_CONSTRAINT_VIOLATION])) {
							outMessages.add(new TableBuilder(
									"Book with ID " + cmdl.getOptionValue(BOOK_ID) + " is not in stock"));
						} else {
							outMessages.add(new TableBuilder("An error occurred. The system exited with state "
									+ e.getSQLState() + " and code " + e.getErrorCode()));
						}
					}
					if (added) {
						outMessages.add(new TableBuilder(
								"Order item with ID " + i.getOrderItemID() + " added to order #" + i.getOrderID()));
					}
				} else {
					outMessages.add(new TableBuilder("order has already shipped"));
				}
			} else {
				outMessages.add(new TableBuilder("Order #" + cmdl.getOptionValue(ORDER_ID) + " not found"));
			}
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#get(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void get(final CommandLine cmdl) throws ParseException {
		Order o = null;

		try {
			o = dao.get(Integer.valueOf(cmdl.getOptionValue(ORDER_ID)));
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("did not get"));
			e.printStackTrace();
		}

		if (o != null && o.exists()) {
			outMessages.add(new TableBuilder("Order ID: " + o.getOrderID()));
			outMessages.add(new TableBuilder("User ID: " + o.getUserID()));
			outMessages.add(new TableBuilder("Address ID: " + o.getAddress().getAddressID()));
			outMessages.add(new TableBuilder("Card ID: " + o.getCard().getCardID()));
			outMessages.add(new TableBuilder("Order status: " + o.getOrderStatus()));
			outMessages.add(new TableBuilder("Created: " + o.getCreationDate()));
			outMessages.add(new TableBuilder("Total: " + priceFormatter.format(o.getTotal())));
			if (o.getDiscountPercent() > DEFAULT_DISCOUNT) {
				outMessages.add(new TableBuilder("Discount: " + (int) (o.getDiscountPercent() * DISCOUNT_COEF) + "%"));
			}
			outMessages.add(new TableBuilder(" "));
			if (cmdl.hasOption(ADDRESS_ID)) {
				outMessages.add(new TableBuilder("Shipping to: "));
				outMessages.add(new TableBuilder(o.getAddress().getLine1()));
				if (StringUtils.isNotBlank(o.getAddress().getLine2())) {
					outMessages.add(new TableBuilder(o.getAddress().getLine2()));
				}
				outMessages.add(new TableBuilder(
						o.getAddress().getCity() + ", " + o.getAddress().getState() + " " + o.getAddress().getZip()));
				outMessages.add(new TableBuilder(" "));
			}
			if (cmdl.hasOption(CARD_ID)) {
				outMessages.add(new TableBuilder("Billed to:"));
				outMessages.add(new TableBuilder("Card number: " + o.getCard().getCardNumber()));
				outMessages.add(new TableBuilder("Card holder name: " + o.getCard().getCardHolderName()));
				outMessages.add(new TableBuilder("Expiration date: " + o.getCard().getExpirationDate()));
				outMessages.add(new TableBuilder("Billing address: "));
				outMessages.add(new TableBuilder(o.getCard().getAddress().getLine1()));
				if (StringUtils.isNotBlank(o.getCard().getAddress().getLine2())) {
					outMessages.add(new TableBuilder(o.getCard().getAddress().getLine2()));
				}
				outMessages.add(new TableBuilder(o.getCard().getAddress().getCity() + ", "
						+ o.getCard().getAddress().getState() + " " + o.getCard().getAddress().getZip()));
				outMessages.add(new TableBuilder(" "));
			}
			outMessages.add(new TableBuilder("Books: "));

			List<String> quantities = new ArrayList<>();
			List<String> titles = new ArrayList<>();
			List<String> authors = new ArrayList<>();
			List<String> bookIDs = new ArrayList<>();
			List<String> orderItemIDs = new ArrayList<>();
			for (OrderItemBook b : o.getItems()) {
				quantities.add(String.valueOf(b.getQuantity()));
				titles.add(b.getTitle());
				authors.add(b.getAuthor());
				bookIDs.add(String.valueOf(b.getBookID()));
				orderItemIDs.add(String.valueOf(b.getOrderItemID()));
			}
			TableBuilder tb = new TableBuilder();
			tb.addColumn("Quantity", quantities);
			tb.addColumn("Order Item ID", orderItemIDs);
			tb.addColumn("Book ID", bookIDs);
			tb.addColumn("Title", titles);
			tb.addColumn("Author", authors);
			outMessages.add(tb);
		} else {
			outMessages
					.add(new TableBuilder("Order with ID " + cmdl.getOptionValue(ORDER_ID) + " not found in database"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#getAll(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void getAll(final CommandLine cmdl) throws ParseException {
		List<Order> orders = new ArrayList<>();
		try {
			if (cmdl.hasOption(USER_ID)) {
				orders = dao.getByUser(Integer.parseInt(cmdl.getOptionValue(USER_ID)));
			} else {
				orders = dao.getAll();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("did not get"));
			e.printStackTrace();
		}
		if (orders.isEmpty()) {
			System.out.println("empty");
		} else {
			for (Order o : orders) {
				outMessages.add(new TableBuilder("Order ID: " + o.getOrderID()));
				outMessages.add(new TableBuilder("User ID: " + o.getUserID()));
				outMessages.add(new TableBuilder("Address ID: " + o.getAddress().getAddressID()));
				outMessages.add(new TableBuilder("Card ID: " + o.getCard().getCardID()));
				outMessages.add(new TableBuilder("Order status: " + o.getOrderStatus()));
				outMessages.add(new TableBuilder("Created: " + o.getCreationDate()));
				outMessages.add(new TableBuilder("Shipping address ID: " + o.getAddress().getAddressID()));
				outMessages.add(new TableBuilder("Card ID: " + o.getCard().getCardID()));
				outMessages.add(new TableBuilder("Total: " + priceFormatter.format(o.getTotal())));
				if (o.getDiscountPercent() > DEFAULT_DISCOUNT) {
					outMessages
							.add(new TableBuilder("Discount: " + (int) (o.getDiscountPercent() * DISCOUNT_COEF) + "%"));
				}
				outMessages.add(new TableBuilder(" "));
				if (cmdl.hasOption(ADDRESS_ID)) {
					outMessages.add(new TableBuilder("Shipping to: "));
					outMessages.add(new TableBuilder(o.getAddress().getLine1()));
					if (StringUtils.isNotBlank(o.getAddress().getLine2())) {
						outMessages.add(new TableBuilder(o.getAddress().getLine2()));
					}
					outMessages.add(new TableBuilder(o.getAddress().getCity() + ", " + o.getAddress().getState() + " "
							+ o.getAddress().getZip()));
					outMessages.add(new TableBuilder(" "));
				}
				if (cmdl.hasOption(CARD_ID)) {
					outMessages.add(new TableBuilder("Billed to:"));
					outMessages.add(new TableBuilder("Card number: " + o.getCard().getCardNumber()));
					outMessages.add(new TableBuilder("Card holder name: " + o.getCard().getCardHolderName()));
					outMessages.add(new TableBuilder("Expiration date: " + o.getCard().getExpirationDate()));
					outMessages.add(new TableBuilder("Billing address: "));
					outMessages.add(new TableBuilder(o.getCard().getAddress().getLine1()));
					if (StringUtils.isNotBlank(o.getCard().getAddress().getLine2())) {
						outMessages.add(new TableBuilder(o.getCard().getAddress().getLine2()));
					}
					outMessages.add(new TableBuilder(o.getCard().getAddress().getCity() + ", "
							+ o.getCard().getAddress().getState() + " " + o.getCard().getAddress().getZip()));
					outMessages.add(new TableBuilder(" "));
				}
				outMessages.add(new TableBuilder("Books: "));

				List<String> quantities = new ArrayList<>();
				List<String> titles = new ArrayList<>();
				List<String> authors = new ArrayList<>();
				List<String> bookIDs = new ArrayList<>();
				List<String> orderItemIDs = new ArrayList<>();
				List<String> prices = new ArrayList<>();
				for (OrderItemBook b : o.getItems()) {
					quantities.add(String.valueOf(b.getQuantity()));
					titles.add(b.getTitle());
					authors.add(b.getAuthor());
					bookIDs.add(String.valueOf(b.getBookID()));
					orderItemIDs.add(String.valueOf(b.getOrderItemID()));
					prices.add(priceFormatter.format(b.getRetailPrice()));
				}
				TableBuilder tb = new TableBuilder();
				tb.addColumn("Quantity", quantities);
				tb.addColumn("Order Item ID", orderItemIDs);
				tb.addColumn("Book ID", bookIDs);
				tb.addColumn("Title", titles);
				tb.addColumn("Author", authors);
				tb.addColumn("Price", prices);
				outMessages.add(tb);

				tb.getFormatString();
				StringBuilder sb = new StringBuilder();
				for (int i : tb.getColWidths()) {
					for (int j = 0; j <= i; j++) {
						sb.append("-");
					}
				}
				outMessages.add(new TableBuilder(sb.toString()));
			}
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#update(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void update(final CommandLine cmdl) throws ParseException {
		Order o = null;
		try {
			o = dao.get(Integer.parseInt(cmdl.getOptionValue(ORDER_ID)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("did not get"));
			e.printStackTrace();
		}
		if (o != null && o.exists()) {
			if (cmdl.hasOption(DISCOUNT)) {
				if (Integer.parseInt(cmdl.getOptionValue(DISCOUNT)) != o.getDiscountPercent()) {
					o.setDiscountPercent(Integer.parseInt(cmdl.getOptionValue(DISCOUNT)));
				} else {
					outMessages.add(new TableBuilder(
							"Discount percent of " + cmdl.getOptionValue(DISCOUNT) + " matched discount in database"));
				}
			}

			if (cmdl.hasOption(ADDRESS_ID)) {
				if (Integer.parseInt(cmdl.getOptionValue(ADDRESS_ID)) != o.getAddressID()) {
					o.setAddressID(Integer.parseInt(cmdl.getOptionValue(ADDRESS_ID)));
				} else {
					outMessages.add(new TableBuilder(
							"Address ID " + cmdl.getOptionValue(ADDRESS_ID) + " matched address ID in database"));
				}
			}

			if (cmdl.hasOption(CARD_ID)) {
				if (Integer.parseInt(cmdl.getOptionValue(CARD_ID)) != o.getCardID()) {
					o.setCardID(Integer.parseInt(cmdl.getOptionValue(CARD_ID)));
				} else {
					outMessages.add(new TableBuilder(
							"Card ID " + cmdl.getOptionValue(CARD_ID) + " matched card ID in database"));
				}
			}

			if (cmdl.hasOption(STATUS)) {
				if (cmdl.getOptionValue(STATUS) == null) {
					if (o.getOrderStatus().toLowerCase().equals(PROCESSING)) {
						o.setOrderStatus(SHIPPING);
					} else if (o.getOrderStatus().equals(SHIPPING)) {
						o.setOrderStatus(DELIVERED);
					} else {
						outMessages.add(new TableBuilder("Please specifiy the order status"));
					}
				} else {
					if (cmdl.getOptionValue(STATUS).toLowerCase().equals(PROCESSING)) {
						o.setOrderStatus(PROCESSING);
					} else if (cmdl.getOptionValue(STATUS).toLowerCase().equals(SHIPPING)) {
						o.setOrderStatus(SHIPPING);
					} else if (cmdl.getOptionValue(STATUS).toLowerCase().equals(DELIVERED)) {
						o.setOrderStatus(DELIVERED);
					} else if (cmdl.getOptionValue(STATUS).toLowerCase().equals(CANCELLED)) {
						o.setOrderStatus(CANCELLED);
					} else {
						outMessages.add(new TableBuilder("Invalid order status. Valid statuses include " + PROCESSING
								+ ", " + SHIPPING + ", " + DELIVERED + ", and " + CANCELLED));
					}
				}
			}

			if (cmdl.hasOption(DISCOUNT) || cmdl.hasOption(ADDRESS_ID) || cmdl.hasOption(CARD_ID)
					|| cmdl.hasOption(STATUS)) {
				try {
					dao.update(o);
				} catch (SQLException e) {
					outMessages.add(new TableBuilder("did not update"));
					e.printStackTrace();
				}
			}

			if (cmdl.hasOption(ITEM_ID)) {
				if (o.hasItemNumber(Integer.parseInt(cmdl.getOptionValue(ITEM_ID)))) {
					if (cmdl.hasOption(QUANTITY)) {
						if (Integer.parseInt(cmdl.getOptionValue(QUANTITY)) != o
								.getItemNumber(Integer.parseInt(cmdl.getOptionValue(ITEM_ID))).getQuantity()) {
							o.getItemNumber(Integer.parseInt(cmdl.getOptionValue(ITEM_ID)))
									.setQuantity(Integer.parseInt(cmdl.getOptionValue(QUANTITY)));
						} else {
							outMessages.add(
									new TableBuilder("The provided quantity matched the quantity in the database"));
						}
					}
					if (cmdl.hasOption(BOOK_ID)) {
						if (Integer.parseInt(cmdl.getOptionValue(BOOK_ID)) != o
								.getItemNumber(Integer.parseInt(cmdl.getOptionValue(ITEM_ID))).getBookID()) {
							o.getItemNumber(Integer.parseInt(cmdl.getOptionValue(ITEM_ID)))
									.setQuantity(Integer.parseInt(cmdl.getOptionValue(BOOK_ID)));
						}
					}

					boolean updated = true;
					try {
						dao.updateOrderItem(o.getItemNumber(Integer.parseInt(cmdl.getOptionValue(ITEM_ID))));
					} catch (NumberFormatException e) {
						updated = false;
						e.printStackTrace();
					} catch (SQLException e) {
						updated = false;
						outMessages.add(new TableBuilder("did not update"));
						e.printStackTrace();
					}
					// this needs to be finished once update is completely done
					if (updated) {
						outMessages.add(new TableBuilder("Updated order #" + o.getOrderID()));
						if (cmdl.hasOption(ADDRESS_ID)) {
							
						}
					}
				} else {
					outMessages.add(new TableBuilder(
							"Order #" + o.getOrderID() + " does not contain item #" + cmdl.getOptionValue(ITEM_ID)));
				}
			} else if (cmdl.hasOption(QUANTITY) || cmdl.hasOption(BOOK_ID)) {
				outMessages.add(new TableBuilder("Provide an order item ID number to update book ID or quantity"));
			}
		} else {
			outMessages.add(new TableBuilder("Order #" + cmdl.getOptionValue(ORDER_ID) + " not found in database"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#delete(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void delete(final CommandLine cmdl) throws ParseException {
		Order o = null;
		try {
			o = dao.get(Integer.parseInt(cmdl.getOptionValue(ORDER_ID)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("did not get"));
			e.printStackTrace();
		}

		if (o != null) {
			if (cmdl.hasOption(ITEM_ID)) {
				OrderItemBook book = null;
				for (OrderItemBook b : o.getItems()) {
					if (b.getOrderItemID() == Integer.parseInt(cmdl.getOptionValue(ITEM_ID))) {
						book = b;
					}
				}
				if (book != null) {
					try {
						dao.deleteItemFromOrder(book.getOrderItemID());
					} catch (SQLException e) {
						outMessages.add(new TableBuilder("did not delete"));
						e.printStackTrace();
					}
				} else {
					outMessages.add(new TableBuilder("Order #" + o.getOrderID() + " does not contain order item #"
							+ cmdl.getOptionValue(ITEM_ID)));
				}
			} else {
				try {
					dao.delete(o);
				} catch (SQLException e) {
					outMessages.add(new TableBuilder("did not delete"));
					e.printStackTrace();
				}
			}
		} else {
			outMessages
					.add(new TableBuilder("Order with ID " + cmdl.getOptionValue(ORDER_ID) + " not found in database"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#search(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void search(final CommandLine cmdl) throws ParseException {
		// search based on order status, book ID
	}

}
