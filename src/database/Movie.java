package database;

public class Movie {
	private Actor[] abridged_cast;
	private String id;
	private String title;
	private int year;
	private String mpaaRating;
	private Ratings ratings;
	
	public Actor[] getAbridged_cast() {
		return abridged_cast;
	}
	public void setAbridged_cast(Actor[] actors) {
		this.abridged_cast = actors;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getMpaaRating() {
		return mpaaRating;
	}
	public void setMpaaRating(String mpaaRating) {
		this.mpaaRating = mpaaRating;
	}
	public Ratings getRatings() {
		return ratings;
	}
	public void setRatings(Ratings ratings) {
		this.ratings = ratings;
	}
}
