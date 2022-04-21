/**
 * Name: Chirag Huria
 * Andrew ID: churia [at] andrew.cmu.edu
 * Project: Project 4
 * Course: Distributed Systems
 * Class: Fall 2021, Heinz, CMU
 */
package ds.Project4Task2;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Landing Page servlet is simply to display a welcome message until a valid endpoint is hit - getLyrics and analyticsLogs
 */
@WebServlet(name = "landingPage", value = "")
public class LyricsLandingPageServlet extends HttpServlet {
    private String welcomeMessage;

    public void init() {
        welcomeMessage = "Welcome to Music Lyrics Search Application Server.";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("welcome", welcomeMessage);
        String nextView = "index.jsp";
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);

    }

    public void destroy() {
    }
}