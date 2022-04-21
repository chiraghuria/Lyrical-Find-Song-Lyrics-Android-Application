package ds.cmu.edu.musiclyricsapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ds.cmu.edu.lyricsapp.R;

public class MusicLyrics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MusicLyrics musicLyrics = this;


        Button submitButton = (Button)findViewById(R.id.submit);

        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String songName = ((EditText)findViewById(R.id.songName)).getText().toString();
                String artistName = ((EditText)findViewById(R.id.artistName)).getText().toString();
                System.out.println("songName = " + songName);
                System.out.println("artistName = " + artistName);
                GetLyrics getLyrics = new GetLyrics();
                getLyrics.search(songName, artistName, musicLyrics); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void displayLyrics(Object[] output) {
        String lyrics = (String)output[0];
        Bitmap thumbnail = (Bitmap)output[1];
        System.out.println(lyrics);
        ImageView musicThumbnailView = (ImageView)findViewById(R.id.musicThumbnail);
        TextView musicLyricsView = (TextView) findViewById(R.id.musicLyrics);

        musicThumbnailView.setImageBitmap(thumbnail);
        System.out.println("thumbnail printed");
        musicThumbnailView.setVisibility(View.VISIBLE);

        musicLyricsView.setText(lyrics);
        System.out.println("lyrics printed");
        musicLyricsView.setVisibility(View.VISIBLE);
    }
}
