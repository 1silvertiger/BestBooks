package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.berry.BCrypt;

import model.DBConnection;
import model.User;
import model.UserDAO;
import util.AsciiBanner;
import util.TableBuilder;

public class MainController {
	private DBConnection connection;
	private Connection c;
	private String currentController = "";
	private String displayController = "";
	private boolean backToMenu = true;
	private boolean logout = false;
	private User myUser = new User();
	private UserDAO userDAO;
	private String[] args;
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();
	private AsciiBanner banner = new AsciiBanner();
	// --------------------------- CONTROLERS --------------------------
	private AdminBookController adminBook;
	private CustBookController custBook;

	// user controllers are instantiated after login
	private AdminUserController adminUser;
	private CustUserController custUser;
	private AdminPublisherController adminPublisher;
	private AdminCardController adminCard;
	private CustCartController custCart;
	private AdminAddressController adminAddress;
	private AdminCartController adminCart;
	private CustOrderController custOrder;
	private AdminOrderController adminOrder;
	private AdminReportController report;

	// --------------------------- COMMANDS -----------------------------
	private static final String BOOK = "book";
	private static final String MENU = "menu";
	private static final String SEARCH = "find";
	private static final String HELP = "help";
	private static final String USER = "user";
	private static final String PUBLISHER = "publisher";
	private static final String CART = "cart";
	private static final String ADDRESS = "address";
	private static final String CARD = "card";
	private static final String ORDER = "order";
	private static final String HISTORY = "history";
	private static final String REPORT = "report";

	private boolean didUpdate = false;
	
