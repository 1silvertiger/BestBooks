package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import model.InventoryBook;
import model.InventoryBookDAO;
import util.TableBuilder;

/**
 * this controller lets admins
 * create, read, update, and delete
 * book data.
 * @author Dakota
 *
 */
public class AdminBookController extends AdminController<InventoryBook> {
	// ---------------------Out messages -------------------------
	/**
	 * the list of messages passed back to main.
	 */
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();

	// DAO
	private InventoryBookDAO bookDAO;

	//money formatter
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance();
	
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

	private static final String SEARCH = "search";
	private Options searchOptions;

	// Options
	private static final String ID = "id";
	private static final String TITLE = "t";
	private static final String AUTHOR = "a";
	private static final String PUBLISHER = "P";
	private static final String ISBN10 = "i";
	private static final String ISBN13 = "I";
	private static final String ISBN = "isbn";
	private static final String GENRE = "g";
	private static final String PRICE = "p";
	private static final String WEIGHT = "w";
	private static final String VERBOSE = "v";
	private static final String COST = "c";
	private static final String QUANTITY = "q";
	private static final String KEYWORD = "k";

	private static final String TITLE_LONG = "title";
	private static final String AUTHOR_LONG = "author";
	private static final String PUBLISHER_LONG = "publisher";
	private static final String ISBN10_LONG = "isbn10";
	private static final String ISBN13_LONG = "isbn13";
	private static final String GENRE_LONG = "genre";
	private static final String PRICE_LONG = "price";
	private static final String WEIGHT_LONG = "weight";
	private static final String VERBOSE_LONG = "verbose";
	private static final String COST_LONG = "cost";
	private static final String QUANTITY_LONG = "quantity";
	private static final String KEYWORD_LONG = "keyword";

	// Numbers
	private static final int UNLIMITED_ARGS = -2;
	private static final String CHECK_ARGS_GOOD = "good";

	private static final int MAX_RANGED_ARGS = 2;

	private static final int RANGED_ARGUMENT_1 = 0;
	private static final int RANGED_ARGUMENT_2 = 1;

	// SQL Exception numbers
	private static final String KNOWN_SQL_ERROR_PREFIX = "45";

	// Messages
	private static final String SQL_EXCEPTION_MESSAGE = "A connection error occurred. Check your internet connection and try again.";

	// Help formatter
	private static final HelpFormatter helpFormatter = new HelpFormatter();

