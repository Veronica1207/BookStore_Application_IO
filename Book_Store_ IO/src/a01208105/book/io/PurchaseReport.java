/**
 * 
 */
package a01208105.book.io;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Formatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a01208105.book.ApplicationException;
import a01208105.book.BookOptions;
import a01208105.book.data.purchases.Purchase;
import a01208105.util.Common;

/**
 * This class generates purchase reports
 * 
 * @author Veronica A01208105
 * @version 2020-10-24
 */
public class PurchaseReport {

	private static final Logger LOG = LogManager.getLogger();

	public static final String HORIZONTAL_LINE = "-------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%-11s %-23s %-21s %-5s%n";
	public static final String PURCHASE_FORMAT = "%-15d %-20d %-20d $%-5.2f%n";

	public static final String HEADER_LESS = "%-20s %-40s %s%n";
	public static final String PURCHASE_FORMAT_LESS = "%-20s %-40s $%.2f%n";

	static Formatter output = null;
	static FileWriter fw = null;
	public static final String FILENAME = "purchases_report.txt";

	/**
	 * Default constructor
	 */
	public PurchaseReport() {
		super();
	}

	/**
	 * Method to generate a standard purchase report
	 * 
	 * @param purchases the collection of purchases to read from
	 * @throws ApplicationException
	 */
	public static void writePurchaseReport(Collection<Purchase> purchases) throws ApplicationException {
		try {
			output = new Formatter(FILENAME);
			output.format("Purchases Report%n");
			output.format("%s%n", HORIZONTAL_LINE);
			output.format(HEADER_FORMAT, "ID", "Customer ID", "Book ID", "Price");
			output.format("%s%n", HORIZONTAL_LINE);

			for (Purchase aPurchase : purchases) {
				output.format(PURCHASE_FORMAT, aPurchase.getPurchaseID(), aPurchase.getCustomer().getId(),
						aPurchase.getBook().getBookId(), aPurchase.getPrice());
			}
		} catch (FileNotFoundException e) {
			LOG.error("File not found");
			throw new ApplicationException();
		} finally {
			output.close();
		}
	}

	/**
	 * Method to generate an extended report with information from the Book and
	 * Customer objects
	 * 
	 * @param purchases is a collection to read info from
	 * @throws ApplicationException 
	 */
	public static void writePurchaseReportExtended(Collection<Purchase> purchases) throws ApplicationException {

		try {
			output = new Formatter(FILENAME);
			output.format("Purchases Report Extended%n");
			output.format("%s%n", HORIZONTAL_LINE);
			output.format(HEADER_LESS, "Name", "Title", "Price");
			output.format("%s%n", HORIZONTAL_LINE);

			for (Purchase aPurchase : purchases) {
				String fullName = (String.format("%s %s", aPurchase.getCustomer().getLastName(),
						aPurchase.getCustomer().getFirstName()));
				output.format(PURCHASE_FORMAT_LESS, fullName, Common.ellipsis(aPurchase.getBook().getTitle(), 40),
						aPurchase.getPrice());
				LOG.debug("Purchase Record Entry " + aPurchase.toString());
			}

		} catch (FileNotFoundException e) {
			LOG.error("File not found");
			throw new ApplicationException();
		} finally {
			output.close();
		}

	}
	/**
	 * Method to generate an individual report
	 * 
	 * @param purchases a collection to read from
	 * @throws ApplicationException 
	 */
	public static void writeIndividualReport(Collection<Purchase> purchases) throws ApplicationException {
		
		try {
			output = new Formatter(FILENAME);
			output.format("Purchases Report (Single Customer)%n");
			output.format("%s%n", HORIZONTAL_LINE);
			output.format(HEADER_LESS, "Name", "Title", "Price");
			output.format("%s%n", HORIZONTAL_LINE);
			
			int books = 0;
			
			for (Purchase aPurchase : purchases) {
				if ((String.valueOf(aPurchase.getCustomer().getId())).equals(BookOptions.getCustomerId())) {
					String fullName = (String.format("%s %s", aPurchase.getCustomer().getLastName(),
							aPurchase.getCustomer().getFirstName()));
					output.format(PURCHASE_FORMAT_LESS, fullName, Common.ellipsis(aPurchase.getBook().getTitle(), 40),
							aPurchase.getPrice());
					LOG.debug("Purchase Record Entry " + aPurchase.toString());
					books++;
				}
			}
			if (books == 0) {
				output.format("%n%nCustomer ID #%s has not bought any books yet%n%n%n", BookOptions.getCustomerId());
			}
		
		} catch (FileNotFoundException e) {
			LOG.error("File not found");
			throw new ApplicationException();
		} finally {
			output.close();
		}

	}

	/**
	 * Method to add total cost of purchases
	 * 
	 * @param purchases a collection to read from
	 * @throws ApplicationException 
	 */
	public static void addTotal(Collection<Purchase> purchases) throws ApplicationException {
		double total = 0;
		if (BookOptions.getCustomerId() != null) {
			for (Purchase aPurchase : purchases) {
				if ((String.valueOf(aPurchase.getCustomer().getId())).equals(BookOptions.getCustomerId())) {
					LOG.debug(String.format("Current total %.3f + add price %.3f", total, aPurchase.getPrice()));
					total += aPurchase.getPrice();
					LOG.debug(String.format("New total: %.3f", total));
				}
			}
		} else {
			for (Purchase aPurchase : purchases) {
				total += aPurchase.getPrice();
			}
			LOG.debug(String.format("Total: %.3f", total));
		}

		try {
			fw = new FileWriter(FILENAME, true);
			fw.write(String.format("%s%n", HORIZONTAL_LINE));
			fw.write(String.format("%s: $%.2f", "Value of purchases", total)); // it was 38
			fw.write(String.format("%n%s%n", HORIZONTAL_LINE));
			fw.close();
		} catch (IOException e) {
			LOG.error("File not found");
			throw new ApplicationException();
		}

	}

}
