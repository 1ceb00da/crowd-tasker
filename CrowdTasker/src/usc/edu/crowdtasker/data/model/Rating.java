package usc.edu.crowdtasker.data.model;

public class Rating {
	
	public static final String FROM_ID_COL = "FROM_ID";
	private long fromId;
	
	public static final String TO_ID_COL = "TO_ID";
	private long toId;
	
	public static final String RATING_COL = "RATING";
	private double rating;
	/**
	 * @return the fromId
	 */
	public long getFromId() {
		return fromId;
	}
	/**
	 * @param fromId the fromId to set
	 */
	public void setFromId(long fromId) {
		this.fromId = fromId;
	}
	/**
	 * @return the toId
	 */
	public long getToId() {
		return toId;
	}
	/**
	 * @param toId the toId to set
	 */
	public void setToId(long toId) {
		this.toId = toId;
	}
	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	
	
}
