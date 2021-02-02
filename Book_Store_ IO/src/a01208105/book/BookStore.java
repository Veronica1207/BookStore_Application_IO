package a01208105.book;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import a01208105.book.data.books.Book;
import a01208105.book.data.customers.Customer;
import a01208105.book.data.purchases.Purchase;
import a01208105.book.io.BookReader;
import a01208105.book.io.BookReport;
import a01208105.book.io.CustomerReader;
import a01208105.book.io.CustomerReport;
import a01208105.book.io.PurchaseReader;
import a01208105.book.io.PurchaseReport;
import a01208105.util.BookStoreSorters;

/**
 * This is the driver class
 * 
 * @author Veronica A01208105
 * @version 2020-10-24
 */
public class BookStore {

	private static final String LOG4J_CONFIG_FILENAME = "log4j2.xml";
	private static final Logger LOG;

	static {
		configureLogging();
		LOG = LogManager.getLogger(BookStore.class);
	}

	/**
	 * Bcmc Constructor. Processes the commandline arguments ex. -inventory
	 * -make=honda -by_count -desc -total -service
	 * 
	 * @throws ApplicationException
	 * @throws ParseException
	 */
	public BookStore(String[] args) throws ApplicationException, ParseException {
		LOG.info("Created Bcmc");

		BookOptions.process(args);
	}

	/**
	 * Entry point to GIS
	 * 
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {
		Instant startTime = Instant.now();
		LOG.info(startTime);

		// start the Book System
		try {
			BookStore book = new BookStore(args);
			if (BookOptions.isHelpOptionSet()) {
				BookOptions.Value[] values = BookOptions.Value.values();
				System.out.format("%-5s %-15s %-10s %s%n", "Option", "Long Option", "Has Value", "Description");
				for (BookOptions.Value value : values) {
					System.out.format("-%-5s %-15s %-10s %s%n", value.getOption(), ("-" + value.getLongOption()),
							value.isHasArg(), value.getDescription());
				}

				return;
			}

			book.run();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}

		Instant endTime = Instant.now();
		LOG.info(endTime);
		LOG.info(String.format("Duration: %d ms", Duration.between(startTime, endTime).toMillis()));
	}

	/**
	 * Configures log4j2 from the external configuration file specified in
	 * LOG4J_CONFIG_FILENAME. If the configuration file isn't found then log4j2's
	 * DefaultConfiguration is used.
	 */
	private static void configureLogging() {
		ConfigurationSource source;
		try {
			source = new ConfigurationSource(new FileInputStream(LOG4J_CONFIG_FILENAME));
			Configurator.initialize(null, source);
		} catch (IOException e) {
			System.out.println(String.format(
					"WARNING! Can't find the log4j logging configuration file %s; using DefaultConfiguration for logging.",
					LOG4J_CONFIG_FILENAME));
			Configurator.initialize(new DefaultConfiguration());
		}
	}

	/**
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * 
	 */
	private void run() throws ApplicationException, FileNotFoundException {
		LOG.debug("run()");
		try {
			generateReports();
		} catch (ApplicationException e) {
			System.out.println("ERROR: the process cannot be complete");
			LOG.error("Application Exception");
		}
	}

