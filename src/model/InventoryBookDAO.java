package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * a DAO for a combination of inventory and books.
 * @author jdowd
 *
 */
public class InventoryBookDAO implements DAO<InventoryBook> {
	
	/**
	 * This is the connection to the database.
	 */
	private Connection c;

	/**
	 * @param connect the connection to the database
	 * @throws SQLException an exception from the database
	 */
	public InventoryBookDAO(final Connection connect) throws SQLException {
		
		c = connect;
	}

	/* (non-Javadoc)
	 * @see model.DAO#update(java.lang.Object)
	 */
	@Override
	public void update(final InventoryBook pObjectToUpdate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspUpdateInvBook(?,?,?,?,?,?,?,?,?,?,?)}");
		stmt.setInt(++n, pObjectToUpdate.getBookID());
		stmt.setDouble(++n, pObjectToUpdate.getPublisherID());
		stmt.setString(++n, pObjectToUpdate.getTitle());
		stmt.setString(++n, pObjectToUpdate.getAuthor());
		stmt.setString(++n, pObjectToUpdate.getIsbn13());
		stmt.setString(++n, pObjectToUpdate.getIsbn10());
		stmt.setDouble(++n, pObjectToUpdate.getShippingWeight());
		stmt.setString(++n, pObjectToUpdate.getGenre());
		stmt.setDouble(++n, pObjectToUpdate.getRetailPrice());
		stmt.setInt(++n, pObjectToUpdate.getQuantityOnHand());
		stmt.setDouble(++n, pObjectToUpdate.getCost());
	
