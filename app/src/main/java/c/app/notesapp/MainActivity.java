package c.app.notesapp;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity implements fragment_createnote.onNewNoteCreatedListener, NavigationView.OnNavigationItemSelectedListener, fragment_shownotes.EditNoteListener {
    private DrawerLayout drawer;
    private fragment_shownotes shownotes;
    private fragment_createnote createnote;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shownotes = new fragment_shownotes();
        createnote = new fragment_createnote();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        request_permissions();

        //--------- nav drawer stuff ---------
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {   //only runs this code if the app is running for the first time (instead of overwriting it if the device is replaced etc.)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, shownotes).addToBackStack(null).commit();
            navigationView.setCheckedItem(R.id.nav_notes);
        }
    }

    public void request_permissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(this, "Cannot access location. You will not be able to save location pins in notes.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {       //this overwrites the action of the back button to close the drawer if it is open rather than exiting the app
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void sendNoteFromCreateNote(Note note, boolean deleteNote) {
        if (!deleteNote) {
            shownotes.createNote(note);      //passes the data from the createnote fragment to the shownotes function to create the note
        }                                    //this avoids duplication of variables or having to run parallel viewmodels which is far more complicated
        else
        {
            shownotes.deleteNote(note);
        }
    }

    @Override
    public void editNoteFromNotes(final Note editedNote) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,createnote).addToBackStack(null).commit();   //initialises the fragment

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {        //this creates a small delay between the fragment call and the data being inputted
            public void run() {                     //as before the data was being passed and set before the fragment fully initialised
                createnote.editNote(editedNote);    //sets the fields to the current note to be edited
            }
        }, 50);   //50ms delay to allow the fragment to fully initialise before running the editnote statement
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_newnote:
                //addToBackStack(null) means back button takes the user back to previous fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,createnote).addToBackStack(null).commit();
                break;
            case R.id.nav_notes:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                {
                    //this simply goes back to previous fragment as there are only 2 fragment options in the application, and shownotes will always be first
                    getSupportFragmentManager().popBackStackImmediate();
                    break;
                }
                else
                {
                    break;
                }
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
