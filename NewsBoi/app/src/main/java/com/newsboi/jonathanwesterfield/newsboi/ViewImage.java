package com.newsboi.jonathanwesterfield.newsboi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ViewImage extends AppCompatActivity
{
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        imageView = (ImageView) findViewById(R.id.imageView);
    }


}
