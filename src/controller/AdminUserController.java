package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.validator.routines.EmailValidator;

import model.User;
import model.UserDAO;
import util.TableBuilder;

/**
 * this controller lets admins
 * create, read, update, and delete
 * address data.
 * @author jdowd
 *
 */
public class AdminUserController {
	// ---------------------Out messages -------------------------
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();

	private User currentUser;
	
	private EmailValidator validator = EmailValidator.getInstance();
	
	// DAO
	private UserDAO userDAO;

	// Commands
	private static final String HELP = "help";
	private Options helpOptions;

	private static final String ADD = "add";
	private Options addOptions;

	private static final String UPDATE = "update";
	private Options updateOptions;

	private static final String GET = "get";
	private Options getOptions;

	private static final String GET_ALL = "getall";
	private Options getAllOptions;

	private static final String DELETE = "delete";
	private static final String DEL = "del";
	private Options deleteOptions;
	
	private Options adminOptions;

	// Options
	private static final String ID = "id";
	private static final String PASSWORD = "p";
	private static final String EMAIL = "e";
	private static final String FIRST_NAME = "f";
	private static final String LAST_NAME = "l";
	private static final String ADMIN = "a";
	private static final String HOME_PHONE = "h";
	private static final String CELL_PHONE = "c";
	private static final String VERBOSE = "v";
	private static final String REVOKE = "r";
	private static final String AUTHORIZE = "a";

	private static final String PASSWORD_LONG = "password";
	private static final String EMAIL_LONG = "email";
	private static final String FIRST_NAME_LONG = "first";
	private static final String LAST_NAME_LONG = "last";
	private static final String ADMIN_LONG = "admin";
	private static final String HOME_PHONE_LONG = "home";
	private static final String CELL_PHONE_LONG = "cell";
	private static final String VERBOSE_LONG = "verbose";
	private static final String REVOKE_LONG = "revoke";
	private static final String AUTHORIZE_LONG = "authorize";

	private static final String ADDRESS = "address";
	private static final String ADDRESS_LINK = "-" + ADDRESS;
	
	private static final String CARD = "card";
	private static final String CARD_LINK = "-" + ADDRESS;
	
	private static final String ADDRESS_ID = "a";
	private static final String ADDRESS_ID_LONG = "addressID";
	
	private static final String CARD_ID = "c";
	private static final String CARD_ID_LONG = "cardID";
	
	private static final String USER = "user";
	// Numbers
	private static final int UNLIMITED_ARGS = -2;
	private static final String CHECK_ARGS_GOOD = "good";

	private static final int MAX_RANGED_ARGS = 2;


	// SQL Exception numbers
	private static final String KNOWN_SQL_ERROR_PREFIX = "45";

	// Messages
	private static final String SQL_EXCEPTION_MESSAGE = "A connection error occurred. Check your internet connection and try again.";

	// Help formatter
	private static final HelpFormatter helpFormatter = new HelpFormatter();
	
	private AdminAddressController addressController;
	private AdminCardController cardController;


