import co.elastic.clients.elasticsearch.ElasticsearchClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Search {
    public static void altSearchContent(ElasticsearchClient client, String query){

        List<String> queryList = Arrays.asList(query.split(" "));
        int querySize = queryList.size();
        double weight = 1.0/querySize;

        StringBuilder queryVector = new StringBuilder();
        for (String term : queryList) {
            queryVector.append("\\\"").append(term).append("\\\": ").append(weight).append(",");
        }

        // Remove the trailing comma
        if (queryVector.length() > 0) {
            queryVector.setLength(queryVector.length() - 1);
        }

        String command = "curl.exe -X POST \"http://localhost:9200/motorcycle_forum/_search\" " +
                "-H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" " +
                "-H \"Content-Type: application/json\" " +
                "-d \"{\\\"query\\\": {\\\"script_score\\\": {\\\"query\\\": {\\\"match\\\": {\\\"Content\\\": \\\"" + query +
                "\\\"}},\\\"script\\\": {\\\"source\\\": \\\"double score = 0.0; for (term in params.queryVector.keySet()) " +
                "{ double queryTFIDF = params.queryVector.get(term); double docTFIDF = 0.0; if (doc.containsKey(term)) " +
                "{ docTFIDF = doc['Content'].tf(term); } score += queryTFIDF * docTFIDF; } return score;\\\",\\\"params\\\": " +
                "{\\\"queryVector\\\": {" + queryVector.toString() + "}}}}}}\"";

        CRUD.executeCommand(command);

        CRUD.closeClient(client);
    }

    public static void altSearchTitle(ElasticsearchClient client, String query){

        List<String> queryList = Arrays.asList(query.split(" "));
        int querySize = queryList.size();
        double weight = 1.0/querySize;

        StringBuilder queryVector = new StringBuilder();
        for (String term : queryList) {
            queryVector.append("\\\"").append(term).append("\\\": ").append(weight).append(",");
        }

        // Remove the trailing comma
        if (queryVector.length() > 0) {
            queryVector.setLength(queryVector.length() - 1);
        }

        String command = "curl.exe -X POST \"http://localhost:9200/motorcycle_forum/_search\" " +
                "-H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" " +
                "-H \"Content-Type: application/json\" " +
                "-d \"{\\\"query\\\": {\\\"script_score\\\": {\\\"query\\\": {\\\"match\\\": {\\\"Title\\\": \\\"" + query +
                "\\\"}},\\\"script\\\": {\\\"source\\\": \\\"double score = 0.0; for (term in params.queryVector.keySet()) " +
                "{ double queryTFIDF = params.queryVector.get(term); double docTFIDF = 0.0; if (doc.containsKey(term)) " +
                "{ docTFIDF = doc['Title'].tf(term); } score += queryTFIDF * docTFIDF; } return score;\\\",\\\"params\\\": " +
                "{\\\"queryVector\\\": {" + queryVector.toString() + "}}}}}}\"";

        CRUD.executeCommand(command);

        CRUD.closeClient(client);
    }

    public static void altSearchTags(ElasticsearchClient client, String query){

        List<String> queryList = Arrays.asList(query.split(" "));
        int querySize = queryList.size();
        double weight = 1.0/querySize;

        StringBuilder queryVector = new StringBuilder();
        for (String term : queryList) {
            queryVector.append("\\\"").append(term).append("\\\": ").append(weight).append(",");
        }

        // Remove the trailing comma
        if (queryVector.length() > 0) {
            queryVector.setLength(queryVector.length() - 1);
        }

        String command = "curl.exe -X POST \"http://localhost:9200/motorcycle_forum/_search\" " +
                "-H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" " +
                "-H \"Content-Type: application/json\" " +
                "-d \"{\\\"query\\\": {\\\"script_score\\\": {\\\"query\\\": {\\\"match\\\": {\\\"Tags\\\": \\\"" + query +
                "\\\"}},\\\"script\\\": {\\\"source\\\": \\\"double score = 0.0; for (term in params.queryVector.keySet()) " +
                "{ double queryTFIDF = params.queryVector.get(term); double docTFIDF = 0.0; if (doc.containsKey(term)) " +
                "{ docTFIDF = doc['Tags'].tf(term); } score += queryTFIDF * docTFIDF; } return score;\\\",\\\"params\\\": " +
                "{\\\"queryVector\\\": {" + queryVector.toString() + "}}}}}}\"";

        CRUD.executeCommand(command);

        CRUD.closeClient(client);
    }

    public static void TF_IDF_Search(ElasticsearchClient client, String query){
        System.out.println("TF_IDF_Search started\n"); // Debugging purposes

        // Split the query into terms
        List<String> queryTerms = Arrays.asList(query.split(" "));
        // Get the total number of documents in your Elasticsearch index
        int totalDocs = getTotalDocs(client);

        // Convert the query terms into a JSON array
        String queryTermsJson = queryTerms.stream()
                .map(term -> "\"" + term + "\"")
                .collect(Collectors.joining(","));

//        String command = "curl.exe -X POST \"http://localhost:9200/motorcycle_forum/_search\" " +
//                "-H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" " +
//                "-H \"Content-Type: application/json\" " +
//                "-d \"{\\\"query\\\": {\\\"script_score\\\": {\\\"query\\\": {\\\"match_all\\\": {}},\\\"script\\\": {\\\"source\\\": \\\"\\n" +
//                "          String[] queryTerms = params.queryTerms;\\n" +
//                "          double score = 0.0;\\n" +
//                "          double queryNorm = 0.0;\\n" +
//                "          double docNorm = 0.0;\\n" +
//                "          Map queryTFIDF = new HashMap();\\n" +
//                "          Map docTFIDF = new HashMap();\\n" +
//                "          \\n" +
//                "          // Calculate query TF-IDF\\n" +
//                "          for (String term : queryTerms) {\\n" +
//                "            double tf = 0;\\n" +
//                "            for (String queryTerm : queryTerms) {\\n" +
//                "              if (term.equals(queryTerm)) {\\n" +
//                "                tf += 1;\\n" +
//                "              }\\n" +
//                "            }\\n" +
//                "            double idf = Math.log((double) params.totalDocs / (1 + doc.containsKey(term) ? doc.get(term).docFreq() : 0));\\n" +
//                "            queryTFIDF.put(term, tf * idf);\\n" +
//                "            queryNorm += Math.pow(tf * idf, 2);\\n" +
//                "          }\\n" +
//                "          \\n" +
//                "          // Calculate document TF-IDF and accumulate score\\n" +
//                "          for (String term : queryTerms) {\\n" +
//                "            if (doc.containsKey(term)) {\\n" +
//                "              double tf = doc.get(term).termFreq();\\n" +
//                "              double idf = Math.log((double) params.totalDocs / (1 + doc.get(term).docFreq()));\\n" +
//                "              double tfidf = tf * idf;\\n" +
//                "              docTFIDF.put(term, tfidf);\\n" +
//                "              docNorm += Math.pow(tfidf, 2);\\n" +
//                "              score += tfidf * (queryTFIDF.containsKey(term) ? queryTFIDF.get(term) : 0.0);\\n" +
//                "            }\\n" +
//                "          }\\n" +
//                "          \\n" +
//                "          // Calculate norms\\n" +
//                "          queryNorm = Math.sqrt(queryNorm);\\n" +
//                "          docNorm = Math.sqrt(docNorm);\\n" +
//                "          \\n" +
//                "          // Return cosine similarity score\\n" +
//                "          return (queryNorm == 0 || docNorm == 0) ? 0 : score / (queryNorm * docNorm);\\n" +
//                "        \\\"\\\",\\\"params\\\": {\\\"queryTerms\\\": [" + queryTermsJson + "],\\\"totalDocs\\\": " + totalDocs + "}}}}}}\"";

        String command = "curl.exe -X POST \"http://localhost:9200/motorcycle_forum/_search\" -H \"Authorization: ApiKey" + ENV_vars.API_KEY + "\" -H \"Content-Type: application/json\" -d @src\\search_script.json";

        CRUD.executeCommand(command);

        CRUD.closeClient(client);

        System.out.println("TF_IDF_Search exited\n"); // Debugging purposes
    }

    private static int getTotalDocs(ElasticsearchClient client) {
        // Implement this method to return the total number of documents in your Elasticsearch index
        return 930; //manual input
    }

    /**
     * We could not resolve the dependencies for this function, so we decided to go manual using the command line tools.
     */
//    private static void searchCosineSimilarity(ElasticsearchClient client, String index, String queryText) throws IOException {
//        Map<String, Double> queryVector = Map.of("Elasticsearch", 0.5, "search", 0.5, "engine", 0.5);
//
//        String script = "double dotProduct = 0.0; " +
//                "double docVectorNorm = 0.0; " +
//                "double queryVectorNorm = 0.0; " +
//                "for (term in params.queryVector.keySet()) { " +
//                "  double queryTFIDF = params.queryVector.get(term); " +
//                "  double docTFIDF = doc['content'].tf(term); " +
//                "  dotProduct += queryTFIDF * docTFIDF; " +
//                "  queryVectorNorm += queryTFIDF * queryTFIDF; " +
//                "  docVectorNorm += docTFIDF * docTFIDF; " +
//                "} " +
//                "return dotProduct / (Math.sqrt(queryVectorNorm) * Math.sqrt(docVectorNorm));";
//
//        ScriptQuery scriptQuery = new ScriptQuery(sq -> sq
//                .script(script)
//                .params(queryVector)
//        );
//
//        Query query = new Query(q -> q
//                .match(m -> m
//                        .field("content")
//                        .query(queryText)
//                )
//                .scriptScore(ss -> ss
//                        .query(q -> q.match(m -> m.field("content").query(queryText)))
//                        .script(s -> s.source(script).params(queryVector))
//                )
//        );
//
//        SearchRequest searchRequest = new SearchRequest(sr -> sr
//                .index(index)
//                .query(query)
//        );
//
//        SearchResponse searchResponse = client.search(searchRequest);
//
//        for (Hit hit : searchResponse.hits().hits()) {
//            System.out.println(hit.source());
//            System.out.println("Score: " + hit.score());
//        }
//    }
}


