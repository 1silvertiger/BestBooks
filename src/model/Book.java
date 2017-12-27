package model;

/**
 * a book object.
 * @author jdowd
 *
 */
public class Book {
	/** the object's book id. */
	private int bookID;
	/** the object's publisher id. */
	private int publisherID;
	/** the object's title. */
	private String title;
	/** the object's author. */
	private String author;
	/** the object's isbn13. */
	private String isbn13;
	/** the object's isbn10. */
	private String isbn10;
	/** the object's shipping weight. */
	private double shippingWeight;
	/** the object's genre. */
	private String genre;
	/** the object's retail price. */
	private double retailPrice;
	/** the object's publisher name. */
	private String publisherName;
	
	/** if the object exists. */
	private boolean exists = false;




	/**
	 * @return the bookID
	 */
	public int getBookID() {
		return bookID;
	}




	/**
	 * @param bookID the bookID to set
	 */
	public void setBookID(final int bookID) {
		this.bookID = bookID;
	}




	/**
	 * @return the publisherID
	 */
	public int getPublisherID() {
		return publisherID;
	}




	/**
	 * @param publisherID the publisherID to set
	 */
	public void setPublisherID(final int publisherID) {
		this.publisherID = publisherID;
	}




	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}




	/**
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}




	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}




	/**
	 * @param author the author to set
	 */
	public void setAuthor(final String author) {
		this.author = author;
	}




	/**
	 * @return the isbn13
	 */
	public String getIsbn13() {
		return isbn13;
	}




	/**
	 * @param isbn13 the isbn13 to set
	 */
	public void setIsbn13(final String isbn13) {
		this.isbn13 = isbn13;
	}




	/**
	 * @return the isbn10
	 */
	public String getIsbn10() {
		return isbn10;
	}




	/**
	 * @param isbn10 the isbn10 to set
	 */
	public void setIsbn10(final String isbn10) {
		this.isbn10 = isbn10;
	}




	/**
	 * @return the shippingWeight
	 */
	public double getShippingWeight() {
		return shippingWeight;
	}




	/**
	 * @param shippingWeight the shippingWeight to set
	 */
	public void setShippingWeight(final double shippingWeight) {
		this.shippingWeight = shippingWeight;
	}




	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}




	/**
	 * @param genre the genre to set
	 */
	public void setGenre(final String genre) {
		this.genre = genre;
	}




	/**
	 * @return the retailPrice
	 */
	public double getRetailPrice() {
		return retailPrice;
	}




	/**
	 * @param retailPrice the retailPrice to set
	 */
	public void setRetailPrice(final double retailPrice) {
		this.retailPrice = retailPrice;
	}




	/**
	 * @return the publisherName
	 */
	public String getPublisherName() {
		return publisherName;
	}




	/**
	 * @param publisherName the publisherName to set
	 */
	public void setPublisherName(final String publisherName) {
		this.publisherName = publisherName;
	}




	/**
	 * @return the exists
	 */
	public boolean isExists() {
		return exists;
	}




	/**
	 * @param exists the exists to set
	 */
	public void setExists(final boolean exists) {
		this.exists = exists;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + bookID;
		result = prime * result + (exists ? 1231 : 1237);
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((isbn10 == null) ? 0 : isbn10.hashCode());
		result = prime * result + ((isbn13 == null) ? 0 : isbn13.hashCode());
		result = prime * result + publisherID;
		result = prime * result + ((publisherName == null) ? 0 : publisherName.hashCode());
		long temp;
		temp = Double.doubleToLongBits(retailPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(shippingWeight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Book other = (Book) obj;
		if (author == null) {
			if (other.author != null) {
				return false;
			}
		} else if (!author.equals(other.author)) {
			return false;
		}
		if (bookID != other.bookID) {
			return false;
		}
		if (exists != other.exists) {
			return false;
		}
		if (genre == null) {
			if (other.genre != null) {
				return false;
			}
		} else if (!genre.equals(other.genre)) {
			return false;
		}
		if (isbn10 == null) {
			if (other.isbn10 != null) {
				return false;
			}
		} else if (!isbn10.equals(other.isbn10)) {
			return false;
		}
		if (isbn13 == null) {
			if (other.isbn13 != null) {
				return false;
			}
		} else if (!isbn13.equals(other.isbn13)) {
			return false;
		}
		if (publisherID != other.publisherID) {
			return false;
		}
		if (publisherName == null) {
			if (other.publisherName != null) {
				return false;
			}
		} else if (!publisherName.equals(other.publisherName)) {
			return false;
		}
		if (Double.doubleToLongBits(retailPrice) != Double.doubleToLongBits(other.retailPrice)) {
			return false;
		}
		if (Double.doubleToLongBits(shippingWeight) != Double.doubleToLongBits(other.shippingWeight)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}



}
