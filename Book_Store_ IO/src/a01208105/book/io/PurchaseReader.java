/**
 * 
 */
package a01208105.book.io;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a01208105.book.ApplicationException;
import a01208105.book.data.books.Book;
import a01208105.book.data.customers.Customer;
import a01208105.book.data.purchases.Purchase;

/**
 * This class reads purchase information from a file into a Map
 * 
 * @author Veronica A01208105
 * @version 2020-10-24
 */
public class PurchaseReader {

	private static final Logger LOG = LogManager.getLogger();

	public final static String FILENAME = "purchases.csv";

	/**
	 * Default constructor
	 */
	public PurchaseReader() {
		
	}
	
	/**
	 * Method to read the file into collection
	 * 
	 * @return collection of purchases
	 * @throws ApplicationException
	 */
	public static Map<Long, Purchase> readPurchases() throws ApplicationException {
		Map<Long, Purchase> purchases = new HashMap<>();
		Iterable<CSVRecord> purchaseRecords;

		try {
			purchaseRecords = CSVReader.reader(FILENAME);
		} catch (ApplicationException e) {
			LOG.error("CSV Reader application exception");
			throw new ApplicationException();
		}
		
		Customer customer = null;
		Book book = null;
		Map<Long, Customer> customers = null;
		Map<Long, Book> books = null;
		try {
			customers = CustomerReader.read();
			books = BookReader.readBooks();
		} catch (ApplicationException | FileNotFoundException e) {
			LOG.error("File not found exception");
			throw new ApplicationException();
		}

		for (CSVRecord record : purchaseRecords) {
			String id = record.get("id");
			String customerID = record.get("customer_id");
			String bookID = record.get("book_id");
			String price = record.get("price");

			// iterate through customers to find a matching customerID
			for (Long aCustomer : customers.keySet()) {
				if (Long.valueOf(customerID).equals(aCustomer)) {
					customer = customers.get(aCustomer);
				}
			}

			// iterate through books to find a matching bookID
			for (Long aBook : books.keySet()) {
				if (Long.valueOf(bookID).equals(aBook)) {
					book = books.get(aBook);
				}
			}

			// generate Purchase objects
			Purchase aPurchase = new Purchase(Long.valueOf(id), customer, book, Double.valueOf(price));
			LOG.debug("Create Purchase Object: " + aPurchase.toString());
			purchases.put(aPurchase.getPurchaseID(), aPurchase);
		}
		return purchases;
	}

}
