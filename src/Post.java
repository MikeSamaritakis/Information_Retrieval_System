import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

/**-------------------------------------- Object Post related methods ------------------------------------------------*/

    String URL;
    String Title;
    String Content;
    String Author;
    String Date;
    String Breadcrumbs;
    ArrayList<String> Tags;
    int index;

    /** Getters */

    public String getURL() {
        return URL;
    }

    public String getTitle() {
        return Title;
    }

    public String getContent() {
        return Content;
    }

    public String getAuthor() {
        return Author;
    }

    public String getDate() {
        return Date;
    }

    public String getBreadcrumbs() {
        return Breadcrumbs;
    }

    public ArrayList<String> getTags() {
        return Tags;
    }

    public int getIndex() {
        return index;
    }

    /** Setters */
    void setPost(String post_url,
                 String post_title,
                 String post_content,
                 String post_author,
                 String post_date,
                 String breadcrumbsElements,
                 ArrayList<String> tagsElements,
                 int index) {
        this.URL = post_url;
        this.Title = post_title;
        this.Content = post_content;
        this.Author = post_author;
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM d, yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        try {
            Date date_tmp = inputFormat.parse(post_date);
            String formattedDate = outputFormat.format(date_tmp);
            this.Date = formattedDate;
            this.Breadcrumbs = breadcrumbsElements;
            this.Tags = tagsElements;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setURL(String url) {
        this.URL = url;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public void setAuthor(String author) {
        this.Author = author;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setBreadcrumbs(String breadcrumbs) {
        this.Breadcrumbs = breadcrumbs;
    }

    public void setTags(ArrayList<String> tags) {
        this.Tags = tags;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /** Print the Post object */
    public void printPost(Post post) {
        System.out.println("\nURL: " + post.URL + "\n");
        System.out.println("Title: " + post.Title + "\n");
        System.out.println("Content: " + post.Content + "\n");
        System.out.println("Author: " + post.Author + "\n");
        System.out.println("Date: " + post.Date + "\n");
        System.out.println("Breadcrumbs: " + post.Breadcrumbs + "\n");
        System.out.println("Tags: " + post.Tags + "\n\n");
    }
}
