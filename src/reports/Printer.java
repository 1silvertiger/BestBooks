package reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import model.Address;
import model.AddressDAO;
import model.AddressType;
import model.CardSecure;
import model.CardSecureDAO;
import model.Order;
import model.OrderDAO;
import model.OrderItemBook;
import model.User;
import model.UserDAO;

/**
 * This class generates various reports into CSV files.
 * 
 * @author Dakota
 *
 */
public class Printer {

	// this is the number of lines on an Excel sheet before the top line
	// disappears
	private static final int OFF_PAGE = 29;

	// these are the values stored in the database to signify an order status
	private static final String DELIVERED = "delivered";
	private static final String RETURNED = "returned";

	// these hold all of the data from the database
	private List<Order> allOrders = new ArrayList<>();
	private List<Address> allAddresses = new ArrayList<>();
	private List<CardSecure> allCards = new ArrayList<>();
	private List<User> allUsers = new ArrayList<>();

	// this formats prices into $XX.XX format
	private NumberFormat priceFormatter = NumberFormat.getCurrencyInstance();

	// this holds all the data to be printed in the report
	private StringBuilder report = new StringBuilder();

	// the daos
	private OrderDAO orderDAO;
	private AddressDAO addressDAO;
	private CardSecureDAO cardDAO;
	private UserDAO userDAO;

	/**
	 * The constructor
	 * 
	 * @param c
	 *            the connection to the database
	 * @throws SQLException
	 *             if the connection malfunctions
	 */
	public Printer(Connection c) throws SQLException {

		orderDAO = new OrderDAO(c);
		addressDAO = new AddressDAO(c);
		cardDAO = new CardSecureDAO(c);
		userDAO = new UserDAO(c);

		// this calls the appropriate getAll() methods for each dao and stores
		// it in the appropriate arraylist
		refresh();
	}

	/**
	 * This generates an invoice for the specified order.
	 * 
	 * @param pOrderID
	 *            the order
	 * @return the absolute file path of the generated invoice
	 * @throws FileNotFoundException
	 *             if the file is not found
	 */
	public String printInvoice(int pOrderID) throws FileNotFoundException {
		// this holds the file name
		StringBuilder fileName = new StringBuilder();

		// we store the root file name
		fileName.append(
				"order_" + String.valueOf(pOrderID) + "_invoice_" + String.valueOf(LocalDateTime.now().toLocalDate()));
		File file = null;
		int i = 0;
		String suffix = "";
		// this iterates through the integers until it finds one that hasn't
		// been used for this file before
		do {
			suffix = "_(" + String.valueOf(++i) + ").csv";
			file = new File(fileName.toString() + suffix);
		} while (file.isFile());
		fileName.append(suffix);

		Order order = null;

		for (Order o : allOrders) {
			if (o.getOrderID() == pOrderID) {
				order = o;
			}
		}

		// make sure the order was found
		if (order != null) {
			// we add header with metadata for every order
			ArrayList<String> orderHeader = new ArrayList<>();
			orderHeader.add("Order Number: " + order.getOrderID());
			orderHeader.add("Creation date: " + String.valueOf(order.getCreationDate()));
			orderHeader.add("Status: " + order.getOrderStatus());
			orderHeader.add("Discount: " + order.getDiscountPercent() * 100 + "%");
			orderHeader.add("Total: " + StringUtils.removeAll(priceFormatter.format(order.getTotal()), ","));
			addToReportByRow(orderHeader);
			insertGap();

			ArrayList<String> shippingAddressInfo = new ArrayList<>();
			shippingAddressInfo.add("SHIPPING ADDRESS:   ");
			shippingAddressInfo.add("Address ID: " + order.getAddress().getAddressID());
			shippingAddressInfo.add(order.getAddress().getLine1() + " " + order.getAddress().getLine2() + " "
					+ order.getAddress().getCity() + " " + order.getAddress().getState() + " "
					+ order.getAddress().getZip());
			addToReportByRow(shippingAddressInfo);
			insertGap();

			ArrayList<String> billingAddressInfo = new ArrayList<>();
			billingAddressInfo.add("BILLING ADDRESS:   ");
			billingAddressInfo.add("Address ID: " + order.getCard().getAddress().getAddressID());
			billingAddressInfo.add(order.getCard().getAddress().getLine1() + " "
					+ order.getCard().getAddress().getLine2() + " " + order.getCard().getAddress().getCity() + " "
					+ order.getCard().getAddress().getState() + " " + order.getCard().getAddress().getZip());
			addToReportByRow(billingAddressInfo);
			insertGap();

			ArrayList<String> cardInfo = new ArrayList<>();
			cardInfo.add("CARD:   ");
			cardInfo.add("Card ID: " + order.getCard().getCardID());
			cardInfo.add("Card holder name: " + order.getCard().getCardHolderName());
			cardInfo.add("Card number: " + order.getCard().getCardNumber());
			addToReportByRow(cardInfo);
			insertGap();

			ArrayList<String> bookHeader = new ArrayList<>();
			bookHeader.add("Book ID");
			bookHeader.add("Order Item ID");
			bookHeader.add("Title");
			bookHeader.add("Quantity");
			bookHeader.add("Price");
			addToReportByRow(bookHeader);

			for (OrderItemBook b : order.getItems()) {
				ArrayList<String> book = new ArrayList<>();
				book.add(String.valueOf(b.getBookID()));
				book.add(String.valueOf(b.getOrderItemID()));
				book.add(b.getTitle());
				book.add(String.valueOf(b.getQuantity()));
				book.add(StringUtils.removeAll(priceFormatter.format(b.getSalePrice()), ","));
				addToReportByRow(book);
			}

			print(fileName.toString());
		} else {
			return "Order " + pOrderID + " not found";
		}

		return file.getAbsolutePath();
	}

