package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;

import model.Address;
import model.Publisher;
import model.PublisherDAO;
import util.TableBuilder;

/**
 * this controller lets admins
 * create, read, update, and delete
 * publisher data.
 * @author Dakota
 *
 */
public class AdminPublisherController extends AdminController<Publisher> {

	private List<TableBuilder> outMessages = new ArrayList<>();

	private PublisherDAO dao;

	private boolean backToMenu = true;

	private static final String HELP = "help";
	private static final String ADD = "add";
	private static final String GET = "get";
	private static final String GET_ALL = "getall";
	private static final String UPDATE = "update";
	private static final String DELETE = "delete";
	private static final String DEL = "del";

	private static final String ID = "id";
	private static final String NAME = "n";
	private static final String PHONE = "p";
	private static final String SALES_REP = "r";
	private static final String VERBOSE = "v";
	private static final String PRINT = "print";

	private static final String NAME_LONG = "name";
	private static final String PHONE_LONG = "phone";
	private static final String SALES_REP_LONG = "rep";
	private static final String VERBOSE_LONG = "verbose";

	private static final String ADDRESS = "-address";

	private static final HelpFormatter helpFormatter = new HelpFormatter();

	/**
	 * this controller sets up the connection, options
	 * and commands.
	 * @param pConnection the connection to the database
	 */
	public AdminPublisherController(final Connection pC) {
		super(pC);
		try {
			dao = new PublisherDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(e.getMessage()));
		}

		// Required options
		Option requiredID = new Option(ID, true, "the publisher ID");
		requiredID.setRequired(true);
		requiredID.setArgName(ID);

		Option requiredName = new Option(NAME, NAME_LONG, true, "the publisher name");
		requiredName.setRequired(true);
		requiredName.setArgs(UNLIMITED_ARGS);
		requiredName.setArgName(NAME_LONG);

		Option optionalName = new Option(NAME, NAME_LONG, true, "the publisher name");
		optionalName.setRequired(false);
		optionalName.setArgs(UNLIMITED_ARGS);
		optionalName.setArgName(NAME_LONG);

		Option requiredPhone = new Option(PHONE, PHONE_LONG, true, "the contact phone number");
		requiredPhone.setRequired(true);
		requiredPhone.setArgName(PHONE_LONG);

		Option optionalPhone = new Option(PHONE, PHONE_LONG, true, "the contact phone number");
		optionalPhone.setRequired(false);
		optionalPhone.setArgName(PHONE_LONG);

		Option requiredSalesRep = new Option(SALES_REP, SALES_REP_LONG, true, "the sales representative");
		requiredSalesRep.setRequired(true);
		requiredSalesRep.setArgs(UNLIMITED_ARGS);
		requiredSalesRep.setArgName(SALES_REP_LONG);

		Option optionalSalesRep = new Option(SALES_REP, SALES_REP_LONG, true, "the sales representative");
		optionalSalesRep.setRequired(false);
		optionalSalesRep.setArgs(UNLIMITED_ARGS);
		optionalSalesRep.setArgName(SALES_REP_LONG);

		// Optional options
		Option optionalVerbose = new Option(VERBOSE, VERBOSE_LONG, false, "more info");

		Option optionalPrint = new Option(PRINT, false, "generates a report");

		addOptions.addOption(requiredSalesRep);
		addOptions.addOption(requiredPhone);
		addOptions.addOption(requiredName);
		addOptions.addOption(optionalVerbose);

		getOptions.addOption(requiredID);
		getOptions.addOption(optionalVerbose);

		getAllOptions.addOption(optionalVerbose);
		getAllOptions.addOption(optionalPrint);

		updateOptions.addOption(requiredID);
		updateOptions.addOption(optionalName);
		updateOptions.addOption(optionalPhone);
		updateOptions.addOption(optionalSalesRep);

		deleteOptions.addOption(requiredID);
		deleteOptions.addOption(optionalVerbose);

		searchOptions.addOption(requiredName);
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#parseCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	public final List<TableBuilder> parseCommand(final String cmd, final String[] args) {
		DefaultParser parser = new DefaultParser();
		CommandLine cmdl = null;