	/**
	 * Generate the reports from the input data
	 * 
	 * @throws FileNotFoundException
	 * @throws ApplicationException
	 */
	private void generateReports() throws FileNotFoundException, ApplicationException {
		LOG.debug("generating the reports");

		// checking over Customer Report options
		if (BookOptions.isCustomersOptionSet()) {
			LOG.debug("generating the customer report");
			System.out.println("Cutomer Report: " + BookOptions.isCustomersOptionSet());
			System.out.println("Cutomer Join Date: " + BookOptions.isByJoinDateOptionSet());
			System.out.println("Cutomer Join Date DESC: " + BookOptions.isDescendingOptionSet());

			if (BookOptions.isCustomersOptionSet() == true) {
				LOG.info("Customer Reader: START");
				Map<Long, Customer> customers = CustomerReader.read();
				LOG.info("Customer Reader: DONE");
				List<Customer> customerList = new ArrayList<>(customers.values());
				if (BookOptions.isByJoinDateOptionSet() == true) {
					LOG.info("Sorting customers by joined date");
					customerList.sort(new BookStoreSorters.CompareByJoinedDate());
					if (BookOptions.isDescendingOptionSet() == true) {
						LOG.info("Sorting customers by joined date in descending order");
						Collections.reverse(customerList);
					}
				}
				CustomerReport.write(customerList);
				LOG.info("Customer Report: COMPLETE");
			}

		}

		// checking over Book Report Options
		if (BookOptions.isBooksOptionSet()) {
			LOG.debug("generating the book report");

			System.out.println("Book Report: " + BookOptions.isBooksOptionSet());
			System.out.println("Book Author: " + BookOptions.isByAuthorOptionSet());
			System.out.println("Cutomer Author DESC: " + BookOptions.isDescendingOptionSet());

			if (BookOptions.isBooksOptionSet() == true) {
				LOG.info("Book Reader: START");
				Map<Long, Book> books = BookReader.readBooks();
				LOG.info("Book Reader: DONE");
				List<Book> bookList = new ArrayList<>(books.values());
				if (BookOptions.isByAuthorOptionSet() == true) {
					LOG.info("Sorting books by Author");
					bookList.sort(new BookStoreSorters.CompareByAuthor());
					if (BookOptions.isDescendingOptionSet() == true) {
						LOG.info("Sorting books by Author in descending order");
						Collections.reverse(bookList);
					}
				}
				BookReport.writeBookReport(bookList);
				LOG.info("Book Report: COMPLETE");
			}
		}

		// checking over Purchase Report Options
		if (BookOptions.isPurchasesOptionSet()) {
			LOG.debug("generating the inventory report");
			
			System.out.println("Purchase Report: " + BookOptions.isPurchasesOptionSet());
			System.out.println("Total Value: " + BookOptions.isTotalOptionSet());
			System.out.println("Purchases by Customers Last Name: " + BookOptions.isByLastnameOptionSet());
			System.out.println("Purchases by Title: " + BookOptions.isByTitleOptionSet());
			System.out.println("Purchases of a customer: " + BookOptions.getCustomerId());

			if (BookOptions.isPurchasesOptionSet() == true) {
				LOG.info("Purchase Reader: START");
				Map<Long, Purchase> purchases = PurchaseReader.readPurchases();
				LOG.info("Purchase Reader: DONE");
				List<Purchase> purchaseList = new ArrayList<>(purchases.values());

				//Sorting by last name
				if (BookOptions.isByLastnameOptionSet() == true) {
					LOG.info("Sorting by Customer's last name");
					purchaseList.sort(new BookStoreSorters.CompareByLastName());

					if (BookOptions.isDescendingOptionSet() == true) {
						LOG.info("Sorting by Customer's last name in descending order");
						Collections.reverse(purchaseList);
					}
					PurchaseReport.writePurchaseReportExtended(purchaseList);
					LOG.info("Extended Purchase Report: COMPLETE");
				
				//sorting by title
				} else if (BookOptions.isByTitleOptionSet() == true) {
					LOG.info("Sorting by Book title");
					purchaseList.sort(new BookStoreSorters.CompareByTitle());

					if (BookOptions.isDescendingOptionSet() == true) {
						LOG.info("Sorting by Book title in descending order");
						Collections.reverse(purchaseList);
					}
					PurchaseReport.writePurchaseReportExtended(purchaseList);
					LOG.info("Extended Purchase Report: COMPLETE");
				} else {
					PurchaseReport.writePurchaseReport(purchaseList);
					LOG.info("Standard Purchase Report: COMPLETE");
				}
				
				//generate individual report
				if (BookOptions.getCustomerId() != null) {					
					PurchaseReport.writeIndividualReport(purchaseList);
					LOG.info("Individual Purchase Report: COMPLETE");
				}
				
				//calculate total
				if (BookOptions.isTotalOptionSet() == true) {
					PurchaseReport.addTotal(purchaseList);
					LOG.info("Total is added to the report");
				}

			}

		}
	}

}