	/**
	 * this controller sets up the connection, options
	 * and commands.
	 * @param pConnection the connection to the database
	 */
	public AdminUserController(Connection c, User myUser) {
		
		addressController = new AdminAddressController(c);
		cardController = new AdminCardController(c);
		
		// current user instance (for permissions)
		currentUser = myUser;
		

		// DAO
		try {
			userDAO = new UserDAO(c);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
		}

		// define options
		Option requiredId = new Option(ID, true, "the id of the user");
		requiredId.setRequired(true);
		requiredId.setArgName("user id number");

		Option requiredRangedId = new Option(ID, true, "the id of the user");
		requiredRangedId.setRequired(true);
		requiredRangedId.setArgName("user id number");
		requiredRangedId.setArgs(MAX_RANGED_ARGS);
		requiredRangedId.setOptionalArg(true);

		Option optionalPassword = new Option(PASSWORD, PASSWORD_LONG, true, "the password of the user");
		optionalPassword.setArgs(UNLIMITED_ARGS);
		optionalPassword.setArgName("password");

		Option requiredPassword = new Option(PASSWORD, PASSWORD_LONG, true, "the password of the user");
		requiredPassword.setArgs(UNLIMITED_ARGS);
		requiredPassword.setRequired(true);
		requiredPassword.setArgName("password");

		Option optionalEmail = new Option(EMAIL, EMAIL_LONG, true, "the email of the user");
		optionalEmail.setArgs(UNLIMITED_ARGS);
		optionalEmail.setArgName("email");

		Option optionalNoArgEmail = new Option(EMAIL, EMAIL_LONG, false, "the email of the user");

		Option requiredEmail = new Option(EMAIL, EMAIL_LONG, true, "the email of the user");
		requiredEmail.setArgs(UNLIMITED_ARGS);
		requiredEmail.setRequired(true);
		requiredEmail.setArgName("email");

		Option optionalFirstName = new Option(FIRST_NAME, FIRST_NAME_LONG, true, "the firstName of the user");
		optionalFirstName.setArgName("first name");

		Option optionalNoArgFirstName = new Option(FIRST_NAME, FIRST_NAME_LONG, false, "the first Name of the user");

		Option requiredFirstName = new Option(FIRST_NAME, FIRST_NAME_LONG, true, "the first Name of the user");
		requiredFirstName.setRequired(true);
		requiredFirstName.setArgName("first name");

		Option optionalLastName = new Option(LAST_NAME, LAST_NAME_LONG, true, "the last Name of the user");
		optionalLastName.setArgName("last name");

		Option optionalNoArgLastName = new Option(LAST_NAME, LAST_NAME_LONG, false, "the last Name of the user");

		Option requiredLastName = new Option(LAST_NAME, LAST_NAME_LONG, true, "the last Name of the user");
		requiredLastName.setRequired(true);
		requiredLastName.setArgName("last name");

		Option optionalAdmin = new Option(ADMIN, ADMIN_LONG, true, "whether the user is admin or not");
		optionalAdmin.setArgName("true or false");
		
		Option optionalNoArgAdmin = new Option(ADMIN, ADMIN_LONG, true, "whether the user is admin or not");
		
		Option optionalRevoke = new Option(REVOKE, REVOKE_LONG, true, "the last Name of the user");
		optionalRevoke.setArgName("user ID");
		
		Option optionalAuthorize = new Option(AUTHORIZE, AUTHORIZE_LONG, true, "the last Name of the user");
		optionalAuthorize.setArgName("user ID");
		
		Option optionalHomePhone = new Option(HOME_PHONE, HOME_PHONE_LONG, true, "the home phone number");
		optionalHomePhone.setRequired(false);
		optionalHomePhone.setArgName("home phone number");
		
		Option optionalNoArgHomePhone = new Option(HOME_PHONE, HOME_PHONE_LONG, true, "the home phone number");
		optionalNoArgHomePhone.setRequired(false);
		
		Option optionalCellPhone = new Option(CELL_PHONE, CELL_PHONE_LONG, true, "the home phone number");
		optionalCellPhone.setRequired(false);
		optionalCellPhone.setArgName("cell phone number");
		
		Option optionalNoArgCellPhone = new Option(CELL_PHONE, CELL_PHONE_LONG, true, "the home phone number");
		optionalNoArgCellPhone.setRequired(false);
		
		Option verbose = new Option(VERBOSE, VERBOSE_LONG, false, "return a confirmation statement");

		Option address = new Option(ADDRESS, false, "adds this address as the shipping address");
		address.setRequired(false);

		Option addressID = new Option(ADDRESS_ID, ADDRESS_ID_LONG, true, "the address ID of the shipping address");
		addressID.setRequired(false);
		
		Option card = new Option(CARD, false, "adds this card as a user's credit card");
		card.setRequired(false);
		
		Option cardID = new Option(CARD_ID, CARD_ID_LONG, true, "the card ID of a user's credit card");
		addressID.setRequired(false);
		
		Option add = new Option(ADD, false, "options for add");
		Option get = new Option(GET, false, "options for get");
		Option getAll = new Option(GET_ALL, false, "options for getall");
		Option update = new Option(UPDATE , false, "options for update");
		Option delete = new Option(DELETE, false, "options for delete");
		Option admin = new Option(ADMIN_LONG, false, "options for admin");

		
		OptionGroup requiredAddressInfo = new OptionGroup();
		requiredAddressInfo.addOption(address);
		requiredAddressInfo.addOption(addressID);
		requiredAddressInfo.setRequired(true);

		OptionGroup optionalAddressInfo = new OptionGroup();
		optionalAddressInfo.addOption(address);
		optionalAddressInfo.addOption(addressID);
		optionalAddressInfo.setRequired(false);
		
		OptionGroup requiredCardInfo = new OptionGroup();
		requiredCardInfo.addOption(card);
		requiredCardInfo.addOption(cardID);
		requiredCardInfo.setRequired(true);

		OptionGroup optionalCardInfo = new OptionGroup();
		optionalCardInfo.addOption(card);
		optionalCardInfo.addOption(cardID);
		optionalCardInfo.setRequired(false);
		
		
		// Add options for add
		addOptions = new Options();
		addOptions.addOption(requiredEmail);
		addOptions.addOption(requiredPassword);
		addOptions.addOption(requiredFirstName);
		addOptions.addOption(requiredLastName);
		addOptions.addOption(optionalCellPhone);
		addOptions.addOption(optionalHomePhone);
		addOptions.addOption(verbose);

		// Add options for update
		updateOptions = new Options();
		updateOptions.addOption(requiredId);
		updateOptions.addOption(optionalPassword);
		updateOptions.addOption(optionalEmail);
		updateOptions.addOption(optionalFirstName);
		updateOptions.addOption(optionalLastName);
		updateOptions.addOption(optionalCellPhone);
		updateOptions.addOption(optionalHomePhone);
		updateOptions.addOption(verbose);

		// Add options for get
		getOptions = new Options();
		getOptions.addOption(requiredId);
		getOptions.addOption(optionalNoArgLastName);
		getOptions.addOption(optionalNoArgAdmin);
		getOptions.addOption(optionalNoArgFirstName);
		getOptions.addOption(optionalNoArgEmail);
		getOptions.addOption(optionalNoArgCellPhone);
		getOptions.addOption(optionalNoArgHomePhone);
		getOptions.addOption(verbose);

		// Add options for getall
		getAllOptions = new Options();
		getAllOptions.addOption(optionalNoArgLastName);
		getAllOptions.addOption(optionalNoArgAdmin);
		getAllOptions.addOption(optionalNoArgFirstName);
		getAllOptions.addOption(optionalNoArgEmail);
		getAllOptions.addOption(optionalNoArgCellPhone);
		getAllOptions.addOption(optionalNoArgHomePhone);
		getAllOptions.addOption(verbose);

		// Add options for delete and del
		deleteOptions = new Options();
		deleteOptions.addOption(requiredId);
		deleteOptions.addOption(verbose);

		// Add options for admin
		adminOptions = new Options();
		adminOptions.addOption(optionalRevoke);
		adminOptions.addOption(optionalAuthorize);
		
		// Add options for help
		OptionGroup helpOptionGroup = new OptionGroup();
		helpOptionGroup.addOption(add);
		helpOptionGroup.addOption(get);
		helpOptionGroup.addOption(getAll);
		helpOptionGroup.addOption(update);
		helpOptionGroup.addOption(delete);
		helpOptionGroup.addOption(admin);
		helpOptions = new Options();
		helpOptions.addOptionGroup(helpOptionGroup);
	}

