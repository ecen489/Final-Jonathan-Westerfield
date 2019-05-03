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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class SavedList extends Fragment
{
    private ListView listView;
    private ArrayList<NewsObj.Article> savedArticleList;
    private NewsAdapter newsAdapter;
    private String saveFilePath = "saved_pages.txt";

    public SavedList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_list, container, false);
        this.listView = (ListView) view.findViewById(R.id.savedList);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        getSavedArticles();
        fillListView();
    }

    public void getSavedArticles()
    {
        this.savedArticleList = new ArrayList<>();
        try
        {
            Scanner scanner = new Scanner(getActivity().openFileInput(this.saveFilePath));
            Gson gson = new Gson();
            String fileLine;
            NewsObj.Article savedArticle;

            while (scanner.hasNext())
            {
                fileLine = scanner.nextLine();
                savedArticle = gson.fromJson(fileLine, NewsObj.Article.class);
                this.savedArticleList.add(savedArticle);
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void fillListView()
    {
        this.newsAdapter = new NewsAdapter(getContext(), savedArticleList);
        this.listView.setAdapter(newsAdapter);
        setListViewClickListener();
    }

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
        showArticle.putExtra("article_object", this.savedArticleList.get(index));
        startActivity(showArticle);
    }
}
