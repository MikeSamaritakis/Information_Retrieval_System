import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class PublicVars {

/**-------------------------------------- Public Variable Declaration ------------------------------------------------*/

    public static final String BASE_URL = "https://www.motorcycleforum.com/";
    static Post post = new Post();
    static ArrayList<Post> posts_array = new ArrayList<Post>();
    public static final String INDEX_NAME = "motorcycle_forum";
    public static final ObjectMapper mapper = new ObjectMapper(); // Used in bulkDataPreparation
    public static List<WebDriver> drivers_list = new ArrayList<>();

    public static int index = 0;
}