	/**
	 * This receives the input, processes it, and returns a list of generated
	 * TableBuilders
	 * 
	 * @param cmd
	 *            the command to execute
	 * @param args
	 *            the options arguments to apply to the command
	 * @param myUser 
	 * @return the generated output
	 */
	public List<TableBuilder> parseCommand(String cmd, String[] args) {
		//this passes arguments to address controller
		String[] addressArgs = null;
		if (ArrayUtils.contains(args, ADDRESS_LINK)) {
			addressArgs = ArrayUtils.subarray(args, ArrayUtils.indexOf(args, ADDRESS_LINK) + 1, args.length);

			args = ArrayUtils.subarray(args, 0, ArrayUtils.indexOf(args, ADDRESS_LINK) + 1);
		}
		
		//this passes arguments to card controller
		String[] cardArgs = null;
		if (ArrayUtils.contains(args, CARD_LINK)) {
			cardArgs = ArrayUtils.subarray(args, ArrayUtils.indexOf(args, CARD_LINK) + 1, args.length);

			args = ArrayUtils.subarray(args, 0, ArrayUtils.indexOf(args, CARD_LINK) + 1);
		}
		
		// This parses the options and arguments into a CommandLine
		DefaultParser cmdlp = new DefaultParser();

		// This stores the parsed options and arguments
		CommandLine cmdl = null;

		// This will tell us whether any options in args were duplicated
		String noDuplicates = checkForDuplicates(args);

		if (noDuplicates.equals(CHECK_ARGS_GOOD)) {
			// We switch the command
			switch (cmd) {
			case HELP:
				try {
					cmdl = cmdlp.parse(helpOptions, args);
				} catch (ParseException e) {
					System.out.println(e.getMessage());
				}
				if (cmdl != null) {
					if (cmdl.hasOption(ADD)) {
						helpFormatter.printHelp(ADD, addOptions);
					} else if (cmdl.hasOption(GET)) {
						helpFormatter.printHelp(GET, getOptions);
					} else if (cmdl.hasOption("get_all")) {
						helpFormatter.printHelp(GET_ALL, getAllOptions);
					} else if (cmdl.hasOption(UPDATE)) {
						helpFormatter.printHelp(UPDATE, updateOptions);
					} else if (cmdl.hasOption(DELETE)) {
						helpFormatter.printHelp(DELETE, deleteOptions);
					} else if (cmdl.hasOption(ADMIN_LONG)) {
						helpFormatter.printHelp(ADMIN_LONG, adminOptions);
					} else {
						helpFormatter.printHelp(ADD, addOptions);
						helpFormatter.printHelp(GET, getOptions);
						helpFormatter.printHelp(GET_ALL, getAllOptions);
						helpFormatter.printHelp(UPDATE, updateOptions);
						helpFormatter.printHelp(DELETE, deleteOptions);
						helpFormatter.printHelp(ADMIN_LONG, adminOptions);
						helpFormatter.printHelp(HELP, helpOptions);
					}
				}
				break;
			case ADD:
				try {
					cmdl = cmdlp.parse(addOptions, args);
				} catch (ParseException e) {
					// If the options can't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to add the user the if the options parsed
				if (cmdl != null) {
					addUser(cmdl);
				}
				break;
			case UPDATE:
				try {
					cmdl = cmdlp.parse(updateOptions, args);
				} catch (ParseException e) {
					// If the options can't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to update the user if the options parsed
				if (cmdl != null) {
					updateUser(cmdl);
				}
				break;
			case GET:
				try {
					cmdl = cmdlp.parse(getOptions, args);
				} catch (ParseException e) {
					// If the options couldn't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to get the user if the options parsed
				if (cmdl != null) {
					getUser(cmdl);
				}
				break;
			case GET_ALL:
				try {
					cmdl = cmdlp.parse(getAllOptions, args);
				} catch (ParseException e) {
					// If the options couldn't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to get all the users if the options parsed
				if (cmdl != null) {
					getAllUsers(cmdl);
				}
				break;
			case DEL:
			case DELETE:
				try {
					cmdl = cmdlp.parse(deleteOptions, args);
				} catch (ParseException e) {
					// If the options couldn't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to delete the user if the options parsed
				if (cmdl != null) {
					deleteUser(cmdl);
				}
				break;
			case USER:
				outMessages.add(new TableBuilder("Welcome to the user controller!"));
				outMessages.add(new TableBuilder("Please enter <help> to see valid commands"));
				break;
			case ADMIN_LONG:
				try {
					cmdl = cmdlp.parse(adminOptions, args);
				} catch (ParseException e) {
					// If the options couldn't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to delete the user if the options parsed
				if (cmdl != null) {
					adminUser(cmdl);
				}
				break;
			default:
				// notify the user that the command is invalid
				outMessages.add(new TableBuilder(
						"Invalid command:" + " '" + cmd + "'" + ", enter <help> to see valid commands"));
			}
		} else {
			// We tell the user which options were duplicated
			outMessages.add(new TableBuilder(noDuplicates));
		}
		
		if (addressArgs != null) {
			outMessages.addAll(addressController.parseCommand(cmd, addressArgs));
		}
		if (cardArgs != null) {
			outMessages.addAll(cardController.parseCommand(cmd, cardArgs));
		}
		return outMessages;
	}

	/**
	 * allows for the granting and revoking
	 * of administrative privileges.
	 * @param cl the current command line
	 */
	private void adminUser(CommandLine cl) {
		if (cl.hasOption(REVOKE)) {
			try {
				userDAO.revokeUserAdmin(Integer.parseInt(cl.getOptionValue(REVOKE)), currentUser);
				outMessages.add(new TableBuilder(
						"user #" + Integer.parseInt(cl.getOptionValue(REVOKE)) + " no longer has admin privileges"));
			} catch (NumberFormatException e) {
				outMessages.add(new TableBuilder(
						"Invalid argument, user ID must be numeric"));
			} catch (SQLException e) {
				if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
					outMessages.add(new TableBuilder(e.getMessage()));
				} else {
					outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
							+ e.getSQLState() + " please notify your database administrator."));
				}
			}
		} else if (cl.hasOption(AUTHORIZE)) {
			try {
				userDAO.setUserAdmin(Integer.parseInt(cl.getOptionValue(AUTHORIZE)));
				outMessages.add(new TableBuilder(
						"user #" + Integer.parseInt(cl.getOptionValue(AUTHORIZE)) + " has been granted admin privileges"));
			} catch (NumberFormatException e) {
				outMessages.add(new TableBuilder(
						"Invalid argument, user ID must be numeric"));
			} catch (SQLException e) {
				if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
					outMessages.add(new TableBuilder(e.getMessage()));
				} else {
					outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
							+ e.getSQLState() + " please notify your database administrator."));
				}
			}
		} else {
			outMessages.add(new TableBuilder(
					"no valid command, enter <help> to see valid commands"));
		}
	}

