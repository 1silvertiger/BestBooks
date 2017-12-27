package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import model.Address;
import model.AddressDAO;
import model.AddressType;
import util.TableBuilder;

/**
 * this controller lets admins create, read, update, and delete address data.
 * 
 * @author Dakota
 *
 */
public class AdminAddressController extends AdminController<Address> {

	/**
	 * the list of messages passed back to main.
	 */
	private List<TableBuilder> outMessages = new ArrayList<>();

	/** the address DAO. */
	private AddressDAO dao;

	/** the constant for the id command. */
	private static final String ID = "id";
	/** the constant for the line 1 command. */
	private static final String LINE1 = "l";
	/** the constant for the line 2 command. */
	private static final String LINE2 = "L";
	/** the constant for the city command. */
	private static final String CITY = "c";
	/** the constant for the state command. */
	private static final String STATE = "s";
	/** the constant for the zip command. */
	private static final String ZIP = "z";
	/** the constant for the owner command. */
	private static final String OWNER = "o";

	/** the constant for the long line 1 command. */
	private static final String LINE1_LONG = "line1";
	/** the constant for the long line 2 command. */
	private static final String LINE2_LONG = "line2";
	/** the constant for the long city command. */
	private static final String CITY_LONG = "city";
	/** the constant for the long state command. */
	private static final String STATE_LONG = "state";
	/** the constant for the long zip command. */
	private static final String ZIP_LONG = "zip";
	/** the constant for the long owner command. */
	private static final String OWNER_LONG = "owner";

	/** the constant for the shipping command. */
	private static final String SHIPPING = "h";
	/** the constant for the billing command. */
	private static final String BILLING = "b";
	/** the constant for the publisher command. */
	private static final String PUBLISHER = "u";

	/** the constant for the long shipping command. */
	private static final String SHIPPING_LONG = "shipping";
	/** the constant for the long billing command. */
	private static final String BILLING_LONG = "billing";
	/** the constant for the long publisher command. */
	private static final String PUBLISHER_LONG = "publisher";
	/** the constant for the address command. */
	private static final String ADDRESS = "address";

	private static final int ZIP_CODE_LENGTH = 5;

	/**
	 * this controller sets up the connection, options and commands.
	 * 
	 * @param pConnection
	 *            the connection to the database
	 */
	public AdminAddressController(final Connection pConnection) {
		// setup the connection and the DAO
		super(pConnection);
		try {
			dao = new AddressDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(e.getMessage()));
		}

		String s = new String();
		Integer i = new Integer(0);

		// create all the options
		Option requiredLine1 = new Option(LINE1, LINE1_LONG, true, "the first line of the address");
		requiredLine1.setRequired(true);
		requiredLine1.setArgs(UNLIMITED_ARGS);
		requiredLine1.setArgName(LINE1_LONG);
		requiredLine1.setType(s.getClass());

		Option requiredLine2 = new Option(LINE2, LINE2_LONG, true, "the first line of the address");
		requiredLine2.setRequired(true);
		requiredLine2.setArgs(UNLIMITED_ARGS);
		requiredLine2.setArgName(LINE2_LONG);
		requiredLine2.setType(s.getClass());

		Option requiredCity = new Option(CITY, CITY_LONG, true, "the city");
		requiredCity.setRequired(true);
		requiredCity.setArgs(UNLIMITED_ARGS);
		requiredCity.setArgName(CITY_LONG);
		requiredCity.setType(s.getClass());

		Option requiredState = new Option(STATE, STATE_LONG, true, "the state");
		requiredState.setRequired(true);
		requiredState.setArgName(STATE_LONG);
		requiredState.setType(s.getClass());

		Option requiredZip = new Option(ZIP, ZIP_LONG, true, "the zip code");
		requiredZip.setRequired(true);
		requiredZip.setArgName(ZIP_LONG);
		requiredZip.setType(s.getClass());

		Option requiredOwner = new Option(OWNER, OWNER_LONG, true, "the user ID of the owner");
		requiredOwner.setRequired(true);
		requiredOwner.setArgName(OWNER_LONG);
		requiredOwner.setType(i.getClass());

