/**
 * 
 */
package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import model.CartItem;
import model.CartItemDAO;
import util.TableBuilder;

/**
 * this controller lets admins
 * create and read
 * cart data.
 * @author Dakota
 *
 */
public class AdminCartController extends AdminController<CartItem> {

	private CartItemDAO dao;

	private static final String ID = "id";

	private static final String USER_ID = "u";
	private static final String USER_ID_LONG = "userID";

	private static final String BOOK_ID = "b";
	private static final String BOOK_ID_LONG = "bookID";

	private static final String QUANTITY = "q";
	private static final String QUANTITY_LONG = "quantity";
	
	private static final String VERBOSE = "v";
	private static final String VERBOSE_LONG = "verbose";

	/**
	 * this controller sets up the connection, options
	 * and commands.
	 * @param pConnection the connection to the database
	 */
	public AdminCartController(final Connection pConnection) {
		super(pConnection);
		try {
			dao = new CartItemDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(e.getMessage()));
		}

		Option requiredBookID = new Option(BOOK_ID, BOOK_ID_LONG, true, "the book ID of the book to add to the cart");
		requiredBookID.setRequired(true);

		Option requiredUserID = new Option(USER_ID, USER_ID_LONG, true, "the user ID of the owner of the cart");
		requiredUserID.setRequired(true);

		Option requiredQuantity = new Option(QUANTITY, QUANTITY_LONG, true, "the quantity to add to the cart");
		requiredQuantity.setRequired(true);

		Option verbose = new Option(VERBOSE, VERBOSE_LONG, false, "more info");
		
		addOptions.addOption(requiredQuantity);
		addOptions.addOption(requiredUserID);
		addOptions.addOption(requiredBookID);

		getOptions.addOption(requiredUserID);
		getOptions.addOption(verbose);
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#parseCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		try {
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
					}
				}
				break;
			case ADD:
				if (checkForDuplicates(args).equals(CHECK_ARGS_GOOD)) {
					try {
						cmdl = parser.parse(addOptions, args);
					} catch (ParseException e) {
						outMessages.add(new TableBuilder(e.getMessage()));
					}
					if (cmdl != null) {
						add(cmdl);
					}
				} else {
					outMessages.add(new TableBuilder(checkForDuplicates(args)));
				}
				break;
			case GET:
				if (checkForDuplicates(args).equals(CHECK_ARGS_GOOD)) {
					try {
						cmdl = parser.parse(getOptions, args);
					} catch (ParseException e) {
						outMessages.add(new TableBuilder(e.getMessage()));
					}
					if (cmdl != null) {
						get(cmdl);
					}
				} else {
					outMessages.add(new TableBuilder(checkForDuplicates(args)));
				}
				break;
			case GET_ALL:
				if (checkForDuplicates(args).equals(CHECK_ARGS_GOOD)) {
					try {
						cmdl = parser.parse(getAllOptions, args);
					} catch (ParseException e) {
						outMessages.add(new TableBuilder(e.getMessage()));
					}
					if (cmdl != null) {
						getAll(cmdl);
					}
				} else {
					outMessages.add(new TableBuilder(checkForDuplicates(args)));
				}
				break;
			case CART:
				outMessages.add(new TableBuilder("Welcome to the cart section!"));
				outMessages.add(new TableBuilder("Enter <help> to see valid commands"));
				break;
			default:

				outMessages.add(new TableBuilder(
						"Unrecognized command " + cmd + ". Enter 'help' to see a list of valid commands"));
			
			}
		} catch (ParseException e) {
			outMessages.add(new TableBuilder(e.getMessage()));
		}
		return outMessages;
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#add(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void add(final CommandLine cmdl) {
		CartItem c = new CartItem();
		c.setBookID(Integer.parseInt(cmdl.getOptionValue(BOOK_ID)));
		c.setQuantity(Integer.parseInt(cmdl.getOptionValue(QUANTITY)));
		c.setUserID(Integer.parseInt(cmdl.getOptionValue(USER_ID)));

		boolean created = false;
		try {
			dao.create(c);
			created = true;
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("the add operation failed"));
			e.printStackTrace();
		}

		if (created) {
			outMessages.add(new TableBuilder("Added " + c.getQuantity() + " books  with ID " + c.getBookID()
					+ " to cart owned by user ID " + c.getUserID()));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#get(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void get(final CommandLine cmdl) throws ParseException {
		List<CartItem> cart = null;
		try {
			cart = dao.getCartByUser(Integer.parseInt(cmdl.getOptionValue(USER_ID)));
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("the get operation failed"));
		}

		if (cart != null) {
			outMessages.add(new TableBuilder("Cart contents of user ID " + cmdl.getOptionValue(USER_ID)));
			List<String> ids = new ArrayList<>();
			List<String> bookIDs = new ArrayList<>();
			List<String> quantities = new ArrayList<>();

			for (CartItem c : cart) {
				ids.add(String.valueOf(c.getCartItemID()));
				bookIDs.add(String.valueOf(c.getBookID()));
				quantities.add(String.valueOf(c.getQuantity()));
			}

			TableBuilder tb = new TableBuilder();
			tb.addColumn("ID", ids);
			tb.addColumn("Book ID", bookIDs);
			tb.addColumn("Quantity", quantities);
			outMessages.add(tb);
		} else {
			outMessages.add(new TableBuilder("Cart item with ID " + cmdl.getOptionValue(ID) + " not found"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#getAll(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void getAll(final CommandLine pCmdl) throws ParseException {
		List<CartItem> cart = null;

		try {
			cart = dao.getAll();
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("the operation failed"));
		}

		if (cart != null) {
			List<String> ids = new ArrayList<>();
			List<String> userIDs = new ArrayList<>();
			List<String> bookIDs = new ArrayList<>();
			List<String> quantities = new ArrayList<>();

			for (CartItem c : cart) {
				ids.add(String.valueOf(c.getCartItemID()));
				userIDs.add(String.valueOf(c.getUserID()));
				bookIDs.add(String.valueOf(c.getBookID()));
				quantities.add(String.valueOf(c.getQuantity()));
			}

			TableBuilder tb = new TableBuilder();
			tb.addColumn("ID", ids);
			tb.addColumn("User ID", userIDs);
			tb.addColumn("Book ID", bookIDs);
			tb.addColumn("Quantity", quantities);
			outMessages.add(tb);
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#update(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void update(final CommandLine pCmdl) throws ParseException {
		//unimplemented for cart
		System.out.println("update is unimplemented for cart");
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#delete(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void delete(final CommandLine pCmdl) throws ParseException {
		//unimplemented for cart
		System.out.println("delete is unimplemented for cart");
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#search(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void search(final CommandLine pCmdl) throws ParseException {
		//unimplemented for cart
		System.out.println("search is unimplemented for cart");
	}

}
