/**
 * 
 */
package a01208105.book.io;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Formatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a01208105.book.ApplicationException;
import a01208105.book.data.books.Book;
import a01208105.util.Common;

/**
 * This class generated Book reports
 * 
 * @author Veronica A01208105
 * @version 2020-10-24
 *
 */
public class BookReport {
	
	private static final Logger LOG = LogManager.getLogger();

	public static final String HORIZONTAL_LINE = "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%-8s %-15s %-40s %-8s %-38s %-16s %-14s %-40s%n";
	public static final String BOOK_FORMAT = "%-8d %-15s %-40s %-8d %-40s %-15.3f %-13d %-40s%n";

	/**
	 * Default constructor
	 */
	public BookReport() {

	}

	/**
	 * Method to write Book report
	 * @throws ApplicationException 
	 */
	public static void writeBookReport(Collection<Book> books) throws ApplicationException {
		try {
			Formatter output = null;
			output = new Formatter("books_report.txt");
			output.format("Books Report%n");
			output.format("%s%n", HORIZONTAL_LINE);
			output.format(HEADER_FORMAT, "ID", "ISBN", "Authors", "Year", "Title",
					"Rating AVRG", "Rating CNT", "Image URL");
			output.format("%s%n", HORIZONTAL_LINE);

			for (Book book : books) {
				output.format(BOOK_FORMAT, book.getBookId(), book.getIsbn(), Common.ellipsis(book.getAuthors(), 40),
						book.getYearPublished(), Common.ellipsis(book.getTitle(), 40), book.getRatingAvrg(), book.getRatingCount(),
						Common.ellipsis(book.getImageURL(), 40));
				LOG.debug("Book Record Entry: " + book.toString());
			}
			output.close();
			
		} catch (FileNotFoundException e) {
			LOG.error("File not found application exception");
			throw new ApplicationException();
		}

	}

}