	/**
	 * This generates an customer invoice for the specified order.
	 * 
	 * @param pOrderID
	 *            the order
	 * @return the absolute file path of the generated invoice
	 * @throws FileNotFoundException
	 *             if the file is not found
	 */
	public String printCustomerInvoice(int pOrderID, int userID) throws FileNotFoundException {
		// this holds the file name
		StringBuilder fileName = new StringBuilder();

		// we store the root file name
		fileName.append(
				"order_" + String.valueOf(pOrderID) + "_invoice_" + String.valueOf(LocalDateTime.now().toLocalDate()));
		File file = null;
		int i = 0;
		String suffix = "";
		// this iterates through the integers until it finds one that hasn't
		// been used for this file before
		do {
			suffix = "_(" + String.valueOf(++i) + ").csv";
			file = new File(fileName.toString() + suffix);
		} while (file.isFile());
		fileName.append(suffix);

		Order order = null;

		try {
			order = orderDAO.getWithBooksByOrderAndUser(pOrderID, userID);
		} catch (SQLException e) {
			return "could not get order";
		}

		// for formatting dates
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

		// make sure the order was found
		if (order != null) {
			// we add header with metadata for every order
			ArrayList<String> orderHeader = new ArrayList<>();
			orderHeader.add("Order Number: " + order.getOrderID());
			orderHeader.add("Order date: " + order.getCreationDate().format(formatter));
			orderHeader.add("Status: " + order.getOrderStatus());
			// dont show the discount if they didn't get one
			if (order.getDiscountPercent() != 0) {
				orderHeader.add("Discount: " + order.getDiscountPercent() * 100 + "%");
			}
			orderHeader.add("Total: " + StringUtils.removeAll(priceFormatter.format(order.getTotal()), ","));
			addToReportByRow(orderHeader);
			insertGap();
			// show shipping address info
			ArrayList<String> shippingAddressInfo = new ArrayList<>();
			shippingAddressInfo.add("SHIPPING ADDRESS:   ");
			shippingAddressInfo.add(order.getAddress().getLine1() + " " + order.getAddress().getLine2() + " "
					+ order.getAddress().getCity() + " " + order.getAddress().getState() + " "
					+ order.getAddress().getZip());
			addToReportByRow(shippingAddressInfo);
			insertGap();
			// show billing address info
			ArrayList<String> billingAddressInfo = new ArrayList<>();
			billingAddressInfo.add("BILLING ADDRESS:   ");
			billingAddressInfo.add(order.getCard().getAddress().getLine1() + " "
					+ order.getCard().getAddress().getLine2() + " " + order.getCard().getAddress().getCity() + " "
					+ order.getCard().getAddress().getState() + " " + order.getCard().getAddress().getZip());
			addToReportByRow(billingAddressInfo);
			insertGap();
			// show card info
			ArrayList<String> cardInfo = new ArrayList<>();
			cardInfo.add("CARD:   ");
			cardInfo.add("Card holder name: " + order.getCard().getCardHolderName());
			cardInfo.add("Card number: " + order.getCard().getCardNumber());
			addToReportByRow(cardInfo);
			insertGap();
			// add headers
			ArrayList<String> bookHeader = new ArrayList<>();
			bookHeader.add("Title");
			bookHeader.add("Author");
			bookHeader.add("Quantity");
			bookHeader.add("Price");
			addToReportByRow(bookHeader);
			// look at the books
			for (OrderItemBook b : order.getItems()) {
				ArrayList<String> book = new ArrayList<>();
				book.add(b.getTitle());
				book.add(b.getAuthor());
				book.add(String.valueOf(b.getQuantity()));
				book.add(StringUtils.removeAll(priceFormatter.format(b.getSalePrice()), ","));
				addToReportByRow(book);
			}
			// print it
			print(fileName.toString());
		} else {
			return "Order " + pOrderID + " not found";
		}

		return file.getAbsolutePath();
	}

