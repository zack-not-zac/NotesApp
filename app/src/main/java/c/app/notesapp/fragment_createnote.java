package c.app.notesapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

class fragment_createnote extends Fragment implements OnMapReadyCallback {

    //for logging
    private static final String TAG = "createnote";

    //for detecting changes in notes
    private boolean isNoteChanged = false;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            isNoteChanged = true;
        }
    };

    //for UI stuff
    private EditText editTextTitle;
    private EditText editTextDesc;
    private MenuItem btn_delete;

    //For fragment communication
    private onNewNoteCreatedListener listener;

    //used when editing notes
    private int id = -1;

    //for mapview & location
    private FusedLocationProviderClient locationProviderClient;
    private Location deviceLocation = null;
    private double noteLat = 0.0;
    private double noteLng = 0.0;
    private MapView mapView;
    private GoogleMap note_googleMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final float mapZoomLevel = 17;
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.createnote_layout, container, false);

        editTextTitle = v.findViewById(R.id.edittxt_title);
        editTextDesc = v.findViewById(R.id.edittxt_description);
        mapView = v.findViewById(R.id.note_mapView);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        GoogleMapInit(savedInstanceState);

        setHasOptionsMenu(true);        //tells the system this fragment uses the OptionsMenu

        return v;
    }

    //Maps code based on tutorial from: https://www.youtube.com/watch?v=118wylgD_ig
    private void GoogleMapInit(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    private void fullscreenNoteText() {
        //sets the text to take up the whole page if a note does not have a map location.
        RelativeLayout layout = v.findViewById(R.id.noteRelativeLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                2.0f
        );
        layout.setLayoutParams(param);
    }

    private void noteHasMapView() {
        //sets the layout weight of the text fields back to half the page when a map is about to display
        RelativeLayout layout = v.findViewById(R.id.noteRelativeLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        layout.setLayoutParams(param);

        mapView.setVisibility(View.VISIBLE);        //shows the map once the button is clicked.
    }

    void newNote() {
        //made this function so that clicking "Add New Note" in the navbar clears the text fields
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {        //this creates a small delay between the fragment call and the data being inputted
            public void run() {                     //as before the data was being passed and set before the fragment fully initialised by MainAcitivity
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add New Note");

                fullscreenNoteText();

                editTextTitle.setText("");
                editTextDesc.setText("");

                editTextTitle.addTextChangedListener(textWatcher);
                editTextDesc.addTextChangedListener(textWatcher);
            }
        }, 50);   //50ms delay to allow the fragment to fully initialise before running the code

        id = -1;

        noteLat = 0.0;
        noteLng = 0.0;

        isNoteChanged = false;
    }

    void saveNote() {
        String title = editTextTitle.getText().toString();
        String desc = editTextDesc.getText().toString();

        if (title.trim().isEmpty())     //dont save the note if it is empty (trim removes empty spaces)
        {
            Toast.makeText(getContext(), "Note title cannot be empty!", Toast.LENGTH_SHORT).show();
        } else {
            //send note to shownotes to be saved in db
            Note newNote = new Note(title, desc);

            if (id != -1) {
                newNote.setId(id);      //sets the id if a note was passed into the editNote function
            }

            if (noteLat != 0.0 && noteLng != 0.0) {
                newNote.setLatitude(noteLat);
                newNote.setLongitude(noteLng);
            }

            if (deviceLocation != null) {
                newNote.setLatitude(deviceLocation.getLatitude());
                newNote.setLongitude(deviceLocation.getLongitude());
            }

            listener.sendNoteFromCreateNote(newNote, false);

            //set edittext items back to empty once note has been saved
            editTextTitle.setText("");
            editTextDesc.setText("");

            //reset variables
            id = -1;
            noteLat = 0.0;
            noteLng = 0.0;

            clearTextListeners();
        }
    }

    void editNote(final Note note) {
        //inserts the note data into the text views. The app then just calls saveNote as normal, then the DAO replaces the object if a conflict exists
        //if it does exist, then the note will appear edited but it is actually just replaced. Otherwise, it will be created.



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {        //this creates a small delay between the fragment call and the data being inputted
            public void run() {                     //as before the data was being passed and set before the fragment fully initialised
                btn_delete.setVisible(true);        //shows the delete button if a note is being edited
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Edit Note");

                String title = note.getTitle();
                String desc = note.getDescription();

                id = note.getId();

                if (note.getLatitude() != 0.0 && note.getLongitude() != 0.0) {
                    noteLat = note.getLatitude();
                    noteLng = note.getLongitude();

                    noteHasMapView();

                    LatLng pos = new LatLng(noteLat, noteLng);

                    note_googleMap.addMarker(new MarkerOptions().position(pos).title(note.getTitle()));
                    note_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, mapZoomLevel));    //zooms in on a specific part of the map
                } else {
                    fullscreenNoteText();
                }

                editTextTitle.setText(title);
                editTextDesc.setText(desc);

                editTextTitle.addTextChangedListener(textWatcher);
                editTextDesc.addTextChangedListener(textWatcher);

                isNoteChanged = false;
            }
        }, 50);   //50ms delay to allow the fragment to fully initialise before running the code

    }

    boolean returnNoteChangedStatus() {
        return isNoteChanged;
    }

    private void deleteNote() {
        String title = editTextTitle.getText().toString();
        String desc = editTextDesc.getText().toString();

        Note note = new Note(title, desc);

        note.setId(id);

        listener.sendNoteFromCreateNote(note, true);

        //set edittext items back to empty once note has been deleted
        editTextTitle.setText("");
        editTextDesc.setText("");
    }

    public void clearTextListeners()
    {
        editTextTitle.removeTextChangedListener(textWatcher);
        editTextDesc.removeTextChangedListener(textWatcher);
    }

    public interface onNewNoteCreatedListener {
        void sendNoteFromCreateNote(Note newNote, boolean deleteNote);
    }

    //for fragment communication
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onNewNoteCreatedListener) {
            listener = (onNewNoteCreatedListener) context;
        } else        //throws an exception if the attached fragment does not implement the onNewNoteCreatedListener
        {
            throw new RuntimeException(context.toString() + " must implement onNewNoteCreatedListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //for options menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.createnote_menu, menu);
        btn_delete = menu.findItem(R.id.delete_note_btn).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note_btn:
                saveNote();         //if the save button is clicked, then saveNote() is called
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
            case R.id.delete_note_btn:
                deleteNote();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
            case R.id.location_btn: //take current lattitude and longitude and save it to deviceLocation variable
                //location code based on tutorial from: https://www.youtube.com/watch?v=XQJiiuk8Feo
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Cannot access location. Check location permissions in your system settings and try again!", Toast.LENGTH_SHORT).show();
                }
                locationProviderClient.getLastLocation().addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        noteHasMapView();

                        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                        note_googleMap.addMarker(new MarkerOptions().position(pos).title("New Marker"));
                        note_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, mapZoomLevel));     //zooms in on a specific part of the map

                        deviceLocation = location;

                        isNoteChanged = true;
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //for MapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        note_googleMap = map;

        note_googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
