package cli;

import java.io.Console;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.commons.validator.routines.EmailValidator;

import controller.MainController;

/**
 * the login routine view.
 * allows for logging in
 * creating a new user
 * or exit the program.
 * @author jdowd
 *
 */
public class LoginRoutine {
	/**
	 * whether the input is valid or not.
	 */
	private boolean goodInput = false;
	/**
	 * whether the login is valid or not.
	 */
	private boolean goodLogin = false;
	/**
	 * the main login options.
	 * @param keyboard the current keyboard
	 * @param c the main controller
	 * @param console the console in use
	 * @return whether the user exited or not.
	 */
	public boolean login (final Scanner keyboard, final MainController c, final Console console){
		goodInput = false;
		goodLogin = false;
		
		do {
			System.out.println("Enter (1) to Login");
			System.out.println("Enter (2) to create a new user");
			System.out.println("Enter (3) to exit the program");
			String selection = keyboard.nextLine();
			if (selection.equals("1") || selection.toLowerCase().equals("login")) {
				In(keyboard, c, console);
				
			} else if (selection.equals("2") || selection.toLowerCase().equals("new")) {
				CreateNew(keyboard, c, console);
				
			} else if (selection.equals("3")) {
				return true;
			} else {
				System.out.println("invalid input, please try again.");
				goodInput = false;
			}
		} while (!goodInput || !goodLogin);
		return false;
		
	}
	/**
	 * the login section.
	 * @param keyboard the current keyboard
	 * @param c the main controller
	 */
	private void In(final Scanner keyboard, final MainController c, final Console console) {
		int loginAccum = 0;
		String email;
		String password;
		
		//loop three times or if the login is valid
		while (!goodLogin && loginAccum <= 2) {
			System.out.print("Enter your email: ");
			email = keyboard.nextLine();
			//check if the console is available or not
	        if (console == null) {
	        	//get the pword
	        	System.out.print("Enter your password: ");
	        	password =  keyboard.nextLine();
	        } else {
	        	char passwordArray[] = console.readPassword("Enter your password: ");
	        	password = new String(passwordArray);
	        }
			
			System.out.println("Logging in...");
			try {
				goodLogin = c.Login(password, email);
			} catch (SQLException e) {
				goodLogin = false;
			} //catch (Exception e) {
//				System.out.println("a login error has occured, please close the program and try again later");
//			}
			if (goodLogin) {
				System.out.println("Success!");
			} else {
				System.out.println("Invalid email and password");
				if (loginAccum == 2) {
					//do nothing
				} else if (loginAccum == 1) {
				System.out.println("1 try left before return to login");
		
				} else {
				System.out.println("2 trys left before return to login");
				}
				++loginAccum;
			}
			
		}
		
		goodInput = true;
	}
	