	/**
	 * This report puts all of the orders of a particular user in a CSV file
	 * 
	 * @param pUserID
	 *            the user ID
	 * @return the absolute file path of the generated report
	 * @throws FileNotFoundException
	 *             if an error occurs with the file
	 */
	public String printAllUserOrders(int pUserID) throws FileNotFoundException {
		// this holds the file name
		StringBuilder fileName = new StringBuilder();

		// we store the root file name
		fileName.append(
				"user_" + String.valueOf(pUserID) + "_orders_" + String.valueOf(LocalDateTime.now().toLocalDate()));
		File file = null;
		int i = 0;
		String suffix = "";
		// this iterates through the integers until it finds one that hasn't
		// been used for this file before
		do {
			suffix = "_(" + String.valueOf(++i) + ").csv";
			file = new File(fileName.toString() + suffix);
		} while (file.isFile());
		fileName.append(suffix);

		// order by highest total
		Order[] orders = new Order[allOrders.size()];
		allOrders.toArray(orders);
		boolean keepGoing = true;
		while (keepGoing) {
			keepGoing = false;
			for (i = 0; i < orders.length; i++) {
				if (i + 1 < orders.length && orders[i].getTotal() > orders[i + 1].getTotal()) {
					ArrayUtils.swap(orders, i, i + 1);
					keepGoing = true;
				}
			}
		}

		// now we go through all the orders and add the ones attributed to the
		// userID to the report
		for (Order o : orders) {
			insertGap();
			if (o.getUserID() == pUserID) {
				// we add header with metadata for every order
				ArrayList<String> orderHeader = new ArrayList<>();
				orderHeader.add("Order ID: " + o.getOrderID());
				orderHeader.add("Creation date: " + String.valueOf(o.getCreationDate()));
				orderHeader.add("Status: " + o.getOrderStatus());
				orderHeader.add("Discount: " + o.getDiscountPercent() * 100 + "%");
				orderHeader.add("Total: " + StringUtils.removeAll(priceFormatter.format(o.getTotal()), ","));
				addToReportByRow(orderHeader);
				insertGap();

				ArrayList<String> shippingAddressInfo = new ArrayList<>();
				shippingAddressInfo.add("SHIPPING ADDRESS:   ");
				shippingAddressInfo.add("Address ID: " + o.getAddress().getAddressID());
				shippingAddressInfo.add(o.getAddress().getLine1() + " " + o.getAddress().getLine2() + " "
						+ o.getAddress().getCity() + " " + o.getAddress().getState() + " " + o.getAddress().getZip());
				addToReportByRow(shippingAddressInfo);
				insertGap();

				ArrayList<String> billingAddressInfo = new ArrayList<>();
				billingAddressInfo.add("BILLING ADDRESS:   ");
				billingAddressInfo.add("Address ID: " + o.getCard().getAddress().getAddressID());
				billingAddressInfo.add(o.getCard().getAddress().getLine1() + " " + o.getCard().getAddress().getLine2()
						+ " " + o.getCard().getAddress().getCity() + ", " + o.getCard().getAddress().getState() + " "
						+ o.getCard().getAddress().getZip());
				addToReportByRow(billingAddressInfo);
				insertGap();

				ArrayList<String> cardInfo = new ArrayList<>();
				cardInfo.add("CARD:   ");
				cardInfo.add("Card ID: " + o.getCard().getCardID());
				cardInfo.add("Card holder name: " + o.getCard().getCardHolderName());
				cardInfo.add("Card number: " + o.getCard().getCardNumber());
				addToReportByRow(cardInfo);
				insertGap();

				ArrayList<String> bookHeader = new ArrayList<>();
				bookHeader.add("Book ID");
				bookHeader.add("Order Item ID");
				bookHeader.add("Title");
				bookHeader.add("Quantity");
				bookHeader.add("Price");
				addToReportByRow(bookHeader);

				for (OrderItemBook b : o.getItems()) {
					ArrayList<String> book = new ArrayList<>();
					book.add(String.valueOf(b.getBookID()));
					book.add(String.valueOf(b.getOrderItemID()));
					book.add(b.getTitle());
					book.add(String.valueOf(b.getQuantity()));
					book.add(StringUtils.removeAll(priceFormatter.format(b.getSalePrice()), ","));
					addToReportByRow(book);
				}
			}
		}
		insertGap();
		print(fileName.toString());
		return file.getAbsolutePath();
	}

