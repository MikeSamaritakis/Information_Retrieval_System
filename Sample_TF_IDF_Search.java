import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.mapping.TermVectorOption;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.transport.TransportUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.common.collect.List;
import org.elasticsearch.common.collect.Map;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class Sample_TF_IDF_Search {

    private static ElasticsearchClient client;

    public static void main(String[] args) throws IOException {
        // Initialize the client
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);

        // Create index and index documents
        createIndex();
        indexDocuments();

        // Calculate TF-IDF and search
        search("honda");

        // Close client
        restClient.close();
    }

    private static void createIndex() throws IOException {
        // Delete the existing index if it exists
        try {
            client.indices().delete(b -> b.index("motorcycle_forum"));
        } catch (ElasticsearchException e) {
            // Index may not exist
        }

        // Create the index with term vectors
        client.indices().create(b -> b.index("motorcycle_forum")
                .mappings(m -> m.properties("content", p -> p.text(t -> t.termVector(TermVectorOption.valueOf("with_positions_offsets_payloads"))))));
    }

    private static void indexDocuments() throws IOException {
        // Index some sample documents
        client.index(i -> i.index("motorcycle_forum").id("1")
                .document(Map.of("content", "Elasticsearch is a distributed, RESTful search and analytics engine.")));
        client.index(i -> i.index("motorcycle_forum").id("2")
                .document(Map.of("content", "Apache Lucene is a free and open-source search engine software library.")));
        client.index(i -> i.index("motorcycle_forum").id("3")
                .document(Map.of("content", "Honda is a popular motorcycle brand.")));
    }

    private static void search(String query) throws IOException {
        // Fetch term vectors for documents
        TermvectorsResponse tvResponse1 = client.termvectors(tv -> tv.index("motorcycle_forum").id("1").fields("content"));
        TermvectorsResponse tvResponse2 = client.termvectors(tv -> tv.index("motorcycle_forum").id("2").fields("content"));
        TermvectorsResponse tvResponse3 = client.termvectors(tv -> tv.index("motorcycle_forum").id("3").fields("content"));

        // Calculate TF-IDF for the documents
        Map<String, Double> doc1Tfidf = calculateTfIdf(tvResponse1, 3);
        Map<String, Double> doc2Tfidf = calculateTfIdf(tvResponse2, 3);
        Map<String, Double> doc3Tfidf = calculateTfIdf(tvResponse3, 3);

        // Create a TF-IDF vector for the query
        Map<String, Double> queryTfidf = createQueryTfidf(query, List.of(doc1Tfidf, doc2Tfidf, doc3Tfidf));

        // Calculate cosine similarity
        double score1 = cosineSimilarity(doc1Tfidf, queryTfidf);
        double score2 = cosineSimilarity(doc2Tfidf, queryTfidf);
        double score3 = cosineSimilarity(doc3Tfidf, queryTfidf);

        // Print results
        System.out.println("Document 1 Score: " + score1);
        System.out.println("Document 2 Score: " + score2);
        System.out.println("Document 3 Score: " + score3);
    }

    private static Map<String, Double> calculateTfIdf(TermVectorsResponse tvResponse, int docCount) {
        Map<String, Double> tfidf = new HashMap<>();
        tvResponse.termVectors().get("content").terms().forEach((term, stats) -> {
            double tf = stats.termFreq();
            double idf = Math.log((double) docCount / stats.docFreq());
            tfidf.put(term, tf * idf);
        });
        return tfidf;
    }

    private static Map<String, Double> createQueryTfidf(String query, List<Map<String, Double>> docTfIdfs) {
        Map<String, Double> queryTfidf = new HashMap<>();
        String[] terms = query.split("\\s+");
        Set<String> uniqueTerms = Arrays.stream(terms).collect(Collectors.toSet());

        for (String term : uniqueTerms) {
            double tf = Arrays.stream(terms).filter(t -> t.equals(term)).count();
            double idf = Math.log((double) docTfIdfs.size() / (1 + docTfIdfs.stream().filter(doc -> doc.containsKey(term)).count()));
            queryTfidf.put(term, tf * idf);
        }
        return queryTfidf;
    }

    private static double cosineSimilarity(Map<String, Double> docVector, Map<String, Double> queryVector) {
        double dotProduct = 0.0;
        double docNorm = 0.0;
        double queryNorm = 0.0;

        for (String key : docVector.keySet()) {
            if (queryVector.containsKey(key)) {
                dotProduct += docVector.get(key) * queryVector.get(key);
            }
            docNorm += Math.pow(docVector.get(key), 2);
        }

        for (double value : queryVector.values()) {
            queryNorm += Math.pow(value, 2);
        }

        docNorm = Math.sqrt(docNorm);
        queryNorm = Math.sqrt(queryNorm);

        if (docNorm != 0.0 && queryNorm != 0.0) {
            return dotProduct / (docNorm * queryNorm);
        } else {
            return 0.0;
        }
    }
}
