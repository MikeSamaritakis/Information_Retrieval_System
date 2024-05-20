# Information Retrieval System

This project's main idea is to create a web-crawler that will crawl a website and index its content into an ElasticSearch
database. The user can then search using queries and the search engine will return the most relevant results using TF-IDF 
weights and cosine similarity.

## How To Configure and Run the Project
*An IDE that supports Java, such as IntelliJ IDEA Ultimate, for testing this project IntelliJ IDEA Ultimate 2020.3 was used.*

**To run the project, you need to have the following installed:**

- Java JDK 17
- Maven 3.8.3 *(After everything is installed make sure to sync the project so that no dependency issues come up)*
- [ChromeDriver](https://chromedriver.chromium.org/downloads)
- [Selenium WebDriver](https://www.selenium.dev/downloads/)
- [ElasticSearch](https://www.elastic.co/downloads/elasticsearch)
- [Kibana](https://www.elastic.co/downloads/kibana)
- CURL for Powershell (optional)

**To run the searching you need to have the following installed:**

*Before running any configuration you must create an ENV_vars.java file in the src/ directory and add the following code:*
```java
public class ENV_vars {
    public static final String API_KEY = "your_api_key_to_kibana_here";
    public static final String chromedriver = "C:\\your_absolute_path_to_the_chromedriver_exe_file\\Web-Crawler\\chromedriver-win64\\chromedriver.exe";
}
// or alternatively use the commented code found in the WebCrawler.WebCrawler() function
```

The project is basically divided into three parts, the web-crawler, the indexing and the search engine.

*You have to run the ElasticSearch and Kibana services before running the project.*

You can do that using the following commands in the terminal after navigating to the proper directory: (for Windows)
```bash
elasticsearch-8.13.2/bin/elasticsearch.bat 
```
and when ElasticSearch is running, run the following command:
```bash
kibana-8.13.2/bin/kibana.bat
```
___
## Running the Project without GUI

**To run the web-crawler:** Either run the configurations found in the .idea/runConfigurations/WebCrawler.xml or run it using the terminal.

- The arguments for the Web-Crawler are as follows:
```bash
javac Main.java
``` 
and the command:
```bash
java Main  -WebCrawler
```
---
**To run the CRUD operations:**

*Before running the CRUD operations you have to create an index in Kibana and make sure that the field:*
```java
public class PublicVars {
    public static final String INDEX_NAME = "your_index_name_here";
    // rest of the public variables ...
}
```
- For **Create** Index: You have to create a post_data_id.json (i.e.: where id = 23) file and specify its path in the run configuration file. 
The file should look like this:
```json
[{
  "Content": "Hello, I have an old 2000 ZX6R I have been repairing. I've been riding recently and it ended up stalling out and it will not crank anymore. I think the stator/regulator are original, so I'm thinking they are the problem. Am I better off replacing both, or just the regulator as it is most likely blown?",
  "Breadcrumbs": "Home\nForums\nMotorcycle Repair, Building, and Restoration\n🔧 Motorcycle Repair 🔧",
  "Title": "Stator/Regulator Replacement",
  "Author": "DVantage",
  "URL": "https://www.motorcycleforum.com/threads/stator-regulator-replacement.253850/",
  "Date": "May 6, 2024",
  "Tags": [
    "charging",
    "starting",
    "stator"
  ]
}]
```
The results of this operation will be found at the terminal.

---
- For **Read** Index: You have to specify the index id in the run configuration file.

The results of this operation will be found at the terminal.

---

- For **Delete** Index: You have to specify the index id in the run configuration file.

The results of this operation will be found at the terminal.

---

**Then either edit and run the configurations found in the .idea/runConfigurations/ folder correspondingly or run it 
using the terminal with the following commands:**

- The arguments for the **Bulk Upload** Index are as follows:
```bash
javac Main.java
``` 
and the command:
```bash
java Main -BulkUpload
```

The results of this operation will be found at the terminal.

---

- The arguments for the **Create** Index are as follows: 
```bash
javac Main.java
``` 
and the command: 
```bash
java Main -CRUD -Create fileToJSON.json // where fileToJSON.json is the file path to the file you want to index
```

The results of this operation will be found at the terminal.

---

- The arguments for the **Read** Index are as follows: 
```bash
javac Main.java
```
and the command:
```bash
java Main -CRUD -Read postId // where postId is the id of the post you want to read
```

The results of this operation will be found at the terminal.

---

- The arguments for the **Delete** Index are as follows:
```bash
javac Main.java
```
  and the command: 
```bash
java Main -CRUD -Delete postId // where postId is the id of the post you want to delete
```

The results of this operation will be found at the terminal.

---

- The arguments for the **Update** Index are as follows: 
```bash
javac Main.java
``` 
and the command:
```bash 
```bash
java Main -CRUD -Update
```

But first you have to create a Json file that follows the format below and specify its filepath and id in the Main.java file: 
```json
{
  "doc": {
    "Content": "content",
    "Breadcrumbs": "Home\nForums\n🏍 Motorcycle Forums 🏍\nForum",
    "Title": "title",
    "Author": "username",
    "URL": "https://www.motorcycleforum.com/threads/example/",
    "Date": "2024-05-03",
    "Tags": ["updated"]
  }
}
```

The results of this operation will be found at the terminal.

---

**To run the search engine:** You have three options:

- For **Content Search**: Write the query you want in the Main.java file and then run the configuration file named Search Content.

The results of this operation will be found at the terminal.

- For **Title Search**: Write the query you want in the Main.java file and then run the configuration file named Search Title.

The results of this operation will be found at the terminal.

- For **Tags Search**: Write the query you want in the Main.java file and then run the configuration file named Search Tags.

The results of this operation will be found at the terminal.

- For **TF-IDF Search**: You have to run it using the GUI and the instructions found below regarding the GUI.

---

## Running the Project with GUI

**Note! All the results will appear in the terminal and NOT the UI.**

**Run the configurations found in the .idea/runConfigurations/GUI.xml or run using 
the terminal with the following commands:**

---

**To run the Web Crawler:** Simply press the button and let it finish running. It could take up to a few hours 
to finish depending on the website you are crawling, the hardware you are using and the internet speed.

---

**To run the CRUD operations:**

*Before running the CRUD operations you have to create an index in Kibana:
```bash
curl.exe -H "Authorization: ApiKey api_key_here" -X PUT  "http://locvalhost:9200/motorcycle_forum/" 
// or instead of motorcycle_forum use the name of your index
```
or alternatively use the Kibana Dev Tools to create an index:
```bash
PUT motorcycle_forum
{
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "term_vector": 
      }
    }
  }
}
```
and make sure that the fields in PublicVars.java are set as follows:
```java
public class PublicVars {
    public static final String INDEX_NAME = "your_index_name_here"; // In this case "motorcycle_forum"
    // rest of the public variables ...
}
```
---
- For **Create** Index: You have to create a post_data_id.json (i.e.: where id = 23) file and specify its path in the window that pops up and click the create button.
  The file should look like this:
```json
[{
  "Content": "Hello, I have an old 2000 ZX6R I have been repairing. I've been riding recently and it ended up stalling out and it will not crank anymore. I think the stator/regulator are original, so I'm thinking they are the problem. Am I better off replacing both, or just the regulator as it is most likely blown?",
  "Breadcrumbs": "Home\nForums\nMotorcycle Repair, Building, and Restoration\n🔧 Motorcycle Repair 🔧",
  "Title": "Stator/Regulator Replacement",
  "Author": "DVantage",
  "URL": "https://www.motorcycleforum.com/threads/stator-regulator-replacement.253850/",
  "Date": "May 6, 2024",
  "Tags": [
    "charging",
    "starting",
    "stator"
  ]
}]
```
---
- For **Bulk Upload** Index: You have to specify the file path in the window that pops up and click the upload button.

---
- For **Read** Index: You have to specify the index id in the window that pops up and click the read button.

The results of this operation will be found at the terminal.

---
- For **Delete** Index: You have to specify the index id in the window that pops up and click the delete button.
---

---
- For **Update** Index: You have to specify the index id in one of the textfields and the file path in the other textfield and click the update button.
But first you have to create a Json file that follows the format below: 
```json
{
  "doc": {
    "Content": "content",
    "Breadcrumbs": "Home\nForums\n🏍 Motorcycle Forums 🏍\nForum",
    "Title": "title",
    "Author": "username",
    "URL": "https://www.motorcycleforum.com/threads/example/",
    "Date": "2024-05-03",
    "Tags": ["updated"]
  }
}
```
---

**To run the search engine:** You have three options:

- For **Content Search**: Write the query you want in the textfield and the click on the dropdown menu to choose Content Search and then click the search button.

- For **Title Search**: Write the query you want in the textfield and the click on the dropdown menu to choose Title Search and then click the search button.

- For **Tags Search**: Write the query you want in the textfield and the click on the dropdown menu to choose Tags Search and then click the search button.

- For **TF-IDF Search**: Edit the file found in src/search_script.json and then click the 'Script Search' button.
Here is an example of the file: (where term1, term2 are the terms you want to search for and 930 is the total number of documents in the index
```json
{
  "query": {
    "script_score": {
      "query": {
        "match_all": {}
      },
      "script": {
        "source": "ArrayList queryTerms = params.queryTerms;double score = 0.0;double queryNorm = 0.0;double docNorm = 0.0;Map queryTFIDF = new HashMap();Map docTFIDF = new HashMap();for (Object termObj : queryTerms) {String term = (String) termObj;double tf = 0;for (Object queryTermObj : queryTerms) {String queryTerm = (String) queryTermObj;if (term.equals(queryTerm)) {tf += 1;}}double idf = Math.log((double) params.totalDocs / (1 + (doc.containsKey(term) ? doc.get(term).docFreq() : 0)));queryTFIDF.put(term, tf * idf);queryNorm += Math.pow(tf * idf, 2);}for (Object termObj : queryTerms) {String term = (String) termObj;if (doc.containsKey(term)) {double tf = doc.get(term).termFreq();double idf = Math.log((double) params.totalDocs / (1 + (doc.containsKey(term) ? doc.get(term).docFreq() : 0)));double tfidf = tf * idf;docTFIDF.put(term, tfidf);docNorm += Math.pow(tfidf, 2);score += tfidf * (queryTFIDF.containsKey(term) ? queryTFIDF.get(term) : 0.0);}}queryNorm = Math.sqrt(queryNorm);docNorm = Math.sqrt(docNorm);return (queryNorm == 0 || docNorm == 0) ? 0 : score / (queryNorm * docNorm);",
        "params": {
          "queryTerms": ["term1", "term2"],
          "totalDocs": 930
        }
      }
    }
  }
}
```