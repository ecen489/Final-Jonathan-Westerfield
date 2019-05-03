package com.newsboi.jonathanwesterfield.newsboi;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_APPEND;
import static com.newsboi.jonathanwesterfield.newsboi.LocationObj.encodeToBase64;


/**
 * A simple {@link Fragment} subclass. Will handle going back to newsfeed and saving an article.
 * When saving an article, the user will then take a picture of where they were.
 */
public class WebButtons extends Fragment
{

    private static final String ARG_PARAM1 = "article_object";
    Button saveBtn;
    Button backBtn;
    private NewsObj.Article article;
    private String saveFilePath = "saved_pages.txt";
    private DatabaseReference dbRef;
    private DatabaseReference dbTable;
    private FirebaseDatabase fireDB;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    static final int CAMERA_REQUEST_CODE = 1;
    //static final int CAMERA_REQUEST_CODE2 = 11;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_PHOTO2 = 2;

    public WebButtons() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_buttons, container, false);
        this.saveBtn = (Button) view.findViewById(R.id.saveBtn);
        setSaveBtnListener();

        this.backBtn = (Button) view.findViewById(R.id.backBtn);
        this.setBackBtnListener();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        fireDB = FirebaseDatabase.getInstance();
        dbRef = fireDB.getReference();
        dbTable = dbRef.child("locations/");

        return view;
    }

    public void setSaveBtnListener()
    {
        this.saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onSave(view);
            }
        });
    }

    public void setBackBtnListener()
    {
        this.backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                returnToPrevActivity(view);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        Intent fromMain = getActivity().getIntent();
        this.article = (NewsObj.Article) fromMain.getSerializableExtra(ARG_PARAM1);
    }

    public void returnToPrevActivity(View view)
    {
        Intent mainIntent = new Intent();
        getActivity().finish();
    }

    /**
     * Push the location to firebase
     * @param location
     */
    public void push(LocationObj location)
    {
        DatabaseReference rankey = dbTable.push();
        rankey.setValue(location, new DatabaseReference.CompletionListener()
        {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference)
            {
                // no error
                if (databaseError == null)
                    Toast.makeText(getContext(), "Successful Upload", Toast.LENGTH_SHORT)
                            .show();
                else
                    Toast.makeText(getContext(), "Upload FAILED", Toast.LENGTH_SHORT)
                            .show();
            }
        });

        return;
    }

    /**
     * Gets the user's current location
     * @return location object with the user's location
     */
    public double[] getLocation()
    {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double latitude = 0.0, longitude = 0.0;

        if(location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        double coordinates[] = {latitude, longitude};

        return coordinates;
    }

    public void onSave(View view)
    {
        try
        {
            NewsPage newsPageFrag = (NewsPage) getFragmentManager().findFragmentById(R.id.newsPageFragment);

            Gson gson = new Gson();
            String articleJson = gson.toJson(article);
            System.out.println("JSON OBJECT");
            System.out.println(articleJson);

            if (isAlreadySaved(articleJson))
                newsPageFrag.showBadInputAlert(view);
            else
            {
                PrintStream out = new PrintStream(getActivity().openFileOutput(this.saveFilePath, MODE_APPEND));
                out.println(articleJson);
                out.close();
                System.out.println("\nSaved Article Object\n");
                newsPageFrag.showArticleSaved(view);
                openCamera();


                // this.alreadySaved = true;
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isAlreadySaved(String json)
    {
        try
        {
            Scanner scanner = new Scanner(getActivity().openFileInput(this.saveFilePath));
            String fileLine;

            while (scanner.hasNext())
            {
                fileLine = scanner.nextLine();
                if(fileLine.equalsIgnoreCase(json))
                    return true;
            }
            return false;
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sometimes we have to ask the user for permission to use the camera
     * Shows OK/Cancel confirmation dialog about camera permission.
     * @param view
     */
    public void showCameraPermissionsAlert(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("We need help").
                setMessage("We need permission to use the camera")
                .setPositiveButton("Yeet", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Activity parent = getActivity();
                        parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                REQUEST_TAKE_PHOTO);
                    }
                })
                .setNegativeButton("Hell Naw",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Activity activity = getActivity();
                                if (activity != null)
                                    activity.finish();
                            }
                        });


        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Open up the camera app to take a picture since I don't know the correct
     * REQ_CODE to take a picture. 90210 from the slides is bullshit and doesn't work.
     * It makes the app crash every time so REQ_CODE is set to be 1 since I couldn't find
     * any documentation that actually lists what the code numbers mean
     */
    public void openCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
    }

    /**
     * Get the picture that was taken with the defualt camera app.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(resultCode == RESULT_OK)
        {
            Bitmap bmp = (Bitmap) intent.getExtras().get("data");
            String base64str = encodeToBase64(bmp);

            double coordinates[] = getLocation();

            LocationObj location = new LocationObj(coordinates[0], coordinates[1], base64str);
            push(location);
        }
    }

}
