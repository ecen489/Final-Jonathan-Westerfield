package com.newsboi.jonathanwesterfield.newsboi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedButtons extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "last_update_time";

    private String updatedTime;

    TextView updateView;
    Button refreshBtn;
    Button viewSavedBtn;
    NewsFeed feedFrag;

    public FeedButtons() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            updatedTime = getArguments().getString(ARG_PARAM1);
        }
    }

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_buttons, container, false);
        this.updateView = (TextView) view.findViewById(R.id.updateTimeTextView);
        this.viewSavedBtn = (Button) view.findViewById(R.id.saveBtn);
        (view.findViewById(R.id.saveBtn)).setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                goToSavedArticles();
            }
        });

        this.refreshBtn = (Button) view.findViewById(R.id.refreshButton);
        (view.findViewById(R.id.refreshButton)).setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) {
                refreshFeed();
            }
        });

        setUpdatedView();

        return view;
    }

    public void goToSavedArticles()
    {
        Intent toSaved = new Intent(getContext(), ViewSavedArticles.class);
        startActivity(toSaved);
    }


    public void refreshFeed()
    {
        NewsFeed feedFrag = (NewsFeed) getFragmentManager().findFragmentById(R.id.newsFeedFragment);
        feedFrag.getNews();
        setUpdatedView();
    }

    /**
     * Displays when the most recent news update was
     */
    public void setUpdatedView()
    {
        DateFormat dateFormat = new SimpleDateFormat("h:mm a, MM/dd/yy ");
        Date date = new Date();
        this.updateView.setText("News Updated as of: " + dateFormat.format(date));
    }
}
