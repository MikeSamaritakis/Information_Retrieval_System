import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.util.BinaryData;
import co.elastic.clients.util.ContentType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BulkUpload {
    // The normal way to upload data to Elasticsearch does not function, so we have to use an alternative method
    public static void BulkUpload(ElasticsearchClient client, String directoryPath) throws IOException {
        System.out.println("BulkUpload started\n");

        File dir = new File(directoryPath);
        File[] jsonFiles = dir.listFiles(file -> file.getName().matches("post_data_\\d+\\.json"));

//        for (File file: jsonFiles) {
//            System.out.println(file.getName() + "\n");
//        }

        BulkRequest.Builder br = new BulkRequest.Builder();
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        for (File file: jsonFiles) {
            FileInputStream input = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            BinaryData data = BinaryData.of(baos.toByteArray(), ContentType.APPLICATION_JSON);

            br.operations(op -> op
                    .index(idx -> idx
                            .index(PublicVars.INDEX_NAME)
                            .document(data)
                    )
            );
        }

        BulkResponse result = client.bulk(br.build());

        // Log errors, if any
        if (result.errors()) {
            System.out.println("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    System.out.println(item.error().reason());
                }
            }
        }

        CRUD.closeClient(client);

        System.out.println("BulkUpload exited\n");
    }

    /**
     * The alternative method to upload data to Elasticsearch using the command line instead of using the Bulk API
     */
    public static void alternativeBulkUpload(){
        try {
            JsonOps.prepareBulkApiData("web_scraped_data", "bulk_web_scraped_data");
            JsonOps.mergeJSON("bulk_web_scraped_data", "bulk_web_scraped_data.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /**EXECUTE CURL COMMAND TO UPLOAD EITHER BY BAT FILE OR SOMEHOW*/
        CRUD.executeCommand("curl.exe -H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" -X POST \"http://localhost:9200/motorcycle_forum/_bulk\" -H \"Content-Type: application/json\" --data-binary @bulk_web_scraped_data.json");

    }
}