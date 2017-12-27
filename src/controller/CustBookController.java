package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import model.CartItem;
import model.CartItemDAO;
import model.InventoryBook;
import model.InventoryBookDAO;
import model.User;
import util.AsciiBanner;
import util.TableBuilder;

/**
 * allows customers to search for
 * books and add them to their cart.
 * @author jdowd
 *
 */
public class CustBookController {
	//---------------------Out messages -------------------------
	private List<TableBuilder> outMessages = new ArrayList<TableBuilder>();
	private AsciiBanner banner = new AsciiBanner();
	
	//used to add searches to cart
	ArrayList<InventoryBook> books;
	
	private InventoryBookDAO bookDAO;
	private CartItemDAO cartDAO;

	private static final String HELP = "help";

	private static final String SEARCH = "search";
	private Options searchOptions;
	
	private static final String CART_LONG = "cart";
	private Options cartOptions;
	private User currentUser;
	
	//money formatter
	private NumberFormat formatCurrency = NumberFormat.getCurrencyInstance();
	
	private static final String TITLE = "t";
	private static final String AUTHOR = "a";
	private static final String ISBN10 = "i";
	private static final String ISBN13 = "I";
	private static final String ISBN = "isbn";
	private static final String GENRE = "g";
	private static final String PRICE = "p";
	private static final String VERBOSE = "v";
	private static final String PRICE_RANGE = "pr";
	private static final String KEYWORD = "k";
	private static final String CART_ADD = "a";
	private static final String CART_QUANTITY = "q";
	
	private static final String TITLE_LONG = "title";
	private static final String AUTHOR_LONG = "author";
	private static final String ISBN10_LONG = "isbn10";
	private static final String ISBN13_LONG = "isbn13";
	private static final String ISBN_LONG = "isbn";
	private static final String GENRE_LONG = "genre";
	private static final String PRICE_LONG = "price";
	private static final String VERBOSE_LONG = "verbose";
	private static final String PRICE_RANGE_LONG = "price_range";
	private static final String KEYWORD_LONG = "keyword";
	private static final String CART_ADD_LONG = "add";
	private static final String FIND = "find";
	private static final String CART_QUANTITY_LONG = "quantity";
	
	private static final String KNOWN_SQL_ERROR_PREFIX = "45";
	
