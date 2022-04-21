/**
 * Name: Chirag Huria
 * Andrew ID: churia [at] andrew.cmu.edu
 * Project: Project 4
 * Course: Distributed Systems
 * Class: Fall 2021, Heinz, CMU
 */
package ds.Project4Task2;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servlet to hit the endpoint getLyrics and return the relevant lyrics to the application
 */
@WebServlet(name = "getLyrics", value = "/getLyrics")
public class MusicLyricsServlet extends HttpServlet {
    private MusicLyricsModel lyricsSearch;
    private JSONObject lyricsJSONResponse;
    private MusicLyricsMongoModel mongoModel;

    /**
     * init method to new up the mongodb connection and musiclyricsmodel class
     */
    public void init() {
        lyricsSearch = new MusicLyricsModel();
        mongoModel = new MusicLyricsMongoModel();

    }

    /**
     * doGet method to cater to the GET HTTP requests. This uses the request and response parameter to
     * keep track of logging and returns the response to the server with song's lyrics and thumbnail
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // request logging
        String requestMethod = request.getMethod();
        String requestURL = String.valueOf(request.getRequestURL());
        String requestProtocol = request.getProtocol();
        boolean requestIsSecure = request.isSecure();
        String requestBrowserName = request.getHeader("User-Agent");
        String requestMachineIP = request.getRemoteAddr();


        String songName = request.getParameter("songName");
        String artistName = request.getParameter("artistName");
        String searchTag = songName + " " + artistName;

        if (songName == null && artistName == null) {
            return;
        }

            try {
                lyricsJSONResponse = lyricsSearch.doGeniusLyricsSearch(searchTag);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        JSONObject requestLogObject = new JSONObject();
        try {
            requestLogObject.put("RequestMethod", requestMethod);
            requestLogObject.put("RequestProtocol",requestProtocol);
            requestLogObject.put("RequestURL",requestURL);
            requestLogObject.put("RequestSecure",requestIsSecure);
            requestLogObject.put("RequestBrowserName",requestBrowserName);
            requestLogObject.put("RequestMachineIP",requestMachineIP);
            requestLogObject.put("RequestSongName",songName);
            requestLogObject.put("RequestArtistName",artistName);

            // [Citation] for current date: https://stackabuse.com/how-to-get-current-date-and-time-in-java/
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
            Date requestDate = new Date(System.currentTimeMillis());
            String requestTimestamp = formatter.format(requestDate);
            requestLogObject.put("RequestTimestamp",requestTimestamp);
            mongoModel.insertRequestLog(requestLogObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // response logging
        // [Citation] for current date: https://stackabuse.com/how-to-get-current-date-and-time-in-java/
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        Date responseDate = new Date(System.currentTimeMillis());
        String responseTimestamp = formatter.format(responseDate);
        JSONObject responseLogObject = lyricsJSONResponse;
        try {
            responseLogObject.put("ResponseTimestamp",responseTimestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mongoModel.insertResponseLog(responseLogObject);


        // printing for the getLyrics endpoint
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Song: " + songName + " and Artist: " + artistName + "</h1>");
        out.println(lyricsJSONResponse);
        out.println("</body></html>");
    }

    public void destroy() {
    }
}