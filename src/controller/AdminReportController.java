/**
 * 
 */
package controller;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import util.TableBuilder;
import reports.Printer;

/**
 * creates and saves reports.
 * @author Dakota
 *
 */
public class AdminReportController {

	private static final String ORDERS = "orders";
	private static final String SALES = "sales";
	private static final String USER_ID = "u";
	private static final String USER_ID_LONG = "user";
	private static final String MONTH = "m";
	private static final String MONTH_LONG = "month";
	private static final String YEAR = "y";
	private static final String YEAR_LONG = "year";
	private static final String PROF = "prof";
	private static final String INVOICE = "invoice";
	private static final String ORDER_ID = "o";
	private static final String ORDER_ID_LONG = "order";

	private static final String JANUARY = "january";
	private static final String JAN = "jan";
	private static final String FEBRUARY = "february";
	private static final String FEB = "feb";
	private static final String MARCH = "march";
	private static final String MAR = "mar";
	private static final String APRIL = "april";
	private static final String APR = "apr";
	private static final String MAY = "may";
	private static final String JUNE = "june";
	private static final String JUN = "jun";
	private static final String JULY = "july";
	private static final String JUL = "jul";
	private static final String AUGUST = "august";
	private static final String AUG = "aug";
	private static final String SEPTEMBER = "september";
	private static final String SEP = "sep";
	private static final String OCTOBER = "october";
	private static final String OCT = "oct";
	private static final String NOVEMBER = "november";
	private static final String NOV = "nov";
	private static final String DECEMBER = "december";
	private static final String DEC = "dec";
	private static final String REPORT = "report";
	private static final String HELP = "help";

	private static List<TableBuilder> outMessages = new ArrayList<>();

	private Printer printer;

	private Option optionalUserID = new Option(USER_ID, USER_ID_LONG, true, "the user ID");
	private Option optionalMonth = new Option(MONTH, MONTH_LONG, true, "the month");
	private Option optionalYear = new Option(YEAR, YEAR_LONG, true, "the year");
	private Option requiredOrderID = new Option(ORDER_ID, ORDER_ID_LONG, true, "the order");

	private Options orderOptions = new Options();
	private Options salesOptions = new Options();
	private Options invoiceOptions = new Options();
	
	/** the formatter for the help menu output. */
	protected static final HelpFormatter helpFormatter = new HelpFormatter();


