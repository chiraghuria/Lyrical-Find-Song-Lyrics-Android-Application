package ds.cmu.edu.musiclyricsapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

/*
 * This class provides capabilities to search for an image on Flickr.com given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView pictureReady method to do the update.
 *
 */
public class GetLyrics {
    MusicLyrics musicLyrics = null;

    /*
     * search is the public GetPicture method.  Its arguments are the search term, and the InterestingPicture object that called it.  This provides a callback
     * path such that the pictureReady method in that object is called when the picture is available from the search.
     */
    public void search(String songName, String artistName, MusicLyrics musicLyrics) {
        this.musicLyrics = musicLyrics;
        new AsyncGeniusLyricsSearch().execute(songName, artistName);
    }

    /*
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncGeniusLyricsSearch extends AsyncTask<String, Void, Object[]> {
        protected Object[] doInBackground(String... inputs) {
            return search(inputs[0], inputs[1]);
        }

        protected void onPostExecute(Object[] output) {
            musicLyrics.displayLyrics(output);
        }

        /*
         * Search Flickr.com for the searchTerm argument, and return a Bitmap that can be put in an ImageView
         */
        private Object[] search(String songName, String artistName){

            songName = songName.replace(" ","+");
            artistName = artistName.replace(" ","+");
            //String urlString = "http://young-plains-79255.herokuapp.com/getLyrics?songName=" + songName + "&artistName=" + artistName;
            String urlString = "https://frozen-beyond-88620.herokuapp.com/getLyrics?songName=" + songName + "&artistName=" + artistName;
            StringBuilder result = new StringBuilder();
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = null;
            Object[] output = new Object[2];
            result = new StringBuilder(result.substring(result.indexOf("{")));
            System.out.println(result.toString());
            try {
                jsonObject = new JSONObject(result.toString().trim().replaceAll("null", "\"none\""));
                System.out.println("Thumbnail: " + jsonObject.getString("thumbnail"));
                System.out.println("Lyrics: " + jsonObject.getString("lyrics"));
                System.out.println("----------------------------------------------------------------");


                if(jsonObject.getString("lyrics").equalsIgnoreCase("no lyrics found")){
                    output[0] = "No Lyrics Found! Please try again";
                    String errorURL = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAilBMVEX///8AAAD8/Py8vLz29vbz8/Pq6uru7u75+fnFxcXX19e+vr7CwsLb29vJycng4OCWlpalpaXPz89QUFBra2uzs7Pl5eV1dXVBQUGHh4ednZ0rKyt+fn5fX1+NjY2Dg4N4eHitra1WVlZubm47OztMTEwYGBg8PDwaGhoREREuLi4iIiI0NDQLCwtMrTuBAAALsElEQVR4nO2b2XbjqhKGAc1CI7bloS2PceZ+/9c7xSgkp/smik/vteq7cGQMiF8URYEIIQiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAiCIAjyXdh/rmKfj83CsRGQEB6aVpOFph1s65KiUbPq3eXzZdkmQ5orXER3AuLaXTISVquiu29OUt+nfRN6uy4tfQ4JER04qjYy5iWtQ1c0XFLaH/cbSpuhOi9nNL5TTHt3nW5Vlj6etIb9/phfIZ8kRDQzd6sb+p4qjTQwDet29DUyfVNTulJX8YneUtNfdGUKQ843XyIjG7q31+ETbbo0OVNajm++pLc5RI2wTXIMCgnp6FJnCtzPCb1ohRF9d4aY0cW4OqZyLojHll7W9vpGO5UneqWjjl7R6+abeu75s0J9y0RlGhQSLlsHzVv4jQtoq8X61WXUDSpGKpovrcIzrcxVR69utDL4tmoerTDS2nyFoS6RuVaqxl1peFddSs9eRVvSW4UgKzWXDR3cDfvoyeHRColupK+Q0J38XPhNYeBG2vvq3hr7M1l8EqdQ9i0zP4R0yLOn6eMVRtoR3fdhCNJHk8Ftc1cdG/pwR+NB4dr3l/2nyUwKCq78JxQGk4TJOIynmQo1ujI6mcx22kx9hZXzlDk8KOYUfuy9ci01BhtDb7IfUbirS6ADauMjB4V2CnMKGYl+L2TntXRST6UlewrDN9valCptRiEzBj0qR8hG5f4JhR7qaUbuybOA/gq1hxQ2e0E/lAs9vE3qyaWNQU7nf4rfvxNzuXxmshqjMBpNwaUuRw40kXl+QmGbG4QwDdBx3GVB6d5YkEla3Ci9as1fKFQPhr64wks7m5zNrOEUFl65WisUxnJ+QuHI6asG9Nfrdb3+pEFqnQlM1jLtiZ5DoroDrHQSdBZmxF6uMueGtqkNBzrrcJxC37nlSn4Ik4lifoWpr9A0QFtpAvEMs5ls97rIsprGW40emKa60EY+8PlxMVmsp/HCWBkryLj98mlupRTOuuSI7hR22mwY+FH7rBObiTv/n+pZceBTiQ+tmyqMO2Fk/Wyz9Cf99+XilTtQyNO6+OiBCmWLqHEVTiGELnaS6P2ByMBhKHdrFYIwkzOmb5+/FO9vH4vbQc4IdoKQvF6JdHc2z+vb4nadS5zibwpDGzoPmRjdmCcsfJ8PZvZsimQu56fO2TaG3eem2cs4oQbjsN2UQ9VgLi7P4qM5+kY8h8JskuIUyijjPFEI469RVsRgoeNGIoP5vlKNHhTCIzjo3xzWSsntzaXe3sd55vc0kz5kfh86o/T6EIJHs2BIn8H/MCUWxtFBJ3oKYbk0VKRwkXdO7dVu+oB/QuG+XVnO5VghS+mLzuQUMvJqo8rone6VWrEBgXpoeQrBiTofo+ldtNbSteq3xj4Zx2FBZsbfs6DKefp9CEYJTWCJ39G5W6qDy6DPtydKf1VDdV68Urqu0ly27rKFaGJ3eFWh6IjthcwMC4oBLift5OA2T6T9QRtY2givSHCI7K/lYXm5rM+Da0zb2msyb0NfwGFYLpK6/6DPfQlmMm7PaktmhvG8q2sVfANyeRAW3tq95uqL8Kb3qLBfkoKLLonLjA92zb29pYSPowIx7KPFWQZhYpYlZEIydXzfJg2mNwn5oDAKdNsrr6lVYfql45XJWXNh0ljgKcwmrR2eUxxU6mEKPr17XJCZ+bvCKtMt9xTWtkAUVM4EE27M2FdY8sl2olOYFraoKCZW+lCFbFAzKAydRWaZN8Y6o8xTGE1s1FOYF3bTNS0mM8pDFLqUMLC3HxRWtgXduKDIlFf0FFobTS0MalELk9TTXnPZicxl6oqUzbvXz75QaI3LjbhBYe0UVONoL9H1DNWV5irlPDCAs1bPrA485xvImCJ3eXjBOZ9VIgsmgwUUhlM1pDJecLBR3TKPTK9IhlFqcqaFSAxVnsTybsJ/OGrdnXObpyzieLrV/z3+rDDlwyRoFQrXqx2fuIi8GlVXuVEau2614zDzB2ipzD6zT27+cfiFQq43NapisN9K6H2OYQDV05ZoySyoE+iEuPSmgdxmNQpT3o3KqTcA1l7mVxhykbuNGpUS8QwoiiwLMjfoCpWWZdxqzKczc6IUpoUcUTCUeJGHdpVvO8gqHA39hCuTKbneMvkJhdD0ygRtWqH1AyzKA2tXNhpJa67NL58uK3VLwRGDQwyjKKkL11XGC1mF4VcKSaaf7w8ovLPSKBjeENppLhvirbBQTSmnfRirPvRbn7vrXA/fvyqM9K0erVBOcxJPIXgOWeJuHOqEUeudz2R6XjcK2RfjkNiZ8eEKE/3FV8iKWv0wDckqXZ2nMHE1aTu1s2ox8aVGopphH64w1T7RV2gayscv3MGJ6uo8hemw0AA7ZU6h8NdilfsSySn24QpNi8cKlclNnGmth9OfFBI5eu2s6s+loWeyso5/x0ohm6861f5nrDD0JkU54VmFqVe09CMHsNN/wdN0pkTulxTGI44U1oEXYJbcKYQgzf4whIH6W9c9WGHJ72aLxLXJC3qEtTW/umjUegjMnEJW2MFXjaPsjt/NQt8m5GVt6TqmGhZHmq5yM34Z6qQ4527dyyqey4fBuozb19aDwrQccioiXrjnFAdCzZ7VdI0vivkVytWKBsIteSomCuxShlfWU2Q6CT7Hnp4Hmah4ULl2hm5jK+BifCcwWm+fpghEXgXFdBmRzq8Q1qQD6tgPiw3JYK1JYtImJs06IfLSd5+liXHLerL2gE7OPT1pLSpR3y8E4/wu6b/DQ47pIQiCID8BI3YHFv64K3bv2r9IGv36z+LEMGIFyjdeM+87/18J7SvJVUvIwQTQ8iRNt+/Xcqs+ypT887I/DfFGbA666ddj8aHvdzKG2R4OTXM4uIMondrIE52sIFm1eks1NDfpUpaaeDZJ4H4C4iP2A882/K2PPiTygMze3HwBMfAlJknTQ0O2EGPFmyAl8dqdAzGn2MhRhprnHj6LJ0jp4mS7iju3qN0fzkAr5Pv/ZSHal0oeHzdvhk813FRXs1rB1+bc7hb7dHaLj7ZLFRD3ASjdJ07hUkWlqXzPC383iRqI9jUhEeeFClG3UFZI3fDrpyqxE2QYlnsXiS71VuUNfu2O5kdQeFwoRVKhziueZu/EaM/kEZn2XILCo1EIlnuxD1MqPJ8npcQ5uUn5W2jWxsTY1dYptFibIMKoSt9BYTMo3CbqdNgqc08jO84sEBQS6L1kQYSncAlWdzHPXyrsp8tkcSaFFLRNSLy0iU/yY6zQ9uFaSwVjLZ3CIwyDIwmkiawqkxec3NOs8ohSSPq4T0gO4/BolEhPk/ebfQ79JBXe/RMEKCRbrhTm5sAII4t0qvC4EqKSu4SuglUwKIykQnKqPIXAet5XT1qhOrqdt24chuZUYnna6z68TceGkKe3Lp1UWO+twhdZaqRw20hPc/YVrgYr1QrJLdEKjUVff0IhkQt3qdCcKim3atYHlp1SeNC716kTKvuQhBs1Dp/sOVJlrmMrtYvjk17eM3LtzCNhZGkUJhuvD+Vzml+hFiMV1vr8KAwWkqjbHXKlMDG3HbaelOvJ9w2kNPaAbHmv0I7fXA9WBuOdMdOhG6IUMlYdA6lQbymcJ6c6Z1B4socN5VnD5hQSdgAfwj7ltl78aWYLrsZYtXGzhT6YuFPnMy9K4skk+S+lnEKyVw0Pb3Lyb1TO3dkohLte4LGcVN2rnsxNFBiFeSU7M79erjr0aC7XpfSh5U7tsVyW64s7hMbylT6zt1S92t7Wy4vpu9bfqlrbTmdkt9mttgttrNv+fF7K0Rif9I83uOVpvT+eFv4x/nlgo6iZuRjV+2auWDhkNaWYCgnUv2hN95007tCXDH/zzE4ZJBJC2STTnyyFjPKl4/3/LCIIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAIgiAP5X+UJpXjQmKScAAAAABJRU5ErkJggg==";
                    URL thumbnailURL = new URL(errorURL);
                    Bitmap thumbnail = getRemoteImage(thumbnailURL);
                    output[1] = thumbnailURL;
                }
                else{
                    String lyrics = jsonObject.getString("lyrics");
                    URL thumbnailURL = new URL(jsonObject.getString("thumbnail"));
                    Bitmap thumbnail = getRemoteImage(thumbnailURL);
                    output[0] = lyrics;
                    output[1] = thumbnail;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return output;

        }

        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}