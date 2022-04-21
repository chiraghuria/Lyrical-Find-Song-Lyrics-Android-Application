/**
 * Name: Chirag Huria
 * Andrew ID: churia [at] andrew.cmu.edu
 * Project: Project 4
 * Course: Distributed Systems
 * Class: Fall 2021, Heinz, CMU
 */
package ds.Project4Task2;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.json.JSONObject;

import java.util.*;

import static com.mongodb.client.model.Aggregates.*;

/**
 * MusicLyricsMongoModel invokes the mongo model, inserts the documents in the collections, computes the
 * metrics for the analytics, and returns the collections and documents as necessary
 */
public class MusicLyricsMongoModel {

    static MongoDatabase database;
    static MongoCollection<Document> requestLog;
    static MongoCollection<Document> responseLog;

    /**
     * Constructor
     */
    public void MusicLyricsModel(){
        connectMongo();
    }

    /**
     * Connection to the MongoDB database
     */
    public void connectMongo(){
        ConnectionString connectionString = new ConnectionString("mongodb://admin:admin@musiclyricscluster-shard-00-00.aggbl.mongodb.net:27017,musiclyricscluster-shard-00-01.aggbl.mongodb.net:27017,musiclyricscluster-shard-00-02.aggbl.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-ao79va-shard-0&authSource=admin&retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("MusicLyricsCluster");
    }

    /**
     * Insert the JSON Object to request logs collection for logging
     * @param jsonRequestObject
     */
    public void insertRequestLog(JSONObject jsonRequestObject){
        if (database == null) {
            connectMongo();
        }
        database.getCollection("requestLog").insertOne(Document.parse(jsonRequestObject.toString()));
    }

    /**
     * Insert the JSON Object to response logs collection for logging
     * @param jsonResponseObject
     */
    public void insertResponseLog(JSONObject jsonResponseObject){
        if (database == null) {
            connectMongo();
        }
        database.getCollection("responseLog").insertOne(Document.parse(jsonResponseObject.toString()));
    }

    /**
     * Return the request logs collection for analyticsLogs servlet
     * @return
     */
    // [Citation]: Code adapted from https://stackoverflow.com/questions/41265952/get-values-from-list-mongodb-java
    // and https://mongodb.github.io/mongo-java-driver/3.6/javadoc/com/mongodb/client/FindIterable.html
    public List<Map<String, String>> getRequestLog(){

         MongoCollection<Document> requestLog = database.getCollection("requestLog");
        // sorting the data in mongodb by timestamp (ascending)
        FindIterable<Document> documents = requestLog.find().sort(Sorts.ascending("RequestTimestamp"));
        // creating the array list of maps to hold the JSON objects or list of documents
        List<Map<String, String>> docList = new ArrayList<>();

        // iterating through the documents to retrieve document objects as list objects
        for(Document document: documents){
            Map<String, String> obj = new HashMap<>();
            obj.put("RequestMethod", String.valueOf(document.get("RequestMethod")));
            obj.put("RequestProtocol", String.valueOf(document.get("RequestProtocol")));
            obj.put("RequestURL", String.valueOf(document.get("RequestURL")));
            obj.put("RequestSecure", String.valueOf(document.get("RequestSecure")));
            obj.put("RequestBrowserName", String.valueOf(document.get("RequestBrowserName")));
            obj.put("RequestMachineIP", String.valueOf(document.get("RequestMachineIP")));
            obj.put("RequestTimestamp", String.valueOf(document.get("RequestTimestamp")));
            obj.put("RequestSongName",String.valueOf(document.get("RequestSongName")));
            obj.put("RequestArtistName",String.valueOf(document.get("RequestArtistName")));
            docList.add(obj);
        }
        return docList;
    }

    /**
     * Return the response logs collection for analyticsLogs servlet
     * @return
     */
    public List<Map<String, String>> getResponseLog() {
        if (database == null) {
            connectMongo();
        }
        MongoCollection<Document> responseLog = database.getCollection("responseLog");
        // sorting the data in mongodb by timestamp (ascending)
        FindIterable<Document> documents = responseLog.find().sort(Sorts.ascending("RequestTimestamp"));
        // creating the array list of maps to hold the JSON objects or list of documents
        List<Map<String, String>> docList = new ArrayList<>();

        // iterating through the documents to retrieve document objects as list objects
        for (Document document : documents) {
            Map<String, String> obj = new HashMap<>();
            obj.put("ResponseThumbnail", String.valueOf(document.get("thumbnail")));
            obj.put("ResponseLyrics", String.valueOf(document.get("lyrics")));
            obj.put("ResponseTimestamp", String.valueOf(document.get("RequestTimestamp")));
            docList.add(obj);
        }
        return docList;
    }

    /**
     * Analytics metric to return total count of song searches
     * @return
     */
    public String getTotalSongSearches(){
        if (database == null) {
            connectMongo();
        }
        String requestCountString;
        MongoCollection<Document> requestLog = database.getCollection("requestLog");
        Document requestsCountDocument = requestLog.aggregate(Arrays.asList((Aggregates.count()))).first();
        requestCountString = String.valueOf(requestsCountDocument.get("count"));
        return requestCountString;
    }

    /**
     * Analytics metric to return total count of song searches
     * @return
     */
    public Map<String, String> getMostSearchedSong() {
        if (database == null) {
            connectMongo();
        }
        Map<String, String> mostSearchedSong = new HashMap<String, String>();

        MongoCollection<Document> requestLog = database.getCollection("requestLog");
        Document mostSearchedRequest = requestLog.aggregate(Arrays.asList(group("$RequestSongName", Accumulators.sum("SearchCount", 1)),
                sort(Sorts.descending("SearchCount")))).first();

        mostSearchedSong.put(String.valueOf(mostSearchedRequest.get("_id")), String.valueOf(mostSearchedRequest.get("SearchCount")));
        return mostSearchedSong;
    }

    /**
     * Anlaytics metric to return the most searched artist's name
     * @return
     */
    public Map<String, String> getMostSearchedArtist() {
        if (database == null) {
            connectMongo();
        }
        Map<String, String> mostSearchedArtist = new HashMap<String, String>();

        MongoCollection<Document> requestLog = database.getCollection("requestLog");
        Document mostSearchedArtistRequest = requestLog.aggregate(Arrays.asList(group("$RequestArtistName", Accumulators.sum("ArtistSearchCount", 1)),
                sort(Sorts.descending("SearchCount")))).first();

        mostSearchedArtist.put(String.valueOf(mostSearchedArtistRequest.get("_id")), String.valueOf(mostSearchedArtistRequest.get("ArtistSearchCount")));

        return mostSearchedArtist;

    }

}