	/**
	 * This generates a report of all activity by month.
	 * 
	 * @return the absolute file name of the generated report
	 * @throws FileNotFoundException
	 *             if an error with the file occurs
	 */
	public String printActivityDataByMonth() throws FileNotFoundException {
		// configure file name
		StringBuilder fileName = new StringBuilder();
		fileName.append("activity_report_" + String.valueOf(LocalDateTime.now().toLocalDate()));
		File file = null;
		int i = 0;
		String suffix = "";
		do {
			suffix = "_(" + String.valueOf(++i) + ").csv";
			file = new File(fileName.toString() + suffix);
		} while (file.isFile());
		fileName.append(suffix);

		// order by highest date
		Order[] orders = new Order[allOrders.size()];
		allOrders.toArray(orders);
		boolean keepGoing = true;
		while (keepGoing) {
			keepGoing = false;
			for (i = 0; i < orders.length; i++) {
				if (i + 1 < orders.length && orders[i].getCreationDate().isBefore(orders[i + 1].getCreationDate())) {
					ArrayUtils.swap(orders, i, i + 1);
					keepGoing = true;
				}
			}
		}

		// generate the header
		ArrayList<String> header = new ArrayList<>();
		header.add("Month");
		header.add("Year");
		header.add("Books sold");
		header.add("Orders placed");
		header.add("Orders completed");
		header.add("Income");
		header.add("Purchasing users");
		addToReportByRow(header);

		// initialize all the containers for the data and set them to 0 or the
		// first value
		Month currentMonth = orders[0].getCreationDate().getMonth();
		int currentYear = orders[0].getCreationDate().getYear();
		int totalBooks = 0;
		int totalOrdersPlaced = 0;
		int totalOrdersCompleted = 0;
		double totalIncome = 0;
		int totalUsers = 0;

		ArrayList<Integer> purchasingUsers = new ArrayList<>();
		for (int j = 0; j < orders.length; j++) {
			// if (!orders[j].getCreationDate().getMonth().equals(currentMonth))
			// {
			// ArrayList<String> monthData = new ArrayList<>();
			// monthData.add(currentMonth.toString());
			// monthData.add(String.valueOf(currentYear));
			// monthData.add(String.valueOf(totalBooks));
			// monthData.add(String.valueOf(totalOrdersPlaced));
			// monthData.add(String.valueOf(totalOrdersCompleted));
			// monthData.add(String.valueOf(priceFormatter.format(totalIncome)));
			// monthData.add(String.valueOf(totalUsers));
			// addToReport(monthData);
			// totalBooks = 0;
			// totalOrdersPlaced = 0;
			// totalOrdersCompleted = 0;
			// totalIncome = 0;
			// totalUsers = 0;
			// currentMonth = orders[j].getCreationDate().getMonth();
			// currentYear = orders[j].getCreationDate().getYear();
			// }

			totalBooks += orders[j].getItems().size();

			totalOrdersPlaced++;
			if (orders[j].getOrderStatus().toLowerCase().equals(DELIVERED)) {
				totalOrdersCompleted++;
				totalIncome += orders[j].getTotal();
			}
			if (!purchasingUsers.contains(orders[j].getUserID())) {
				purchasingUsers.add(orders[j].getUserID());
				totalUsers++;
			}
			if (j == orders.length - 1 || !orders[j].getCreationDate().getMonth().equals(currentMonth)) {
				ArrayList<String> monthData = new ArrayList<>();
				monthData.add(currentMonth.toString());
				monthData.add(String.valueOf(currentYear));
				monthData.add(String.valueOf(totalBooks));
				monthData.add(String.valueOf(totalOrdersPlaced));
				monthData.add(String.valueOf(totalOrdersCompleted));
				monthData.add(String.valueOf(StringUtils.removeAll(priceFormatter.format(totalIncome), ",")));
				monthData.add(String.valueOf(totalUsers));
				addToReportByRow(monthData);
				totalBooks = 0;
				totalOrdersPlaced = 0;
				totalOrdersCompleted = 0;
				totalIncome = 0;
				totalUsers = 0;
				if (j + 1 < orders.length) {
					currentMonth = orders[j].getCreationDate().getMonth();
					currentYear = orders[j].getCreationDate().getYear();
				}
			}
		}
		print(fileName.toString());
		return file.getAbsolutePath();
	}

