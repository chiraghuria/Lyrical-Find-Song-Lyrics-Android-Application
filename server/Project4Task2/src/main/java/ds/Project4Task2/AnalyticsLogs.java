/**
 * Name: Chirag Huria
 * Andrew ID: churia [at] andrew.cmu.edu
 * Project: Project 4
 * Course: Distributed Systems
 * Class: Fall 2021, Heinz, CMU
 */

package ds.Project4Task2;

import java.io.*;
import java.util.List;
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;

/**
 * Analytics logs class computes the various metrics like total searches, most searched song and artist,
 * along with keeps track of the response and request logs
 */
@WebServlet(name = "analyticsLogs", value = "/analyticsLogs")
public class AnalyticsLogs extends HttpServlet {
    MusicLyricsMongoModel musicLyricsMongoModel;

    public void init() {
        musicLyricsMongoModel = new MusicLyricsMongoModel();
    }

    /**
     * doGet method to handle the get requests to the analytics logs servlet
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String totalSearchCount = musicLyricsMongoModel.getTotalSongSearches();
        request.setAttribute("totalSearchCount", totalSearchCount);
        Map<String, String> mostSearchedSong = musicLyricsMongoModel.getMostSearchedSong();
        request.setAttribute("mostSearchedSong", mostSearchedSong);
        Map<String, String> mostSearchedArtist = musicLyricsMongoModel.getMostSearchedArtist();
        request.setAttribute("mostSearchedArtist", mostSearchedArtist);

        List<Map<String,String>> requestLogs = musicLyricsMongoModel.getRequestLog();
        List<Map<String,String>> responseLogs = musicLyricsMongoModel.getResponseLog();
        request.setAttribute("requestLogs",requestLogs);
        request.setAttribute("responseLogs",responseLogs);

        String nextView = "analyticsLogs.jsp";
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);

    }

    public void destroy() {
    }
}