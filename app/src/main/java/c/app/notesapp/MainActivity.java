package c.app.notesapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements fragment_createnote.onNewNoteCreatedListener, NavigationView.OnNavigationItemSelectedListener, fragment_shownotes.EditNoteListener {
    private DrawerLayout drawer;
    private fragment_shownotes shownotes;
    private fragment_createnote createnote;
    private NavigationView navigationView;

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

        if (savedInstanceState == null)
        {   //only runs this code if the app is running for the first time (instead of overwriting it if the device is replaced etc.)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, shownotes).commit();
            navigationView.setCheckedItem(R.id.nav_notes);
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
        }                                       //this avoids duplication of variables or having to run parallel viewmodels which is far more complicated
        else
        {
            shownotes.deleteNote(note);
        }

        navigationView.setCheckedItem(R.id.nav_notes);
    }

    @Override
    public void editNoteFromNotes(final Note editedNote) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,createnote)
                .setCustomAnimations(R.anim.slide_in_fromright,R.anim.slide_out_up,R.anim.slide_in_fromright,R.anim.slide_out_up)
                .addToBackStack(null).commit();   //initialises the fragment

        createnote.editNote(editedNote);    //sets the fields to the current note to be edited
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_newnote:
                if (createnote.isVisible()) {
                    //this clears the whole backstack if a user goes from editing a note to making a new note
                    getSupportFragmentManager().popBackStackImmediate();
                }
                //then calls the fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,createnote)
                        .setCustomAnimations(R.anim.slide_in_fromright,R.anim.slide_out_up,R.anim.slide_in_fromright,R.anim.slide_out_up)
                        .addToBackStack(null).commit();
                createnote.newNote();               //calls the newNote function to clear text fields
                break;
            case R.id.nav_notes:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                {
                    //this clears the whole backstack to keep fragment management simple
                    getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    navigationView.setCheckedItem(R.id.nav_notes);
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
