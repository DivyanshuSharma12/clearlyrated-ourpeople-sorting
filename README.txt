# Our People Group Sorting Test

This project automates the verification of profile sorting on the **"Our People"** page of ClearlyRated's website's homepage.  
It uses **Java + Selenium + WebDriver manager + Maven + TestNG** .

## Features
- Fetches **all profiles across multiple paginated pages**.
- Groups profiles into:
  1. **Photo + Rating**
  2. **Photo + No Rating**
  3. **No Photo**
- Applies sorting rules for each group:
  1. **Highest star rating first** (if applicable)
  2. **Most survey responses first**
  3. **Alphabetical by first name**
- Compares the **UI order** vs. **expected sorted order** and reports mismatches.

## Notes
- Profiles are dynamically loaded; scrolling is handled automatically.  
- Pagination is supported to fetch all profiles, but in the current version of the site, not all profiles or pagination data may be available.  
- Due to this, the framework fetches the available profiles and performs grouping and sorting verification on the data that is present.  