	/**
	 * this constructor sets up the connection to the database
	 * and passes that connection to the admin controllers.
	 */
	public MainController() {

		// the connection to the database, passed to any controller that needs
		// it.
		try {
			connection = new DBConnection();
			c = connection.getConnection();
			// ---------------------------------- DAO's ---------------------------
			try {
				userDAO = new UserDAO(c);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			adminBook = new AdminBookController(c);
			adminPublisher = new AdminPublisherController(c);
			adminAddress = new AdminAddressController(c);
			adminCard = new AdminCardController(c);
			adminCart = new AdminCartController(c);
			adminOrder = new AdminOrderController(c);
			report = new AdminReportController(c);
		} catch (SQLException e1) {
			outMessages.add(new TableBuilder("connection error. please try relaunching the program later"));
		}



	}

	/**
	 * This receives the input, processes it, and returns a list of generated
	 * TableBuilders
	 * 
	 * @param input the user's input.
	 * @return the generated output
	 */
	@SuppressWarnings("unchecked")
	public final List<TableBuilder> parseCommand(final String input) {
		outMessages.clear();

		// split the string at the spaces
		String[] tempArgs = input.split("\\s+");
		// make first args the command
		String cmd = "";
		try {
			cmd = tempArgs[0];
			// make the args a copy of tempargs, but without the command
			args = Arrays.copyOfRange(tempArgs, 1, tempArgs.length);
			// if the user ever enters back, let them leave the current
			// controller
			if (cmd.toLowerCase().equals(MENU)) {
				backToMenu = true;
			}

			if (backToMenu) {
				currentController = cmd;
				displayController = currentController;
			}

			// check if the arguments have any duplicates
			if (!myUser.isAdmin()) {
				if (hasDupes(args)) {
					outMessages.add(new TableBuilder("please note that duplicate commands are not processed"));
				}
			}
		} catch (ArrayIndexOutOfBoundsException j) {
			// set current controller to a blank value
			currentController = "";
			displayController = "";
		}
		// ---------------User specific commands----------------
		// determine what controllers to give to the user
		if (myUser.isAdmin()) {
			String addressLink = "-" + ADDRESS;
			String cardLink = "-" + CARD;
			String bookLink = "-" + BOOK;
			String cartLink = "-" + CART;
			String orderLink = "-" + ORDER;
			String publisherLink = "-" + PUBLISHER;
			String reportLink = "-" + REPORT;
			String[] links = { addressLink, cardLink, bookLink, cartLink, orderLink, publisherLink, reportLink };

			int numOfControllers = 6;

			Hashtable<String, List<TableBuilder>> tableBuilders = new Hashtable<String, List<TableBuilder>>();

			List<TableBuilder> addressInfo = new ArrayList<>();
			List<TableBuilder> cardInfo = new ArrayList<>();
			List<TableBuilder> bookInfo = new ArrayList<>();
			List<TableBuilder> cartInfo = new ArrayList<>();
			List<TableBuilder> orderInfo = new ArrayList<>();
			List<TableBuilder> publisherInfo = new ArrayList<>();

			tableBuilders.put(addressLink, addressInfo);
			tableBuilders.put(cardLink, cardInfo);
			tableBuilders.put(bookLink, bookInfo);
			tableBuilders.put(cartLink, cartInfo);
			tableBuilders.put(orderLink, orderInfo);
			tableBuilders.put(publisherLink, publisherInfo);

			boolean hasLink = false;
			for (String s : args) {
				if (ArrayUtils.contains(links, s)) {
					hasLink = true;
				}
			}

			if (hasLink) {
				AdminAddressController addressController = new AdminAddressController(c);
				AdminCardController cardController = new AdminCardController(c);
				AdminBookController bookController = new AdminBookController(c);
				AdminCartController cartController = new AdminCartController(c);
				AdminOrderController orderController = new AdminOrderController(c);
				AdminPublisherController publisherController = new AdminPublisherController(c);

				Hashtable<String, Integer> indeces = new Hashtable<String, Integer>(numOfControllers);
				indeces.put(addressLink, ArrayUtils.indexOf(args, addressLink));
				indeces.put(cardLink, ArrayUtils.indexOf(args, cardLink));
				indeces.put(bookLink, ArrayUtils.indexOf(args, bookLink));
				indeces.put(cartLink, ArrayUtils.indexOf(args, cartLink));
				indeces.put(orderLink, ArrayUtils.indexOf(args, orderLink));
				indeces.put(publisherLink, ArrayUtils.indexOf(args, publisherLink));

				@SuppressWarnings("rawtypes")
				Hashtable<String, AdminController> controllers = new Hashtable<String, AdminController>(
						numOfControllers);
				controllers.put(addressLink, addressController);
				controllers.put(cardLink, cardController);
				controllers.put(bookLink, bookController);
				controllers.put(cartLink, cartController);
				controllers.put(orderLink, orderController);
				controllers.put(publisherLink, publisherController);

				int firstLink = args.length;
				for (String s : links) {
					if (ArrayUtils.contains(args, s)) {
						String[] tempArgs1 = { "" };
						int index = indeces.get(s);
						int i = index + 1;
						if (args.length > i && index != ArrayUtils.INDEX_NOT_FOUND) {
							while (i < args.length && !ArrayUtils.contains(links, args[i])) {
								tempArgs1 = ArrayUtils.add(tempArgs1, args[i]);
								i++;
							}
						}

						tableBuilders.get(s).addAll(controllers.get(s).parseCommand(cmd, tempArgs1));

						if (index != ArrayUtils.INDEX_NOT_FOUND && index < firstLink) {
							firstLink = index;
						}
					}
				}

//				if (currentController.equals(REPORT) && cmd == "update") {
//					report.refresh();
//				}
				
				if (args.length > 1) {
					args = ArrayUtils.removeAll(args, firstLink, args.length - 1);
				}
			} else {
				switch (currentController) {
				case BOOK:
					// if going to different controller, backtomenu must be set
					// to false
					backToMenu = false;
					outMessages = adminBook.parseCommand(cmd, args);
					break;
				case USER:
					// if going to different controller, backtomenu must be set
					// to false
					backToMenu = false;
					outMessages = adminUser.parseCommand(cmd, args);
					break;
				case MENU:
					outMessages.add(new TableBuilder(
							"Welcome to the main menu, " + myUser.getFirstName() + " " + myUser.getLastName() + "!"));
					outMessages.add(new TableBuilder("Enter <help> to see commands"));
					break;
				case PUBLISHER:
					backToMenu = false;
					outMessages = adminPublisher.parseCommand(cmd, args);
					break;
				case ADDRESS:
					backToMenu = false;
					outMessages = adminAddress.parseCommand(cmd, args);
					break;
				case CARD:
					backToMenu = false;
					outMessages = adminCard.parseCommand(cmd, args);
					break;
				case CART:
					backToMenu = false;
					outMessages = adminCart.parseCommand(cmd, args);
					break;
				case ORDER:
					backToMenu = false;
					outMessages = adminOrder.parseCommand(cmd, args);
					break;
				case REPORT:
					if (didUpdate) {
						didUpdate = false;
						report.refresh();
					}
					backToMenu = false;
					outMessages = report.parseCommand(cmd, args);
					break;
				case HELP:
					menuHelp();
					displayController = "";
					break;
				default:
					System.out.println(currentController);
					outMessages.add(new TableBuilder("Invalid command: " + cmd + ". Enter <help> to see valid commands"));
					displayController = "";
				}
			}
			for (String s : links) {
				if (tableBuilders.get(s) != null && !tableBuilders.get(s).isEmpty()) {
					outMessages.add(new TableBuilder(""));
					outMessages.add(new TableBuilder(StringUtils.capitalize(s.substring(1)) + " results: "));
					outMessages.addAll(tableBuilders.get(s));
				}
			}
			if (cmd.equals("update")) {
				didUpdate = true;
			}
			tableBuilders.clear();
		} else {
			switch (currentController) {
			case SEARCH:
				// if going to different controller, backtomenu must be set to
				// false
				backToMenu = false;
				outMessages = custBook.parseCommand(cmd, args);
				break;
			case MENU:
				try {
					outMessages.addAll(banner.getBanner("MENU"));
				} catch (IOException e) {
					//do nothing
				}
				outMessages.add(new TableBuilder(
						"Welcome to the main menu, " + myUser.getFirstName() + " " + myUser.getLastName() + "!"));
				outMessages.add(new TableBuilder("Enter <help> to see commands"));
				break;
			case USER:
				// if going to different controller, backtomenu must be set to
				// false
				backToMenu = false;
				outMessages = custUser.parseCommand(cmd, args);
				logout = custUser.getLogout();
				if (cmd.equals(ADDRESS)) {
					displayController = ADDRESS;
				} else if (cmd.equals(CARD)) {
					displayController = CARD;
				} else if (cmd.equals(USER)) {
					displayController = USER;
				}
				break;
			case CART:
				// if going to different controller, backtomenu must be set to
				// false
				backToMenu = false;
				outMessages = custCart.parseCommand(cmd, args);
				break;
			case HISTORY:
				// if going to different controller, backtomenu must be set to
				// false
				backToMenu = false;
				outMessages = custOrder.parseCommand(cmd, args);
				break;
			case HELP:
				menuHelp();
				displayController = "";
				break;
			default:
				outMessages.add(new TableBuilder("Invalid command, enter <help> to see valid commands"));
				displayController = "";
			}
		}

		return outMessages;
	}
	/**
	 * runs the logout procedures.
	 */
	public final void Logout() {
		clear();
	}

	/**
	 * clears the current information in the controller.
	 */
	private void clear() {
		try {
			userDAO = new UserDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder("A connection error has occured, please check your internet connection and relaunch the program"));
		}

		myUser = new User();
		logout = false;
		backToMenu = true;
		currentController = "";
		displayController = "";
	}

