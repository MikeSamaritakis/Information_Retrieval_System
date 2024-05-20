import java.io.IOException;

public class Main {

/**--------------------------------------- Main Operations Here ------------------------------------------------------*/

    /**
     * The main method
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) throws IOException {

        //System.out.println(args.length > 0 ? args[0] + (args.length > 1 ? "\n" + args[1] : "") : ""); // Debugging purposes

        // Check the command line arguments and act accordingly
        if (args.length == 0) {
            System.out.println("No arguments provided. Please check the configuration file."); // Debugging purposes
        }
        else if (args[0].equals("-WebCrawler")) {
            //System.out.println(args[0]); // Debugging purposes
            WebCrawler.WebCrawler();
        }
        else if (args[0].equals("-CRUD") && args.length > 1) {
            // CRUD operations for Elasticsearch to be added and tested
            if (args[1].equals("-Create")) {
                //System.out.println(args[1]); // Debugging purposes
                if (args[2] != null) {
                    try {
                        /** Replace the file path with your desired JSON file path */
                        String filepath = args[2];
                        //CRUD.indexDocument(CRUD.createClient(), filepath);
                        CRUD.altindexDocument(CRUD.createClient(), filepath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else if (args[1].equals("-Read")) {
                //System.out.println(args[1]); // Debugging purposes
                //String docID = "2"; /** Replace with your actual document ID */
                String docID = args[2];
                try {
                    CRUD.getDocument(CRUD.createClient(), docID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (args[1].equals("-Update")) {
                //System.out.println(args[1]); // Debugging purposes
                try {
                    String docId = "1000";
                    String jsonFilePath = "post_data_1000.json";
                    CRUD.altupdateDocument(CRUD.createClient(), docId, jsonFilePath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (args[1].equals("-Delete")) {
                //System.out.println("ARG 1: " + args[1]); // Debugging purposes
                //System.out.println("ARG 2: " + args[2]); // Debugging purposes
                if (args[2] != null) {
                    //System.out.println("ARG 2: " + args[2]); // Debugging purposes
                    try {
                        String docID = args[2];
                        CRUD.deleteDocument(CRUD.createClient(), docID);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else {
                System.out.println("Invalid CRUD operation. Please check the configuration file."); // Debugging purposes
            }
        }
        else if (args[0].equals("-BulkUpload")) {
            //System.out.println(args[0]); // Debugging purposes
            //BulkUpload.BulkUpload(CRUD.createClient(), "bulk_web_scraped_data"); COULD NOT GET THIS TO WORK
            BulkUpload.alternativeBulkUpload();
        }
        else if (args[0].equals("-Search")) {
            //System.out.println(args[0]); // Debugging purposes
            if (args[1].equals("-Content")) {
                //System.out.println("Searching for content: " + args[2]); // Debugging purposes
                try {
                    String query = "honda engine 250";
                    Search.altSearchContent(CRUD.createClient(), query);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (args[1].equals("-Title")){
                try {
                    String query = "honda engine 250";
                    Search.altSearchTitle(CRUD.createClient(), query);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (args[1].equals("-Tags")){
                try {
                    String query = "honda engine 250";
                    Search.altSearchTags(CRUD.createClient(), query);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            System.out.println("Invalid configuration. Please check the configuration file."); // Debugging purposes
        }

    }
}