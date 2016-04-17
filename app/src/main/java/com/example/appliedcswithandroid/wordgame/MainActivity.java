package com.example.appliedcswithandroid.wordgame;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Word Game";
    Node root = null;
    Random random;
    ArrayList<Character> list = null;
    GridView gridView;
    Button wordFormed, reset;
    TextView score, timer;
    MyAdapter adapter = null;
    String wordEntered = "";
    private static int WORD_COUNT = 0;
    CountDownTimer countDownTimer;

    AdapterView<?> p = null;
    ArrayList<View> views;

    String s;
    char[] startingLetters = new char[16];
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        random = new Random();
        gridView = (GridView) findViewById(R.id.gridview);

        views = new ArrayList<View>();

        score = (TextView) findViewById(R.id.score);
        score.setText("Score: " + WORD_COUNT);

        wordFormed = (Button) findViewById(R.id.word_formed);
        reset = (Button) findViewById(R.id.reset_button);

        InputStream inputStream = null;
        AssetManager assetManager = getAssets();
        try {
            inputStream = assetManager.open("words.txt");

        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        //For inserting the dictionary words in a data Structure
        try {
            while ((line = in.readLine()) != null) {
                Node curr = new Node(line.length(), new ArrayList<String>());
                curr.arrayList.add(line);
                if (root == null)
                    root = curr;
                else
                    Insert_Node(root, curr, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Log.d(TAG, "Dictionary Formed");
/*
        String s = getWord(4,root) + getWord(4,root) + getWord(5,root) + getWord(3,root);

        char[] startingLetters = new char[16];
        startingLetters = s.toUpperCase().toCharArray();

        list = new ArrayList<Character>();
        for(int i=0;i<16;i++)
            list.add(startingLetters[i]);

        Collections.shuffle(list);
        gridView.setAdapter(new MyAdapter(this, list));
*/
        reset_grid();

        start_timer();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                p=parent;
                views.add(view);
                if (gridView.getAdapter().getView(position, view, parent).isEnabled()) {    //to disable the buttons once used
                    char letter = list.get(position);

                    wordEntered += letter;
                    wordFormed.setText(wordEntered);

                    gridView.getAdapter().getView(position, view, parent).setEnabled(false);
                }
            }
        });

        wordFormed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordEntered.length() < 3) {
                    Toast.makeText(MainActivity.this, "The word must be of size greater than 2", Toast.LENGTH_SHORT).show();
                    wordEntered = "";
                    wordFormed.setText(wordEntered);
                    enable_buttons();
                } else
                    check(wordEntered);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_grid();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getWord(int size, Node curr) {
        System.out.println(curr.arrayList.get(0));
        if (curr.size == size)
            return curr.arrayList.get(random.nextInt(curr.arrayList.size()));
        else if (curr.size > size)
            return getWord(size, curr.left);
        else
            return getWord(size, curr.right);

    }

    public static boolean Search(String word, Node curr) {
        int size = word.length();
        if (curr == null)
            return false;

        if (curr.size == size) {
            if (curr.arrayList.contains(word))
                return true;
            else
                return false;
        } else if (curr.size > size)
            return Search(word, curr.left);
        else
            return Search(word, curr.right);

    }

    public void Insert_Node(Node currRoot, Node curr, String word) {
        int size = word.length();
        if (currRoot.size == size)
            currRoot.arrayList.add(word);
        else if (currRoot.size > size) {
            if (currRoot.left == null)
                currRoot.left = curr;
            else
                Insert_Node(currRoot.left, curr, word);
        } else {
            if (currRoot.right == null)
                currRoot.right = curr;
            else
                Insert_Node(currRoot.right, curr, word);
        }
    }

    public void check(String word) {
        word = word.toLowerCase();

        if (!Search(word, root)) {
            Toast.makeText(this.getApplicationContext(), "The word you entered is not valid", Toast.LENGTH_SHORT).show();
            wordEntered = "";
            wordFormed.setText(wordEntered);
            enable_buttons();
        } else {
            WORD_COUNT++;
            score.setText("Score: " + WORD_COUNT);


            for (int i = 0; i < word.length(); i++) {
                list.remove((Object) word.charAt(i));
            }





            String newWord = this.getWord(word.length(), root).toUpperCase();

            for (int i = 0; i < newWord.length(); i++)
                list.add(newWord.charAt(i));

            Collections.shuffle(list);
            adapter.notifyDataSetChanged();

            wordEntered = "";
            wordFormed.setText(wordEntered);
            enable_buttons();
        }
    }

    public void reset_grid() {
        WORD_COUNT = 0;

        wordEntered = "";
        wordFormed.setText(wordEntered);

        score.setText("Score: " + WORD_COUNT);

        s = getWord(4, root) + getWord(4, root) + getWord(5, root) + getWord(3, root);

        startingLetters = s.toUpperCase().toCharArray();

        if (list == null)
            list = new ArrayList<>();

        for (int i = 0; i < 16; i++)
            list.add(startingLetters[i]);

        Collections.shuffle(list);



        if (adapter == null) {
            adapter = new MyAdapter(this, list);
            gridView.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();

        gridView.setEnabled(true);
        wordFormed.setEnabled(true);

        enable_buttons();

        start_timer();
    }
    public void enable_buttons()
    {
        if(p!=null) {
            for (int i = 0; i < views.size(); i++) {

                views.get(i).setEnabled(true);
            }
            views.clear();
        }
    }
    public void start_timer() {
        timer = (TextView) findViewById(R.id.timer);

        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(120000, 1000) {
                public void onTick(long millisUntilFinished) {
                    timer.setText("Time left: " + millisUntilFinished / 1000 + " ");
                }

                public void onFinish() {
                    gridView.setEnabled(false);
                    wordFormed.setEnabled(false);
                    timer.setText("Game Over ");
                }
            };
        }

        countDownTimer.start();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.appliedcswithandroid.wordgame/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.appliedcswithandroid.wordgame/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

class Node {
    int size;
    ArrayList<String> arrayList;
    Node right;
    Node left;

    Node(int size, ArrayList<String> arrayList) {
        this.size = size;
        this.arrayList = arrayList;
        right = null;
        left = null;
    }
}