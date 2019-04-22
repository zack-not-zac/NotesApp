package c.app.notesapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements fragment_createnote.onNewNoteCreatedListener, NavigationView.OnNavigationItemSelectedListener, fragment_shownotes.EditNoteListener {
    private DrawerLayout drawer;
    private fragment_shownotes shownotes;
    private fragment_createnote createnote;
    private NavigationView navigationView;

    //location variables. Location code based off tutorial from: https://www.youtube.com/watch?v=1f4b2-Y_q2A&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=4
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int ERROR_DIALOG_REQUEST = 3;
    private boolean isLocationPermissionGranted = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shownotes = new fragment_shownotes();
        createnote = new fragment_createnote();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //--------- nav drawer stuff ---------
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        while (!isLocationPermissionGranted) {
            request_permissions();
        }

        if (savedInstanceState == null) {   //only runs this code if the app is running for the first time (instead of overwriting it if the lifecycle is replaced etc.)
            Log.d(TAG, "Building shownotes fragment...");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, shownotes).commit();
            navigationView.setCheckedItem(R.id.nav_notes);
        }
    }

    private Boolean checkMapServices() {

        Log.d(TAG, "checkMapServices...");
        if (isServicesEnabled()) {
            Log.d(TAG, "Services Enabled.");
            if (isMapsEnabled()) {
                Log.d(TAG, "Maps Enabled.");
                return true;
            }
        }
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Unable to contact location services, please check your settings and try again.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean isServicesEnabled() {
        Log.d(TAG, "isServicesEnabled: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (!(available == ConnectionResult.SUCCESS)) {
            Toast.makeText(this, "Unable to contact Google Services. Check your location services and try again", Toast.LENGTH_LONG).show();
            return false;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
            return false;
        }

        return true;
    }

    public void request_permissions() {
        if (checkMapServices()) {
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                isLocationPermissionGranted = true;
                Log.d(TAG, "Location permissions granted.");
            }
        }
    }

    @Override
    public void onBackPressed() {       //this overwrites the action of the back button to close the drawer if it is open rather than exiting the app
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (createnote.isVisible()) {
                navigationView.setCheckedItem(R.id.nav_notes);
            }
            super.onBackPressed();
        }
    }

    @Override
    public void sendNoteFromCreateNote(Note note, boolean deleteNote) {
        if (!deleteNote) {
            shownotes.createNote(note);      //passes the data from the createnote fragment to the shownotes function to create the note
        }                                    //this avoids duplication of variables or having to run parallel viewmodels which is far more complicated
        else {
            shownotes.deleteNote(note);
        }
        navigationView.setCheckedItem(R.id.nav_notes);
    }

    @Override
    public void editNoteFromNotes(final Note editedNote) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, createnote).addToBackStack(null).commit();   //initialises the fragment

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {        //this creates a small delay between the fragment call and the data being inputted
            public void run() {                     //as before the data was being passed and set before the fragment fully initialised
                createnote.editNote(editedNote);    //sets the fields to the current note to be edited
            }
        }, 50);   //50ms delay to allow the fragment to fully initialise before running the editnote statement
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_newnote:
                 if (createnote.isVisible())
                 {
                    getSupportFragmentManager().popBackStackImmediate();    //stops the fragment "looping" causing the app to crash
                 }
                //addToBackStack(null) means back button takes the user back to previous fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, createnote).addToBackStack(null).commit();
                createnote.newNote();       //clears the text fields for a new note to be written
                break;
            case R.id.nav_notes:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    //this clears the backstack then goes back to the shownotes fragment to prevent a huge backstack
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, shownotes).commit();
                    break;
                } else {
                    break;
                }
            case R.id.nav_webview:
                Intent intent = new Intent(this,WebViewActivity.class);
                startActivity(intent);
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

}