	/**
	 * lets a user create an account.
	 * @param keyboard the keyboard in use
	 * @param c the main controller in use
	 * @param console the cosole in use
	 */
	private void CreateNew(final Scanner keyboard, final MainController c, final Console console) {
		String password = null;
		String password2;
		String email = null;
		String firstName = null;
		String lastName = null;
		String homePhone = "";
		String cellPhone = "";
		String endEntry;
		boolean passMatch = false;
		boolean goodPhone = false;
		boolean goodEmail = false;
		boolean goodName = false;
		boolean looksGood = false;
		boolean validEntry = false;
		boolean noDupe = false;
		
		
		while (!looksGood) {
			passMatch = false;
			noDupe = false;
			validEntry = false;
			goodName = false;
			//get the name
			while(!goodName) {
				System.out.println("Enter your first name");
				firstName = keyboard.nextLine();
				System.out.println("Enter your last name");
				lastName = keyboard.nextLine();
				if (firstName.equals("") || lastName.equals("")){
					System.out.println("Invalid name, please try again");
				} else {
					goodName = true;
				}
				
			}
			goodPhone = false;
			//get the home phone number from the user
			while (!goodPhone) {
				System.out.println("Enter your home phone number (or enter nothing to not enter a home phone number)");
				homePhone = keyboard.nextLine();
				homePhone = homePhone.replaceAll("[^\\d]", "");
				
				if (homePhone.equals("")) {
					goodPhone = true;
					System.out.println("no home phone number added");
					System.out.println("to add a home phone at a later date, go to <user> then enter <update>");
				} else if (homePhone.length() == 10) {
					goodPhone = true;
				} else {
					System.out.println("the home phone number you entered is not valid");
					System.out.println("please make sure you enter only a 10 digit phone number and try again");
				}
			}
			
			goodPhone = false;
			//get the cell phone number from the user
			while (!goodPhone) {
				System.out.println("Enter your cell phone number (or enter nothing to not enter a cell phone number)");
				cellPhone = keyboard.nextLine();
				cellPhone = cellPhone.replaceAll("[^\\d]", "");
				
				if (cellPhone.equals("")) {
					goodPhone = true;
					System.out.println("no cell phone number added");
					System.out.println("to add a cell phone at a later date, go to <user> then enter <update>");
				} else if (cellPhone.length() == 10) {
					goodPhone = true;
				} else {
					System.out.println("the cell phone number you entered is not valid");
					System.out.println("please make sure you enter only a 10 digit phone number and try again");
				}
			}
			
				
			passMatch = false;
			//get the password and confirm
			while (!passMatch) {
				
				
				//check if the console is available or not
		        if (console == null) {
		        	//get the pword
		        	System.out.print("Enter your password: ");
		        	password =  keyboard.nextLine();
		        } else {
		        	char passwordArray[] = console.readPassword("Enter your password: ");
		        	password = new String(passwordArray);
		        }
				
				
				if (console == null) {
					System.out.print("Confirm password: ");
					password2 =  keyboard.nextLine();
				} else {
					char passwordArray[] = console.readPassword("confirm password: ");
					password2 = new String(passwordArray);
				}
				if (password.equals(password2)) {
					passMatch = true;
				} else {
					System.out.println("passwords do not match, please try again.");
				}
			}
			
			goodEmail = false;
			EmailValidator validator = EmailValidator.getInstance();
			while (!goodEmail) {
				//get the user's email
				System.out.println("Enter email address");
				email = keyboard.nextLine();
				
				if  (validator.isValid(email)) {
					goodEmail = true;
				} else {
					System.out.println("invalid email. please try again");
				}
			}
			
			//let the user confirm all the information
			System.out.println("here are your settings:");
			System.out.println("name: " + firstName + " " + lastName);
			System.out.println("email: " + email );
			if (!homePhone.equals("")) {
				System.out.println("home phone number: " + homePhone );
			}
			if (!cellPhone.equals("")) {
				System.out.println("cell phone number: " + cellPhone );
			}
			
			while (!validEntry) {
				// the user to create the new account
				System.out.println("Enter (1) to save these settings");
				System.out.println("Enter (2) to retry");
				System.out.println("Enter (3) to return to login");
				
				endEntry = keyboard.nextLine();
				//if the entry is to save
				if (endEntry.equals("1") || endEntry.equals("confirm")) {
					looksGood=true;
					validEntry=true;
					noDupe = c.CreateUser(firstName, lastName, password, email, homePhone, cellPhone);
					if (!noDupe) {
						System.out.println("email address is already in use for an account. please try a new one");
						looksGood = false;
					} else {
						System.out.println("Account created");
					}
				//if they asked to retry
				} else if (endEntry.equals("2") || endEntry.equals("retry")){
					System.out.println("retrying...");
					looksGood=false;
					validEntry=true;
				//if they canceled the process
				} else if (endEntry.equals("3")) {
					validEntry=true;
					looksGood=true;
				//if they did not enter a good entry
				} else {
					System.out.println(endEntry + " is not a valid entry, please try again.");
				}
				
			}
		}
	
		goodInput = true;
	}

}