//curl.exe -X POST "http://localhost:9200/motorcycle_forum/_search" -H "Authorization: ApiKey bTNnVFA0OEIzTTl2dkhNUVhjOXk6LXFXM0wzUDVRb3lWQzhlXzJFTTB6Zw==" -H "Content-Type: application/json" -d "{\"query\": {\"script_score\": {\"query\": {\"match_all\": {} },\"script\": {\"source\": \" String[] queryTerms = params.queryTerms;double score = 0.0;double queryNorm = 0.0;double docNorm = 0.0;Map queryTFIDF = new HashMap();Map docTFIDF = new HashMap();for (String term : queryTerms) {double tf = 0;for (String queryTerm : queryTerms) {if (term.equals(queryTerm)) {tf += 1;}}double idf = Math.log((double) params.totalDocs / (1 + doc.containsKey(term) ? doc.get(term).docFreq() : 0));queryTFIDF.put(term, tf * idf);queryNorm += Math.pow(tf * idf, 2);}for (String term : queryTerms) {if (doc.containsKey(term)) {double tf = doc.get(term).termFreq();double idf = Math.log((double) params.totalDocs / (1 + doc.get(term).docFreq()));double tfidf = tf * idf;docTFIDF.put(term, tfidf);docNorm += Math.pow(tfidf, 2);score += tfidf * (queryTFIDF.containsKey(term) ? queryTFIDF.get(term) : 0.0);}}queryNorm = Math.sqrt(queryNorm);docNorm = Math.sqrt(docNorm);return (queryNorm == 0 || docNorm == 0) ? 0 : score / (queryNorm * docNorm);\",\"params\": {\"queryTerms\": [queryTermsJson],\"totalDocs\": totalDocs }}}}}"