	/**
	 * This adds a user to the database
	 * 
	 * @param cl
	 *            a CommandLine with the options and arguments parsed
	 */
	private void addUser(CommandLine cl) {	
		// We will put the user's information in this User and then add it to
		// the database
		User user = new User();
		StringBuilder value = new StringBuilder();
		String[] values;
		
		// We get the password from the CommandLine. We use a StingBuilder
		// because the password could include several arguments
		values = cl.getOptionValues(PASSWORD);
		for (int i = 0; i < values.length; i++) {
			value.append(values[i]);
			//if the is still more to go add a space
			if ((i + 1) < values.length) {
				value.append(" ");
			}
		}
		
		user.setUserPassword(value.toString());
		value.delete(0, value.length());

	
		
		user.setEmailAddress(cl.getOptionValue(EMAIL));

		user.setFirstName(cl.getOptionValue(FIRST_NAME));

		user.setLastName(cl.getOptionValue(LAST_NAME));
		
		if (cl.hasOption(HOME_PHONE)) {
			//get rid of anything extra added to the phone number
			user.setHomePhone(cl.getOptionValue(HOME_PHONE).replaceAll("[^\\d]", ""));
		}
		if (cl.hasOption(CELL_PHONE)) {
			//get rid of anything extra added to the phone number
			user.setCellPhone(cl.getOptionValue(CELL_PHONE).replaceAll("[^\\d]", ""));
		}
		
		//error for bad email
		if (!validator.isValid(user.getEmailAddress())) {
			outMessages.add(new TableBuilder("invalid email address format, user creation canceled"));
		
		//error for bad number
		} else if (cl.hasOption(HOME_PHONE) && user.getHomePhone().length() != 10) {
			outMessages.add(new TableBuilder("invalid home phone number format (enter 10 digits), user creation canceled"));
		
		//error for bad number
		} else if (cl.hasOption(CELL_PHONE) && user.getCellPhone().length() != 10) {
			outMessages.add(new TableBuilder("invalid cell phone number format (enter 10 digits), user creation canceled"));
		
		//runs if stuff checks out
		} else {
			// We try to add the User to the database
			try {
				userDAO.create(user);
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
	
			// If the user gave the verbose options, we give them a confirmation.
			if (cl.hasOption(VERBOSE)) {
				outMessages.add(new TableBuilder("Created user: " + user.getUserID()));
			}
		}
	}

	/**
	 * This updates a user already in the database
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	private void updateUser(CommandLine cl) {
		// This will hold the information the user provides and use the
		// information from the database if the user doesn't want to change it
		User user = null;

		//hold multiple agrument values
		StringBuilder value = new StringBuilder();
		String[] values;
		
		try {
			user = userDAO.get(Integer.parseInt(cl.getOptionValue(ID)));
		} catch (SQLException e) {
			if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
				// If it's a SQLException we expected, we let the user know what
				// they did wrong
				outMessages.add(new TableBuilder(e.getMessage()));
			} else {
				// Otherwise, we give the user the exception details and tell
				// them to contact us
				outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
						+ e.getSQLState() + " please notify your database administrator."));
			}
		} catch (NumberFormatException e) {
			// We let the user know they need to provide an integer for the user
			// ID
			outMessages.add(new TableBuilder("ID must be an integer"));
		}

		// If we connected to the database and the user was in the database...
		if (user != null) {
			// We go through all the options and if the user specified them, we
			// add the information to our user
			if (cl.hasOption(PASSWORD)) {
				values = cl.getOptionValues(PASSWORD);
				for (int i = 0; i < values.length; i++) {
					value.append(values[i]);
					//if the is still more to go add a space
					if ((i + 1) < values.length) {
						value.append(" ");
					}
				}
				
				user.setUserPassword(value.toString());
				value.delete(0, value.length());
			}
			if (cl.hasOption(EMAIL)) {
				if (!cl.getOptionValue(EMAIL).equals(user.getEmailAddress())) {
					user.setEmailAddress(cl.getOptionValue(EMAIL));
				} else {
					outMessages.add(new TableBuilder("The email you entered matched the email in the database"));
				}
			}
			if (cl.hasOption(FIRST_NAME)) {
				if (!cl.getOptionValue(FIRST_NAME).equals(String.valueOf(user.getFirstName()))) {
					user.setFirstName(cl.getOptionValue(FIRST_NAME));
				} else {
					outMessages
							.add(new TableBuilder("The first name you entered matched the first name in the database"));
				}
			}
			if (cl.hasOption(LAST_NAME)) {
				if (!cl.getOptionValue(LAST_NAME).equals(user.getLastName())) {
					user.setLastName(cl.getOptionValue(LAST_NAME));
				} else {
					outMessages.add(new TableBuilder("The last name you entered matched the last name in the database"));
				}
			}

			if (cl.hasOption(HOME_PHONE)) {
				//get rid of anything extra added to the phone number
				user.setHomePhone(cl.getOptionValue(HOME_PHONE).replaceAll("[^\\d]", ""));
			}
			if (cl.hasOption(CELL_PHONE)) {
				//get rid of anything extra added to the phone number
				user.setCellPhone(cl.getOptionValue(CELL_PHONE).replaceAll("[^\\d]", ""));
			}
			
			// Now we try to update the information
			boolean didUpdate = false;
			//error for bad email
			if (cl.hasOption(EMAIL) && !validator.isValid(user.getEmailAddress())) {
				outMessages.add(new TableBuilder("invalid email address format, user update canceled"));
			
			//error for bad number
			} else if (cl.hasOption(HOME_PHONE) && user.getHomePhone().length() != 10) {
				outMessages.add(new TableBuilder("invalid home phone number format (enter 10 digits), user update canceled"));
			
			//error for bad number
			} else if (cl.hasOption(CELL_PHONE) && user.getCellPhone().length() != 10) {
				outMessages.add(new TableBuilder("invalid cell phone number format (enter 10 digits), user update canceled"));
			
			//runs if stuff checks out
			} else {
				try {
					userDAO.update(user);
					didUpdate = true;
				} catch (SQLException e) {
					// If it's an error we expected, we tell the user what they did
					// wrong
					sqlExceptionMessage(e);
				}
	
				// We only show output if we updated the database
				if (didUpdate) {
					outMessages.add(0, new TableBuilder("Successfully updated!"));
					// If the user gave us the verbose option, we show all the
					// information about the user
					if (cl.hasOption(VERBOSE)) {
						TableBuilder table = new TableBuilder();
						table.addColumn("ID: ",  String.valueOf(user.getUserID()));
						table.addColumn("Email: " , user.getEmailAddress());
						table.addColumn("first name: ", user.getFirstName());
						table.addColumn("last name: ", user.getLastName());
						table.addColumn("admin: ", String.valueOf(user.isAdmin()));
						outMessages.add(table);
					} else {
						// Otherwise, we only display it if the user changed it
						for (Option o : cl.getOptions()) {
							if (o.hasArg()) {
								StringBuilder sb = new StringBuilder();
								if (o.hasLongOpt()) {
									sb.append(o.getLongOpt() + ": ");
								} else {
									sb.append(o.getOpt() + ": ");
								}
	
								for (String s : o.getValues()) {
									sb.append(s + " ");
								}
								outMessages.add(new TableBuilder(sb.toString()));
							}
						}
					}
				}
			}
		} 
	}

	/**
	 * This gets a user (or a range of users) from the database by user ID.
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	private void getUser(CommandLine cl) {
		User user = null;
		try {
			user = userDAO.get(Integer.parseInt(cl.getOptionValue(ID)));
		} catch (SQLException e) {
			if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
				outMessages.add(new TableBuilder(e.getMessage()));
			} else {
				outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
						+ e.getSQLState() + " please notify your database administrator."));
			}
		}

		if (user != null) {
			TableBuilder table = new TableBuilder();
			table.addColumn("ID: ",  String.valueOf(user.getUserID()));
			table.addColumn("Email: " , user.getEmailAddress());

			if (cl.hasOption(VERBOSE)) {
				table.addColumn("first name: ", user.getFirstName());
				table.addColumn("last name: ", user.getLastName());
				table.addColumn("admin: ", String.valueOf(user.isAdmin()));
				table.addColumn("cell phone #", user.getCellPhone());
				table.addColumn("home phone #", user.getHomePhone());
			} else {
				if (cl.hasOption(FIRST_NAME)) {
					table.addColumn("first name: ", user.getFirstName());
				}
				if (cl.hasOption(LAST_NAME)) {
					table.addColumn("last name: ", user.getLastName());
				}
				if (cl.hasOption(ADMIN)) {
					table.addColumn("admin: ", String.valueOf(user.isAdmin()));
				}
				if (cl.hasOption(CELL_PHONE)) {
					table.addColumn("cell phone #", user.getCellPhone());
				}
				if (cl.hasOption(HOME_PHONE)) {
					table.addColumn("home phone #", user.getHomePhone());
				}
			}
			outMessages.add(table);
		} 
	}

	/**
	 * This gets all the users from the database and can display some or all of
	 * their attributes based on the options
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	private void getAllUsers(CommandLine cl) {
		List<User> users = null;
		List<String> userIDs = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();
		List<String> firstNames = new ArrayList<String>();
		List<String> lastNames = new ArrayList<String>();
		List<String> admins = new ArrayList<String>();
		List<String> cells = new ArrayList<String>();
		List<String> homes = new ArrayList<String>();
		try {
			users = userDAO.getAll();
		} catch (SQLException e) {
			if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
				outMessages.add(new TableBuilder(e.getMessage()));
			} else {
				outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
						+ e.getSQLState() + " please notify your database administrator."));
			}
		}

		for (User user : users) {
			userIDs.add(String.valueOf(user.getUserID()));
			emails.add(user.getEmailAddress());
			firstNames.add(user.getFirstName());
			lastNames.add(user.getLastName());
			admins.add(String.valueOf(user.isAdmin()));
			cells.add(user.getCellPhone());
			homes.add(user.getHomePhone());
		}
		
		TableBuilder table = new TableBuilder();
		table.addColumn("ID: ",  userIDs);
		table.addColumn("Email: " , emails);

		if (cl.hasOption(VERBOSE)) {
			table.addColumn("first name: ", firstNames);
			table.addColumn("last name: ", lastNames);
			table.addColumn("admin: ", admins);
			table.addColumn("cell phone #", cells);
			table.addColumn("home phone #", homes);
		} else {
			if (cl.hasOption(FIRST_NAME)) {
				table.addColumn("first name: ", firstNames);
			}
			if (cl.hasOption(LAST_NAME)) {
				table.addColumn("last name: ", lastNames);
			}
			if (cl.hasOption(ADMIN)) {
				table.addColumn("admin: ", admins);
			}
			if (cl.hasOption(CELL_PHONE)) {
				table.addColumn("cell phone #", cells);
			}
			if (cl.hasOption(HOME_PHONE)) {
				table.addColumn("home phone #", homes);
			}
		}
		outMessages.add(table);
	}
	
	/**
	 * This deletes a user from the database by user ID and can return a
	 * confirmation a statement based on the options.
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	private void deleteUser(CommandLine cl) {
		User b = null;
		try {
			b = userDAO.get(Integer.parseInt(cl.getOptionValue(ID)));
		} catch (SQLException e) {
			if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
				outMessages.add(new TableBuilder(e.getMessage()));
			} else {
				outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
						+ e.getSQLState() + " please notify your database administrator."));
			}
		}

		if (b != null) {
			try {
				userDAO.safeDelete(b, currentUser);
				if (cl.hasOption(VERBOSE)) {
					outMessages.add(new TableBuilder("Deleted user:"));
					outMessages.add(new TableBuilder("ID: " + b.getUserID()));
					outMessages.add(new TableBuilder("Email: " + b.getEmailAddress()));
				}
			} catch (SQLException e) {
				if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
					outMessages.add(new TableBuilder(e.getMessage()));
				} else {
					outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
							+ e.getSQLState() + " please notify your database administrator."));
				}
			}
	
		} else {
			outMessages.add(new TableBuilder("User with  ID " + cl.getOptionValue(ID) + " not found."));
		}
	}

	/**
	 * This checks an array of Strings for duplicate long and short options.
	 * 
	 * @param pInput
	 *            the input parsed into an array of Strings
	 * @return either a message indicating which options were duplicated or
	 *         indicating no options were duplicated
	 */
	private String checkForDuplicates(String[] pInput) {
		for (String s1 : pInput) {
			for (String s2 : pInput) {
				if (s1 != s2 && s1.startsWith("-") && s1.equals(s2)) {
					return s1 + " cannot be used twice";
				}
			}
		}
		return CHECK_ARGS_GOOD;
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
}