	/**
	 * This generates a report on the activity for a specific month.
	 * 
	 * @param month
	 *            the month
	 * @param year
	 *            the year
	 * @return the absolute file path of the generated report
	 * @throws FileNotFoundException
	 *             if an error with the file occurs
	 */
	public String printActivityDataForMonth(String month, String year) throws FileNotFoundException {
		// configure file name
		StringBuilder fileName = new StringBuilder();
		fileName.append("sales_report_" + month + "_" + year);
		File file = null;
		int i = 0;
		String suffix = "";
		do {
			suffix = "_(" + String.valueOf(++i) + ").csv";
			file = new File(fileName.toString() + suffix);
		} while (file.isFile());
		fileName.append(suffix);

		// order by highest date
		Order[] orders = new Order[allOrders.size()];
		allOrders.toArray(orders);
		boolean keepGoing = true;
		while (keepGoing) {
			keepGoing = false;
			for (i = 0; i < orders.length; i++) {
				if (i + 1 < orders.length && orders[i].getCreationDate().isBefore(orders[i + 1].getCreationDate())) {
					ArrayUtils.swap(orders, i, i + 1);
					keepGoing = true;
				}
			}
		}

		// remove duplicates left by swapping
		for (int j = 0; j < orders.length; j++) {
			if (j > 0) {
				if (orders[j].equals(orders[j - 1])) {
					orders = ArrayUtils.remove(orders, j);
					j -= 1;
				}
			}
		}

		// add the title
		ArrayList<String> title = new ArrayList<>();
		title.add("Activity report for " + StringUtils.capitalize(month) + " " + year);
		title.add("Generated: " + StringUtils.capitalize(LocalDateTime.now().getMonth().toString().toLowerCase()) + " "
				+ LocalDateTime.now().getDayOfMonth() + " " + LocalDateTime.now().getYear());
		addToReportByRow(title);

		// add the header
		ArrayList<String> header = new ArrayList<>();
		header.add("Date");
		header.add("Titles sold");
		header.add("Quantity sold");
		header.add("Orders placed");
		header.add("Orders completed");
		header.add("Orders returned");
		header.add("Order total");
		header.add("Purchasing users");
		addToReportByRow(header);

		int currentDay = orders[0].getCreationDate().getDayOfMonth();

		// these hold the daily totals
		int dayDifferentBooks = 0;
		int dayOrdersPlaced = 0;
		int dayOrdersCompleted = 0;
		int dayOrdersReturned = 0;
		double dayIncome = 0;
		ArrayList<Integer> dailyUsers = new ArrayList<>();
		int dailyBooks = 0;

		// these hold the grand totals
		int totalDifferentBooks = 0;
		int totalOrdersPlaced = 0;
		int totalOrdersCompleted = 0;
		int totalOrdersReturned = 0;
		double totalIncome = 0;
		ArrayList<Integer> totalUsers = new ArrayList<>();
		int totalBooks = 0;

		// extract the data for the month
		for (int j = 0; j < orders.length; j++) {
			if (orders[j].getCreationDate().getMonth().toString().toLowerCase().equals(month.toLowerCase())
					&& String.valueOf(orders[j].getCreationDate().getYear()).equals(year)) {

				dayDifferentBooks += orders[j].getItems().size();

				dayOrdersPlaced++;

				for (OrderItemBook b : orders[j].getItems()) {
					dailyBooks += b.getQuantity();
				}

				if (orders[j].getOrderStatus().toLowerCase().equals(DELIVERED)
						|| orders[j].getOrderStatus().toLowerCase().equals(RETURNED)) {
					dayOrdersCompleted++;
					dayIncome += orders[j].getTotal();
				}
				if (orders[j].getOrderStatus().toLowerCase().equals(RETURNED)) {
					dayOrdersReturned++;
				}

				if (!dailyUsers.contains(orders[j].getUserID())) {
					dailyUsers.add(orders[j].getUserID());
				}

				if (j > OFF_PAGE && j % OFF_PAGE == 0) {
					addToReportByRow(header);
				}

				if (j == orders.length - 1 || orders[j + 1].getCreationDate().getDayOfMonth() != currentDay) {
					ArrayList<String> dayData = new ArrayList<>();
					dayData.add(String.valueOf(
							StringUtils.capitalize(orders[j].getCreationDate().getDayOfWeek().toString().toLowerCase())
									+ " "
									+ StringUtils
											.capitalize(orders[j].getCreationDate().getMonth().toString().toLowerCase())
									+ " " + orders[j].getCreationDate().getDayOfMonth()));
					dayData.add(String.valueOf(dayDifferentBooks));
					dayData.add(String.valueOf(dailyBooks));
					dayData.add(String.valueOf(dayOrdersPlaced));
					dayData.add(String.valueOf(dayOrdersCompleted));
					dayData.add(String.valueOf(dayOrdersReturned));
					dayData.add(String.valueOf(StringUtils.removeAll(priceFormatter.format(dayIncome), ",")));
					dayData.add(String.valueOf(dailyUsers.size()));
					addToReportByRow(dayData);

					totalDifferentBooks += dayDifferentBooks;
					totalBooks += dailyBooks;
					totalOrdersPlaced += dayOrdersPlaced;
					totalOrdersCompleted += dayOrdersCompleted;
					totalOrdersReturned += dayOrdersReturned;
					totalIncome += dayIncome;
					for (int n : dailyUsers) {
						if (!totalUsers.contains(n)) {
							totalUsers.add(n);
						}
					}

					dayDifferentBooks = 0;
					dayOrdersPlaced = 0;
					dayOrdersCompleted = 0;
					dayOrdersReturned = 0;
					dayIncome = 0;
					dailyUsers.clear();
					dailyBooks = 0;
					if (j < orders.length - 1) {
						currentDay = orders[j + 1].getCreationDate().getDayOfMonth();
					}
				}
			}
		}
		insertGap();

		// add the totals
		ArrayList<String> summary = new ArrayList<>();
		summary.add("TOTAL:");
		summary.add(String.valueOf(totalDifferentBooks));
		summary.add(String.valueOf(totalBooks));
		summary.add(String.valueOf(totalOrdersPlaced));
		summary.add(String.valueOf(totalOrdersCompleted));
		summary.add(String.valueOf(totalOrdersReturned));
		summary.add(String.valueOf(StringUtils.removeAll(priceFormatter.format(totalIncome), ",")));
		summary.add(String.valueOf(totalUsers.size()));
		addToReportByRow(summary);
		print(fileName.toString());

		return file.getAbsolutePath();
	}

