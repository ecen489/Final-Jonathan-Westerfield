package com.newsboi.jonathanwesterfield.newsboi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsPage extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "article_object";
    private WebView webView;

    private NewsObj.Article article;

    public NewsPage() {
        // Required empty public constructor
    }

    public static NewsPage newInstance(NewsObj.Article article)
    {
        NewsPage fragment = new NewsPage();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, article);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_page, container, false);

        this.webView = (WebView) view.findViewById(R.id.webView);
        this.webView.setWebViewClient(new WebViewClient());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        Intent fromMain = getActivity().getIntent();
        this.article = (NewsObj.Article) fromMain.getSerializableExtra(ARG_PARAM1);

        this.webView.loadUrl(this.article.getUrl());
    }

    public void showBadInputAlert(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Article Already Saved").
                setMessage("You have already saved this article and location.")
                .setNeutralButton("OK", null);;

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showArticleSaved(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Article Saved").
                setMessage("You have saved this article and location for future reading.")
                .setNeutralButton("OK", null);;

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