	/**
	 * logs the user in and instantiates the customer controllers.
	 * @param password the password to use in login
	 * @param email the email to use in login
	 * @return whether the login could be completed or not
	 * @throws SQLException an exception from the database.
	 */
	public final boolean Login(final String password, final String email) throws SQLException {
		// make the user login first
		myUser = userDAO.login(email);
		if (myUser.getUserPassword() != null) {
			boolean compareComputed = BCrypt.checkpw(password, myUser.getUserPassword());
			if (compareComputed) {
				myUser = userDAO.get(myUser.getUserID());
				myUser.setPlainPassword(password);
				if (myUser.isAdmin()){
					adminUser = new AdminUserController(c, myUser);
				} else {
					custUser = new CustUserController(c, myUser);
					custBook = new CustBookController(c, myUser);
					custCart = new CustCartController(c, myUser);
					custOrder = new CustOrderController(c, myUser);
				}
				return true;
			} else {
				clear();
				return false;
			}
		} else {
			clear();
			return false;
		}
	}

	/**
	 * displays the help dialogue.
	 * @return the help option tableBuilder list
	 */
	public final List<TableBuilder> menuHelp() {
		String[] headers = new String[2];
		String[] row = new String[2];
		headers[0] = "command: ";
		headers[1] = "action: ";
		TableBuilder table = new TableBuilder(headers);

		if (backToMenu) {
			row[0] = "help";
			row[1] = "to show this dialogue";
			table.addRow(row);
			row = new String[2];

			row[0] = "logout";
			row[1] = "to logout of your account";
			table.addRow(row);
			row = new String[2];

			row[0] = "quit";
			row[1] = "to close the program";
			table.addRow(row);
			row = new String[2];
			if (myUser.isAdmin()) {
				row[0] = "book";
				row[1] = "to edit, create, delete, and search for books";
				table.addRow(row);
				row = new String[2];

				row[0] = "user";
				row[1] = "to edit, create, or delete users";
				table.addRow(row);
				row = new String[2];

				row[0] = "publisher";
				row[1] = "to edit, create, delete, and search for publishers";
				table.addRow(row);
				row = new String[2];

				row[0] = "address";
				row[1] = "to edit, create, delete, and search for addresses";
				table.addRow(row);
				row = new String[2];

				row[0] = "card";
				row[1] = "to edit, create, delete, and search for credit cards";
				table.addRow(row);
				row = new String[2];

				row[0] = "cart";
				row[1] = "to create and view cart items";
				table.addRow(row);
				row = new String[2];
				
				row[0] = "order";
				row[1] = "to edit, create, delete, and search for orders";
				table.addRow(row);
				row = new String[2];

				row[0] = "report";
				row[1] = "to generate reports as comma separated value files";
				table.addRow(row);
				row = new String[2];
			} else { // they are a customer
				row[0] = "find";
				row[1] = "to search for books";
				table.addRow(row);
				row = new String[2];

				row[0] = "user";
				row[1] = "to modify user information";
				table.addRow(row);
				row = new String[2];

				row[0] = "cart";
				row[1] = "to view cart and make orders";
				table.addRow(row);
				row = new String[2];

				row[0] = "history";
				row[1] = "to view past orders";
				table.addRow(row);
				row = new String[2];
			}
		}
		outMessages.add(table);
		return outMessages;
	}