	private static final int UNLIMITED_ARGS = -2;
	private static final HelpFormatter helpFormatter = new HelpFormatter();

	
	/**
	 * this constructor sets up the connection for
	 * the DAO.
	 * @param c the connection to the database
	 * @param myUser the active user
	 */
	public CustBookController(final Connection c, final User myUser) {
		
		currentUser = myUser;
		
		//set up DAO connections
		try {
			bookDAO = new InventoryBookDAO(c);
			cartDAO = new CartItemDAO(c);
		} catch (SQLException e) {
			sqlExceptionMessage(e);
		}

		// define options
		Option optionalTitle = new Option(TITLE, TITLE_LONG, true, "search by the title of the book");
		optionalTitle.setArgs(UNLIMITED_ARGS);
		optionalTitle.setArgName("Title");

		Option optionalAuthor = new Option(AUTHOR, AUTHOR_LONG, true, "search by the author of the book");
		optionalAuthor.setArgs(UNLIMITED_ARGS);
		optionalAuthor.setArgName("Author");

		Option optionalIsbn10 = new Option(ISBN10, ISBN10_LONG, true, "search by the ISBN10 number of the book");
		optionalIsbn10.setArgName("ISBN10");
		
		Option optionalIsbn13 = new Option(ISBN13, ISBN13_LONG, true, "search by the ISBN13 number of the book");
		optionalIsbn13.setArgName("ISBN13");
		
		Option optionalIsbn = new Option(ISBN, ISBN_LONG, true, "search by all or part of the ISBN number of the book");
		optionalIsbn.setArgName("ISBN");
		
		Option optionalGenre = new Option(GENRE, GENRE_LONG, true, "search by the genre of the book");
		optionalGenre.setArgName("Genre");
		
		Option optionalPrice = new Option(PRICE, PRICE_LONG, true, "search by the price of the the book");
		optionalPrice.setArgName("Price");
		
		Option verbose = new Option(VERBOSE, VERBOSE_LONG, false, "adds extra information to search results");

		Option cartAdd = new Option(CART_ADD, CART_ADD_LONG, true, "add a book from the last search to the cart");
		cartAdd.setArgName("book number");
		cartAdd.setRequired(true);

		Option cartQuantity = new Option(CART_QUANTITY, CART_QUANTITY_LONG, true, "set the quantity of a specified book to order");
		cartQuantity.setArgName("quantity");
		
		Option priceRange = Option.builder(PRICE_RANGE)
				//the long way of issuing the command
				.longOpt(PRICE_RANGE_LONG)
				
				//the description that shows up in the help dialogue
                .desc( "search within a price range" )
                
                //the name of the arguments used, displays in help as well
                .argName( "begining range> <ending range" )
                
                //let it know it can have multiple
                .hasArgs()
                
                //number of args that it can have
                .numberOfArgs(2)
                
                //finalize
                .build();
		
		Option keyword = new Option(KEYWORD, KEYWORD_LONG, true, "search for a book by a keyword");
		keyword.setArgs(UNLIMITED_ARGS);

		// define search
		searchOptions = new Options();
		searchOptions.addOption(optionalTitle);
		searchOptions.addOption(optionalPrice);
		searchOptions.addOption(optionalGenre);
		searchOptions.addOption(optionalIsbn13);
		searchOptions.addOption(optionalIsbn10);
		searchOptions.addOption(optionalIsbn);
		searchOptions.addOption(optionalAuthor);
		searchOptions.addOption(priceRange);
		searchOptions.addOption(keyword);
		searchOptions.addOption(verbose);
		
		// define cart
		cartOptions = new Options();
		cartOptions.addOption(cartAdd);
		cartOptions.addOption(cartQuantity);
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
	public List<TableBuilder> parseCommand(final String cmd, final String[] args) {

		
		
		DefaultParser cmdlp = new DefaultParser();
		CommandLine cmdl = null;
		switch (cmd) {
		case HELP:
			helpFormatter.printHelp(SEARCH + " -option argument(s)", searchOptions);
			helpFormatter.printHelp(CART_LONG + " -option argument(s)", cartOptions);
			outMessages.add(new TableBuilder("Enter <menu> to return back to the main menu"));
			break;
		//lets a user search for books
		case SEARCH:
			try {
				cmdl = cmdlp.parse(searchOptions, args);
				searchInventoryBooks(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder("invalid options: enter <help> to see usage"));
				//e.printStackTrace();
			}
			break;
		//lets the user add an item to their cart
		case CART_LONG:
			try {
				cmdl = cmdlp.parse(cartOptions, args);
				cartInventoryBooks(cmdl);
			} catch (ParseException e) {
				outMessages.add(new TableBuilder("invalid options: enter <help> to see usage"));
			}
			break;
		//displays when the user enters this controller
		case FIND:
			try {
				outMessages.addAll(banner.getBanner("BOOKS"));
			} catch (IOException e) {
				outMessages.add(new TableBuilder("Welcome to the book section!"));
			}
			outMessages.add(new TableBuilder("Please enter <help> to see commands"));
			outMessages.add(new TableBuilder("Please enter <menu> to go back to the main menu"));
			break;
		default:
			// notify the user that the command is invalid
			outMessages.add(new TableBuilder("Invalid command, enter <help> to see valid commands"));
		}
		return outMessages;

	}

	/**
	 * This searches the database for books based on the options.
	 * 
	 * @param cl
	 *            a CommandLine with the parsed options and their arguments
	 */
	private void searchInventoryBooks(final CommandLine cl) {
		boolean isValid = true;
		StringBuilder value = new StringBuilder();
		String[] values;
		
		books = new ArrayList<>();
		//books which will store individual search options
		ArrayList<InventoryBook> booksByGenre = new ArrayList<>();
		ArrayList<InventoryBook> booksByTitle = new ArrayList<>();
		ArrayList<InventoryBook> booksByAuthor = new ArrayList<>();
		ArrayList<InventoryBook> booksByPublisherID = new ArrayList<>();
		ArrayList<InventoryBook> booksByIsbn = new ArrayList<>();
		ArrayList<InventoryBook> booksByPrice = new ArrayList<>();
		ArrayList<InventoryBook> booksByWeight = new ArrayList<>();
		ArrayList<InventoryBook> booksByKeyword = new ArrayList<>();
		
		//if they searched by title
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
				//execute search
				booksByTitle.addAll(bookDAO.searchByTitle(value.toString()));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		}
		//searched by genre
		if (cl.hasOption(GENRE)) {
			values = cl.getOptionValues(GENRE);
			//puts the arguements back together as one string
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
		//by author
		if (cl.hasOption(AUTHOR)) {
			values = cl.getOptionValues(AUTHOR);
			//put the arguments back together as one string
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
		//by ISBN
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
			try {
				booksByPrice.addAll(bookDAO.searchByPriceRange(Double.parseDouble(cl.getOptionValue(PRICE)),
						Double.parseDouble(cl.getOptionValue(PRICE))));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			} catch (NumberFormatException n) {
				outMessages.add(new TableBuilder("invalid price argument: nonnumeric"));
				isValid = false;
			}
		}
		if (cl.hasOption(PRICE_RANGE)) {
			String [] priceValues = cl.getOptionValues(PRICE_RANGE);
			try {
				booksByPrice.addAll(bookDAO.searchByPriceRange(Double.parseDouble(priceValues[0]),
						Double.parseDouble(priceValues[1])));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			} catch (NumberFormatException n) {
				outMessages.add(new TableBuilder("invalid price range arguments: nonnumeric"));
				isValid = false;
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
				booksByKeyword.addAll(bookDAO.searchByKeyword(value.toString()));
			} catch (SQLException e) {
				sqlExceptionMessage(e);
			}
		}
		
		//intersect all the books into one array
		books = intersectInventoryBookArrayLists(books, booksByIsbn);
		books = intersectInventoryBookArrayLists(books, booksByGenre);
		books = intersectInventoryBookArrayLists(books, booksByPublisherID);
		books = intersectInventoryBookArrayLists(books, booksByAuthor);
		books = intersectInventoryBookArrayLists(books, booksByTitle);
		books = intersectInventoryBookArrayLists(books, booksByPrice);
		books = intersectInventoryBookArrayLists(books, booksByWeight);
		books = intersectInventoryBookArrayLists(books, booksByKeyword);
		//make sure that at least one option was used
		if (Arrays.asList(cl.getOptions()).isEmpty()) {
			isValid = false;
			outMessages.add(new TableBuilder("No valid options found, enter <help> to see valid commands and options."));
		} 
		if (isValid) {
			// make it print the categories searched for if their any books
			if (books == null || books.isEmpty()) {
				outMessages.add(new TableBuilder("No books match those search terms."));
			} else {
				
				//these are the items which may be displayed back to the user
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
				List<String> bookNumb = new ArrayList<>();
				
				int counter = 1;
				//add the items for the books to their arrays
				for (InventoryBook b : books) {
					bookIDs.add(String.valueOf(b.getBookID()));
					titles.add(b.getTitle());
					authors.add(b.getAuthor());
					publisherIDs.add(String.valueOf(b.getPublisherID()));
					publisherNames.add(b.getPublisherName());
					genres.add(b.getGenre());
					ISBN10s.add(b.getIsbn10());
					ISBN13s.add(b.getIsbn13());
					prices.add(formatCurrency.format(b.getCost()));
					weights.add(b.getShippingWeight() + " lbs");
					//if the quantity is zero, show out of stock
					if (b.getQuantityOnHand() > 0) {
						quantities.add(String.valueOf(b.getQuantityOnHand()));
					} else {
						quantities.add("out of stock");
					}
					bookNumb.add(" "+counter++);
				}	
				
				TableBuilder table = new TableBuilder();
			
				
				//add the columns and headers to the table
				table.addColumn("Book Number: ", bookNumb);
				table.addColumn("Title: ", titles);
				table.addColumn("Author: ", authors);
				table.addColumn("Price: ", prices);
				table.addColumn("Quantity on Hand: ", quantities);
				
				//if the user wanted more informaiton
				if (cl.hasOption(VERBOSE)) {
					table.addColumn("Publisher name: ", publisherNames);
					table.addColumn("Genre: ", genres);
					table.addColumn("ISBN-10: ", ISBN10s);
					table.addColumn("ISBN-13: ",ISBN13s);
					
				//if the user searched for specific things that are not normally displayed
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
				outMessages.add(table);
			}
		
		} 
		
	}
	
	/**
	 * This allows a user to add an item to their cart.
	 * @param cl
	 * 			a CommandLine with the parsed options and their arguments.
	 */
	private void cartInventoryBooks(final CommandLine cl) {
		CartItem cart = new CartItem();
		

		//make sure a search has been done
		if (books != null && !books.isEmpty()) {
			int bookNumb = 0;
			int quant = 0;
			
			boolean goodNumbers = true;
			
			try {
				bookNumb = Integer.parseInt(cl.getOptionValue(CART_ADD)) - 1;
				//tell the user they cant order the item if there is none in inventory
				if (books.get(bookNumb).getQuantityOnHand() <= 0) {
					outMessages.add(new TableBuilder("Unfortunatly, this book is out of stock. Check back later, we will have it in stock soon!"));
					goodNumbers = false;
				}
			} catch (NumberFormatException n) {
				outMessages.add(new TableBuilder("invalid book number, please enter only a number which appears in the most recent search"));
				goodNumbers = false;
			}
			if (cl.hasOption(CART_QUANTITY)) {
				try {
					quant = Integer.parseInt(cl.getOptionValue(CART_QUANTITY));
				} catch (NumberFormatException n) {
					outMessages.add(new TableBuilder("invalid quantity, please enter only a number"));
					goodNumbers = false;
				}
			} else {
				quant = 1;
			}
			
			//make sure the numbers entered where good
			if (goodNumbers) {
				//make sure their is a book that can be retrieved
				if (bookNumb < books.size() && bookNumb >= 0) {
					if (quant > 0) {
						if (quant <= books.get(bookNumb).getQuantityOnHand()) {
							cart.setBookID(books.get(bookNumb).getBookID());
							cart.setQuantity(quant);
							cart.setUserID(currentUser.getUserID());
							
							
							try {
								cartDAO.create(cart);
								outMessages.add(new TableBuilder("added " 
										+ quant
										+ " of book #" 
										+ (bookNumb + 1)
										+ " (" + books.get(bookNumb).getTitle() + ") "
										+ "to cart"));
								outMessages.add(new TableBuilder("(to view cart items, enter <menu> to go back to the main menu, then enter <cart> to view cart items and make orders)"));
							} catch (SQLException e) {
								sqlExceptionMessage(e);
							}
						} else {
							outMessages.add(new TableBuilder("invalid quantity number (cannot be greater than quantity on hand)"));
						}
					} else {
						outMessages.add(new TableBuilder("invalid quantity number (cannot be less than zero)"));
					}
				} else {
					outMessages.add(new TableBuilder("invalid book number (enter a number from 1 to " + books.size() +")"));
				}
			}
		} else {
			outMessages.add(new TableBuilder("please execute a search before adding books to cart"));
		}
	}
	
	/**
	 * This intersects two ArrayLists of Books
	 * 
	 * @param booksByTitle
	 *            the first ArrayList to be intersected
	 * @param booksByGenre
	 *            the second ArrayList to be intersected
	 * @return an ArrayList of the intersection between list1 and list2
	 */
	public ArrayList<InventoryBook> intersectInventoryBookArrayLists(final ArrayList<InventoryBook> list1, final ArrayList<InventoryBook> list2) {
		if (!list1.isEmpty() && !list2.isEmpty()) {
			ArrayList<InventoryBook> intersection = new ArrayList<>();
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
	
	/**
	 * gets a sql exception and adds the
	 * proper message to the output.
	 * @param e the error message
	 */
	private void sqlExceptionMessage(final SQLException e) {
		if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX+"010")) {
			outMessages.add(new TableBuilder("Item already in cart, please enter <menu>, <cart>, then <update> to change the quantity of this item in cart"));
		} else if (e.getSQLState().startsWith(KNOWN_SQL_ERROR_PREFIX)) {
			outMessages.add(new TableBuilder(e.getMessage()));
		} else {
			outMessages.add(new TableBuilder("Error code " + e.getErrorCode() + " occurred with SQL state "
					+ e.getSQLState() + " please notify your database administrator."));
		}
		
	}
}