	/**
	 * This generates a report on where users ship and bill to
	 * 
	 * @return the absolute file path of the generated report
	 * @throws FileNotFoundException
	 *             if an error with the file occurs
	 */
	public String printDemographicReport() throws FileNotFoundException {
		// configure file name
		StringBuilder fileName = new StringBuilder();
		fileName.append("customer_demographics_report");
		File file = null;
		int i = 0;
		String suffix = "";
		do {
			suffix = "_(" + String.valueOf(++i) + ").csv";
			file = new File(fileName.toString() + suffix);
		} while (file.isFile());
		fileName.append(suffix);

		// add the title
		ArrayList<String> title = new ArrayList<>();
		title.add("Customer Demographics Report");
		title.add("Generated" + LocalDateTime.now().getMonth() + " " + LocalDateTime.now().getDayOfMonth() + " "
				+ LocalDateTime.now().getYear());

		// switch users to an array
		User[] users = new User[allUsers.size()];
		allUsers.toArray(users);

		// associate each address with its user
		Hashtable<User, ArrayList<Address>> userAddresses = new Hashtable<User, ArrayList<Address>>();
		for (int j = 0; j < users.length; j++) {
			ArrayList<Address> tempAddresses = new ArrayList<>();
			for (Address a : allAddresses) {
				if (a.getType() == AddressType.SHIPPING && a.getOwnerID() == users[j].getUserID()) {
					tempAddresses.add(a);
				} else if (a.getType() == AddressType.CARD) {
					for (CardSecure c : allCards) {
						if (c.getCardID() == a.getOwnerID() && c.getUserID() == users[j].getUserID()) {
							tempAddresses.add(a);
						}
					}
				}
			}
			userAddresses.put(users[j], tempAddresses);
		}

		for (int j = 0; j < users.length; j++) {
			// order by admin or not
			if (j + 1 < users.length && users[j].isAdmin() && !users[j + 1].isAdmin()) {
				ArrayUtils.swap(users, j, j + 1);
				j -= 2;
			}
		}

		// the array lists hold all the data
		ArrayList<String> userIDs = new ArrayList<>();
		ArrayList<String> billingAddresses = new ArrayList<>();
		ArrayList<String> shippingAddresses = new ArrayList<>();

		// we compile the data for all the users
		for (User u : users) {
			userIDs.add(String.valueOf(u.getUserID()));

			StringBuilder ba = new StringBuilder();
			StringBuilder sa = new StringBuilder();
			for (Address a : userAddresses.get(u)) {
				if (!userAddresses.get(u).isEmpty()) {
					if (a.getType() == AddressType.CARD && !ba.toString().contains(a.getState())) {
						ba.append(a.getState() + ";");
					} else if (a.getType() == AddressType.SHIPPING && !sa.toString().contains(a.getState())) {
						sa.append(a.getState() + ";");
					}
				}
			}

			billingAddresses.add(ba.length() == 0 ? "none on file" : ba.toString());
			shippingAddresses.add(sa.length() == 0 ? "none on file" : sa.toString());
		}

		// these hash tables hold all the data and the header
		Hashtable<String, ArrayList<String>> output = new Hashtable<String, ArrayList<String>>();
		output.put("User ID:", userIDs);
		output.put("Ships to:", shippingAddresses);
		output.put("Bills to:", billingAddresses);

		addToReportByColumn(output);
		print(fileName.toString());
		return file.getAbsolutePath();
	}