	/**
	 * this controller sets up the connection, options
	 * and commands.
	 * @param pConnection the connection to the database
	 */
	public AdminBookController(final Connection pConnection) {
		super(pConnection);
		// DAO
		try {
			bookDAO = new InventoryBookDAO(pConnection);
		} catch (SQLException e) {
			outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
		}

		// define options
		Option requiredId = new Option(ID, true, "the id of the book");
		requiredId.setRequired(true);
		requiredId.setArgName("book ID");

		Option requiredRangedId = new Option(ID, true, "the id of the book");
		requiredRangedId.setRequired(true);
		requiredRangedId.setArgName("BOOK ID");
		requiredRangedId.setArgs(MAX_RANGED_ARGS);
		requiredRangedId.setOptionalArg(true);

		Option optionalTitle = new Option(TITLE, TITLE_LONG, true, "the title of the book");
		optionalTitle.setArgs(UNLIMITED_ARGS);
		optionalTitle.setArgName("TITLE");

		Option requiredTitle = new Option(TITLE, TITLE_LONG, true, "the title of the book");
		requiredTitle.setArgs(UNLIMITED_ARGS);
		requiredTitle.setRequired(true);
		requiredTitle.setArgName("TITLE");

		Option optionalAuthor = new Option(AUTHOR, AUTHOR_LONG, true, "the author of the book");
		optionalAuthor.setArgs(UNLIMITED_ARGS);
		optionalAuthor.setArgName("AUTHOR");

		Option requiredAuthor = new Option(AUTHOR, AUTHOR_LONG, true, "the author of the book");
		requiredAuthor.setArgs(UNLIMITED_ARGS);
		requiredAuthor.setRequired(true);
		requiredAuthor.setArgName("AUTHOR");

		Option optionalPublisher = new Option(PUBLISHER, PUBLISHER_LONG, true, "the publisher of the book");
		optionalPublisher.setArgName("PUBLISHER ID");

		Option optionalNoArgPublisher = new Option(PUBLISHER, PUBLISHER_LONG, false, "the publisher of the book");

		Option requiredPublisher = new Option(PUBLISHER, PUBLISHER_LONG, true, "the publisher of the book");
		requiredPublisher.setRequired(true);
		requiredPublisher.setArgName("PUBLISHER ID");

		Option optionalIsbn10 = new Option(ISBN10, ISBN10_LONG, true, "the isbn10 number of the book");
		optionalIsbn10.setArgName("ISBN10");

		Option optionalNoArgIsbn10 = new Option(ISBN10, ISBN10_LONG, false, "the isbn10 number of the book");

		Option requiredIsbn10 = new Option(ISBN10, ISBN10_LONG, true, "the isbn10 number of the book");
		requiredIsbn10.setRequired(true);
		requiredIsbn10.setArgName("ISBN10");

		Option optionalIsbn13 = new Option(ISBN13, ISBN13_LONG, true, "the isbn13 number of the book");
		optionalIsbn13.setArgName("ISBN13");

		Option optionalNoArgIsbn13 = new Option(ISBN13, ISBN13_LONG, false, "the isbn13 number of the book");

		Option requiredIsbn13 = new Option(ISBN13, ISBN13_LONG, true, "the isbn13 number of the book");
		requiredIsbn13.setRequired(true);
		requiredIsbn13.setArgName("ISBN13");

		Option optionalIsbn = new Option(ISBN, true, "all or part of the ISBN number of the book");
		optionalIsbn.setArgName("ISBN");

		Option optionalGenre = new Option(GENRE, GENRE_LONG, true, "the genre of the book");
		optionalGenre.setArgName("GENRE");

		Option optionalNoArgGenre = new Option(GENRE, GENRE_LONG, false, "the genre of the book");

		Option requiredGenre = new Option(GENRE, GENRE_LONG, true, "the genre of the book");
		requiredGenre.setRequired(true);
		requiredGenre.setArgName("GENRE");

		Option optionalPrice = new Option(PRICE, PRICE_LONG, true, "the price of the the book");
		optionalPrice.setArgName("PRICE");

		Option optionalNoArgPrice = new Option(PRICE, PRICE_LONG, false, "the price of the the book");

		Option requiredPrice = new Option(PRICE, PRICE_LONG, true, "the price of the the book");
		requiredPrice.setRequired(true);
		requiredPrice.setArgName("PRICE");

		Option optionalRangedPrice = new Option(PRICE, PRICE_LONG, true, "the price of the book");
		optionalRangedPrice.setArgs(MAX_RANGED_ARGS);
		optionalRangedPrice.setOptionalArg(true);
		optionalRangedPrice.setArgName("PRICE");

		Option optionalWeight = new Option(WEIGHT, WEIGHT_LONG, true, "the weight of the book");
		optionalWeight.setArgName("WEIGHT");

		Option optionalNoArgWeight = new Option(WEIGHT, WEIGHT_LONG, false, "the weight of the book");

		Option requiredWeight = new Option(WEIGHT, WEIGHT_LONG, true, "the weight of the book");
		requiredWeight.setRequired(true);
		requiredWeight.setArgName("WEIGHT");

		Option optionalRangedWeight = new Option(WEIGHT, WEIGHT_LONG, true, "the weight of the book");
		optionalRangedWeight.setArgs(MAX_RANGED_ARGS);
		optionalRangedWeight.setOptionalArg(true);
		optionalRangedWeight.setArgName("WEIGHT");

		Option optionalCost = new Option(COST, COST_LONG, true, "the cost of the book");
		optionalCost.setArgName("COST");

		Option optionalNoArgCost = new Option(COST, COST_LONG, false, "the cost of the book");

		Option requiredCost = new Option(COST, COST_LONG, true, "the cost of the book");
		requiredCost.setRequired(true);
		requiredCost.setArgName("COST");
		
		Option optionalQuantity = new Option(QUANTITY, QUANTITY_LONG, true, "the quantity of the book in inventory");
		optionalQuantity.setArgName("QUANTITY");

		Option optionalNoArgQuantity = new Option(QUANTITY, QUANTITY_LONG, false, "the quantity of the book in inventory");

		Option requiredQuantity = new Option(QUANTITY, QUANTITY_LONG, true, "the quantity of the book in inventory");
		requiredQuantity.setRequired(true);
		requiredQuantity.setArgName("QUANTITY");
		
		Option keyword = new Option(KEYWORD, KEYWORD_LONG, true, "search for a book by a keyword");
		keyword.setArgs(UNLIMITED_ARGS);
		
		Option verbose = new Option(VERBOSE, VERBOSE_LONG, false, "return a confirmation statement");

		Option add = new Option(ADD, false, "options for add");
		Option get = new Option(GET, false, "options for get");
		Option getAll = new Option(GET_ALL, false, "options for getall");
		Option update = new Option(UPDATE, false, "options for update");
		Option delete = new Option(DELETE, false, "options for delete");
		Option search = new Option(SEARCH, false, "options for search");

		// Add options for add
		addOptions = new Options();
		addOptions.addOption(requiredAuthor);
		addOptions.addOption(requiredTitle);
		addOptions.addOption(requiredPublisher);
		addOptions.addOption(requiredIsbn10);
		addOptions.addOption(requiredIsbn13);
		addOptions.addOption(requiredGenre);
		addOptions.addOption(requiredPrice);
		addOptions.addOption(requiredWeight);
		addOptions.addOption(requiredCost);
		addOptions.addOption(requiredQuantity);
		addOptions.addOption(verbose);

		// Add options for update
		updateOptions = new Options();
		updateOptions.addOption(requiredId);
		updateOptions.addOption(optionalTitle);
		updateOptions.addOption(optionalAuthor);
		updateOptions.addOption(optionalPublisher);
		updateOptions.addOption(optionalIsbn10);
		updateOptions.addOption(optionalIsbn13);
		updateOptions.addOption(optionalGenre);
		updateOptions.addOption(optionalPrice);
		updateOptions.addOption(optionalWeight);
		updateOptions.addOption(optionalCost);
		updateOptions.addOption(optionalQuantity);
		updateOptions.addOption(verbose);

		// Add options for get
		getOptions = new Options();
		getOptions.addOption(requiredRangedId);
		getOptions.addOption(optionalNoArgIsbn10);
		getOptions.addOption(optionalNoArgIsbn13);
		getOptions.addOption(optionalNoArgPublisher);
		getOptions.addOption(optionalNoArgGenre);
		getOptions.addOption(optionalNoArgPrice);
		getOptions.addOption(optionalNoArgWeight);
		getOptions.addOption(optionalNoArgCost);
		getOptions.addOption(optionalNoArgQuantity);
		getOptions.addOption(verbose);

		// Add options for getall
		getAllOptions = new Options();
		getAllOptions.addOption(optionalNoArgIsbn10);
		getAllOptions.addOption(optionalNoArgIsbn13);
		getAllOptions.addOption(optionalNoArgPublisher);
		getAllOptions.addOption(optionalNoArgGenre);
		getAllOptions.addOption(optionalNoArgPrice);
		getAllOptions.addOption(optionalNoArgWeight);
		getAllOptions.addOption(optionalNoArgCost);
		getAllOptions.addOption(optionalNoArgQuantity);
		getAllOptions.addOption(verbose);

		// Add options for delete and del
		deleteOptions = new Options();
		deleteOptions.addOption(requiredRangedId);
		deleteOptions.addOption(verbose);

		// Add options for search
		searchOptions = new Options();
		searchOptions.addOption(optionalTitle);
		//searchOptions.addOption(optionalRangedWeight);
		searchOptions.addOption(optionalRangedPrice);
		searchOptions.addOption(optionalGenre);
		searchOptions.addOption(optionalIsbn);
		searchOptions.addOption(optionalPublisher);
		searchOptions.addOption(optionalAuthor);
		searchOptions.addOption(verbose);

		// Add options for help
		OptionGroup helpOptionGroup = new OptionGroup();
		helpOptionGroup.addOption(add);
		helpOptionGroup.addOption(get);
		helpOptionGroup.addOption(getAll);
		helpOptionGroup.addOption(update);
		helpOptionGroup.addOption(delete);
		helpOptionGroup.addOption(search);
		helpOptions = new Options();
		helpOptions.addOptionGroup(helpOptionGroup);
	}


