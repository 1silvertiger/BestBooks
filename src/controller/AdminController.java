package controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import util.TableBuilder;

/**
 * this is the abstract admin controller.
 * @author Dakota
 *
 * @param <T> the object used
 */
public abstract class AdminController<T> {

	/**
	 * the list of messages passed back to main.
	 */
	protected List<TableBuilder> outMessages = new ArrayList<>();
	/**
	 * the parser for commands.
	 */
	protected DefaultParser parser = new DefaultParser();
	/**
	 * the current command line.
	 */
	protected CommandLine cmdl = null;

	/** the constant for the help command. */
	protected static final String HELP = "help";
	/** the constant for the add command. */
	protected static final String ADD = "add";
	/** the constant for the update command. */
	protected static final String UPDATE = "update";
	/** the constant for the get command. */
	protected static final String GET = "get";
	/** the constant for the get all command. */
	protected static final String GET_ALL = "getall";
	/** the constant for the delete command. */
	protected static final String DELETE = "delete";
	/** the constant for the del command. */
	protected static final String DEL = "del";
	/** the constant for the search command. */
	protected static final String SEARCH = "search";
	
	/** the constant for the address command. */
	protected static final String ADDRESS  = "address";
	/** the constant for the card command. */
	protected static final String CARD  = "card";
	/** the constant for the book command. */
	protected static final String BOOK  = "book";
	/** the constant for the cart command. */
	protected static final String CART  = "cart";
	/** the constant for the order command. */
	protected static final String ORDER  = "order";
	/** the constant for the publisher command. */
	protected static final String PUBLISHER  = "publisher";
	
	/** the group of options for help. */
	protected Options helpOptions = new Options();
	/** the group of options for add. */
	protected Options addOptions = new Options();
	/** the group of options for update. */
	protected Options updateOptions = new Options();
	/** the group of options for get. */
	protected Options getOptions = new Options();
	/** the group of options for get all. */
	protected Options getAllOptions = new Options();
	/** the group of options for delete. */
	protected Options deleteOptions = new Options();
	/** the group of options for search. */
	protected Options searchOptions = new Options();
	
	/** the constant for making arguments unlimited. */
	protected static final int UNLIMITED_ARGS = -2;
	/** the constant which holds check arguments good. */
	protected static final String CHECK_ARGS_GOOD = "checkArgsGood";

	/** the constant with known sql error prefixes. */
	protected static final String[] KNOWN_SQL_EXCEPTION_PREFIXES = { "45", "23" };
	/** the constant for the issue with the book. */
	protected static final int ISSUE_WITH_BOOK = 0;
	/** the constant for a foreign key violation. */
	protected static final int FOREIGN_KEY_CONSTRAINT_VIOLATION = 1;

	/** the constant for displaying a connection error message. */
	protected static final String SQL_EXCEPTION_MESSAGE = "A connection error occurred. Check your internet connection and try again.";

	/** the formatter for the help menu output. */
	protected static final HelpFormatter helpFormatter = new HelpFormatter();
	
	/**
	 * the connection to the database.
	 */
	protected Connection c;

	
	/**
	 * this constructor sets up the commands 
	 * and options.
	 * @param pConnection the connection to the database
	 */
	public AdminController(final Connection pConnection) {
		//the options
		Option optionalAdd = new Option(ADD, "prints help for add command");
		Option optionalGet = new Option(GET, "prints help for get command");
		Option optionalGetAll = new Option(GET_ALL, "prints help for getall command");
		Option optionalUpdate = new Option(UPDATE, "print help for update command");
		Option optionalDelete = new Option(DELETE, "print help for delete command");

		//add the options to their groups

		
		helpOptions = new Options();
		helpOptions.addOption(optionalAdd);
		helpOptions.addOption(optionalGet);
		helpOptions.addOption(optionalGetAll);
		helpOptions.addOption(optionalUpdate);
		helpOptions.addOption(optionalDelete);

		c = pConnection;
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
	abstract List<TableBuilder> parseCommand(String cmd, String[] args);

	/**
	 * This adds an object to the database.
	 * 
	 * @param cl
	 *            a CommandLine with the options and arguments parsed
	 */
	abstract void add(CommandLine cmdl);

	/**
	 * This gets an object from the database by its primary key.
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	abstract void get(CommandLine cmdl) throws ParseException;

	/**
	 * This gets all the objects from the database
	 *  and can display some or all of
	 * their attributes based on the options.
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	abstract void getAll(CommandLine cmdl) throws ParseException;

	/**
	 * This updates an object already in the database
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	abstract void update(CommandLine cmdl) throws ParseException;

	/**
	 * This deletes an object from the database
	 *  by its primary key ID and can return a
	 * confirmation a statement based on the options.
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	abstract void delete(CommandLine cmdl) throws ParseException;

	/**
	 * This searches the database for objects based on the options.
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	abstract void search(CommandLine cmdl) throws ParseException;

	/**
	 * This checks an array of Strings for duplicate long and short options.
	 * 
	 * @param pInput
	 *            the input parsed into an array of Strings
	 * @return either a message indicating which options were duplicated or
	 *         indicating no options were duplicated
	 */
	protected String checkForDuplicates(final String[] args) {
		for (String s1 : args) {
			for (String s2 : args) {
				if (s1 != s2 && s1.startsWith("-") && s1.equals(s2)) {
					return s1 + " cannot be used twice";
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		for (String s : args) {
			sb.append(s + " ");
		}
		String input = sb.toString();

		if (input.contains(" - ") || input.endsWith(" -")) {
			return "unrecognized character: -";
		}
		return CHECK_ARGS_GOOD;
	}

	/**
	 * This intersects two ArrayLists of objects
	 * 
	 * @param list1
	 *            the first ArrayList to be intersected
	 * @param list2
	 *            the second ArrayList to be intersected
	 * @return an ArrayList of the intersection between list1 and list2
	 */
	protected ArrayList<T> intersectArrayLists(final ArrayList<T> list1, final ArrayList<T> list2) {
		if (!list1.isEmpty() && !list2.isEmpty()) {
			ArrayList<T> intersection = new ArrayList<>();
			for (int i = 0; i < list1.size(); i++) {
				for (int j = 0; j < list2.size(); j++) {
					if (list1.get(i).equals(list2.get(j))) {
						intersection.add(list1.get(i));
					}
				}
			}
			return intersection;
		} else {
			if (list1.isEmpty()) {
				return list2;
			} else {
				return list1;
			}
		}
	}
}