		Option requiredId = new Option(ID, true, "the address ID");
		requiredId.setRequired(true);
		requiredId.setArgName(ID);
		requiredId.setType(i.getClass());

		Option optionalId = new Option(ID, true, "the address ID");
		optionalId.setRequired(false);
		optionalId.setArgName(ID);
		optionalId.setType(i.getClass());

		Option optionalLine1 = new Option(LINE1, LINE1_LONG, true, "the first line of the address");
		optionalLine1.setRequired(false);
		optionalLine1.setArgs(UNLIMITED_ARGS);
		optionalLine1.setArgName(LINE1_LONG);
		optionalLine1.setType(s.getClass());

		Option optionalLine2 = new Option(LINE2, LINE2_LONG, false, "the first line of the address");
		optionalLine2.setRequired(false);
		optionalLine2.setOptionalArg(true);
		optionalLine2.setArgs(UNLIMITED_ARGS);
		optionalLine2.setArgName(LINE2_LONG);
		optionalLine2.setType(s.getClass());

		Option optionalCity = new Option(CITY, CITY_LONG, true, "the city");
		optionalCity.setRequired(false);
		optionalCity.setArgs(UNLIMITED_ARGS);
		optionalCity.setArgName(CITY_LONG);
		optionalCity.setType(s.getClass());

		Option optionalState = new Option(STATE, STATE_LONG, true, "the state");
		optionalState.setRequired(false);
		optionalState.setArgName(STATE_LONG);
		optionalState.setType(s.getClass());

		Option optionalZip = new Option(ZIP, ZIP_LONG, true, "the zip code");
		optionalZip.setRequired(false);
		optionalZip.setArgName(ZIP_LONG);
		optionalZip.setType(i.getClass());

		Option optionalOwner = new Option(OWNER, true, "the user or publisher ID of the owner");
		optionalOwner.setRequired(false);
		optionalOwner.setArgName(OWNER_LONG);
		optionalOwner.setType(i.getClass());

		Option shipping = new Option(SHIPPING, SHIPPING_LONG, false, "indicates this is a shipping address");
		Option billing = new Option(BILLING, BILLING_LONG, false, "indicates this is a billing address");
		Option publisher = new Option(PUBLISHER, PUBLISHER_LONG, false, "indicates this is a publisher address");

		OptionGroup requiredType = new OptionGroup();
		requiredType.addOption(publisher);
		requiredType.addOption(billing);
		requiredType.addOption(shipping);
		requiredType.setRequired(true);

		OptionGroup optionalType = new OptionGroup();
		optionalType.addOption(publisher);
		optionalType.addOption(billing);
		optionalType.addOption(shipping);
		optionalType.setRequired(false);

		// add options to their groups
		addOptions.addOption(requiredZip);
		addOptions.addOption(requiredState);
		addOptions.addOption(requiredCity);
		addOptions.addOption(optionalLine2);
		addOptions.addOption(requiredLine1);
		addOptions.addOption(requiredOwner);
		addOptions.addOptionGroup(requiredType);

		getOptions.addOption(requiredId);

		getAllOptions.addOptionGroup(optionalType);

		updateOptions.addOption(requiredId);
		updateOptions.addOption(optionalZip);
		updateOptions.addOption(optionalState);
		updateOptions.addOption(optionalCity);
		updateOptions.addOption(optionalLine2);
		updateOptions.addOption(optionalLine1);

		deleteOptions.addOption(requiredId);

