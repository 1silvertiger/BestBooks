package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DAO for books.
 * @author jdowd
 *
 */
public class BookDAO implements DAO<Book> {
	
	/**
	 * This is the connection to the database
	 */
	private Connection c;

	/**
	 * @param connect the connection to the database
	 * @throws SQLException an exception from the database
	 */
	public BookDAO(final Connection connect) throws SQLException {
		
		c = connect;
	}

	/* (non-Javadoc)
	 * @see model.DAO#update(java.lang.Object)
	 */
	@Override
	public void update(final Book pObjectToUpdate) throws SQLException {
		// TODO Auto-generated method stub
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspUpdateBook(?,?,?,?,?,?,?,?,?)}");
		stmt.setInt(++n, pObjectToUpdate.getBookID());
		stmt.setDouble(++n, pObjectToUpdate.getPublisherID());
		stmt.setString(++n, pObjectToUpdate.getTitle());
		stmt.setString(++n, pObjectToUpdate.getAuthor());
		stmt.setString(++n, pObjectToUpdate.getIsbn13());
		stmt.setString(++n, pObjectToUpdate.getIsbn10());
		stmt.setDouble(++n, pObjectToUpdate.getShippingWeight());
		stmt.setString(++n, pObjectToUpdate.getGenre());
		stmt.setDouble(++n, pObjectToUpdate.getRetailPrice());

		stmt.executeQuery();
	}

	/* (non-Javadoc)
	 * @see model.DAO#create(java.lang.Object)
	 */
	@Override
	public void create(final Book pObjectToCreate) throws SQLException {
		// TODO Auto-generated method stub
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspCreateBook(?,?,?,?,?,?,?,?)}");
		stmt.setDouble(++n, pObjectToCreate.getPublisherID());
		stmt.setString(++n, pObjectToCreate.getTitle());
		stmt.setString(++n, pObjectToCreate.getAuthor());
		stmt.setString(++n, pObjectToCreate.getIsbn13());
		stmt.setString(++n, pObjectToCreate.getIsbn10());
		stmt.setDouble(++n, pObjectToCreate.getShippingWeight());
		stmt.setString(++n, pObjectToCreate.getGenre());
		stmt.setDouble(++n, pObjectToCreate.getRetailPrice());

		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			pObjectToCreate.setBookID(r.getInt("bookID"));
		}
	}

	/* (non-Javadoc)
	 * @see model.DAO#getAll()
	 */
	@Override
	public List<Book> getAll() throws SQLException {
		// TODO Auto-generated method stub
		List<Book> myBooks = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetAllBooksWithPublisherNames()}");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book tempBook = new Book();
			tempBook.setAuthor(rs.getString("author"));
			tempBook.setBookID(rs.getInt("bookID"));
			tempBook.setGenre(rs.getString("genre"));
			tempBook.setIsbn10(rs.getString("ISBN10"));
			tempBook.setIsbn13(rs.getString("ISBN13"));
			tempBook.setPublisherID(rs.getInt("publisherID"));
			tempBook.setPublisherName(rs.getString("publisherName"));
			tempBook.setRetailPrice(rs.getDouble("retailPrice"));
			tempBook.setShippingWeight(rs.getDouble("shippingWeight"));
			tempBook.setTitle(rs.getString("title"));
			myBooks.add(tempBook);
		}
		return myBooks;
	}

	/* (non-Javadoc)
	 * @see model.DAO#get(int)
	 */
	@Override
	public Book get(final int pId) throws SQLException {
		Book myBook = new Book();
		CallableStatement stmt = c.prepareCall("{call uspGetBookWithPublisherName(?)}");
		int n = 0;
		stmt.setInt(++n, pId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			myBook.setAuthor(rs.getString("author"));
			myBook.setBookID(rs.getInt("bookID"));
			myBook.setGenre(rs.getString("genre"));
			myBook.setIsbn10(rs.getString("ISBN10"));
			myBook.setIsbn13(rs.getString("ISBN13"));
			myBook.setPublisherID(rs.getInt("publisherID"));
			myBook.setPublisherName(rs.getString("publisherName"));
			myBook.setRetailPrice(rs.getDouble("retailPrice"));
			myBook.setShippingWeight(rs.getDouble("shippingWeight"));
			myBook.setTitle(rs.getString("title"));
			myBook.setExists(true);
		} else {
			myBook.setExists(false);
		}
		return myBook;
	}
	
	
	/* (non-Javadoc)
	 * @see model.DAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(final Book pObjectToDelete) throws SQLException {
		CallableStatement stmt = c.prepareCall("{call uspDeleteBook(?)}");
		int n = 0;
		stmt.setInt(++n, pObjectToDelete.getBookID());
		stmt.executeQuery();
	}

	/**
	 * @param title the title to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByTitle(final String title) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByTitle(?)}");
		int n = 0;
		stmt.setString(++n, title);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}

	/**
	 * @param genre the genre to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByGenre(final String genre) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByGenre(?)}");
		int n = 0;
		stmt.setString(++n, genre);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}

	/**
	 * @param author the author to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByAuthor(final String author) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByAuthor(?)}");
		int n = 0;
		stmt.setString(++n, author);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}

	/**
	 * @param publisherID the publisherID to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByPublisherID(final int publisherID) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByPublisherID(?)}");
		int n = 0;
		stmt.setInt(++n, publisherID);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}

	/**
	 * @param isbn the isbn to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByIsbn(final String isbn) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByIsbn(?)}");
		int n = 0;
		stmt.setString(++n, isbn);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}
	
	/**
	 * @param low the low range of a price
	 * @param high the high range of a price
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByPriceRange(final double low, final double high) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByPriceRange(?,?)}");
		int n = 0;
		stmt.setDouble(++n, low);
		stmt.setDouble(++n, high);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}
	
	/**
	 * @param low the low range of a weight
	 * @param high the high range of a weight
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByWeightRange(final double low, final double high) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByShippingWeightRange(?,?)}");
		int n = 0;
		stmt.setDouble(++n, low);
		stmt.setDouble(++n, high);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}
	
	/**
	 * @param keyword the keyword to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<Book> searchByKeyword(final String keyword) throws SQLException {
		ArrayList<Book> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchBooksByKeyword(?)}");
		int n = 0;
		stmt.setString(++n, keyword);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Book b = new Book();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			books.add(b);
		}
		return books;
	}
}
