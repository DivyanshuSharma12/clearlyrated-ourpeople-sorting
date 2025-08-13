package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OurPeoplePage extends BasePage {

	@FindAll(@FindBy(css = ".profilepeople__card"))
	private List<WebElement> profileCards;

	@FindBy(css = ".pagination__stepper--next")
	private WebElement nextButton;

	@FindBy(css = ".pagination__range")
	private WebElement resultsInfo;

	private JavascriptExecutor js;

	public OurPeoplePage(WebDriver driver) {
		super(driver);
		js = (JavascriptExecutor) driver;
	}

	public List<ProfileData> getAllProfiles() {
		List<ProfileData> allProfiles = new ArrayList<>();

		do {
			List<ProfileData> currentPageProfiles = getProfilesOnCurrentPage();
			if (currentPageProfiles.isEmpty()) {
				String msg = "No profiles found on page when pagination suggested there should be data and so continuing test with profiles we got till now";
				System.out.println(msg);
				return allProfiles;
			}
			allProfiles.addAll(currentPageProfiles);
		} while (goToNextPageIfExists());

		return allProfiles;
	}

	private List<ProfileData> getProfilesOnCurrentPage() {
		List<ProfileData> profiles = new ArrayList<>();

		for (WebElement card : profileCards) {
			js.executeScript("arguments[0].scrollIntoView(true);", card);
			boolean hasPhoto = elementExists(card, By.cssSelector(".profilepeople__image--link"));

			String name = card.findElement(By.cssSelector(".profilepeople__name--link")).getText().trim();
			String firstName = name.split(" ")[0];

			Double rating = null;
			List<WebElement> ratingElements = findElementsQuick(card,
					By.cssSelector("div.profilepeople-stars__text > span:first-child"));
			for (WebElement ratingElement : ratingElements) {
				try {
					double value = Double.parseDouble(ratingElement.getText().trim().replaceAll("[^0-9.]", ""));
					if (rating == null || value > rating) {
						rating = value; // keep the highest rating
					}
				} catch (NumberFormatException ignored) {

				}
			}

			int totalSurveyResponses = 0;
			List<WebElement> surveyElements = findElementsQuick(card,
					By.cssSelector("div.profilepeople-stars__text > span:nth-child(3)"));
			for (WebElement survey : surveyElements) {
				String num = survey.getText().replaceAll("[^0-9]", "");
				if (!num.isEmpty()) {
					totalSurveyResponses += Integer.parseInt(num);
				}
			}
			ProfileData profile = new ProfileData(firstName, hasPhoto, rating, totalSurveyResponses);
			profiles.add(profile);
		}

		return profiles;
	}

	private boolean elementExists(WebElement parent, By locator) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
		boolean exists = !parent.findElements(locator).isEmpty();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		return exists;
	}

	private List<WebElement> findElementsQuick(WebElement parent, By locator) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
		List<WebElement> elements = parent.findElements(locator);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		return elements;
	}

	private boolean goToNextPageIfExists() {
		String text = resultsInfo.getText(); // e.g: "Showing 1-10 of 786"
		String tempCleaned = text.replaceAll("-", " ");
		String cleaned = tempCleaned.replaceAll("[^0-9 ]", "");
		String[] parts = cleaned.trim().split("\\s+");

		int lastNum = Integer.parseInt(parts[1]);
		int totalNum = Integer.parseInt(parts[2]);

		// Only click next if there are still profiles left
		if (lastNum < totalNum && nextButton.isDisplayed() && nextButton.isEnabled()) {
			nextButton.click();

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			// Wait until scroll starts and then stops
			wait.until(driver -> {
				long initialY = ((Number) js.executeScript("return window.scrollY;")).longValue();

				// Wait until scroll position changes (scroll started)
				long startTime = System.currentTimeMillis();
				while (((Number) js.executeScript("return window.scrollY;")).longValue() == initialY) {
					if (System.currentTimeMillis() - startTime > 5000)
						return false; // timeout safeguard
				}

				// Now wait until scrollY stops changing (scroll ended)
				long lastY;
				do {
					lastY = ((Number) js.executeScript("return window.scrollY;")).longValue();
					try {
						Thread.sleep(100);
					} catch (InterruptedException ignored) {
					}
				} while (((Number) js.executeScript("return window.scrollY;")).longValue() != lastY);

				return true;
			});
			// Reinitialize all page elements
			PageFactory.initElements(driver, this);
			return true;
		}
		return false;
	}
}