		searchOptions.addOption(optionalZip);
		searchOptions.addOption(optionalState);
		searchOptions.addOption(optionalCity);
		searchOptions.addOption(optionalOwner);
		searchOptions.addOptionGroup(optionalType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.AdminController#parseCommand(java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		DefaultParser parser = new DefaultParser();
		CommandLine cmdl = null;

		if (checkForDuplicates(args).equals(CHECK_ARGS_GOOD)) {
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
				case UPDATE:
					if (checkForDuplicates(args).equals(CHECK_ARGS_GOOD)) {
						try {
							cmdl = parser.parse(updateOptions, args);
						} catch (ParseException e) {
							outMessages.add(new TableBuilder(e.getMessage()));
						}
						if (cmdl != null) {
							update(cmdl);
						}
					} else {
						outMessages.add(new TableBuilder(checkForDuplicates(args)));
					}
					break;
				case DELETE:
					if (checkForDuplicates(args).equals(CHECK_ARGS_GOOD)) {
						try {
							cmdl = parser.parse(deleteOptions, args);
						} catch (ParseException e) {
							outMessages.add(new TableBuilder(e.getMessage()));
						}
						if (cmdl != null) {
							delete(cmdl);
						}
					} else {
						outMessages.add(new TableBuilder(checkForDuplicates(args)));
					}
					break;
				case ADDRESS:
					outMessages.add(new TableBuilder("Welcome to the address section!"));
					outMessages.add(new TableBuilder("Enter <help> to see valid commands"));
					break;
				case SEARCH:
					if (checkForDuplicates(args).equals(CHECK_ARGS_GOOD)) {
						try {
							cmdl = parser.parse(searchOptions, args);
						} catch (ParseException e) {
							outMessages.add(new TableBuilder(e.getMessage()));
						}
						if (cmdl != null) {
							search(cmdl);
						}
					} else {
						outMessages.add(new TableBuilder(checkForDuplicates(args)));
					}
					break;
				default:
					outMessages.add(new TableBuilder(
							"Unrecognized command " + cmd + ". Enter 'help' to see a list of valid commands"));
				}
			} catch (ParseException e) {
				outMessages.add(new TableBuilder(e.getMessage()));
			}
		} else {
			outMessages.add(new TableBuilder(checkForDuplicates(args)));
		}
		return outMessages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.AdminController#add(org.apache.commons.cli.CommandLine)
	 */
	@Override
	final void add(final CommandLine cmdl) {
		boolean cleanInput = true;
		Address a = new Address();

		if (StringUtils.isAlphanumericSpace(cmdl.getOptionValuesAsString(CITY).trim())) {
			a.setCity(StringUtils.capitalize(cmdl.getOptionValuesAsString(CITY).trim()));
		} else {
			cleanInput = false;
			outMessages.add(new TableBuilder("Enter a valid city."));
		}
		if (StringUtils.isAlphanumericSpace(cmdl.getOptionValuesAsString(LINE1).trim())) {
			a.setLine1(cmdl.getOptionValuesAsString(LINE1).trim());
		} else {
			cleanInput = false;
			outMessages.add(new TableBuilder("Enter a valid first line."));
		}

		if (cmdl.hasOption(LINE2)) {
			if (StringUtils.isAlphanumericSpace(cmdl.getOptionValuesAsString(LINE2).trim())) {
				a.setLine2(cmdl.getOptionValuesAsString(LINE2).trim());
			} else {
				cleanInput = false;
				outMessages.add(new TableBuilder("Enter a valid second line."));
			}
		} else {
			a.setLine2(" ");
		}

		if (StringUtils.isNumeric(cmdl.getOptionValue(OWNER).trim())) {
			a.setOwnerID(Integer.parseInt(cmdl.getOptionValue(OWNER).trim()));
		} else {
			cleanInput = false;
			outMessages.add(new TableBuilder("Enter a valid owner ID."));
		}

		if (StringUtils.isAlpha(cmdl.getOptionValue(STATE).trim().toUpperCase())
				&& cmdl.getOptionValue(STATE).trim().toUpperCase().length() == 2) {
			a.setState(cmdl.getOptionValue(STATE).trim().toUpperCase());
		} else {
			cleanInput = false;
			outMessages.add(new TableBuilder("State must be two letters."));
		}

		if (StringUtils.isNumeric(cmdl.getOptionValue(ZIP).trim())
				&& cmdl.getOptionValue(ZIP).trim().length() == ZIP_CODE_LENGTH) {
			a.setZip(cmdl.getOptionValue(ZIP));
		} else {
			cleanInput = false;
			outMessages.add(new TableBuilder("Zip code must be five digits."));
		}

		if (cmdl.hasOption(BILLING)) {
			a.setType(AddressType.CARD);
		} else if (cmdl.hasOption(SHIPPING)) {
			a.setType(AddressType.SHIPPING);
		} else {
			a.setType(AddressType.PUBLISHER);
		}

		if (cleanInput) {
			try {
				dao.create(a);
				outMessages.add(new TableBuilder("Created address with ID " + a.getAddressID()));
				outMessages.add(new TableBuilder("Line 1: " + a.getLine1()));
				if (cmdl.hasOption(LINE2)) {
					outMessages.add(new TableBuilder("Line 2: " + a.getLine2()));
				}
				outMessages.add(new TableBuilder("City: " + a.getCity()));
				outMessages.add(new TableBuilder("State: " + a.getState()));
				outMessages.add(new TableBuilder("Zip code: " + a.getZip()));
				outMessages.add(new TableBuilder(
						StringUtils.capitalize(a.getType().toString().toLowerCase()) + "ID: " + a.getOwnerID()));
				outMessages.add(new TableBuilder("Type: " + a.getType().toString().toLowerCase()));
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
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.AdminController#get(org.apache.commons.cli.CommandLine)
	 */
	@Override
	final void get(final CommandLine cmdl) {
		Address a = null;

		try {
			a = dao.get(Integer.parseInt(cmdl.getOptionValue(ID)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("publisher ID must be an integer"));
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
			}
		}

		if (a != null && a.exists()) {
			outMessages.add(new TableBuilder("Address ID: " + a.getAddressID() + " | "
					+ StringUtils.capitalize(a.getType().toString().toLowerCase()) + " ID: " + a.getOwnerID()
					+ " | Type: " + StringUtils.capitalize(a.getType().toString().toLowerCase())));
			outMessages.add(new TableBuilder(a.getLine1()));
			if (a.getLine2() != null) {
				if (!a.getLine2().equals(" ")) {
					outMessages.add(new TableBuilder(a.getLine2()));
				}
			}
			outMessages.add(new TableBuilder(a.getCity() + ", " + a.getState() + " " + a.getZip()));
		} else if (a != null && !a.exists()) {
			outMessages.add(new TableBuilder("address with ID " + cmdl.getOptionValue(ID) + " not found in database"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * controller.AdminController#getAll(org.apache.commons.cli.CommandLine)
	 */
	@Override
	final void getAll(final CommandLine cmdl) {
		List<Address> addresses = null;
		try {
			addresses = dao.getAll();
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
			}
		}

		if (addresses != null) {
			List<String> addressIDs = new ArrayList<>();
			List<String> ownerIDs = new ArrayList<>();
			List<String> types = new ArrayList<>();
			List<String> line1s = new ArrayList<>();
			List<String> line2s = new ArrayList<>();
			List<String> cities = new ArrayList<>();
			List<String> states = new ArrayList<>();
			List<String> zips = new ArrayList<>();
			for (Address a : addresses) {
				if ((cmdl.hasOption(BILLING) && a.getType() == AddressType.CARD)
						|| (cmdl.hasOption(SHIPPING) && a.getType() == AddressType.SHIPPING)
						|| (cmdl.hasOption(PUBLISHER) && a.getType() == AddressType.PUBLISHER)
						|| cmdl.getOptions().length == 0) {
					addressIDs.add(String.valueOf(a.getAddressID()));
					line1s.add(a.getLine1());
					if (a.getLine2() != null) {
						line2s.add(a.getLine2());
					} else {
						line2s.add(" ");
					}
					cities.add(a.getCity());
					states.add(a.getState());
					zips.add(a.getZip());
					ownerIDs.add(String.valueOf(a.getOwnerID()));
					if (a.getType() == null) {
						System.out.println(a.getAddressID());
					}
					types.add(a.getType().toString().toLowerCase());
				}
			}
			TableBuilder tb = new TableBuilder();
			tb.addColumn("Address ID", addressIDs);
			tb.addColumn("Owner ID", ownerIDs);
			tb.addColumn("Type", types);
			tb.addColumn("Line 1", line1s);
			tb.addColumn("Line 2", line2s);
			tb.addColumn("City", cities);
			tb.addColumn("State", states);
			tb.addColumn("Zip code", zips);
			outMessages.add(tb);
		} else {
			outMessages.add(new TableBuilder("no addresses were returned"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * controller.AdminController#update(org.apache.commons.cli.CommandLine)
	 */
	@Override
	final void update(final CommandLine cmdl) throws ParseException {
		Address a = null;
		try {
			a = dao.get(Integer.parseInt(cmdl.getOptionValue(ID)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("publisher ID must be an integer"));
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
			}
		}
		if (a != null && a.exists()) {
			if (cmdl.hasOption(LINE1)) {
				if (!cmdl.getOptionValuesAsString(LINE1).equals(a.getLine1())) {
					a.setLine1(cmdl.getOptionValuesAsString(LINE1));
				} else {
					outMessages
							.add(new TableBuilder(cmdl.getOptionValuesAsString(LINE1) + " matched line 1 in database"));
				}
			}

			if (cmdl.hasOption(LINE2)) {
				if (cmdl.getOptionValue(LINE2) == null) {
					a.setLine2(" ");
				} else if (!cmdl.getOptionValuesAsString(LINE2).equals(a.getLine2())) {
					a.setLine2(cmdl.getOptionValuesAsString(LINE2));
				} else {
					outMessages
							.add(new TableBuilder(cmdl.getOptionValuesAsString(LINE2) + " matched line 2 in database"));
				}
			}

			if (cmdl.hasOption(CITY)) {
				if (!cmdl.getOptionValuesAsString(CITY).equals(a.getCity())) {
					a.setCity(cmdl.getOptionValuesAsString(CITY));
				} else {
					outMessages.add(new TableBuilder(cmdl.getOptionValuesAsString(CITY) + " matched city in database"));
				}
			}

			if (cmdl.hasOption(STATE)) {
				if (!cmdl.getOptionValues(STATE).equals(a.getState())) {
					a.setState(cmdl.getOptionValue(STATE).toUpperCase());
				} else {
					outMessages.add(new TableBuilder(cmdl.getOptionValue(STATE) + " matched state in database"));
				}
			}

			if (cmdl.hasOption(ZIP)) {
				if (!cmdl.getOptionValue(ZIP).equals(a.getZip())) {
					a.setZip(cmdl.getOptionValue(ZIP));
				} else {
					outMessages.add(new TableBuilder(cmdl.getOptionValue(ZIP) + " matched zip code in database"));
				}
			}

			if (cmdl.hasOption(SHIPPING) || cmdl.hasOption(BILLING) || cmdl.hasOption(PUBLISHER)) {
				outMessages.add(new TableBuilder("cannot change address type"));
			}

			try {
				dao.update(a);
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
				}
			}

			outMessages.add(new TableBuilder("Updated address: " + a.getAddressID()));
			if (cmdl.hasOption(LINE1)) {
				outMessages.add(new TableBuilder("Line 1: " + a.getLine1()));
			}

			if (cmdl.hasOption(LINE2)) {
				outMessages.add(new TableBuilder("Line 2: " + a.getLine2()));
			}

			if (cmdl.hasOption(CITY)) {
				outMessages.add(new TableBuilder("City: " + a.getCity()));
			}

			if (cmdl.hasOption(STATE)) {
				outMessages.add(new TableBuilder("State: " + a.getState()));
			}

			if (cmdl.hasOption(ZIP)) {
				outMessages.add(new TableBuilder("Zip code: " + a.getZip()));
			}
		} else if (a != null && !a.exists()) {
			outMessages.add(new TableBuilder("address with ID #" + cmdl.getOptionValue(ID) + " not found in database"));
		} else {
			outMessages.add(new TableBuilder("fail"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * controller.AdminController#delete(org.apache.commons.cli.CommandLine)
	 */
	@Override
	final void delete(final CommandLine cmdl) {
		Address a = null;
		try {
			a = dao.get(Integer.parseInt(cmdl.getOptionValue(ID)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("publisher ID must be an integer"));
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
			}
		}
		if (a != null) {
			try {
				dao.delete(a);
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
				}
			}
		} else {
			outMessages.add(new TableBuilder("address " + cmdl.getOptionValue(ID) + " not found in database"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * controller.AdminController#search(org.apache.commons.cli.CommandLine)
	 */
	@Override
	final void search(final CommandLine cmdl) throws ParseException {
		List<Address> addresses = null;
		try {
			addresses = dao.getAll();
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
			}
		}
		ArrayList<Address> intersection = new ArrayList<>();
		ArrayList<Address> byType = new ArrayList<>();
		ArrayList<Address> byCity = new ArrayList<>();
		ArrayList<Address> byState = new ArrayList<>();
		ArrayList<Address> byOwner = new ArrayList<>();
		ArrayList<Address> byZip = new ArrayList<>();
		if (addresses != null) {
			for (Address a : addresses) {
				if (cmdl.hasOption(BILLING) && a.getType() == AddressType.CARD) {
					byType.add(a);
				} else if (cmdl.hasOption(SHIPPING) && a.getType() == AddressType.SHIPPING) {
					byType.add(a);
				} else if (cmdl.hasOption(PUBLISHER) && a.getType() == AddressType.PUBLISHER) {
					byType.add(a);
				}
				if (cmdl.hasOption(CITY) && a.getCity().contains(cmdl.getOptionValuesAsString(CITY))) {
					byCity.add(a);
				}
				if (cmdl.hasOption(STATE) && a.getState().equals(cmdl.getOptionValue(STATE))) {
					byState.add(a);
				}
				if (cmdl.hasOption(ZIP) && a.getZip().equals(cmdl.getOptionValue(ZIP))) {
					byZip.add(a);
				}
				if (cmdl.hasOption(OWNER) && a.getOwnerID() == Integer.parseInt(cmdl.getOptionValue(OWNER))) {
					byOwner.add(a);
				}
			}
			intersection = intersectArrayLists(byType, byCity);
			intersection = intersectArrayLists(intersection, byState);
			intersection = intersectArrayLists(intersection, byZip);
			intersection = intersectArrayLists(intersection, byOwner);

			if (intersection.isEmpty()) {
				outMessages.add(new TableBuilder("no addresses match those terms"));
			} else {
				List<String> addressIDs = new ArrayList<>();
				List<String> ownerIDs = new ArrayList<>();
				List<String> types = new ArrayList<>();
				List<String> line1s = new ArrayList<>();
				List<String> line2s = new ArrayList<>();
				List<String> cities = new ArrayList<>();
				List<String> states = new ArrayList<>();
				List<String> zips = new ArrayList<>();
				for (Address a : intersection) {
					addressIDs.add(String.valueOf(a.getAddressID()));
					line1s.add(a.getLine1());
					if (a.getLine2() != null) {
						line2s.add(a.getLine2());
					} else {
						line2s.add(" ");
					}
					cities.add(a.getCity());
					states.add(a.getState());
					zips.add(a.getZip());
					ownerIDs.add(String.valueOf(a.getOwnerID()));
					types.add(a.getType().toString().toLowerCase());
				}
				TableBuilder tb = new TableBuilder();
				tb.addColumn("Address ID", addressIDs);
				tb.addColumn("Owner ID", ownerIDs);
				if (cmdl.getOptions().length == 0) {
					tb.addColumn("Type", types);
				}
				tb.addColumn("Line 1", line1s);
				tb.addColumn("Line 2", line2s);
				tb.addColumn("City", cities);
				tb.addColumn("State", states);
				tb.addColumn("Zip code", zips);
				outMessages.add(tb);
			}
		} else {
			outMessages.add(new TableBuilder("figure out what this is. the array list is null"));
		}
	}
}
