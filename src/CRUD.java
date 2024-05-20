import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.openqa.selenium.NotFoundException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class CRUD {

/**-------------------- Elasticsearch related methods CRUD: Create, Read, Update, Delete -----------------------------*/

    /**
     * This method creates an Elasticsearch client using an API key
     */
    public static ElasticsearchClient createClient() {
        // Do not forget to replace "api_key_here" with your actual API key.
        String apiKey = "your_api_key"; // Replace with your actual API key
        Header[] defaultHeaders = new Header[]{new BasicHeader("Authorization", "ApiKey " + ENV_vars.API_KEY)};
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http"))
                .setDefaultHeaders(defaultHeaders)
                .setRequestConfigCallback(
                        requestConfigBuilder -> requestConfigBuilder
                                .setConnectTimeout(60000) // Connection timeout
                                .setSocketTimeout(60000) // Socket timeout
                )
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    /**
     * This method closes the Elasticsearch client
     * @param client The Elasticsearch client
     */
    public static void closeClient(ElasticsearchClient client) {
        System.out.println("closeClient started\n"); // Debugging purposes
        RestClientTransport transport = (RestClientTransport) client._transport();
        try {
            transport.close();
            System.out.println("closeClient exited\n"); // Debugging purposes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates an index in Elasticsearch
     * @param client The Elasticsearch client
     * @param jsonFilePath The JSON file path for the post we want to create in the index
     * @throws IOException
     */
    public static void indexDocument(ElasticsearchClient client, String jsonFilePath) throws Exception {
        System.out.println("indexDocument startedn\n"); // Debugging purposes

        Post post = JsonOps.createPostFromJson(jsonFilePath); // Create a Post object from a JSON file
        String post_id = JsonOps.getIdFromJsonFilePath(jsonFilePath); // Get the post ID from the JSON file path

        // Convert the Post object to a Map<String, Object>
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> postMap = mapper.convertValue(post, Map.class);

        IndexResponse response = client.index(i -> i
                .index(PublicVars.INDEX_NAME)
                //.id(String.valueOf(PublicVars.posts_array.size())) // The document ID
                .id(post_id)
                .document(postMap) // Pass the Map instead of the Post object
                .refresh(Refresh.True)
        );

        // This creates a bulk ready file just in case
        JsonOps.prepareBulkApiDataSINGLE(jsonFilePath); // Prepare the bulk data for the API for a single document

        CRUD.closeClient(client);

        System.out.println("Indexed document ID: " + response.id());
        System.out.println("indexDocument exited\n"); // Debugging purposes
    }

    public static void altindexDocument(ElasticsearchClient client, String jsonFilePath) throws Exception {
        System.out.println("altindexDocument started\n"); // Debugging purposes

        String command = "curl.exe -X POST \"http://localhost:9200/motorcycle_forum/_doc/" + JsonOps.getIdFromJsonFilePath(jsonFilePath) + "\" " +
                "-H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" " +
                "-H \"Content-Type: application/json\" " +
                "-d @" + jsonFilePath;;

        CRUD.executeCommand(command);

        CRUD.closeClient(client);

        System.out.println("altindexDocument exited\n"); // Debugging purposes
    }

    /**
     * This method retrieves a document from Elasticsearch
     * @param client The Elasticsearch client
     * @param docID The document ID
     * @throws Exception
     */
    public static void getDocument(ElasticsearchClient client, String docID) throws Exception {
        System.out.println("getDocument started\n"); // Debugging purposes

        try {
            GetResponse<Post> response = client.get(g -> g
                        .index(PublicVars.INDEX_NAME)
                        .id(docID),
                Post.class
            );
            if (response.found()) {
                System.out.println("Document found: " + response.toString());
                //displayIndexContents(client);
                //System.out.println("Document found: " + response.source());
                System.out.println("Response index: " + response.index());
                System.out.println("Response id: " + response.id());
                System.out.println("Response found: " + response.found());

                // Display the document using the existing local database of web_scraped_data
                ObjectMapper mapper = new ObjectMapper();

                try {
                    JsonNode rootNode = mapper.readTree(new File("web_scraped_data/" + "post_data_" + docID + ".json"));
                    System.out.println(rootNode.toPrettyString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("Document not found");
            }
        } catch (NotFoundException e) {
            System.out.println("Document not found");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeClient(client);
        }

        System.out.println("getDocument exited\n");
    }

    /**
     * This method retrieves a document from Elasticsearch using the command line
     * @param client The Elasticsearch client
     * @param docID The document ID
     * @throws Exception
     */
    public static void altgetDocument(ElasticsearchClient client, String docID) throws Exception {
        System.out.println("altgetDocument started\n"); // Debugging purposes

        executeCommand("curl.exe -H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" -X GET \"http://localhost:9200/motorcycle_forum/_doc/" + docID + "\"");

        closeClient(client);

        System.out.println("altgetDocument exited\n"); // Debugging purposes
        }

    /**
     * This method deletes a document from Elasticsearch
     * @param client The Elasticsearch client
     * @param docID The document ID
     * @throws Exception
     */
    public static void deleteDocument(ElasticsearchClient client, String docID) throws Exception {
        System.out.println("deleteDocument started\n"); // Debugging purposes

        DeleteResponse response = client.delete(d -> d
                .index(PublicVars.INDEX_NAME)
                .id(docID)
        );

        CRUD.closeClient(client);

        System.out.println("Deleted document status: " + response.result());
        System.out.println("deleteDocument exited\n"); // Debugging purposes
    }


    public static void altupdateDocument(ElasticsearchClient client, String docId, String jsonFileName) throws Exception {
        System.out.println("altupdateDocument started\n"); // Debugging purposes

        String command = "curl.exe -X POST \"localhost:9200/" + PublicVars.INDEX_NAME +"/_update/" + docId + "\" -H \"Content-Type: application/json\" -H \"Authorization: ApiKey " + ENV_vars.API_KEY  + "\" -d @" + jsonFileName + "";

        executeCommand(command);

        CRUD.closeClient(client);

        System.out.println("altupdateDocument exited\n"); // Debugging purposes
    }

    /**
     * This method executes a specified command in the command line
     * @param command The command to execute
     */
    public static void executeCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}