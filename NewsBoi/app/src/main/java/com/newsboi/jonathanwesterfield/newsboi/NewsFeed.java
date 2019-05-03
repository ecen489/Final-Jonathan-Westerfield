package com.newsboi.jonathanwesterfield.newsboi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class NewsFeed extends Fragment
{

    private String apiKey = "36c22d187f7b4bbeb0b749be85785cf7";
    private String newsURL = "https://newsapi.org/v2/top-headlines?" +
            "country=us&apiKey=" + apiKey;
    private static final String ARG_PARAM1 = "article_list";

    private NewsAdapter newsAdapter;
    private ArrayList<NewsObj.Article> articleList;
    private NewsObj newsObj;
    private ListView listView;
    private FeedButtons btnsFrag;


    public NewsFeed() {
        // Required empty public constructor
    }

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        this.listView = (ListView) view.findViewById(R.id.list);

        setListViewClickListener();

        getNews();

        return view;
    }

    public void printNewsObj()
    {
        System.out.println(this.newsObj.toString());
    }

    public void getNews()
    {
        Ion.with(this)
                .load(this.newsURL)
                .as(new TypeToken<NewsObj>(){})
                .setCallback(new FutureCallback<NewsObj>()
                {
                    public void onCompleted(Exception e, NewsObj result)
                    {
                        System.out.println("\nGot the Articles\n");
                        newsObj = result;
                        articleList = convertToArrayList(result.getArticles());
                        fillListView();
                    }
                });
    }

    public ArrayList convertToArrayList(NewsObj.Article[] articles)
    {
        ArrayList<NewsObj.Article> tempList = new ArrayList<>();
        for (NewsObj.Article article : articles)
            tempList.add(article);

        return tempList;
    }

    public void fillListView()
    {
        this.newsAdapter = new NewsAdapter(getContext(), articleList);
        this.listView.setAdapter(newsAdapter);
        setListViewClickListener();
    }

    /** Listener Section */

    public void setListViewClickListener()
    {
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int articleIndex, long l)
            {
                switchToNewsLookActivity(articleIndex);
            }
        });
    }

    public void switchToNewsLookActivity(int index)
    {
        Intent showArticle = new Intent(getContext(), NewsLookActivity.class);
        showArticle.putExtra("article_object", this.articleList.get(index));
        startActivity(showArticle);
    }
}
