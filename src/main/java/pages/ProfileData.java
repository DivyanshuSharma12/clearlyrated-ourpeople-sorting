package pages;

public class ProfileData {
	private String firstName;
	private boolean hasPhoto;
	private Double starRating;
	private int totalSurveyResponses;

	public ProfileData(String firstName, boolean hasPhoto, Double starRating, int totalSurveyResponses) {
		this.firstName = firstName;
		this.hasPhoto = hasPhoto;
		this.starRating = starRating;
		this.totalSurveyResponses = totalSurveyResponses;
	}

	public String getFirstName() {
		return firstName;
	}

	public boolean hasPhoto() {
		return hasPhoto;
	}

	public Double getStarRating() {
		return starRating;
	}

	public int getTotalSurveyResponses() {
		return totalSurveyResponses;
	}

	@Override
	public String toString() {
		return "ProfileData{" + "firstName='" + firstName + '\'' + ", hasPhoto=" + hasPhoto + ", starRating="
				+ (starRating != null ? starRating : "N/A") + ", totalSurveyResponses=" + totalSurveyResponses + '}';
	}
}