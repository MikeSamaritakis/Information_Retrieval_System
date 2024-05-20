import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class WebCrawler {

/**------------------------------------- Web crawling related methods ------------------------------------------------*/

    /**
     * This method is the main method that crawls the web
     */
    public static void WebCrawler(){
        //System.setProperty("webdriver.chrome.driver", "chromedriver-win64/chromedriver.exe"); // THIS ALSO WORKS
        System.setProperty("webdriver.chrome.driver", ENV_vars.chromedriver); // Path to the chromedriver executable

        List<String> URL_List = makeListofURLs(PublicVars.BASE_URL);

        // Loop through the list of URLs and extract the post details using the method extractPostDetails()
        for (String url : URL_List) {
            extractPostDetails(url);//, driver);
            // Write the post data to a JSON file also happens in the extractPostDetails() method
        }

        closeAll();

        try {
            JsonOps.prepareBulkApiData("web_scraped_data", "bulk_web_scraped_data");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //System.out.println("\n\n\nCrawling process completed. Total posts extracted: " + posts_array.size() + "\n");
    }

    /**
     * This method makes a list of URLs to crawl
     * @param url The BASE URL to crawl
     * @return A list of URLs to crawl
     */
    private static List<String> makeListofURLs(String url) {
        WebDriver driver = new ChromeDriver();

        driver.get(PublicVars.BASE_URL);

        // Wait for the cookies button to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Locate the cookies button and wait until it's clickable, then click it
        WebElement cookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.css-47sehv")));
        cookiesButton.click();
        System.out.print("Cookies button clicked\n"); // Debugging purposes

        // Locate the "Load More" button and click it
        WebElement button = driver.findElement(By.cssSelector("button.button.button--alt.feed-button"));
        // Scroll to the element
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
        System.out.print("Load More button clicked for the first time\n"); // Debugging purposes

        long endTime = System.currentTimeMillis() + 60000; // 10 seconds in future
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (System.currentTimeMillis() < endTime) {
            WebElement footer = driver.findElement(By.cssSelector(".p-footer-inner"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", footer);
            System.out.println("We reached the footer of the page"); // Debugging purposes

            try {
                WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement loadMoreButton = wait2.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.button.button--alt.feed-button")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loadMoreButton);
                if (loadMoreButton.isDisplayed() == false) {
                    System.out.println("Load More button not displayed"); // Debugging purposes
                } else {
                    loadMoreButton.click();
                    System.out.println("Load More button clicked"); // Debugging purposes
                }

                try {
                    Thread.sleep(1000); // Scroll every 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (TimeoutException e) {
                System.out.println("Load More button not found"); // Debugging purposes
            }
        }

        WebElement top_of_page = driver.findElement(By.cssSelector("div.p-header-content"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", top_of_page);
        System.out.println("Scrolled to the top of the page\n"); // Debugging purposes


        // Get all the links on the page
        List<WebElement> links = driver.findElements(By.tagName("a"));
        List<String> postLinks = new ArrayList<>();

        // Extract the URLs from the links
        for (WebElement link : links) {
            String tmpurl = link.getAttribute("href");
            if (tmpurl != null && postLinks.contains(tmpurl) == false && tmpurl.contains("threads") == true && tmpurl.endsWith("/latest") == false) {
                postLinks.add(tmpurl);
                //System.out.println(tmpurl); // Debugging purposes
            }
            else if (tmpurl != null && postLinks.contains(tmpurl) == true && tmpurl.contains("threads") == true) {
                //System.out.println("Duplicate: " + tmpurl + "\n"); // Debugging purposes
            }

        }

        // The first two URLs are always repeated, so remove them
        postLinks.remove(0);
        postLinks.remove(1);

        // Print the URLs for Debugging purposes
        for (String item : postLinks) {
            System.out.println(item);
        }

        System.out.println("Number of links: " + postLinks.size()); // Debugging purposes

        kill_driver(driver);

        return postLinks;
    }

    /**
     * This method extracts the post details from a given URL
     * @param post_url The URL of the post
     */
    private static void extractPostDetails(String post_url){//, WebDriver driver) {
        try {
            WebDriver driver = new ChromeDriver();
            driver.get(post_url);

            // Add the driver to the list of drivers to ensure they are all closed at the end
            PublicVars.drivers_list.add(driver);
            System.out.println("Fetching URL: " + post_url + "\n");

            String post_title;
            try {
                post_title = driver.getTitle();
                if (post_title == null || post_title.isEmpty()) {
                    // Error handling for the title not being found
                    try{
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
                        WebElement contentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.MessageCard__thread-title")));
                        post_title = contentElement.getText();
                    } catch (NoSuchElementException e) {
                        post_title = "NONE"; // Default value
                    }
                }
                post_title = post_title.replaceFirst(" \\| Motorcycle Forum", "");
            } catch (java.util.NoSuchElementException e) {
                post_title = "NONE"; // Default value
            }

            String post_content;
            try {
                //post_content = driver.findElement(By.cssSelector(".message-body.js-selectToQuote .bbWrapper")).getText();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
                WebElement contentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message-body.js-selectToQuote .bbWrapper")));
                post_content = contentElement.getText();
            } catch (NoSuchElementException e) {
                post_content = "NONE"; // Default value
            }

            String post_author = driver.findElement(By.cssSelector("a.MessageCard__user-info__name")).getText();

            String post_date = driver.findElement(By.cssSelector("time[qid='post-date-time']")).getAttribute("data-date-string");

            String post_breadcrumbs = driver.findElement(By.cssSelector("ul.p-breadcrumbs")).getText();

            ArrayList<String> post_tags = new ArrayList<String>();
            List<WebElement> tags = driver.findElements(By.cssSelector("div.additional-header__tags dl.tagList dd span.js-tagList a.tagItem"));
            for (WebElement tag : tags) {
                //System.out.println(tag.getText()); // Debugging purposes
                post_tags.add(tag.getText());
            }

            // Set post data
            PublicVars.post.setPost(post_url, post_title, post_content, post_author, post_date, post_breadcrumbs, post_tags, PublicVars.index);
            JsonOps.writePostToJsonFile(PublicVars.post);
            PublicVars.index++;
            //PublicVars.post.printPost(PublicVars.post); // Debugging purposes

            // Add the post to the Post Array
            PublicVars.posts_array.add(PublicVars.post);

            System.out.println("Extracted content from URL: " + post_url); // Debugging purposes

            kill_driver(driver);

        } catch (TimeoutException e) {
            System.out.println("Page did not load within 8 seconds, skipping: " + post_url);
        } catch (Exception e) {
            System.out.println("Page did not load within the expected time, skipping: " + post_url + "\n");
            System.out.println("Error while extracting data: " + e.getMessage());
        }
    }

    /**
     * This method kills the crawler
     * @param driver The WebDriver object
     */
    public static void kill_driver(WebDriver driver){
        driver.quit();
        System.out.println("\n\nThe driver is killed\n\n");
    }

    /**
     * This method closes all the drivers that have somehow stayed open
     */
    public static void closeAll() {
        for (WebDriver driver : PublicVars.drivers_list) {
            driver.quit();
        }
    }

}