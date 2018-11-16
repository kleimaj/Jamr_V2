package com.example.kleimaj.jamr_v2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static String picturePath;
    private static int RESULT_LOAD_IMAGE = 1;
    public static ArtistModel currentUser;
    DatabaseReference currentUserDb;
    private FirebaseAuth mAuth;
    DatabaseManager db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragment1();
                    break;
                case R.id.navigation_dashboard:
                    Toast.makeText(MainActivity.this,
                            "Messaging Coming Soon!", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_notifications:
                    switchToFragment3();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = new DatabaseManager();

        //can't find display??
        //TextView display = (TextView) findViewById(R.id.ArtistName);
        //must load data from local file first... still need to figure that out
       // display.setText(currentUser.getName());
      //  initialize();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchToFragment1();
    }


    public void switchToFragment1() {
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, SwipeScreen1.newInstance());
        ft.commit();
    }

    public void switchToFragment2(){

    }


    public void switchToFragment3() {
        //initialize();
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, ProfileActivity.newInstance());//MyInfor.newInstance());
        ft.commit();
        //initialize();
    }


    public static String returnPicturePath() {
        return picturePath;
    }


    /* FUNCTIONS FOR MATCHING ALGORITHM */

    //use for users who have full preferences, algo works for unfilled preferences --
    // (i.e if maxAge is set to 0, if gender is null, arraylists are empty)
    public static ArrayList<ArtistModel> getArtists(ArrayList<String> genres, ArrayList<String>
      identities, String gender, int maxAge) {
        //arraylist of all artists from another function
        ArrayList<ArtistModel> usersFromDatabase = new ArrayList<ArtistModel>(); //should be full

        ArrayList<ArtistModel> returnedUsers = new ArrayList<ArtistModel>();

        for (int i = 0; i < usersFromDatabase.size(); i++) {
            ArtistModel currentUser = usersFromDatabase.get(i);
            if (matchingAge(maxAge,currentUser.getAge()) &&
                matchingGender(gender, currentUser.getGender()) &&
                matchingIdentities(identities,currentUser.getIdentities()) &&
                matchingGenres(genres, currentUser.getGenres())) {

                returnedUsers.add(currentUser);
            }
        }
        return returnedUsers;
    }
    //if userIdentities contains AT LEAST ONE identity in identityPreferences, return true
    public static boolean matchingIdentities(ArrayList<String> identityPreferences,
                                             ArrayList<String> userIdentities) {
        //if this user has no preferences, they will see every user
        if (identityPreferences.size() == 0) {
            return true;
        }
        for (int i = 0; i < identityPreferences.size(); i++) {
            String thisIdentity = identityPreferences.get(i);
            if (userIdentities.contains(thisIdentity)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchingGenres(ArrayList<String> genrePreferences,
                                         ArrayList<String> userGenres) {
        if (genrePreferences.size() == 0) {
            return true;
        }
        for (int i = 0; i < genrePreferences.size(); i++) {
            String thisGenre = genrePreferences.get(i);
            if (userGenres.contains(thisGenre)){
                return true;
            }
        }
        return false;
    }

    public static boolean matchingGender(String genderPreference, String userGender) {
        if (genderPreference.isEmpty()) {
            return true;
        }
        if (userGender.equals(genderPreference)) {
            return true;
        }
        return false;
    }

    public static boolean matchingAge(int agePref, int userAge) {
        if (agePref == 0) {
            return true;
        }
        else if (agePref >= userAge) {
            return true;
        }
        else {
            return false;
        }
    }

    public String readUserFile(boolean isBand){
        Context context = getApplicationContext();
        BufferedReader reader = null;
        StringBuilder text = new StringBuilder();
        String userId = mAuth.getCurrentUser().getUid();
        //Try Catch block to open/read files from directory and put into view
        try {
            FileInputStream stream = context.openFileInput(userId+"profileInfo.txt");
            InputStreamReader streamReader = new InputStreamReader(stream);
            reader = new BufferedReader(streamReader);

            String line;
            while((line = reader.readLine()) !=null){
                text.append(line);
                text.append('\n');
            }
            reader.close();
            stream.close();
            streamReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
