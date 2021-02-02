/**
 * Project: A00123456Lab3
 * File: CustomerReader.java
 */

package a01208105.book.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a01208105.book.ApplicationException;
import a01208105.book.data.customers.Customer;
import a01208105.util.Validator;

/**
 * Read the customer input.
 * 
 * @author Veronica A01208105
 * @version 2020-10-24
 */
public class CustomerReader {

	private static final Logger LOG = LogManager.getLogger();
	
	public static final String FIELD_DELIMITER = "\\|";
	
	public final static String FILENAME = "customers.dat";
	
	/**
	 * private constructor to prevent instantiation
	 */
	private CustomerReader() {
	}

	/**
	 * Read the customer input data.
	 * 
	 * @param data The input data.
	 * @return A list of customers.
	 * @throws ApplicationException
	 * @throws FileNotFoundException 
	 */
	public static Map<Long, Customer> read() throws ApplicationException, FileNotFoundException {

		Map<Long, Customer> customers = new HashMap<>();

			Scanner scanner = new Scanner(new File(FILENAME));
			scanner.nextLine(); //to skip the first line

			while (scanner.hasNext()) {
				String row = scanner.nextLine();
				Customer customer = readCustomerString(row);
				customers.put(customer.getId(), customer);
				LOG.debug("Read customer from the file to the Map" + customer.toString());
			}
			scanner.close();
		

		return customers;
	}

	/**
	 * Parse a Customer data string into a Customer object;
	 * 
	 * @param row
	 * @throws ApplicationException
	 */
	private static Customer readCustomerString(String data) throws ApplicationException {
		String[] elements = data.split(FIELD_DELIMITER);
		if (elements.length != Customer.ATTRIBUTE_COUNT) {
			LOG.debug("Incomplete Atribute Count - throw an Application Exception");
			throw new ApplicationException(String.format("Expected %d but got %d: %s", Customer.ATTRIBUTE_COUNT,
					elements.length, Arrays.toString(elements)));			
		}

		int index = 0;
		long id = Integer.parseInt(elements[index++]);
		String firstName = elements[index++];
		String lastName = elements[index++];
		String street = elements[index++];
		String city = elements[index++];
		String postalCode = elements[index++];
		String phone = elements[index++];

		String emailAddress = elements[index++];
		if (!Validator.validateEmail(emailAddress)) {
			LOG.error("Invalid email - throw an Application Exception");
			throw new ApplicationException(String.format("Invalid email: %s", emailAddress));
		}
		String yyyymmdd = elements[index];
		if (!Validator.validateJoinedDate(yyyymmdd)) {
			LOG.error("Invalid Date - throw an Application Exception");
			throw new ApplicationException(String.format("Invalid joined date: %s for customer %d", yyyymmdd, id));
		}
		int year = Integer.parseInt(yyyymmdd.substring(0, 4));
		int month = Integer.parseInt(yyyymmdd.substring(4, 6));
		int day = Integer.parseInt(yyyymmdd.substring(6, 8));

		return new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street)
				.setCity(city).setPostalCode(postalCode).setEmailAddress(emailAddress).setJoinedDate(year, month, day)
				.build();
	}

}
