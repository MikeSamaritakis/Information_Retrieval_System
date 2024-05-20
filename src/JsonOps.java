import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class JsonOps {

    /**
     * This method writes the post data to a JSON file
     *
     * @param post The list of posts
     */
    public static void writePostToJsonFile(Post post) {
        // Directory check for storing the JSON files
        Path path = Paths.get("web_scraped_data\\");

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Directory created"); // Debugging purposes
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Directory already exists"); // Debugging purposes
        }

        // Create a new JSONArray
        JSONArray postDataArray = new JSONArray();

        // Check if the post is null or if the post URL or post title is empty and throw an error if so.
        if (post == null || post.getURL().isEmpty() || post.getTitle().isEmpty()) {
            assert post != null;
            errorInExtractingPostDetails(post.getURL());
            //continue;
        }

        JSONObject postData = new JSONObject();
        postData.put("URL", post.getURL());
        postData.put("Title", post.getTitle());
        postData.put("Author", post.getAuthor());
        postData.put("Date", dateFormatting(post.getDate()));
        //System.out.println("Date: " + dateFormatting(post.getPostDate())); // Debugging purposes
        postData.put("Breadcrumbs", post.Breadcrumbs);
        postData.put("Tags", post.Tags);
        String contentWithLineBreaks = post.getContent().replace("\\n", System.lineSeparator());
        postData.put("Content", contentWithLineBreaks);

        // Read the existing data from the file
        File file = new File("web_scraped_data\\" + "post_data_" + PublicVars.posts_array.size() + ".json");
        if (file.length() != 0) {
            try (FileReader reader = new FileReader("web_scraped_data\\" + "post_data_" + PublicVars.posts_array.size() + ".json")) {
                postDataArray = new JSONArray(new JSONTokener(reader));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            postDataArray = new JSONArray();
        }

        // Add the new post data to the JSONArray
        postDataArray.put(postData);

        // Write the updated JSONArray back to the file
        try (FileWriter fileWriter = new FileWriter("web_scraped_data\\" + "post_data_" + PublicVars.posts_array.size() + ".json")) {
            fileWriter.write(postDataArray.toString(2)); // Pretty print
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when there is an error in extracting post details
     *
     * @param url The URL of the post
     */
    private static void errorInExtractingPostDetails(String url) {
        // Construct the JSON object
        JSONObject postData = new JSONObject();

        if (url == null || url.isEmpty()) {
            postData.put("Error", "Error in extracting post details for URL: null@ file: post_data_" + PublicVars.posts_array.size() + ".json");
            return;
        }

        // Add the URL to the JSON object
        postData.put("Error", "Error in extracting post details for URL: " + url + "@ file: post_data_" + PublicVars.posts_array.size() + ".json");

        // Save the JSON object to a file
        try (FileWriter file = new FileWriter("post_data_errors.json", true)) { // true to append
            file.write(postData.toString(2)); // Pretty print
            file.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method prepares the data for the bulk API
     *
     * @param sourceDir The source directory
     * @param targetDir The target directory
     * @throws IOException
     */
    public static void prepareBulkApiData(String sourceDir, String targetDir) throws IOException {
        System.out.println("prepareBulkApiData started\n"); // Debugging purposes

        ObjectMapper mapper = new ObjectMapper();
        File[] files = new File(sourceDir).listFiles((dir, name) -> name.endsWith(".json"));

        // Create the target directory if it doesn't exist
        File targetDirectory = new File(targetDir);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String postId = fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('.'));
                //System.out.println("POSTID: " + postId); // Debugging purposes

                // Remove the part "_data" from each postId
                postId = postId.replace("data_", "");

                List<Map<String, Object>> docs = mapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {
                });

                for (Map<String, Object> doc : docs) {
                    // Edit the date format to be compatible with ElasticSearch
                    doc.put("Date", dateFormatting((String) doc.get("Date")));
                    //System.out.println("Date: " + dateFormatting((String) doc.get("Date"))); // Debugging purposes

                    Map<String, Object> action = Map.of("index", Map.of(
                            "_index", PublicVars.INDEX_NAME,
                            "_id", postId
                    ));

                    String actionJson = mapper.writeValueAsString(action) + " \n";
                    String docJson = mapper.writeValueAsString(doc) + "\n";

                    String bulkData = actionJson + docJson;

                    Path targetPath = Paths.get(targetDir, fileName);
                    Files.write(targetPath, bulkData.getBytes());

                    System.out.println("prepareBulkApiData exited\n"); // Debugging purposes
                }
            }
        }

        //prepareBulkApiData("web_scraped_data", "bulk_web_scraped_data");
    }

    /**
     * This method creates a Post object from a JSON file
     *
     * @param jsonFilePath The path to the JSON file
     * @return The Post object
     */
    public static Post createPostFromJson(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        Post post = new Post();

        try {
            JsonNode rootNode = mapper.readTree(new File(jsonFilePath));

            if (rootNode.has("URL")) {
                post.setURL(rootNode.get("URL").asText());
                System.out.println("URL: " + post.getURL());
            }
            if (rootNode.has("Title")) {
                post.setTitle(rootNode.get("Title").asText());
                System.out.println("Title: " + post.getTitle());
            }
            if (rootNode.has("Author")) {
                post.setAuthor(rootNode.get("Author").asText());
                System.out.println("Author: " + post.getAuthor());
            }
            if (rootNode.has("Date")) {
                post.setDate(rootNode.get("Date").asText());
                System.out.println("Date: " + post.getDate());
            }
            if (rootNode.has("Content")) {
                post.setContent(rootNode.get("Content").asText());
                System.out.println("Content: " + post.getContent());
            }
            if (rootNode.has("Breadcrumbs")) {
                post.setBreadcrumbs(rootNode.get("Breadcrumbs").asText());
                System.out.println("Breadcrumbs: " + post.getBreadcrumbs());
            }
            if (rootNode.has("Tags")) {
                ArrayList<String> tags = new ArrayList<>();
                for (JsonNode tagNode : rootNode.get("Tags")) {
                    tags.add(tagNode.asText());
                    System.out.println("Tags: " + tags.toString());
                }
                post.setTags(tags);
            }
            if (rootNode.has("index")) {
                post.setIndex(rootNode.get("index").asInt());
                System.out.println("Index: " + post.getIndex());
            }

            post.printPost(post);
            return post;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method extracts the ID that corresponds to each generated JSON document
     *
     * @param jsonFilePath
     * @return The docID as a String value
     */
    public static String getIdFromJsonFilePath(String jsonFilePath) {
        System.out.println("getIdFromJsonFilePath started\n"); // Debugging purposes
        if (jsonFilePath.contains("/") || jsonFilePath.contains("\\")) {
            jsonFilePath = jsonFilePath.substring(jsonFilePath.lastIndexOf("/") + 1); // Extract the file name from the path
        }
        String number = jsonFilePath.substring(jsonFilePath.indexOf('_') + 1, jsonFilePath.indexOf('.')); // Extract the number from the file name
        number = number.replace("post_", "");
        number = number.replace("data_", "");
        //System.out.println("Number: " + number); // Debugging purposes
        System.out.println("getIdFromJsonFilePath exited\n"); // Debugging purposes
        return number;
    }

    /**
     * This method prepares the data for the bulk API for a single document
     *
     * @param jsonFilePath The path to the JSON file
     * @throws IOException
     */
    public static void prepareBulkApiDataSINGLE(String jsonFilePath) throws IOException {
        System.out.println("prepareBulkApiDataSINGLE started\n"); // Debugging purposes

        String targetDir = "bulk_web_scraped_data";
        ObjectMapper mapper = new ObjectMapper();

        // Create the target directory if it doesn't exist
        File targetDirectory = new File(targetDir);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        File file = new File(jsonFilePath);
        if (file.exists() && file.getName().endsWith(".json")) {
            String fileName = file.getName();
            String postId = fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('.'));
            //System.out.println("POSTID: " + postId); // Debugging purposes

            // Remove the part "_data" from each postId
            postId = postId.replace("post_data_", "");

            List<Map<String, Object>> docs = mapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {
            });

            for (Map<String, Object> doc : docs) {
                Map<String, Object> action = Map.of("index", Map.of(
                        "_index", PublicVars.INDEX_NAME,
                        "_id", postId
                ));

                String actionJson = mapper.writeValueAsString(action) + "\n";
                String docJson = mapper.writeValueAsString(doc) + "\n";

                String bulkData = actionJson + docJson;

                Path targetPath = Paths.get(targetDir, fileName);
                Files.write(targetPath, bulkData.getBytes());

                System.out.println("prepareBulkApiDataSINGLE exited\n"); // Debugging purposes
            }
        }
    }

    /**
     * This method formats the date to be compatible with ElasticSearch
     *
     * @param old_date The old date format
     * @return The new date format
     */
    public static String dateFormatting(String old_date) {
        List<String> formats = Arrays.asList(
                "yyyy-MM-dd",
                "MMM d, yyyy",
                "EEE MMM dd HH:mm:ss zzz yyyy",
                "dd-MM-yyyy",
                "MM/dd/yyyy",
                "yyyy/MM/dd",
                "dd MMM yyyy"
        );
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (String format : formats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format);
                Date date_tmp = inputFormat.parse(old_date);
                return outputFormat.format(date_tmp);
            } catch (ParseException e) {
                // Ignore and try the next format
            }
        }

        // If none of the formats can parse the date, throw an exception
        throw new RuntimeException("Unparseable date: " + old_date);
    }

    /**
     * This method reads a file and returns its content
     *
     * @param filePath The path to the file
     * @return The content of the file
     * @throws IOException
     */
    private static String readFile(String filePath) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            fileContent.append(line);
        }
        reader.close();
        fileContent.append("\n");
        return fileContent.toString();
    }

    /**
     * This method writes the content to a file
     *
     * @param content  The content to write
     * @param filePath The path to the file
     * @throws IOException
     */
    private static void writeFile(String content, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.write("\n");
        writer.close();
    }

    /**
     * This method merges JSON files into a single JSON file
     *
     * @param directoryPath The path to the directory containing the JSON files
     * @param outputFile    The path to the output JSON file
     * @throws IOException
     */
    public static void mergeJSON(String directoryPath, String outputFile) throws IOException {
        StringBuilder combinedJson = new StringBuilder();

        for (String fileName : new java.io.File(directoryPath).list()) {
            if (fileName.endsWith(".json")) {
                String filePath = directoryPath + "/" + fileName;
                String fileIdentifier = readJsonFileLineByLineIdentifier(filePath);
                String fileContent = readJsonFileLineByLineContent(filePath);
                combinedJson.append(fileIdentifier);
                combinedJson.append("\n");
                combinedJson.append(fileContent);
                combinedJson.append("\n");
            }
        }

        writeFile(combinedJson.toString(), outputFile);

        System.out.println("JSON files concatenated to: " + outputFile);
    }

    /**
     * This method reads a JSON file line by line and returns the line that contains the identifier
     *
     * @param filePath The path to the JSON file
     * @return The identifier line
     */
    public static String readJsonFileLineByLineIdentifier(String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            String identifier = lines.filter(line -> line.contains(PublicVars.INDEX_NAME)).findFirst().orElse(null);
            return identifier;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method reads a JSON file line by line and returns the line that contains the content
     *
     * @param filePath The path to the JSON file
     * @return The content line
     */
    public static String readJsonFileLineByLineContent(String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            String content = lines.filter(line -> line.contains("Content")).findFirst().orElse(null);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}