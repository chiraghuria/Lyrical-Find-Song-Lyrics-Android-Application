/**
 * Name: Chirag Huria
 * Andrew ID: churia [at] andrew.cmu.edu
 * Project: Project 4
 * Course: Distributed Systems
 * Class: Fall 2021, Heinz, CMU
 */
package ds.Project4Task2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * MusicLyricsModel class intereacts with the Genius API and executes the GET HTTP requests to retrieve information
 * from the API. Genius API returns the relevant songs list for each successful search.
 */
public class MusicLyricsModel {

    final String apiToken = "Lo_E5HPti6o2d0a4Dz-clwyq6H4j1hL25dhurrQbACwA_T_3ySQhmQegIW2LfoMV";

    /**
     * Constructor
     */
    public MusicLyricsModel(){

    }

    /**
     * getLyrics() methods uses the songName and artistName along with a JSON array to invoke methods to identify
     * a relevant match from the list of successful matches that the Genius API returns.
     * @param songName
     * @param artistName
     * @param hitsArray
     * @return
     * @throws JSONException
     */
    public JSONObject getLyrics(String songName, String artistName, JSONArray hitsArray) throws JSONException {

        int mostPopularSong = getMostPopularSong(hitsArray);
        int i = mostPopularSong;

        JSONObject song = hitsArray.getJSONObject(i);

        String lyricsURL = (String)song.getJSONObject("result").get("path");
        String baseURL = "https://genius.com";

        String lyrics = fetch(baseURL + lyricsURL);
        String thumbnail = (String)song.getJSONObject("result").get("header_image_url");

        JSONObject lyricsResponse = new JSONObject();
        lyricsResponse.put("lyrics",lyrics);
        lyricsResponse.put("thumbnail",thumbnail);

        return lyricsResponse;

    }

    /**
     * getMostPopularSong() methods returns the most relevant song based on the maximum number of pageviews
     * that exist corresponding to a particular search query
     * @param hitsArray
     * @return
     * @throws JSONException
     */
    private int getMostPopularSong(JSONArray hitsArray) throws JSONException {
        int maxPageViews = -1;
        int mostPopularSongIndex = 0;
        int pageviews = 0;
        for(int i=0; i<hitsArray.length(); i++){
            JSONObject song = hitsArray.getJSONObject(i);
            try{
                pageviews = (int)song.getJSONObject("result").getJSONObject("stats").get("pageviews");
            }
            catch(Exception e){
                pageviews = 0;
            }
            if(pageviews > maxPageViews){
                maxPageViews = pageviews;
                mostPopularSongIndex = i;
            }
        }
        return mostPopularSongIndex;
    }

    /**
     * doGeniusLyricsSearch method uses the search tag to invoke calls to the API and internal calls
     * to methods that fetch lyrics to get relevant song, its lyrics and thumbnail returned as a JSONObject
     * @param searchTag
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject doGeniusLyricsSearch(String searchTag)
            throws IOException, JSONException {

        String songName = searchTag.split(" ")[0];
        String artistName = searchTag.split(" ")[1];

        searchTag = searchTag.toLowerCase();
        searchTag = URLEncoder.encode(searchTag, "UTF-8");

        String response = "";

        String geniusURL =
                "http://api.genius.com/search?q=" +
                        searchTag +
                        "&access_token=" +
                        apiToken;

        try {
            URL url = new URL(geniusURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                response += str;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int startIndex = response.indexOf("href=\"");
        int endIndex = response.indexOf("\">");
        response = response.substring(startIndex+6, endIndex);
        geniusURL = response;
        response = fetchAPIResults(geniusURL);

        JSONObject jsonObject = new JSONObject(response);
        JSONArray hitsArray = jsonObject.getJSONObject("response").getJSONArray("hits");
        JSONObject lyricsResponse = new JSONObject();
        if(hitsArray.length() == 0){
            lyricsResponse.put("thumbnail", "No thumbnail found");
            lyricsResponse.put("lyrics", "No lyrics found");
        }
        else{
            lyricsResponse = getLyrics(songName, artistName, hitsArray);
        }

        return lyricsResponse;
    }


    /**
     * Fetches response from the Genius API
     * @param urlString
     * @return
     */
    private String fetchAPIResults(String urlString){
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * This method uses the lyrics URL received from the Genius API to parse and return the lyrics
     * @param urlString
     * @return
     */
    private String fetch(String urlString){
        String response = "";

        Document doc = null;
        try {
            doc = Jsoup.connect(urlString).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.prettyPrint(false);
        doc.outputSettings(outputSettings);

        Elements elementLyrics = doc.getElementsByAttribute("data-lyrics-container");
        for(Element element: elementLyrics){
            String html = element.html();
            html = html.replaceAll("<br>","\n");
            String line = Jsoup.clean(html,"", Whitelist.none(),outputSettings);
            response += line + "\n";
        }

        return response;
    }

}
