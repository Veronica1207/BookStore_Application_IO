package a01208105.book.io;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Formatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a01208105.book.data.customers.Customer;
import a01208105.util.Common;

/**
 * Prints a formated Customers report.
 * 
 * @author Veronica A01208105
 * @version 2020-10-24
 */
public class CustomerReport {

	private static final Logger LOG = LogManager.getLogger();
	

	public static final String HORIZONTAL_LINE = "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%4s. %-6s %-12s %-12s %-40s %-25s %-12s %-15s %-42s%s%n";
	public static final String CUSTOMER_FORMAT = "%4d. %06d %-12s %-12s %-40s %-25s %-12s %-15s %-42s%s%n";

	/**
	 * private constructor to prevent instantiation
	 */
	private CustomerReport() {
	}

	/**
	 * Print the report.
	 * 
	 * @param customers
	 */
	public static void write(Collection<Customer> customers) {
		

		if (customers != null) {
			try {
				Formatter output = null;
				output = new Formatter("customers_report.txt");
				output.format("Customers Report%n");
				output.format("%s%n", HORIZONTAL_LINE);
				output.format(HEADER_FORMAT, "#", "ID", "First name", "Last name", "Street", "City", "Postal Code",
						"Phone", "Email", "Join Date");
				output.format("%s%n", HORIZONTAL_LINE);

				int i = 0;
				for (Customer customer : customers) {
					LocalDate date = customer.getJoinedDate();
					output.format(CUSTOMER_FORMAT, ++i, customer.getId(), customer.getFirstName(),
							customer.getLastName(), customer.getStreet(), customer.getCity(), customer.getPostalCode(),
							customer.getPhone(), customer.getEmailAddress(), Common.DATE_FORMAT.format(date));
					LOG.debug("Add customer to the report: " + customer.toString());
				}
				output.close();

			} catch (FileNotFoundException e) {
				LOG.error(e.getMessage());
			}
		}
	}
}