	/**
	 * This gets all of the relevant data from the database.
	 * 
	 * @throws SQLException
	 *             if an error occurs connecting to the database
	 */
	public void refresh() throws SQLException {
		allOrders = orderDAO.getAll();
		allAddresses = addressDAO.getAll();
		allCards = cardDAO.getAll();
		allUsers = userDAO.getAll();
	}

	/**
	 * This adds a row to the report.
	 * 
	 * @param data
	 *            the data in the row to add
	 */
	private void addToReportByRow(ArrayList<String> data) {
		for (String s : data) {
			report.append(s);
			report.append(',');
		}
		report.deleteCharAt(report.lastIndexOf(","));
		report.append('\n');
	}

	/**
	 * This adds a column to the report.
	 * 
	 * @param column
	 *            the data (including header) to add to the column
	 */
	private void addToReportByColumn(Hashtable<String, ArrayList<String>> column) {
		ArrayList<String> header = new ArrayList<>();
		header.addAll(column.keySet());
		addToReportByRow(header);

		for (int i = 0; i < column.get(header.get(0)).size(); i++) {
			ArrayList<String> temp = new ArrayList<>();
			for (String h : header) {
				temp.add(column.get(h).get(i));
			}
			addToReportByRow(temp);
			temp.clear();
		}

	}

	/**
	 * This inserts an empty row into the report.
	 */
	private void insertGap() {
		report.append('\n');
	}

	/**
	 * This writes the file to the disk.
	 * 
	 * @param fileName
	 *            the name to give the file
	 * @throws FileNotFoundException
	 *             if an error occurs writing the file
	 */
	private void print(String fileName) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(fileName));
		pw.print(report.toString());
		pw.close();
		report.delete(0, report.length());
	}

}