	/**
	 * checks if their were duplicate options entered.
	 * @param args the arguments to check
	 * @return whether there are duplicate options or not
	 */
	public final boolean hasDupes(final String[] args) {
		char firstChar;
		boolean dupes = false;
		List<String> argStrings = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			firstChar = args[i].charAt(0);
			if (firstChar == '-') {
				argStrings.add(args[i]);
			}
		}
		for (int i = 0; i < argStrings.size(); i++) {
			for (int n = 0; n < argStrings.size(); n++) {
				if (i == n) {
					// ignore it if they are the same
				} else if (argStrings.get(i).equals(argStrings.get(n))) {
					dupes = true;
				}
			}
		}
		return dupes;
	}

	/**
	 * creates a new user.
	 * @param firstName the first name to create
	 * @param lastName the last name to create
	 * @param password the password
	 * @param email the email to create
	 * @param homePhone the home phone number to create
	 * @param cellPhone the cell phone number ot create
	 * @return whether the user could be created or not
	 */
	public final boolean CreateUser(final String firstName
			, final String lastName
			, final String password
			, final String email
			, final String homePhone
			, final String cellPhone) {
		myUser.setFirstName(firstName);
		myUser.setLastName(lastName);
		myUser.setUserPassword(password);
		myUser.setEmailAddress(email);
		myUser.setHomePhone(homePhone);
		myUser.setCellPhone(cellPhone);
		
		try {
			userDAO.create(myUser);
			return true;
		} catch (SQLException e) {
			// if the users email already existed, trigger as false
			if (e.getSQLState().startsWith("45000")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * used to request a logout from a controller.
	 */
	public final void RequestLogout() {
		logout = true;
	}

	/**
	 * @return whether to logout or not.
	 */
	public final boolean LogoutRequested() {
		return logout;
	}

	/**
	 * @return the active controller
	 */
	public final String getCurrentController() {
		return displayController;
	}

	/**
	 * @return the name of the current user
	 */
	public final String getCurrentUserNameAbbreviated() {
		return myUser.getFirstName().charAt(0) + ". " + myUser.getLastName();
	}
	
	/**
	 * @return the full name of the current user
	 */
	public final String getCurrentUserName() {
		return myUser.getFirstName() + " " + myUser.getLastName();
	}
}