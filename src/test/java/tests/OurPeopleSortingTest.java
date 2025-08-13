package tests;

import base.TestBase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.OurPeoplePage;
import pages.ProfileData;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class OurPeopleSortingTest extends TestBase {

	@Test
	public void verifyOurPeopleSorting() {
		driver.get("https://www.clearlyrated.com/staffing/md-usa/hanover-md/actalent-hanover-md");
		try {
			WebElement acceptAllBtn = driver.findElement(By.cssSelector(
					"div.cky-consent-container > div.cky-consent-bar > button.cky-banner-btn-close > img[alt=\"Close\"]"));
			acceptAllBtn.click();
			System.out.println("Cookie popup accepted.");
		} catch (NoSuchElementException e) {
			System.out.println("No cookie popup found.");
		}
		OurPeoplePage peoplePage = new OurPeoplePage(driver);
		List<ProfileData> profiles = peoplePage.getAllProfiles();

		// Group 1: Photo + Rating
		List<ProfileData> group1 = new ArrayList<>();
		// Group 2: Photo + No Rating
		List<ProfileData> group2 = new ArrayList<>();
		// Group 3: No Photo
		List<ProfileData> group3 = new ArrayList<>();

		// Manual grouping
		for (ProfileData p : profiles) {
			if (p.hasPhoto() && p.getStarRating() != null) {
				group1.add(p);
			} else if (p.hasPhoto() && p.getStarRating() == null) {
				group2.add(p);
			} else if (!p.hasPhoto()) {
				group3.add(p);
			}
		}
		// Verify sorting for each group
		verifySorting(group1, true);
		verifySorting(group2, false);
		verifySorting(group3, true);
	}

	private void verifySorting(List<ProfileData> group, boolean hasRatings) {
		List<ProfileData> expectedSorted = new ArrayList<>(group);
		for (int i = 0; i < expectedSorted.size() - 1; i++) {
			for (int j = i + 1; j < expectedSorted.size(); j++) {
				if (shouldSwap(expectedSorted.get(i), expectedSorted.get(j), hasRatings)) {
					ProfileData temp = expectedSorted.get(i);
					expectedSorted.set(i, expectedSorted.get(j));
					expectedSorted.set(j, temp);
				}
			}
		}
		System.out.println("\n--- UI Order ---");
		printGroup(group);

		System.out.println("\n--- Expected Sorted Order ---");
		printGroup(expectedSorted);

		Assert.assertEquals(group, expectedSorted, "Sorting mismatch in group");

	}

	private void printGroup(List<ProfileData> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println("Index " + i + ": " + list.get(i));
		}
	}

	private boolean shouldSwap(ProfileData p1, ProfileData p2, boolean hasRatings) {
		if (hasRatings) {
			// Rule 1: Highest star rating first
			Double r1 = p1.getStarRating();
			Double r2 = p2.getStarRating();

			if (r1 != null && r2 != null) {
				if (!r1.equals(r2)) {
					return r1 < r2;
				}
			}

		}

		// Rule 2: More survey responses first (total if multiple)
		int s1 = p1.getTotalSurveyResponses();
		int s2 = p2.getTotalSurveyResponses();
		if (s1 != s2) {
			return s1 < s2;
		}

		// Rule 3: Alphabetical by first name
		return p1.getFirstName().compareToIgnoreCase(p2.getFirstName()) > 0;
	}
}