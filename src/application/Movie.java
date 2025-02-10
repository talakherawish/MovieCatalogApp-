package application;

public class Movie {
	private String title;
	private String description;
	private int releaseYear;
	private double rating;

	public Movie(String title, String description, int releaseYear, double rating) {
		this.title = title;
		this.description = description;
		this.releaseYear = releaseYear;
		if (rating >= 0.0 && rating <= 10.0) {
			this.rating = rating;
		} else {
			throw new IllegalArgumentException("Rating must be between 0.0 and 10.0");
		}
	}

	// Getters and setters
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		if (rating >= 0.0 && rating <= 10.0) {
			this.rating = rating;
		} else {
			throw new IllegalArgumentException("Rating must be between 0.0 and 10.0");
		}
	}
}