	/* (non-Javadoc)
	 * @see controller.AdminController#parseCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	public List<TableBuilder> parseCommand(final String cmd, final String[] args) {
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
					} else if (cmdl.hasOption(SEARCH)) {
						helpFormatter.printHelp(SEARCH, searchOptions);
					} else {
						helpFormatter.printHelp(ADD, addOptions);
						helpFormatter.printHelp(GET, getOptions);
						helpFormatter.printHelp(GET_ALL, getAllOptions);
						helpFormatter.printHelp(UPDATE, updateOptions);
						helpFormatter.printHelp(DELETE, deleteOptions);
						helpFormatter.printHelp(SEARCH, searchOptions);
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
				// We only try to add the book the if the options parsed
				if (cmdl != null) {
					add(cmdl);
				}
				break;
			case UPDATE:
				try {
					cmdl = cmdlp.parse(updateOptions, args);
				} catch (ParseException e) {
					// If the options can't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to update the book if the options parsed
				if (cmdl != null) {
					update(cmdl);
				}
				break;
			case GET:
				try {
					cmdl = cmdlp.parse(getOptions, args);
				} catch (ParseException e) {
					// If the options couldn't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to get the book if the options parsed
				if (cmdl != null) {
					get(cmdl);
				}
				break;
			case GET_ALL:
				try {
					cmdl = cmdlp.parse(getAllOptions, args);
				} catch (ParseException e) {
					// If the options couldn't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to get all the books if the options parsed
				if (cmdl != null) {
					getAll(cmdl);
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
				// We only try to delete the book if the options parsed
				if (cmdl != null) {
					delete(cmdl);
				}
				break;
			case SEARCH:
				try {
					cmdl = cmdlp.parse(searchOptions, args);
				} catch (ParseException e) {
					// If the options couldn't parse, we tell the user why
					outMessages.add(new TableBuilder(e.getMessage()));
				}
				// We only try to search the books if the options parsed
				if (cmdl != null) {
					search(cmdl);
				}
				break;
			case BOOK:
				outMessages.add(new TableBuilder("Welcome to the book controller!"));
				outMessages.add(new TableBuilder("Please enter <help> to see valid commands"));
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
		return outMessages;
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#add(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void add(final CommandLine cl) {
		// We will put the user's information in this Book and then add it to
		// the database
		InventoryBook b = new InventoryBook();

		// We get the title from the CommandLine. We use a StingBuilder
		// because the title could include several arguments
		StringBuilder sb = new StringBuilder();
		for (String s : cl.getOptionValues(TITLE)) {
			sb.append(s + " ");
		}
		b.setTitle(sb.toString());
		sb.delete(0, sb.length());

		// We do the same thing to author that we did to title
		for (String s : cl.getOptionValues(AUTHOR)) {
			sb.append(s + " ");
		}
		b.setAuthor(sb.toString());

		// We clear sb so we can use it again
		sb.delete(0, sb.length());

		// Now we try to set the numeric information

		try {
			b.setPublisherID(Integer.parseInt(cl.getOptionValue(PUBLISHER)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("Publisher must be an integer"));
		}
		try {
			b.setRetailPrice(Double.parseDouble(cl.getOptionValue(PRICE)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("Price must be a number"));
		}
		try {
			b.setShippingWeight(Double.parseDouble(cl.getOptionValue(WEIGHT)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("Weight must be a number"));
		}
		try {
			b.setCost(Double.parseDouble(cl.getOptionValue(COST)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("Cost must be a number"));
		}
		try {
			b.setQuantityOnHand(Integer.parseInt(cl.getOptionValue(QUANTITY)));
		} catch (NumberFormatException e) {
			outMessages.add(new TableBuilder("Quantity must be a number"));
		}

		// We set the remaining information
		b.setGenre(cl.getOptionValue(GENRE));
		b.setIsbn10(cl.getOptionValue(ISBN10));
		b.setIsbn13(cl.getOptionValue(ISBN13));

		// We try to add the Book to the database
		try {
			bookDAO.create(b);
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}

		// If the user gave the verbose options, we give them a confirmation.
		// This should probably show all the information
		if (cl.hasOption(VERBOSE) && b.exists()) {
			outMessages.add(new TableBuilder("Created book: " + b.getBookID()));
		}
	}
	
	/* (non-Javadoc)
	 * @see controller.AdminController#update(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void update(final CommandLine cl) {
		// This will hold the information the user provides and use the
		// information from the database if the user doesn't want to change it
		InventoryBook b = null;

		try {
			b = bookDAO.get(Integer.parseInt(cl.getOptionValue(ID)));
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		} catch (NumberFormatException e) {
			// We let the user know they need to provide an integer for the book
			// ID
			outMessages.add(new TableBuilder("ID must be an integer"));
		}

		// If we connected to the database and the book was in the database...
		if (b != null && b.exists()) {
			// We go through all the options and if the user specified them, we
			// add the information to our book
			if (cl.hasOption(TITLE)) {
				// We only change it if it's new information, otherwise we tell
				// the user we didn't change anything
				if (!cl.getOptionValue(TITLE).equals(b.getTitle())) {
					StringBuilder sb = new StringBuilder();
					for (String s : cl.getOptionValues(TITLE)) {
						sb.append(s + " ");
					}
					b.setTitle(sb.toString());
				} else {
					outMessages.add(new TableBuilder("The title you entered matched the title in the database"));
				}
			}
			if (cl.hasOption(AUTHOR)) {
				if (!cl.getOptionValue(AUTHOR).equals(b.getAuthor())) {
					StringBuilder sb = new StringBuilder();
					for (String s : cl.getOptionValues(AUTHOR)) {
						sb.append(s + " ");
					}
					b.setAuthor(sb.toString());
				} else {
					outMessages.add(new TableBuilder("The author you entered matched the author in the database"));
				}
			}
			if (cl.hasOption(PUBLISHER)) {
				if (!cl.getOptionValue(PUBLISHER).equals(String.valueOf(b.getPublisherID()))) {
					b.setPublisherID(Integer.parseInt(cl.getOptionValue(PUBLISHER)));
				} else {
					outMessages
							.add(new TableBuilder("The publisher you entered matched the publisher in the database"));
				}
			}
			if (cl.hasOption(PRICE)) {
				if (!cl.getOptionValue(PRICE).equals(String.valueOf(b.getRetailPrice()))) {
					b.setRetailPrice(Double.parseDouble(cl.getOptionValue(PRICE)));
				} else {
					outMessages.add(new TableBuilder("The price you entered matched the price in the database"));
				}
			}
			if (cl.hasOption(WEIGHT)) {
				if (!cl.getOptionValue(WEIGHT).equals(String.valueOf(b.getShippingWeight()))) {
					b.setShippingWeight(Double.parseDouble(cl.getOptionValue(WEIGHT)));
				} else {
					outMessages.add(new TableBuilder("The weight you entered matched the weight in the database"));
				}
			}
			if (cl.hasOption(GENRE)) {
				if (!cl.getOptionValue(GENRE).equals(b.getGenre())) {
					b.setGenre(cl.getOptionValue(GENRE));
				} else {
					outMessages.add(new TableBuilder("The genre you entered matched the genre in the database"));
				}
			}
			if (cl.hasOption(ISBN10)) {
				if (!cl.getOptionValue(ISBN10).equals(b.getIsbn10())) {
					b.setIsbn10(cl.getOptionValue(ISBN10));
				} else {
					outMessages.add(new TableBuilder("The ISBN-10 you entered matched the ISBN-10 in the database"));
				}
			}
			if (cl.hasOption(ISBN13)) {
				if (!cl.getOptionValue(ISBN13).equals(b.getIsbn13())) {
					b.setIsbn13(cl.getOptionValue(ISBN13));
				} else {
					outMessages.add(new TableBuilder("The ISBN-13 you entered matched the ISBN-13 in the database"));
				}
			}
			if (cl.hasOption(COST)) {
				if (!cl.getOptionValue(COST).equals(String.valueOf(b.getCost()))) {
					try {
						b.setCost(Double.parseDouble(cl.getOptionValue(COST)));
					} catch (NumberFormatException e) {
						outMessages.add(new TableBuilder("please enter a number for cost"));
					}
				} else {
					outMessages.add(new TableBuilder("The cost you entered matched the cost in the database"));
				}
			}
			if (cl.hasOption(QUANTITY)) {
				if (!cl.getOptionValue(QUANTITY).equals(String.valueOf(b.getQuantityOnHand()))) {
					try {
						b.setQuantityOnHand(Integer.parseInt(cl.getOptionValue(QUANTITY)));
					} catch (NumberFormatException e) {
						outMessages.add(new TableBuilder("please enter an integer number for quantity"));
					}
				} else {
					outMessages.add(new TableBuilder("The quantity you entered matched the quantity in the database"));
				}
			}

			// Now we try to update the information
			boolean didUpdate = false;
			try {
				bookDAO.update(b);
				didUpdate = true;
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}

			// We only show output if we updated the database
			if (didUpdate) {
				outMessages.add(0, new TableBuilder("Successfully updated!"));
				// If the user gave us the verbose option, we show all the
				// information about the book
				
				TableBuilder table = new TableBuilder();
				
				if (cl.hasOption(VERBOSE)) {
					table.addColumn("ID: ", String.valueOf(b.getBookID()));
					table.addColumn("Title: ", b.getTitle());
					table.addColumn("Author: ", b.getAuthor());
					table.addColumn("Genre: ", b.getGenre());
					table.addColumn("Publisher: ", String.valueOf(b.getPublisherID()));
					table.addColumn("ISBN-10: ", b.getIsbn10());
					table.addColumn("ISBN-13: ", b.getIsbn13());
					table.addColumn("Price: ", String.valueOf(b.getRetailPrice()));
					table.addColumn("Weight ", String.valueOf(b.getShippingWeight()));
					table.addColumn("Quantity: ", String.valueOf(b.getQuantityOnHand()));
					table.addColumn("Cost: ", formatCurrency.format(b.getCost()));
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
		} else if (b != null && !b.exists()) {
			outMessages.add(new TableBuilder("Book with  ID " + cl.getOptionValue(ID) + " not found."));
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#get(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void get(final CommandLine cl) {
		InventoryBook b = null;
		try {
			b = bookDAO.get(Integer.parseInt(cl.getOptionValue(ID)));
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}

		TableBuilder table = new TableBuilder();
		
		
		if (b != null && b.exists()) {
			table.addColumn("ID: ", String.valueOf(b.getBookID()));
			table.addColumn("Title: ", b.getTitle());
			table.addColumn("Author: ", b.getAuthor());

			if (cl.hasOption(VERBOSE)) {
				table.addColumn("Publisher ID: ", String.valueOf(b.getPublisherID()));
				table.addColumn("Publisher name: ", b.getPublisherName());
				table.addColumn("Genre: ", b.getGenre());
				table.addColumn("ISBN-10: ", b.getIsbn10());
				table.addColumn("ISBN-13: ", b.getIsbn13());
				table.addColumn("Price: ", String.valueOf(b.getRetailPrice()));
				table.addColumn("Weight: ", b.getShippingWeight() + " lbs");
				table.addColumn("Quantity: ", String.valueOf(b.getQuantityOnHand()));
				table.addColumn("Cost: ", formatCurrency.format(b.getCost()));
			} else {
				if (cl.hasOption(GENRE)) {
					table.addColumn("Genre: ", b.getGenre());
				}
				if (cl.hasOption(PUBLISHER)) {
					table.addColumn("Publisher ID: ", String.valueOf(b.getPublisherID()));
					table.addColumn("Publisher name: ", b.getPublisherName());
				}
				if (cl.hasOption(ISBN10)) {
					table.addColumn("ISBN-10: ", b.getIsbn10());
				}
				if (cl.hasOption(ISBN13)) {
					table.addColumn("ISBN-13: ", b.getIsbn13());
				}
				if (cl.hasOption(PRICE)) {
					table.addColumn("Price: ", String.valueOf(b.getRetailPrice()));
				}
				if (cl.hasOption(WEIGHT)) {
					table.addColumn("Weight: ", b.getShippingWeight() + " lbs");
				}
				if (cl.hasOption(QUANTITY)) {
					table.addColumn("Quantity: ", String.valueOf(b.getQuantityOnHand()));
				}
				if (cl.hasOption(COST)) {
					table.addColumn("Cost: ", formatCurrency.format(b.getCost()));
				}
			}

			
			List<String> bookIDs = new ArrayList<String>();
			List<String> titles = new ArrayList<String>();
			List<String> authors = new ArrayList<String>();
			List<String> publisherIDs = new ArrayList<String>();
			List<String> publisherNames = new ArrayList<String>();
			List<String> genres = new ArrayList<String>();
			List<String> ISBN10s = new ArrayList<String>();
			List<String> ISBN13s = new ArrayList<String>();
			List<String> prices = new ArrayList<String>();
			List<String> weights = new ArrayList<String>();
			List<String> quantities = new ArrayList<String>();
			List<String> costs = new ArrayList<String>();
			
			if (cl.getOptionValues(ID).length == MAX_RANGED_ARGS) {
				for (int i = Integer.parseInt(cl.getOptionValues(ID)[RANGED_ARGUMENT_1]) + 1; i <= Integer
						.parseInt(cl.getOptionValues(ID)[RANGED_ARGUMENT_2]); i++) {
					try {
						b = bookDAO.get(i);
					} catch (SQLException e) {
						sqlExceptionMessage(e);
					}

					
					bookIDs.add(String.valueOf(b.getBookID()));
					titles.add(b.getTitle());
					authors.add(b.getAuthor());
					publisherIDs.add(String.valueOf(b.getPublisherID()));
					publisherNames.add(b.getPublisherName());
					genres.add(b.getGenre());
					ISBN10s.add(b.getIsbn10());
					ISBN13s.add(b.getIsbn13());
					prices.add(formatCurrency.format(b.getRetailPrice()));
					weights.add(b.getShippingWeight() + " lbs");
					costs.add(formatCurrency.format(b.getCost()));
					quantities.add(String.valueOf(b.getQuantityOnHand()));
				}
				
				
				if (b.exists()) {
					table.addColumn("ID: ", bookIDs);
					table.addColumn("Title: ", titles);
					table.addColumn("Author: ", authors);

					if (cl.hasOption(VERBOSE)) {
						table.addColumn("Publisher ID: ", publisherIDs);
						table.addColumn("Publisher name: ", publisherNames);
						table.addColumn("Genre: ", genres);
						table.addColumn("ISBN-10: ", ISBN10s);
						table.addColumn("ISBN-13: ",ISBN13s);
						table.addColumn("Price: ", prices);
						table.addColumn("Weight: ", weights);
						table.addColumn("Quantity: ", quantities);
						table.addColumn("Cost: ", costs);
					} else {
						if (cl.hasOption(GENRE)) {
							table.addColumn("Genre: ", genres);
						}
						if (cl.hasOption(PUBLISHER)) {
							table.addColumn("Publisher ID: ", publisherIDs);
							table.addColumn("Publisher name: ", publisherNames);
						}
						if (cl.hasOption(ISBN10)) {
							table.addColumn("ISBN-10: ", ISBN10s);
						}
						if (cl.hasOption(ISBN13)) {
							table.addColumn("ISBN-13: ",ISBN13s);
						}
						if (cl.hasOption(PRICE)) {
							table.addColumn("Price: ", prices);
						}
						if (cl.hasOption(WEIGHT)) {
							table.addColumn("Weight: ", weights);
						}
						if (cl.hasOption(QUANTITY)) {
							table.addColumn("Quantity: ", quantities);
						}
						if (cl.hasOption(COST)) {
							table.addColumn("Cost: ", costs);
						}
					}
				}

			}
			outMessages.add(table);
		} else if (b != null && !b.exists()) {
			outMessages.add(new TableBuilder("Book with ID " + cl.getOptionValue(ID) + " not found."));
		}
	}


	/* (non-Javadoc)
	 * @see controller.AdminController#getAll(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void getAll(final CommandLine cl) {
		List<InventoryBook> books = null;
		
		List<String> bookIDs = new ArrayList<String>();
		List<String> titles = new ArrayList<String>();
		List<String> authors = new ArrayList<String>();
		List<String> publisherIDs = new ArrayList<String>();
		List<String> publisherNames = new ArrayList<String>();
		List<String> genres = new ArrayList<String>();
		List<String> ISBN10s = new ArrayList<String>();
		List<String> ISBN13s = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();
		List<String> weights = new ArrayList<String>();
		List<String> quantities = new ArrayList<String>();
		List<String> costs = new ArrayList<String>();
		
		try {
			books = bookDAO.getAll();
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}

		if (books == null) {
			System.out.println("no books appeared in the database");
		} else {
			for (InventoryBook b : books) {
				bookIDs.add(String.valueOf(b.getBookID()));
				titles.add(b.getTitle());
				authors.add(b.getAuthor());
				publisherIDs.add(String.valueOf(b.getPublisherID()));
				publisherNames.add(b.getPublisherName());
				genres.add(b.getGenre());
				ISBN10s.add(b.getIsbn10());
				ISBN13s.add(b.getIsbn13());
				prices.add(formatCurrency.format(b.getRetailPrice()));
				weights.add(b.getShippingWeight() + " lbs");
				costs.add(formatCurrency.format(b.getCost()));
				quantities.add(String.valueOf(b.getQuantityOnHand()));
			}
			
			TableBuilder table = new TableBuilder();
			
			if (books != null) {
				table.addColumn("ID: ", bookIDs);
				table.addColumn("Title: ", titles);
				table.addColumn("Author: ", authors);
	
				if (cl.hasOption(VERBOSE)) {
					table.addColumn("Publisher ID: ", publisherIDs);
					table.addColumn("Publisher name: ", publisherNames);
					table.addColumn("Genre: ", genres);
					table.addColumn("ISBN-10: ", ISBN10s);
					table.addColumn("ISBN-13: ",ISBN13s);
					table.addColumn("Price: ", prices);
					table.addColumn("Weight: ", weights);
					table.addColumn("Quantity: ", quantities);
					table.addColumn("Cost: ", costs);
				} else {
					if (cl.hasOption(GENRE)) {
						table.addColumn("Genre: ", genres);
					}
					if (cl.hasOption(PUBLISHER)) {
						table.addColumn("Publisher ID: ", publisherIDs);
						table.addColumn("Publisher name: ", publisherNames);
					}
					if (cl.hasOption(ISBN10)) {
						table.addColumn("ISBN-10: ", ISBN10s);
					}
					if (cl.hasOption(ISBN13)) {
						table.addColumn("ISBN-13: ",ISBN13s);
					}
					if (cl.hasOption(PRICE)) {
						table.addColumn("Price: ", prices);
					}
					if (cl.hasOption(WEIGHT)) {
						table.addColumn("Weight: ", weights);
					}
					if (cl.hasOption(QUANTITY)) {
						table.addColumn("Quantity: ", quantities);
					}
					if (cl.hasOption(COST)) {
						table.addColumn("Cost: ", costs);
					}
				}
			}
			outMessages.add(table);
		}
	}


	/* (non-Javadoc)
	 * @see controller.AdminController#delete(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void delete(final CommandLine cl) {
		InventoryBook b = null;
		try {
			b = bookDAO.get(Integer.parseInt(cl.getOptionValue(ID)));
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}

		if (b != null && b.exists()) {
			try {
				bookDAO.delete(b);
			} catch (SQLException e) {
				outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
			}
			if (cl.hasOption(VERBOSE)) {
				outMessages.add(new TableBuilder("Deleted book:"));
				outMessages.add(new TableBuilder("ID: " + b.getBookID()));
				outMessages.add(new TableBuilder("Title: " + b.getTitle()));
				outMessages.add(new TableBuilder("Author: " + b.getAuthor()));
			}
		} else {
			outMessages.add(new TableBuilder("Book with  ID " + cl.getOptionValue(ID) + " not found."));
		}

		if (cl.getOptionValues(ID).length == MAX_RANGED_ARGS) {
			int j = 0;
			for (int i = Integer.parseInt(cl.getOptionValues(ID)[RANGED_ARGUMENT_1]) + 1; i <= Integer
					.parseInt(cl.getOptionValues(ID)[RANGED_ARGUMENT_2]); i++) {
				try {
					b = bookDAO.get(i);
				} catch (SQLException e) {
					outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
				}
				if (b != null && b.exists()) {
					outMessages.add(new TableBuilder("" + ++j));
					try {
						bookDAO.delete(b);
					} catch (SQLException e) {
						outMessages.add(new TableBuilder(SQL_EXCEPTION_MESSAGE));
					}
					if (cl.hasOption(VERBOSE)) {
						outMessages.add(new TableBuilder("Deleted book:"));
						outMessages.add(new TableBuilder("ID: " + b.getBookID()));
						outMessages.add(new TableBuilder("Title: " + b.getTitle()));
						outMessages.add(new TableBuilder("Author: " + b.getAuthor()));
					}
				} else {
					outMessages.add(new TableBuilder("Book with  ID " + i + " not found."));
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see controller.AdminController#search(org.apache.commons.cli.CommandLine)
	 */
	@Override
	void search(final CommandLine cl) {
		boolean isValid = true;
		StringBuilder value = new StringBuilder();
		String[] values;
		
		ArrayList<InventoryBook> books = new ArrayList<>();
		ArrayList<InventoryBook> booksByGenre = new ArrayList<>();
		ArrayList<InventoryBook> booksByTitle = new ArrayList<>();
		ArrayList<InventoryBook> booksByAuthor = new ArrayList<>();
		ArrayList<InventoryBook> booksByPublisherID = new ArrayList<>();
		ArrayList<InventoryBook> booksByIsbn = new ArrayList<>();
		ArrayList<InventoryBook> booksByPrice = new ArrayList<>();
		ArrayList<InventoryBook> booksByWeight = new ArrayList<>();


		if (cl.hasOption(TITLE)) {
			values = cl.getOptionValues(TITLE);
			for (int i = 0; i < values.length; i++) {
				value.append(values[i]);
				//if the is still more to go add a space
				if ((i + 1) < values.length) {
					value.append(" ");
				}
			}
			try {
				booksByTitle.addAll(bookDAO.searchByTitle(value.toString()));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		}
		if (cl.hasOption(GENRE)) {
			values = cl.getOptionValues(GENRE);
			for (int i = 0; i < values.length; i++) {
				value.append(values[i]);
				//if the is still more to go add a space
				if ((i + 1) < values.length) {
					value.append(" ");
				}
			}
			try {
				booksByGenre.addAll(bookDAO.searchByGenre(value.toString()));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		}
		if (cl.hasOption(AUTHOR)) {
			values = cl.getOptionValues(AUTHOR);
			for (int i = 0; i < values.length; i++) {
				value.append(values[i]);
				//if the is still more to go add a space
				if ((i + 1) < values.length) {
					value.append(" ");
				}
			}
			try {
				booksByAuthor.addAll(bookDAO.searchByAuthor(value.toString()));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		}
		if (cl.hasOption(ISBN)) {
			try {
				booksByIsbn.addAll(bookDAO.searchByIsbn(cl.getOptionValue(ISBN)));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		} else if (cl.hasOption(ISBN13)) {
			try {
				booksByIsbn.addAll(bookDAO.searchByIsbn(cl.getOptionValue(ISBN13)));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		} else if (cl.hasOption(ISBN10)) {
			try {
				booksByIsbn.addAll(bookDAO.searchByIsbn(cl.getOptionValue(ISBN10)));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		}
		if (cl.hasOption(PRICE)) {
			if (cl.getOptionValues(PRICE).length == MAX_RANGED_ARGS) {
				try {
					booksByPrice.addAll(
							bookDAO.searchByPriceRange(Double.parseDouble(cl.getOptionValues(PRICE)[RANGED_ARGUMENT_1]),
									Double.parseDouble(cl.getOptionValues(PRICE)[RANGED_ARGUMENT_2])));
				} catch (SQLException e) {
					sqlExceptionMessage(e);
				}
			} else {
				try {
					booksByPrice.addAll(bookDAO.searchByPriceRange(Double.parseDouble(cl.getOptionValue(PRICE)),
							Double.parseDouble(cl.getOptionValue(PRICE))));
				} catch (SQLException e) {
					sqlExceptionMessage(e);
				}
			}
		}
		if (cl.hasOption(KEYWORD)) {
			values = cl.getOptionValues(KEYWORD);
			for (int i = 0; i < values.length; i++) {
				value.append(values[i]);
				//if the is still more to go add a space
				if ((i + 1) < values.length) {
					value.append(" ");
				}
			}
			
			try {
				booksByIsbn.addAll(bookDAO.searchByKeyword(value.toString()));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		}
		
		books = intersectArrayLists(booksByTitle, booksByGenre);
		books = intersectArrayLists(books, booksByPublisherID);
		books = intersectArrayLists(books, booksByAuthor);
		books = intersectArrayLists(books, booksByIsbn);
		books = intersectArrayLists(books, booksByPrice);
		books = intersectArrayLists(books, booksByWeight);
		//make sure that at least one option was used
		if (Arrays.asList(cl.getOptions()).isEmpty()) {
			isValid = false;
			outMessages.add(new TableBuilder("No commands found, enter <help> to see valid commands."));
		} 
		if (isValid) {
			// make it print the categories searched for
			if (books.isEmpty()) {
				outMessages.add(new TableBuilder("No books match those search terms."));
			} else {
				List<String> bookIDs = new ArrayList<String>();
				List<String> titles = new ArrayList<String>();
				List<String> authors = new ArrayList<String>();
				List<String> publisherIDs = new ArrayList<String>();
				List<String> publisherNames = new ArrayList<String>();
				List<String> genres = new ArrayList<String>();
				List<String> ISBN10s = new ArrayList<String>();
				List<String> ISBN13s = new ArrayList<String>();
				List<String> prices = new ArrayList<String>();
				List<String> weights = new ArrayList<String>();
				List<String> quantities = new ArrayList<String>();
				List<String> costs = new ArrayList<String>();
				List<String> bookNumb = new ArrayList<>();
				
				int counter = 1;
				for (InventoryBook b : books) {
					bookIDs.add(String.valueOf(b.getBookID()));
					titles.add(b.getTitle());
					authors.add(b.getAuthor());
					publisherIDs.add(String.valueOf(b.getPublisherID()));
					publisherNames.add(b.getPublisherName());
					genres.add(b.getGenre());
					ISBN10s.add(b.getIsbn10());
					ISBN13s.add(b.getIsbn13());
					prices.add(formatCurrency.format(b.getRetailPrice()));
					weights.add(b.getShippingWeight() + " lbs");
					quantities.add(String.valueOf(b.getQuantityOnHand() - 1));
					costs.add(formatCurrency.format(b.getCost()));
					bookNumb.add("#" + counter++);
				}	
				
				TableBuilder table = new TableBuilder();
				
				if (books != null) {
					table.addColumn("Book ID: ", bookIDs);
					table.addColumn("Title: ", titles);
					table.addColumn("Author: ", authors);
					table.addColumn("Price: ", prices);

					if (cl.hasOption(VERBOSE)) {
						table.addColumn("Publisher name: ", publisherNames);
						table.addColumn("Genre: ", genres);
						table.addColumn("ISBN-10: ", ISBN10s);
						table.addColumn("ISBN-13: ",ISBN13s);
						table.addColumn("Weight: ", weights);
						table.addColumn("Quantity on Hand: ", quantities);
						table.addColumn("Cost: ", costs);
					} else {
						if (cl.hasOption(GENRE)) {
							table.addColumn("Genre: ", genres);
						}
						if (cl.hasOption(ISBN10)) {
							table.addColumn("ISBN-10: ", ISBN10s);
						}
						if (cl.hasOption(ISBN13)) {
							table.addColumn("ISBN-13: ",ISBN13s);
						}
					}
				}
				outMessages.add(table);
			}
		
		} 
		
	}


	/* (non-Javadoc)
	 * @see controller.AdminController#checkForDuplicates(java.lang.String[])
	 */
	@Override
	protected String checkForDuplicates(final String[] pInput) {
		for (String s1 : pInput) {
			for (String s2 : pInput) {
				if (s1 != s2 && s1.startsWith("-") && s1.equals(s2)) {
					return s1 + " cannot be used twice";
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		for (String s : pInput) {
			sb.append(s + " ");
		}
		String input = sb.toString();

		if (input.contains(" - ") || input.endsWith(" -")) {
			return "unrecognized character: -";
		}
		if (input.contains(AUTHOR) && input.contains(AUTHOR_LONG)) {
			return "use either " + AUTHOR + " or " + AUTHOR_LONG;
		}
		if (input.contains(TITLE) && input.contains(TITLE_LONG)) {
			return "use either " + TITLE + " or " + TITLE_LONG;
		}
		if (input.contains(PUBLISHER) && input.contains(PUBLISHER_LONG)) {
			return "use either " + PUBLISHER + " or " + PUBLISHER_LONG;
		}
		if (input.contains(ISBN10) && input.contains(ISBN10_LONG)) {
			return "use either " + ISBN10 + " or " + ISBN10_LONG;
		}
		if (input.contains(ISBN13) && input.contains(ISBN13_LONG)) {
			return "use either " + ISBN13 + " or " + ISBN13_LONG;
		}
		if (input.contains(GENRE) && input.contains(GENRE_LONG)) {
			return "use either " + GENRE + " or " + GENRE_LONG;
		}
		if (input.contains(PRICE) && input.contains(PRICE_LONG)) {
			return "use either " + PRICE + " or " + PRICE_LONG;
		}
		if (input.contains(WEIGHT) && input.contains(WEIGHT_LONG)) {
			return "use either " + WEIGHT + " or " + WEIGHT_LONG;
		}
		if (input.contains(VERBOSE) && input.contains(VERBOSE_LONG)) {
			return "use either " + VERBOSE + " or " + VERBOSE_LONG;
		}
		return CHECK_ARGS_GOOD;
	}


	/* (non-Javadoc)
	 * @see controller.AdminController#intersectArrayLists(java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	protected ArrayList<InventoryBook> intersectArrayLists(final ArrayList<InventoryBook> booksByTitle, final ArrayList<InventoryBook> booksByGenre) {
		if (!booksByTitle.isEmpty() && !booksByGenre.isEmpty()) {
			ArrayList<InventoryBook> intersection = new ArrayList<>();
			for (int i = 0; i < booksByTitle.size(); i++) {
				for (int j = 0; j < booksByGenre.size(); j++) {
					if (booksByTitle.get(i).equals(booksByGenre.get(j))) {
						intersection.add(booksByTitle.get(i));
					}
				}
			}
			return intersection;
		} else {
			if (booksByTitle.isEmpty()) {
				return booksByGenre;
			} else {
				return booksByTitle;
			}
		}
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
			outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
					+ e.getSQLState() + " please notify your database administrator."));
		}
		
	}

}