		stmt.executeQuery();
	}

	/* (non-Javadoc)
	 * @see model.DAO#create(java.lang.Object)
	 */
	@Override
	public void create(final InventoryBook pObjectToCreate) throws SQLException {
		int n = 0;
		CallableStatement stmt = c.prepareCall("{call uspCreateInvBook(?,?,?,?,?,?,?,?,?,?)}");
		stmt.setDouble(++n, pObjectToCreate.getPublisherID());
		stmt.setString(++n, pObjectToCreate.getTitle());
		stmt.setString(++n, pObjectToCreate.getAuthor());
		stmt.setString(++n, pObjectToCreate.getIsbn13());
		stmt.setString(++n, pObjectToCreate.getIsbn10());
		stmt.setDouble(++n, pObjectToCreate.getShippingWeight());
		stmt.setString(++n, pObjectToCreate.getGenre());
		stmt.setDouble(++n, pObjectToCreate.getRetailPrice());
		stmt.setInt(++n, pObjectToCreate.getQuantityOnHand());
		stmt.setDouble(++n, pObjectToCreate.getCost());
		
		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			pObjectToCreate.setBookID(r.getInt("bookID"));
		}
	}

	/* (non-Javadoc)
	 * @see model.DAO#getAll()
	 */
	@Override
	public List<InventoryBook> getAll() throws SQLException {
		List<InventoryBook> myInventoryBooks = new ArrayList<>();
		CallableStatement stmt = c.prepareCall("{call uspGetAllInvBooksWithPublisherNames()}");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook tempInventoryBook = new InventoryBook();
			tempInventoryBook.setAuthor(rs.getString("author"));
			tempInventoryBook.setBookID(rs.getInt("bookID"));
			tempInventoryBook.setGenre(rs.getString("genre"));
			tempInventoryBook.setIsbn10(rs.getString("ISBN10"));
			tempInventoryBook.setIsbn13(rs.getString("ISBN13"));
			tempInventoryBook.setPublisherID(rs.getInt("publisherID"));
			tempInventoryBook.setPublisherName(rs.getString("publisherName"));
			tempInventoryBook.setRetailPrice(rs.getDouble("retailPrice"));
			tempInventoryBook.setShippingWeight(rs.getDouble("shippingWeight"));
			tempInventoryBook.setTitle(rs.getString("title"));
			tempInventoryBook.setQuantityOnHand(rs.getInt("quantityOnHand"));
			tempInventoryBook.setCost(rs.getDouble("cost"));
			myInventoryBooks.add(tempInventoryBook);
		}
		return myInventoryBooks;
	}

	/* (non-Javadoc)
	 * @see model.DAO#get(int)
	 */
	@Override
	public InventoryBook get(final int pId) throws SQLException {
		InventoryBook myInventoryBook = new InventoryBook();
		CallableStatement stmt = c.prepareCall("{call uspGetInvBookWithPublisherName(?)}");
		int n = 0;
		stmt.setInt(++n, pId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			myInventoryBook.setAuthor(rs.getString("author"));
			myInventoryBook.setBookID(rs.getInt("bookID"));
			myInventoryBook.setGenre(rs.getString("genre"));
			myInventoryBook.setIsbn10(rs.getString("ISBN10"));
			myInventoryBook.setIsbn13(rs.getString("ISBN13"));
			myInventoryBook.setPublisherID(rs.getInt("publisherID"));
			myInventoryBook.setPublisherName(rs.getString("publisherName"));
			myInventoryBook.setRetailPrice(rs.getDouble("retailPrice"));
			myInventoryBook.setShippingWeight(rs.getDouble("shippingWeight"));
			myInventoryBook.setTitle(rs.getString("title"));
			myInventoryBook.setCost(rs.getDouble("cost"));
			myInventoryBook.setQuantityOnHand(rs.getInt("quantityOnHand"));
			myInventoryBook.setExists(true);
		} else {
			myInventoryBook.setExists(false);
		}
		return myInventoryBook;
	}
	
	
	/* (non-Javadoc)
	 * @see model.DAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(final InventoryBook pObjectToDelete) throws SQLException {
		InventoryBook b = get(pObjectToDelete.getBookID());
		CallableStatement stmt = c.prepareCall("{call uspDeleteInventoryItem(?)}");
		int n = 0;
		stmt.setInt(++n, b.getBookID());
		stmt.executeQuery();
	}
	/**
	 * @param title the title to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<InventoryBook> searchByTitle(final String title) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByTitle(?)}");
		int n = 0;
		stmt.setString(++n, title);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setPublisherName(rs.getString("publisherName"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setCost(rs.getDouble("cost"));
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
	public ArrayList<InventoryBook> searchByGenre(final String genre) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByGenre(?)}");
		int n = 0;
		stmt.setString(++n, genre);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setCost(rs.getDouble("cost"));
			b.setPublisherName(rs.getString("publisherName"));
			books.add(b);
		}
		return books;
	}
	/**
	 * @param author the author to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<InventoryBook> searchByAuthor(final String author) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByAuthor(?)}");
		int n = 0;
		stmt.setString(++n, author);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setCost(rs.getDouble("cost"));
			b.setPublisherName(rs.getString("publisherName"));
			books.add(b);
		}
		return books;
	}
	/**
	 * @param publisherID the publisherID to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<InventoryBook> searchByPublisherID(final int publisherID) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByPublisherID(?)}");
		int n = 0;
		stmt.setInt(++n, publisherID);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setCost(rs.getDouble("cost"));
			b.setPublisherName(rs.getString("publisherName"));
			books.add(b);
		}
		return books;
	}
	/**
	 * @param isbn the isbn to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<InventoryBook> searchByIsbn(final String isbn) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByIsbn(?)}");
		int n = 0;
		stmt.setString(++n, isbn);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setCost(rs.getDouble("cost"));
			b.setPublisherName(rs.getString("publisherName"));
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
	public ArrayList<InventoryBook> searchByPriceRange(final double low, final double high) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByPriceRange(?,?)}");
		int n = 0;
		stmt.setDouble(++n, low);
		stmt.setDouble(++n, high);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setPublisherName(rs.getString("publisherName"));
			b.setCost(rs.getDouble("cost"));
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
	public ArrayList<InventoryBook> searchByWeightRange(final double low, final double high) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByShippingWeightRange(?,?)}");
		int n = 0;
		stmt.setDouble(++n, low);
		stmt.setDouble(++n, high);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setPublisherName(rs.getString("publisherName"));
			b.setCost(rs.getDouble("cost"));
			books.add(b);
		}
		return books;
	}
	/**
	 * @param keyword the keyword to search for
	 * @return the book returned from the search
	 * @throws SQLException an exception from the database
	 */
	public ArrayList<InventoryBook> searchByKeyword(final String keyword) throws SQLException {
		ArrayList<InventoryBook> books = new ArrayList<>();

		CallableStatement stmt = c.prepareCall("{call uspSearchInvBooksByKeyword(?)}");
		int n = 0;
		stmt.setString(++n, keyword);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			InventoryBook b = new InventoryBook();
			b.setAuthor(rs.getString("author"));
			b.setTitle(rs.getString("title"));
			b.setBookID(rs.getInt("bookID"));
			b.setGenre(rs.getString("genre"));
			b.setIsbn10(rs.getString("ISBN10"));
			b.setIsbn13(rs.getString("ISBN13"));
			b.setPublisherID(rs.getInt("publisherID"));
			b.setRetailPrice(rs.getDouble("retailPrice"));
			b.setShippingWeight(rs.getDouble("shippingWeight"));
			b.setQuantityOnHand(rs.getInt("quantityOnHand"));
			b.setPublisherName(rs.getString("publisherName"));
			b.setCost(rs.getDouble("cost"));
			books.add(b);
		}
		return books;
	}
}