	/**
	 * sets up the connections.
	 * @param c the connection
	 */
	public AdminReportController(Connection c) {
		try {
			printer = new Printer(c);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		optionalMonth.setOptionalArg(true);

		orderOptions.addOption(optionalUserID);

		salesOptions.addOption(optionalMonth);
		salesOptions.addOption(optionalYear);

		invoiceOptions.addOption(requiredOrderID);
	}

	/**
	 * parses input and executes commands.
	 * @param cmd the current command
	 * @param args the command arguments
	 * @return the out messages
	 */
	public List<TableBuilder> parseCommand(String cmd, String[] args) {
		DefaultParser parser = new DefaultParser();

		try {
			switch (cmd) {
			case ORDERS:
				generateOrdersReport(parser.parse(orderOptions, args));
				break;
			case SALES:
				generateSalesReport(parser.parse(salesOptions, args));
				break;
			case PROF:
				System.out.print(                  
				 "               `.,#@@@@@@@@@@@@+.                 \n"
				+"            ,@@#@@@@@@@@@@@@@@@@@@':              \n"
				+"          ;@@@@@@@@@@@@@@@@@@@@@@@@@@+`           \n"
				+"       `,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@#`         \n"
				+"     `+@@@@@@@@@@@@@@@@@@@@@@@@#@@@@@@@@@:`       \n"
				+"    ,@@@@@@@@@#+;'@@@@@#;:+#''` . `.'#@@@@:`      \n"
				+"    @@@@@@+:,         ``            `,:@@@@@.     \n"
				+"   '@@@@+:`                          ,,@@@@@@#`   \n"
				+"  `@@@@#:`                           `.'@@@@@@#   \n"
				+"  `@@@#'`                             `;@@@@@@@   \n"
				+"  `@@@+:`                             `:@@@@@@@`  \n"
				+"  `@@@#'++@@@@'.         '';;,.` `.`  .:+@@@@@@`  \n"
				+"  ,@@@@#``. ,`,,',       .``..`  `+:+:,,:@@@@@@#  \n"
				+" `@@@@#:'+##@+;:,,'`      ,:;'+;,..  ,:';'@@@@@@` \n"
				+" #@@@@,';@.@@@,,, ;,   .` .::,@@@+:,   .::#@@@@@@;\n"
				+"`.@@@',  ,;;:.   `;:   .  ````,:;;#'`   ,,+@@@@@@`\n"
				+"  #@@:,+'.       .#:   `          `..   `,#@@@@@  \n"
				+" ,.@#,.;.        '+    `                 ,+@@@: ,`\n"
				+" ;,;+,          ,;.     `.               :'#+    `\n"
				+" ,`.@,`        `'':      ``             `++#` ` `.\n"
				+"    #+`         ;:#+.:``.,,             ,#;+    `.\n"
				+"    :#;`      .'@@@',,;@@@+,`          ,:;;'`   ` \n"
				+"    ,#.: `#@@@@@@@@#::'#@@@@@@@@@,     .,:;+;.,,` \n"
				+"    .@,;+@#,,++#@@'::,,:;+++:,;'+@,    ..,;@      \n"
				+"     #','',.  ,+#':`   `.'+',`   .    `, .+:`     \n"
				+"     ,@;.#,#`  ,;:,;;,:.``      ,,    `` .+`      \n"
				+"    ``##+@@#:;;;,.##@@##+,..````;.,`  ` .+..      \n"
				+"       :@@@@@#;'+#,::.;`+;'.,,`'+`'  ``.#+        \n"
				+"        @@@@@@@@@@:``  `,#@##+;'','.;;+@:+..      \n"
				+"       `;'@@@@@@@+@+:....:@@@+@#@@:'#@+. +@@@@+`  \n"
				+"      `##;+@@@@@@@@+,+;++;@@@@@@@'';':` `#@@@@@@; \n"
				+"     :@@@:.+@@@@@@@@@#@@#@@@@@@@';,.`   `#@@@@@@@#\n"
				+"    ,@@@@;``:#@@@@@@@@@@@@@@@#':.        '+@@@@@@@\n"
				+"    @@@@@;`  `,'#@@@@@@@@@#',.           ;,.'@@@@@\n"
				+":#@@@@@@@;`      `..,,,.`                .:  +@@@@\n"
				+"+@@@@@@#@;`                               .``@@@@@\n"
				+"+@@@@@@@@;                                  @@@@@@\n"
				+"+@@@@@@@@'                                 +@@@@@@\n"
				);
				break;
			case USER_ID_LONG:
				generateDemographicReport();
				break;
			case HELP:
				helpFormatter.printHelp(ORDERS, orderOptions);
				helpFormatter.printHelp(INVOICE, invoiceOptions);
				helpFormatter.printHelp(SALES, salesOptions);
				outMessages.add(new TableBuilder("usage: user"));
				break;
			case INVOICE:
				generateInvoice(parser.parse(invoiceOptions, args));
				break;
			case REPORT:
				outMessages.add(new TableBuilder("Welcome to the report section!"));
				break;
			default:
				outMessages.add(new TableBuilder("Unrecognized command '" + cmd + "'"));
			}
		} catch (ParseException e) {
			outMessages.add(new TableBuilder(e.getMessage()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return outMessages;
	}

	/**
	 * refreshes the printer.
	 */
	public void refresh() {
		try {
			printer.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void generateInvoice(CommandLine cmdl) {
		if (StringUtils.isNumeric(cmdl.getOptionValue(ORDER_ID))) {
			String output;
			try {
				output = printer.printInvoice(Integer.parseInt(cmdl.getOptionValue(ORDER_ID)));
				outMessages.add(new TableBuilder(output.startsWith("C:") ? "Invoice saved to: " + output : output));
			} catch (NumberFormatException e) {
				outMessages.add(new TableBuilder("invalid input: enter only a number"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * generates a orders report.
	 * @param cmdl the current command line
	 * @throws NumberFormatException if there is a bad format
	 * @throws FileNotFoundException if the file is not found
	 */
	private void generateOrdersReport(CommandLine cmdl) throws NumberFormatException, FileNotFoundException {
		if (cmdl.hasOption(USER_ID)) {
			outMessages.add(new TableBuilder(
					"Report saved to: " + printer.printAllUserOrders(Integer.valueOf(cmdl.getOptionValue(USER_ID)))));
		} else {

		}
	}

	/**
	 * creates a sales report.
	 * @param cmdl the current command line
	 */
	private void generateSalesReport(CommandLine cmdl) {
		if (cmdl.hasOption(MONTH)) {
			String month = null;
			String year = null;
			if (cmdl.getOptionValue(MONTH) != null) {
				switch (cmdl.getOptionValue(MONTH)) {
				case JANUARY:
				case JAN:
					month = java.time.Month.JANUARY.toString().toLowerCase();
					break;
				case FEBRUARY:
				case FEB:
					month = java.time.Month.FEBRUARY.toString().toLowerCase();
					break;
				case MARCH:
				case MAR:
					month = java.time.Month.MARCH.toString().toLowerCase();
					break;
				case APRIL:
				case APR:
					month = java.time.Month.APRIL.toString().toLowerCase();
					break;
				case MAY:
					month = java.time.Month.MAY.toString().toLowerCase();
					break;
				case JUNE:
				case JUN:
					month = java.time.Month.JUNE.toString().toLowerCase();
					break;
				case JULY:
				case JUL:
					month = java.time.Month.JULY.toString().toLowerCase();
					break;
				case AUGUST:
				case AUG:
					month = java.time.Month.AUGUST.toString().toLowerCase();
					break;
				case SEPTEMBER:
				case SEP:
					month = java.time.Month.SEPTEMBER.toString().toLowerCase();
					break;
				case OCTOBER:
				case OCT:
					month = java.time.Month.OCTOBER.toString().toLowerCase();
					break;
				case NOVEMBER:
				case NOV:
					month = java.time.Month.NOVEMBER.toString().toLowerCase();
					break;
				case DECEMBER:
				case DEC:
					month = java.time.Month.DECEMBER.toString().toLowerCase();
					break;
				}
				if (cmdl.hasOption(YEAR)) {
					year = cmdl.getOptionValue(YEAR);
				} else {
					year = String.valueOf(LocalDateTime.now().getYear());
				}
				try {
					outMessages.add(
							new TableBuilder("Report saved to: " + printer.printActivityDataForMonth(month, year)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				try {
					outMessages.add(new TableBuilder("Report saved to: " + printer.printActivityDataByMonth()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * creates a demographics report.
	 */
	private void generateDemographicReport() {
		try {
			outMessages.add(new TableBuilder("Saved to " + printer.printDemographicReport()));
		} catch (FileNotFoundException e) {
			outMessages.add(new TableBuilder("An error occured with the file. Please try again."));
		}
	}

}