		switch (cmd) {
		case HELP:
			if (ArrayUtils.contains(args, ADDRESS)) {
				AdminAddressController aac = new AdminAddressController(c);
				String[] addressArgs = ArrayUtils.remove(args, ArrayUtils.indexOf(args, ADDRESS));
				outMessages.addAll(aac.parseCommand(cmd, addressArgs));
			} else {
				try {
					cmdl = parser.parse(helpOptions, args);
				} catch (ParseException e) {
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				if (cmdl != null) {
					if (cmdl.getOptions().length != 0) {
						if (cmdl.hasOption(ADD)) {
							helpFormatter.printHelp(ADD, addOptions);
						}
						if (cmdl.hasOption(GET)) {
							helpFormatter.printHelp(GET, getOptions);
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
						if (cmdl.hasOption(SEARCH)) {
							helpFormatter.printHelp(SEARCH, searchOptions);
						}
					} else {
						helpFormatter.printHelp(ADD, addOptions);
						helpFormatter.printHelp(GET, getOptions);
						helpFormatter.printHelp(GET_ALL, getAllOptions);
						helpFormatter.printHelp(UPDATE, updateOptions);
						helpFormatter.printHelp(DELETE, deleteOptions);
						helpFormatter.printHelp(SEARCH, searchOptions);
					}
				}
			}
			break;
		case ADD:
			if (ArrayUtils.contains(args, ADDRESS)) {
				AdminAddressController aac = new AdminAddressController(c);
				String[] addressArgs = ArrayUtils.remove(args, ArrayUtils.indexOf(args, ADDRESS));
				outMessages.addAll(aac.parseCommand(cmd, addressArgs));
			} else {
				try {
					cmdl = parser.parse(addOptions, args);
					add(cmdl);
				} catch (ParseException e) {
					outMessages.add(new TableBuilder(e.getMessage()));
				}
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
			if (ArrayUtils.contains(args, ADDRESS)) {
				AdminAddressController aac = new AdminAddressController(c);
				String[] addressArgs = ArrayUtils.remove(args, ArrayUtils.indexOf(args, ADDRESS));
				outMessages.addAll(aac.parseCommand(cmd, addressArgs));
			} else {
				try {
					cmdl = parser.parse(updateOptions, args);
					update(cmdl);
				} catch (ParseException e) {
					outMessages.add(new TableBuilder(e.getMessage()));
				}
			}
			break;
		case DEL:
		case DELETE:
			if (ArrayUtils.contains(args, ADDRESS)) {
				AdminAddressController aac = new AdminAddressController(c);
				String[] addressArgs = ArrayUtils.remove(args, ArrayUtils.indexOf(args, ADDRESS));
				outMessages.addAll(aac.parseCommand(cmd, addressArgs));
			} else {
				try {
					cmdl = parser.parse(deleteOptions, args);
					delete(cmdl);
				} catch (ParseException e) {
					outMessages.add(new TableBuilder(e.getMessage()));
				}
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
				outMessages.add(new TableBuilder("Welcome to the publisher controller!"));
				outMessages.add(new TableBuilder("Please enter <help> to see valid commands"));
				backToMenu = false;
			} else {
				outMessages.add(new TableBuilder(
						"Invalid command:" + " '" + cmd + "'" + ", enter <help> to see valid commands"));
				outMessages.add(new TableBuilder("command '" + cmd + "' not recognized"));
			}
		}
		return outMessages;
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#add(org.apache.commons.cli.CommandLine)
	 */
	@Override
	public final void add(final CommandLine cmdl) {
		Publisher p = new Publisher();

		// Make the database check that this is numeric
		p.setPhoneNumber(cmdl.getOptionValue(PHONE).trim());

		StringBuilder sb = new StringBuilder();
		for (String s : cmdl.getOptionValues(NAME)) {
			sb.append(s + " ");
		}
		p.setPublisherName(cmdl.getOptionValuesAsString(NAME).trim());

		sb.delete(0, sb.length());
		for (String s : cmdl.getOptionValues(SALES_REP)) {
			sb.append(s + " ");
		}
		p.setSalesRepName(cmdl.getOptionValuesAsString(SALES_REP).trim());

		try {
			dao.create(p);
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

		if (cmdl.hasOption(VERBOSE)) {
			outMessages.add(new TableBuilder("Added publisher with ID: " + String.valueOf(p.getPublisherID())));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#get(org.apache.commons.cli.CommandLine)
	 */
	@Override
	public final void get(final CommandLine cmdl) {
		Publisher p = null;
		try {
			p = dao.get(Integer.parseInt(cmdl.getOptionValue(ID).trim()));
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
		if (p != null && p.exists()) {
			outMessages.add(new TableBuilder(String.valueOf("ID: " + p.getPublisherID())));
			outMessages.add(new TableBuilder("Name: " + p.getPublisherName()));
			outMessages.add(new TableBuilder("Sales representative: " + p.getSalesRepName()));
			outMessages.add(new TableBuilder("Phone number: " + p.getPhoneNumber()));
			outMessages.add(new TableBuilder(""));
			int i = 0;
			for (Address a : p.getAddresses()) {
				outMessages.add(new TableBuilder("Address #" + String.valueOf(++i) + ":"));
				outMessages.add(new TableBuilder("Address ID: " + String.valueOf(a.getAddressID())));
				outMessages.add(new TableBuilder(a.getLine1()));
				if (a.getLine2() != null) {
					outMessages.add(new TableBuilder(a.getLine2()));
				}
				outMessages.add(new TableBuilder(a.getCity() + ", " + a.getState() + " " + a.getZip()));
				outMessages.add(new TableBuilder(""));
			}
		} else {
			outMessages.add(new TableBuilder("Publisher " + cmdl.getOptionValue(ID).trim() + " not found in database"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#getAll(org.apache.commons.cli.CommandLine)
	 */
	@Override
	public final void getAll(final CommandLine cmdl) {
		List<Publisher> publishers = new ArrayList<>();
		try {
			publishers = dao.getAll();
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
		if (!publishers.isEmpty()) {
			List<String> publisherIDs = new ArrayList<>();
			List<String> names = new ArrayList<>();
			List<String> phoneNumbers = new ArrayList<>();
			List<String> repNames = new ArrayList<>();
			for (Publisher p : publishers) {
				publisherIDs.add(String.valueOf(p.getPublisherID()));
				names.add(p.getPublisherName());
				phoneNumbers.add(p.getPhoneNumber());
				repNames.add(p.getSalesRepName());

			}
			TableBuilder tb = new TableBuilder();
			tb.addColumn("Publisher ID", publisherIDs);
			tb.addColumn("Name", names);
			tb.addColumn("Phone number", phoneNumbers);
			tb.addColumn("Sales rep name", repNames);
			outMessages.add(tb);
		}

	}

	/* (non-Javadoc)
	 * @see controller.AdminController#update(org.apache.commons.cli.CommandLine)
	 */
	@Override
	public final void update(final CommandLine cmdl) {
		Publisher p = null;
		try {
			p = dao.get(Integer.parseInt(cmdl.getOptionValue(ID).trim()));
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

		if (p != null && p.exists()) {
			if (cmdl.hasOption(NAME)) {
				StringBuilder sb = new StringBuilder();
				for (String s : cmdl.getOptionValues(NAME)) {
					sb.append(s + " ");
				}
				p.setPublisherName(cmdl.getOptionValuesAsString(NAME).trim());
			}
			if (cmdl.hasOption(PHONE)) {
				p.setPhoneNumber(cmdl.getOptionValue(PHONE).trim());
			}
			if (cmdl.hasOption(SALES_REP)) {
				StringBuilder sb = new StringBuilder();
				for (String s : cmdl.getOptionValues(SALES_REP)) {
					sb.append(s + " ");
				}
				p.setSalesRepName(cmdl.getOptionValuesAsString(SALES_REP).trim());
			}

			try {
				dao.update(p);
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
			outMessages.add(new TableBuilder("Updated publisher ID:" + cmdl.getOptionValue(ID)));

			try {
				p = dao.get(Integer.parseInt(cmdl.getOptionValue(ID).trim()));
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

			if (cmdl.hasOption(VERBOSE)) {
				outMessages.add(new TableBuilder(p.getPublisherName()));
				outMessages.add(new TableBuilder(p.getPhoneNumber()));
				outMessages.add(new TableBuilder(p.getSalesRepName()));
			} else {
				if (cmdl.hasOption(NAME)) {
					outMessages.add(new TableBuilder(p.getPublisherName()));
				}
				if (cmdl.hasOption(PHONE)) {
					outMessages.add(new TableBuilder(p.getPhoneNumber()));
				}
				if (cmdl.hasOption(SALES_REP)) {
					outMessages.add(new TableBuilder(p.getSalesRepName()));
				}
			}
		} else {
			outMessages.add(new TableBuilder("publisher " + p.getPublisherID() + " not found in database"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#delete(org.apache.commons.cli.CommandLine)
	 */
	@Override
	public final void delete(final CommandLine cmdl) {
		Publisher p = null;
		try {
			p = dao.get(Integer.parseInt(cmdl.getOptionValue(ID)));
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

		if (p != null && p.exists()) {
			try {
				dao.delete(p);
				if (cmdl.hasOption(VERBOSE)) {
					outMessages.add(new TableBuilder("deleted publisher ID " + p.getPublisherID()));
				}
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
			outMessages.add(new TableBuilder("publisher " + cmdl.getOptionValue(ID) + " not found in database"));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#search(org.apache.commons.cli.CommandLine)
	 */
	@Override
	public final void search(final CommandLine cmdl) {
		List<Publisher> publishers = null;

		try {
			publishers = dao.getAll();
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
		if (publishers != null) {
			List<String> names = new ArrayList<>();
			List<String> ids = new ArrayList<>();
			List<String> salesRepNames = new ArrayList<>();
			List<String> phoneNumbers = new ArrayList<>();
			for (Publisher p : publishers) {
				if (p.getPublisherName().toLowerCase().contains(cmdl.getOptionValuesAsString(NAME).toLowerCase())) {
					ids.add(String.valueOf(p.getPublisherID()));
					names.add(p.getPublisherName());
					salesRepNames.add(p.getSalesRepName());
					phoneNumbers.add(p.getPhoneNumber());
				}
			}
			if (!names.isEmpty()) {
				TableBuilder tb = new TableBuilder();
				tb.addColumn("ID number:", ids);
				tb.addColumn("Name:", names);
				tb.addColumn("Sales rep:", salesRepNames);
				tb.addColumn("Phone number:", phoneNumbers);
				outMessages.add(tb);
			} else {
				outMessages.add(new TableBuilder("no publishers match those search criteria"));
			}
		} else {
			outMessages.add(new TableBuilder("some connection issue i think"));
		}
	}
}
