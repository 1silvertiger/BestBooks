package cli;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.MainController;
import util.TableBuilder;

/**
 * This program allows for interaction with the
 * bookstore database. For both admins and customers.
 * @author jdowd
 *
 */
public final class Main {

	/**
	 * not used.
	 */
	private Main() {
		//not used
	}
	
	/**
	 * the main view of the program.
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			System.out.println("Loading...");
			//used for output
			List<TableBuilder> printStrings = new ArrayList<TableBuilder>();
			
			
			//controller of everything
			MainController c = new MainController();
			
			//scanner for keyboard input
			Scanner keyboard = new Scanner(System.in);
			
			//console for console IO
			Console console = System.console();
			
			//holds keyboard input
			String input;
			
			//check for certain commands
			boolean exit = false;
			boolean showHelp = true;
			boolean logout = true;
			
			//used for login routines
			LoginRoutine login = new LoginRoutine();
			
			//clear the current viewed items
			for (int i = 0; i < 50; ++i) {
				System.out.println();
			}
			
			System.out.println(
				                
			 "    ,-----.                 ,--.      \n"
			+"    |  |) /_  ,---.  ,---.,-'  '-.    \n"
			+"    |  .-.  )| .-. :(  .-''-.  .-'    \n"
			+"    |  '--' /(   --..-'  `) |  |      \n" 
			+"    `------'  `----'`----'  `--'      \n"
			+",-----.                ,--.           \n" 
			+"|  |) /_  ,---.  ,---. |  |,-.  ,---. \n" 
			+"|  .-.  )| .-. || .-. ||     / (  .-' \n" 
			+"|  '--' /' '-' '' '-' '|  |  ( .-'  `)\n" 
			+"`------'  `---'  `---' `--'`--'`----' " 
	                
					);
			System.out.print(
					
					
			 " ___________________________________ \n"
			+"(                                   )\n"		
			+"|               .,;`                |\n"
		    +"|          .:''''''''';             |\n"
		    +"|    .;'+++++++''''''''';           |\n"
		    +"|   `#+''+++++++++'++++#++;         |\n"
		    +"|   .##+''++###+#++##+++++++;       |\n"
		    +"|    :##+''++##+#+##++++++++++;     |\n"
		    +"|     :###''++#++++++++++++##+++;   |\n"
		    +"|      ,###+'++++++++##++++#++++++: |\n"
		    +"|       .+##+'++++##+##+++++++':.`  |\n"
		    +"|        `+##+''++++++++++:,.````:  |\n"
		    +"|         `'##+''++++',.`````:':`   |\n"
		    +"|           ;##++;,:`````,':.       |\n"
		    +"|            :#+.`.;.:+:`           |\n"
		    +"|             .+#++:`               |\n"
		    +"|                                   |\n"
		    +"(___________________________________)\n"		
					);
			
			do {
				
				//get rid of the stale strings in this table builder
				printStrings.clear();
				
				//run until the user has logged out
				while (!logout) {
					
					//show main menu welcome and help options only after a login
					if (showHelp) {
						System.out.println("\nWelcome to the main menu, " + c.getCurrentUserName() + "!\n");
						//print menu help
						printStrings = c.menuHelp();
						showHelp = false;
					}
					
					//run for all the table builders in the array
					for (int i = 0; i < printStrings.size(); i++){
						//print all the table builders returned
						if (console == null) {
							System.out.printf(printStrings.get(i).getFormatString(), printStrings.get(i).getHeaderStrings());
							System.out.println("");
						} else {
							console.printf(printStrings.get(i).getFormatString(), printStrings.get(i).getHeaderStrings());
							System.out.println("");
						}
						for(int h = 0; h < printStrings.get(i).getNumberOfRows(); h++) {
							if (console == null) {
								System.out.format(printStrings.get(i).getFormatString(), printStrings.get(i).getColumnStrings(h));
								System.out.println("");
							} else {
								console.format(printStrings.get(i).getFormatString(), printStrings.get(i).getColumnStrings(h));
								System.out.println("");
							}
						}
					}
					
					if (console == null) {
						// display the command header
						if (c.getCurrentController().equals("") || c.getCurrentController().equals("menu")) {
							System.out.print(c.getCurrentUserNameAbbreviated() + "\\menu>>> ");
						} else {
							System.out.print(c.getCurrentUserNameAbbreviated() + "\\" + c.getCurrentController() + ">>> ");
						}
						//get the keyboard input
						input = keyboard.nextLine();
					} else {
						// display the command header
						if (c.getCurrentController().equals("") || c.getCurrentController().equals("menu")) {
							//get the console input
							input = console.readLine(c.getCurrentUserNameAbbreviated() + "\\menu>>> ");
						} else {
							//get the console input
							input = console.readLine(c.getCurrentUserNameAbbreviated() + "\\" + c.getCurrentController() + ">>> ");
						}
					}
					
					//if its a normal entry, process commands
					if (!input.trim().equals("quit") && !input.trim().equals("logout")) {
						printStrings = c.parseCommand(input);
						
					//if the user wants to quit the program, let them
					} else if (input.equals("quit")) {
						//make sure they want to quit
						System.out.println("are you sure you would like to quit? <yes> or <no>");
						String shouldLogout = keyboard.nextLine().toLowerCase();
						if (shouldLogout.equals("yes") || shouldLogout.equals("y")) {
							exit = true;
							logout  = true;
						}
					//if the user wants to logout, let them=
					} else if (input.equals("logout")) {
						c.Logout();
						logout = true;
					} else {
						//TODO
						System.out.println("you should never see this statement");
					}
					
					//logout if one of the controllers said to
					if (c.LogoutRequested()) {
						c.Logout();
						logout = true;
					}
				} 
				
				//if they did logout without exiting, run the login routine
				if (logout && !exit){
					exit = login.login(keyboard, c, console);
					logout = false;
					showHelp=true;
				}
		
			} while (!exit);
			System.out.println("the program has ended");
			keyboard.close();
		} catch (Exception e){
			System.out.println("A critical error has occured: the program has ended");
			System.out.println("Restart and try again");
		}
	}
}
