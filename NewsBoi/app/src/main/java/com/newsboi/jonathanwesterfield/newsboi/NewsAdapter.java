package com.newsboi.jonathanwesterfield.newsboi;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter
{
    Context mcontext;
    ArrayList<NewsObj.Article> articles;
    private int lastPosition = -1;

    // Class to hold all of the elements of the view so we can populate them in the getView method
    private static class ViewHolder
    {
        TextView listTitle;
        TextView listDescription;
        ImageView listImage;
    }

    public NewsAdapter(Context context, ArrayList<NewsObj.Article> articles)
    {
        super(context, R.layout.row_layout, articles);
        this.articles = articles;
        this.mcontext = context;
    }

    /**
     * Got this from https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
     *
     * Pulls all of the information from our article list and populates each individual
     * element of the listview
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        NewsObj.Article article = articles.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_layout, parent, false);
            viewHolder.listTitle = (TextView) convertView.findViewById(R.id.listTitle);
            viewHolder.listDescription = (TextView) convertView.findViewById(R.id.listDescription);
            viewHolder.listImage = (ImageView) convertView.findViewById(R.id.listImage);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.listTitle.setText(article.getTitle());
        viewHolder.listDescription.setText(article.getDescription());

        // Load the article image
        Ion.with(viewHolder.listImage)
                .error(R.mipmap.ic_launcher)
                .load(article.getUrlToImage());
        viewHolder.listImage.setBackgroundColor(Color.rgb(250, 250, 250));

        // Return the completed view to render on screen
        return convertView;
    }

}
