package com.newsboi.jonathanwesterfield.newsboi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This service pulls from the database and and will constantly check the user location.
 * If the user is near a past location, we will send a notification to the user.
 */
public class MyService extends Service
{
    ArrayList<LocationObj> locations;
    private DatabaseReference dbRef;
    private DatabaseReference dbTable;
    private FirebaseDatabase fireDB;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private int inc = 0;

    public MyService()
    {
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        fireDB = FirebaseDatabase.getInstance();
        dbRef = fireDB.getReference();
        dbTable = dbRef.child("locations/");
    }

    /**
     * Will constantly update our locations list and then will constantly go through the list of
     * locations we get back from the database to check and see if we are near a past location.
     * If we get a match, we sent a notification and then sleep for 10 minutes so we don't bother
     * the user (assuming they sat down to read their saved articles)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int id)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Initialize to avoid NullPtrExceptions
                locations = new ArrayList<>();

                while(true)
                {
                    try
                    {
                        getLocations();
                        double coordinates[] = getLocation();

                        LocationObj currentLocation = new LocationObj(coordinates[0], coordinates[1], "Current Location");

                        LocationObj matchLocation = checkLocations(currentLocation);
                        if (matchLocation != null)
                        {
                            sendNotification(matchLocation);
                            // Sleep for 10 minutes then check back in since we don't want to bother them
                            Thread.sleep(600000);
                        }
                        else
                            Thread.sleep(5000); // Check in every 5 seconds

                        matchLocation = null;

                    }
                    catch(InterruptedException e)
                    {
                        System.err.println("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();


        return START_STICKY;
    }

    /**
     * Goes through all of the old locations and compares them to the new one
     * @param currentLoc
     * @return the matched location or null if nothing is found
     */
    synchronized public LocationObj checkLocations(LocationObj currentLoc)
    {
        for (LocationObj oldLocation : this.locations)
        {
            if(isEqual(currentLoc, oldLocation))
                return oldLocation;
        }
        return null;
    }

    /**
     * Check to see if 2 locations are equal using latitude and longitude
     * @param current current location object
     * @param old old location object
     * @return
     */
    synchronized public boolean isEqual(LocationObj current, LocationObj old)
    {
        if (current.getLatitude() == old.getLatitude())
        {
            if (current.getLongitude() == old.getLongitude())
                return true;
            return false;
        }
        return false;
    }

    synchronized public void sendNotification(LocationObj location)
    {
        Bitmap bmp = LocationObj.decodeFromBase64(location.getImage());
        String text = "You've come back a previous location where you saved an article. ";
        text += "Go through your list of saved articles to see which one you saved here.";

        String ticker = "Don't forget!";

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(this.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "NewsBoi Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(channel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);


            notificationManager.createNotificationChannel(mChannel);

            builder = new NotificationCompat.Builder(this, channel_id)
                    .setContentTitle("You've Returned")
                    .setContentText(text)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentIntent(null)
                    .setAutoCancel(true)
                    .setLargeIcon(bmp)
                    .setTicker(ticker);

            Notification notification = builder.build();

            //        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
        else
            builder = new NotificationCompat.Builder(this);





        return;
    }

    synchronized int getInc()
    {
        return ++inc;
    }

    /**
     * Gets the user's current location
     * @return location object with the user's location
     */
    public double[] getLocation()
    {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

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

    /**
     * Pulls all of the locations from firebase and save them to locations arraylist
     */
    public void getLocations()
    {
        Query query = dbTable.orderByChild("latitude");
        query.addListenerForSingleValueEvent(getValueEventListener());

        return;
    }

    /**
     * Event listener for both queries. Since it just fills up the GradeObj arraylist,
     * can be used for both query 1 and 2.
     * @return
     */
    public ValueEventListener getValueEventListener()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    // clear the arraylist to refill it
                    locations = new ArrayList<>();
                    //Toast.makeText(getApplicationContext(),"listening",Toast.LENGTH_SHORT).show();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {

                        LocationObj location = snapshot.getValue(LocationObj.class);

                        locations.add(location);
                    }
                    Toast.makeText(getApplicationContext(), "Locations Query Finished", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), "Locations Query Failed", Toast.LENGTH_SHORT).show();
            }
        };

        return valueEventListener;
    }







    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
