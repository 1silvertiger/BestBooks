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
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;

import model.Address;
import model.CardSecure;
import model.CardSecureDAO;
import util.TableBuilder;

/**
 * this controller lets admins
 * create, read, update, and delete
 * card data.
 * @author Dakota
 *
 */
public class AdminCardController extends AdminController<CardSecure> {

	private CardSecureDAO dao;

	private boolean backToMenu = true;

	private static final String ID = "id";

	private static final String USER_ID = "u";
	private static final String USER_ID_LONG = "userID";
	private static final String NUMBER = "d";
	private static final String NUMBER_LONG = "number";
	private static final String EXPIRATION_DATE = "x";
	private static final String EXPIRATION_DATE_LONG = "expiration";
	private static final String NAME = "n";
	private static final String NAME_LONG = "name";

	private static final String ADDRESS_ID = "a";
	private static final String ADDRESS_ID_LONG = "addressID";

	private static final String ADDRESS = "address";
	private static final String ADDRESS_LINK = "-" + ADDRESS;

	private static final String CCV = "ccv";

	private static final String VERBOSE = "v";
	private static final String VERBOSE_LONG = "verbose";

	private static final int CARD_NUMBER_ARGS = 4;

	private AdminAddressController addressController;

	/**
	 * this controller sets up the connection, options
	 * and commands.
	 * @param pConnection the connection to the database
	 */
	public AdminCardController(final Connection pConnection) {
		super(pConnection);
		try {
			dao = new CardSecureDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(e.getMessage()));
		}

		addressController = new AdminAddressController(c);

		Option requiredID = new Option(ID, true, "the card ID");

		Option requiredUserID = new Option(USER_ID, USER_ID_LONG, true, "the user ID of the owner of this card");
		requiredUserID.setRequired(true);

		Option requiredNumber = new Option(NUMBER, NUMBER_LONG, true, "the credit card number");
		requiredNumber.setRequired(true);
		requiredNumber.setArgs(CARD_NUMBER_ARGS);

		Option requiredExpirationDate = new Option(EXPIRATION_DATE, EXPIRATION_DATE_LONG, true, "the expiration date");
		requiredExpirationDate.setRequired(true);

		Option requiredName = new Option(NAME, NAME_LONG, true, "the card holder's name");
		requiredName.setRequired(true);
		requiredName.setArgs(UNLIMITED_ARGS);

		Option requiredCCV = new Option(CCV, true, "the CCV number on the back of the card");
		requiredCCV.setRequired(true);

		Option optionalNumber = new Option(NUMBER, NUMBER_LONG, true, "the credit card number");
		optionalNumber.setRequired(false);
		optionalNumber.setArgs(CARD_NUMBER_ARGS);

		Option optionalName = new Option(NAME, NAME_LONG, true, "the card holder's name");
		optionalName.setRequired(false);
		optionalName.setArgs(UNLIMITED_ARGS);

		Option optionalExpirationDate = new Option(EXPIRATION_DATE, EXPIRATION_DATE_LONG, true, "the expiration date");
		optionalExpirationDate.setRequired(false);

		Option optionalCCV = new Option(CCV, true, "the CCV number on the back of the card");
		optionalCCV.setRequired(false);

		Option optionalUserID = new Option(USER_ID, USER_ID_LONG, true, "the user ID");
		optionalUserID.setRequired(false);

		Option optionalVerbose = new Option(VERBOSE, VERBOSE_LONG, false, "display more information");

		Option addressID = new Option(ADDRESS_ID, ADDRESS_ID_LONG, true, "the address ID of the billing address");
		addressID.setRequired(false);

		Option address = new Option(ADDRESS, false, "adds this address as the billing address");
		address.setRequired(false);

		OptionGroup requiredAddressInfo = new OptionGroup();
		requiredAddressInfo.addOption(address);
		requiredAddressInfo.addOption(addressID);
		requiredAddressInfo.setRequired(true);

		OptionGroup optionalAddressInfo = new OptionGroup();
		optionalAddressInfo.addOption(address);
		optionalAddressInfo.addOption(addressID);
		optionalAddressInfo.setRequired(false);

		addOptions.addOption(requiredName);
		addOptions.addOption(requiredExpirationDate);
		addOptions.addOption(requiredNumber);
		addOptions.addOption(requiredUserID);
		addOptions.addOption(requiredCCV);
		addOptions.addOptionGroup(requiredAddressInfo);

		getOptions.addOption(requiredID);
		getOptions.addOption(optionalCCV);
		getOptions.addOption(optionalVerbose);

		getAllOptions.addOption(optionalVerbose);

		updateOptions.addOption(requiredID);
		updateOptions.addOption(optionalExpirationDate);
		updateOptions.addOption(optionalName);
		updateOptions.addOption(optionalNumber);
		updateOptions.addOption(optionalCCV);
		updateOptions.addOptionGroup(optionalAddressInfo);
		updateOptions.addOption(optionalVerbose);

		deleteOptions.addOption(requiredID);
		deleteOptions.addOption(optionalVerbose);

		searchOptions.addOption(optionalUserID);
		searchOptions.addOption(optionalVerbose);
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#parseCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	List<TableBuilder> parseCommand(final String cmd, final String[] pArgs) {
		String[] args = pArgs;
		String[] addressArgs = null;
		if (ArrayUtils.contains(args, ADDRESS_LINK)) {
			addressArgs = ArrayUtils.subarray(args, ArrayUtils.indexOf(args, ADDRESS_LINK) + 1, args.length);

			args = ArrayUtils.subarray(args, 0, ArrayUtils.indexOf(args, ADDRESS_LINK) + 1);
		}
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
		case ADDRESS:
			outMessages.addAll(addressController.parseCommand(cmd, args));
			break;
		default:
			if (backToMenu) {
				outMessages.add(new TableBuilder("Welcome to the card controller!"));
				backToMenu = false;
			} else {
				outMessages.add(new TableBuilder(
						"Unrecognized command " + cmd + ". Enter 'help' to see a list of valid commands"));
			}
		}

		if (addressArgs != null) {
			outMessages.addAll(addressController.parseCommand(cmd, addressArgs));
		}

		return outMessages;
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#add(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void add(final CommandLine cmdl) throws NumberFormatException {
		CardSecure c = new CardSecure();
		c.setCardHolderName(cmdl.getOptionValuesAsString(NAME).trim());
		c.setCardNumber(cmdl.getOptionValuesAsString(NUMBER).trim());
		c.setExpirationDate(cmdl.getOptionValue(EXPIRATION_DATE).trim());
		c.setUserID(Integer.parseInt(cmdl.getOptionValue(USER_ID).trim()));
		c.setCvc(cmdl.getOptionValue(CCV));

		if (cmdl.hasOption(ADDRESS_ID)) {
			c.getAddress().setAddressID(Integer.parseInt(cmdl.getOptionValue(ADDRESS_ID)));
		}

		try {
			dao.create(c);
		} catch (SQLException e) {
			boolean known = false;
			for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
				if (e.getSQLState().startsWith(s)) {
					outMessages.add(new TableBuilder(e.getMessage()));
					known = true;
				}
			}
			if (!known) {
				outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
				e.printStackTrace();
			}
		}

		outMessages.add(new TableBuilder("Added new card!"));
		outMessages.add(new TableBuilder("ID: " + c.getCardID()));
		outMessages.add(new TableBuilder("Number: " + c.getCardNumber()));
		outMessages.add(new TableBuilder("Card holder name: " + c.getCardHolderName()));
		outMessages.add(new TableBuilder("Expiration date: " + c.getExpirationDate()));
	}

	@Override
	final
	void get(final CommandLine cmdl) throws ParseException {
		CardSecure c = null;
		try {
			c = dao.get(Integer.parseInt(cmdl.getOptionValue(ID)));
		} catch (SQLException e) {
			boolean known = false;
			for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
				if (e.getSQLState().startsWith(s)) {
					outMessages.add(new TableBuilder(e.getMessage()));
					known = true;
				}
			}
			if (!known) {
				outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
				e.printStackTrace();
			}
		} catch (Exception e) {
			outMessages.add(new TableBuilder("invalid card input: please try again"));
		}
		if (c != null && c.exists()) {
			outMessages.add(new TableBuilder("ID: " + c.getCardID()));
			if (cmdl.hasOption(CCV)) {
				c.setCvc(cmdl.getOptionValue(CCV));
				c.decryptFirstTwelveDigits();
				outMessages.add(new TableBuilder("Card number: " + c.getFirstTwelveDigits() + c.getLastFourDigits()));
			} else {
				outMessages.add(new TableBuilder("Card number: " + c.getCardNumber()));
			}
			outMessages.add(new TableBuilder("Card holder name: " + c.getCardHolderName()));
			outMessages.add(new TableBuilder("Expiration date: " + c.getExpirationDate()));
			outMessages.add(new TableBuilder("User ID: " + c.getUserID()));
			if (cmdl.hasOption(VERBOSE)) {
				Address a = c.getAddress();
				outMessages.add(new TableBuilder("Billing address ID: " + a.getAddressID()));
				outMessages.add(new TableBuilder(String.valueOf(a.getLine1())));
				if (!a.getLine2().equals(" ")) {
					outMessages.add(new TableBuilder(a.getLine2()));
				}
				outMessages.add(new TableBuilder(a.getCity() + ", " + a.getState() + " " + a.getZip()));
			}
		} else {
			outMessages.add(new TableBuilder("Card #" + cmdl.getOptionValue(ID) + " not found in database."));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#getAll(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void getAll(final CommandLine cmdl) throws ParseException {
		List<CardSecure> cards = null;
		try {
			cards = dao.getAll();
		} catch (SQLException e) {
			boolean known = false;
			for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
				if (e.getSQLState().startsWith(s)) {
					outMessages.add(new TableBuilder(e.getMessage()));
					known = true;
				}
			}
			if (!known) {
				outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
				e.printStackTrace();
			}
		}
		List<String> ids = new ArrayList<>();
		List<String> cardNumbers = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<String> dates = new ArrayList<>();
		List<String> userIDs = new ArrayList<>();

		List<String> addressIDs = new ArrayList<>();
		List<String> line1s = new ArrayList<>();
		List<String> line2s = new ArrayList<>();
		List<String> cities = new ArrayList<>();
		List<String> states = new ArrayList<>();
		List<String> zips = new ArrayList<>();
		for (CardSecure c : cards) {
			ids.add(String.valueOf(c.getCardID()));
			cardNumbers.add(c.getCardNumber());
			names.add(c.getCardHolderName());
			dates.add(c.getExpirationDate());
			userIDs.add(String.valueOf(c.getUserID()));
			if (cmdl.hasOption(VERBOSE)) {
				Address a = c.getAddress();
				addressIDs.add(String.valueOf(a.getAddressID()));
				line1s.add(a.getLine1());
				line2s.add(a.getLine2());
				cities.add(a.getCity());
				states.add(a.getState());
				zips.add(a.getZip());
			}
		}
		TableBuilder tb = new TableBuilder();
		tb.addColumn("ID", ids);
		tb.addColumn("Credit card number", cardNumbers);
		tb.addColumn("Name", names);
		tb.addColumn("Expiration date", dates);
		tb.addColumn("User ID", userIDs);
		if (cmdl.hasOption(VERBOSE)) {
			tb.addColumn("Address ID", addressIDs);
			tb.addColumn("Address line 1", line1s);
			tb.addColumn("Address line 2", line2s);
			tb.addColumn("City", cities);
			tb.addColumn("State", states);
			tb.addColumn("Zip code", zips);
		}
		outMessages.add(tb);
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#update(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void update(final CommandLine cmdl) throws ParseException {
		CardSecure c = null;
		try {
			c = dao.get(Integer.parseInt(cmdl.getOptionValue(ID)));
		} catch (SQLException e) {
			boolean known = false;
			for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
				if (e.getSQLState().startsWith(s)) {
					outMessages.add(new TableBuilder(e.getMessage()));
					known = true;
				}
			}
			if (!known) {
				outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
				e.printStackTrace();
			}
		}
		if (c != null && c.exists()) {

			if (cmdl.hasOption(CCV)) {
				c.setCvc(cmdl.getOptionValue(CCV));
				c.decryptFirstTwelveDigits();
			}

			if (cmdl.hasOption(NAME) && !cmdl.getOptionValue(NAME).equals(c.getCardHolderName())) {
				c.setCardHolderName(cmdl.getOptionValuesAsString(NAME).trim());
			} else if (cmdl.hasOption(NAME) && cmdl.getOptionValue(NAME).equals(c.getCardHolderName())) {
				outMessages.add(new TableBuilder(cmdl.getOptionValue(NAME) + " matched name in database"));
			}
			if (cmdl.hasOption(NUMBER) && cmdl.hasOption(CCV)
					&& !cmdl.getOptionValuesAsString(NUMBER).equals(c.getFirstTwelveDigits() + c.getLastFourDigits())) {
				c.setCardNumber(cmdl.getOptionValuesAsString(NUMBER).trim());
				c.encryptFirstTwelveDigits();
			} else if (!cmdl.hasOption(CCV)) {
				outMessages.add(new TableBuilder("to update the credit card number, you must include the CCV number"));
			} else if (cmdl.getOptionValuesAsString(NUMBER).equals(c.getFirstTwelveDigits() + c.getLastFourDigits())) {
				outMessages.add(new TableBuilder(
						cmdl.getOptionValuesAsString(NUMBER) + " matched the credit card number in the database"));
			}
			if (cmdl.hasOption(EXPIRATION_DATE)
					&& !cmdl.getOptionValue(EXPIRATION_DATE).equals(c.getExpirationDate())) {
				c.setExpirationDate(cmdl.getOptionValue(EXPIRATION_DATE).trim());
			} else if (cmdl.hasOption(EXPIRATION_DATE)
					&& cmdl.getOptionValue(EXPIRATION_DATE).equals(c.getExpirationDate())) {
				outMessages.add(new TableBuilder(
						cmdl.getOptionValue(EXPIRATION_DATE) + " matched the expiration date in the database"));
			}

			try {
				dao.update(c);
			} catch (SQLException e) {
				boolean known = false;
				for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
					if (e.getSQLState().startsWith(s)) {
						outMessages.add(new TableBuilder(e.getMessage()));
						known = true;
					}
				}
				if (!known) {
					outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
					e.printStackTrace();
				}
			}

			outMessages.add(new TableBuilder("Updated card."));
			outMessages.add(new TableBuilder("ID: " + c.getCardID()));
			if (!cmdl.hasOption(VERBOSE)) {
				if (cmdl.hasOption(NUMBER)) {
					outMessages.add(new TableBuilder("Number: " + c.getCardNumber()));
				}
				if (cmdl.hasOption(NAME)) {
					outMessages.add(new TableBuilder("Card holder name: " + c.getCardHolderName()));
				}
				if (cmdl.hasOption(EXPIRATION_DATE)) {
					outMessages.add(new TableBuilder("Expiration date: " + c.getExpirationDate()));
				}
			} else {
				outMessages.add(new TableBuilder("Number: " + c.getCardNumber()));
				outMessages.add(new TableBuilder("Card holder name: " + c.getCardHolderName()));
				outMessages.add(new TableBuilder("Expiration date: " + c.getExpirationDate()));
				if (cmdl.hasOption(ADDRESS)) {
					System.out.println("had an address");
					outMessages.add(new TableBuilder("Address (" + "ID: " + c.getAddress().getAddressID() + "): "));
					outMessages.add(new TableBuilder(c.getAddress().getLine1()));
					if (!c.getAddress().getLine2().isEmpty()) {
						outMessages.add(new TableBuilder(c.getAddress().getLine2()));
					}
					outMessages.add(new TableBuilder(c.getAddress().getCity() + ", " + c.getAddress().getState() + " "
							+ c.getAddress().getZip()));
				}
			}
		} else {
			outMessages.add(new TableBuilder("Card with ID #" + cmdl.getOptionValue(ID) + " not found"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#delete(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void delete(final CommandLine cmdl) throws ParseException {
		CardSecure c = null;

		try {
			c = dao.get(Integer.parseInt(cmdl.getOptionValue(ID)));
		} catch (SQLException e) {
			boolean known = false;
			for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
				if (e.getSQLState().startsWith(s)) {
					outMessages.add(new TableBuilder(e.getMessage()));
					known = true;
				}
			}
			if (!known) {
				outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
				e.printStackTrace();
			}
		}

		if (c != null && c.exists()) {
			boolean successful = false;
			try {
				dao.delete(c);
				successful = true;
			} catch (SQLException e) {
				boolean known = false;
				for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
					if (e.getSQLState().startsWith(s)) {
						outMessages.add(new TableBuilder(e.getMessage()));
						known = true;
					}
				}
				if (!known) {
					outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
					e.printStackTrace();
				}
			}

			if (successful) {
				outMessages.add(new TableBuilder("Deleted card #" + c.getCardID()));
				if (cmdl.hasOption(VERBOSE)) {
					outMessages.add(new TableBuilder("CCN: " + c.getCardNumber()));
					outMessages.add(new TableBuilder("Card holder name: " + c.getCardHolderName()));
					outMessages.add(new TableBuilder("Expiration date: " + c.getExpirationDate()));
					outMessages.add(new TableBuilder("User ID: " + c.getUserID()));
					outMessages.add(new TableBuilder("Address (ID " + c.getAddress().getAddressID() + "):"));
					outMessages.add(new TableBuilder(c.getAddress().getLine1()));
					if (!c.getAddress().getLine2().equals(" ")) {
						outMessages.add(new TableBuilder(c.getAddress().getLine2()));
					}
					outMessages.add(new TableBuilder(c.getAddress().getCity() + ", " + c.getAddress().getState() + " "
							+ c.getAddress().getZip()));
				}
			} else {
				outMessages.add(new TableBuilder("Delete failed"));
			}
		} else {
			outMessages.add(new TableBuilder("Card #" + cmdl.getOptionValue(ID) + " not found in database"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#search(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void search(final CommandLine cmdl) throws ParseException {
		List<CardSecure> cards = null;
		try {
			cards = dao.getAll();
		} catch (SQLException e) {
			boolean known = false;
			for (String s : KNOWN_SQL_EXCEPTION_PREFIXES) {
				if (e.getSQLState().startsWith(s)) {
					outMessages.add(new TableBuilder(e.getMessage()));
					known = true;
				}
			}
			if (!known) {
				outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
				e.printStackTrace();
			}
		}
		List<String> ids = new ArrayList<>();
		List<String> cardNumbers = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<String> dates = new ArrayList<>();
		List<String> userIDs = new ArrayList<>();

		List<String> addressIDs = new ArrayList<>();
		List<String> line1s = new ArrayList<>();
		List<String> line2s = new ArrayList<>();
		List<String> cities = new ArrayList<>();
		List<String> states = new ArrayList<>();
		List<String> zips = new ArrayList<>();

		for (CardSecure c : cards) {
			if (String.valueOf(c.getUserID()).equals(cmdl.getOptionValue(USER_ID))) {
				ids.add(String.valueOf(c.getCardID()));
				cardNumbers.add(c.getCardNumber());
				names.add(c.getCardHolderName());
				dates.add(c.getExpirationDate());
				userIDs.add(String.valueOf(c.getUserID()));
				if (cmdl.hasOption(VERBOSE)) {
					Address a = c.getAddress();
					addressIDs.add(String.valueOf(a.getAddressID()));
					line1s.add(a.getLine1());
					line2s.add(a.getLine2());
					cities.add(a.getCity());
					states.add(a.getState());
					zips.add(a.getZip());
				}
			}
		}
		TableBuilder tb = new TableBuilder();
		tb.addColumn("ID", ids);
		tb.addColumn("Credit card number", cardNumbers);
		tb.addColumn("Name", names);
		tb.addColumn("Expiration date", dates);
		tb.addColumn("User ID", userIDs);
		if (cmdl.hasOption(VERBOSE)) {
			tb.addColumn("Address ID", addressIDs);
			tb.addColumn("Address line 1", line1s);
			tb.addColumn("Address line 2", line2s);
			tb.addColumn("City", cities);
			tb.addColumn("State", states);
			tb.addColumn("Zip code", zips);
		}
		outMessages.add(tb);
	}
}
