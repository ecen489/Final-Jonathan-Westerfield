package com.newsboi.jonathanwesterfield.newsboi;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class NewsLookActivity extends AppCompatActivity {

    private WebView webView;
    private Button saveBtn;
    private Button backBtn;
    private NewsObj.Article article;
    // private boolean alreadySaved = false;
    private String saveFilePath = "saved_pages.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_look);

        // initializeInterfaces();

        // Intent fromMain = getIntent();
        // this.article = (NewsObj.Article) fromMain.getSerializableExtra("article_object");

        // displayArticle();
    }

    /**
     * Allow javascript to run on the news pages
     */
    public void initializeInterfaces()
    {
        // this.webView = (WebView) findViewById(R.id.webView);
        this.webView.setWebViewClient(new WebViewClient());
        this.saveBtn = (Button) findViewById(R.id.saveBtn);
        this.backBtn = (Button) findViewById(R.id.backBtn);
    }

    public void displayArticle()
    {
        this.webView.loadUrl(this.article.getUrl());
        // this.webView.setWebViewClient(MyWebViewClient);
    }

    public void returnToPrevActivity(View view)
    {
        Intent mainIntent = new Intent();
        finish();
    }

    public void onSave(View view)
    {
        try
        {
            Gson gson = new Gson();
            String articleJson = gson.toJson(article);
            System.out.println("JSON OBJECT");
            System.out.println(articleJson);

            if (isAlreadySaved(view, articleJson))
                showBadInputAlert(view);
            else
            {
                PrintStream out = new PrintStream(openFileOutput(this.saveFilePath, MODE_APPEND));
                out.println(articleJson);
                out.close();
                System.out.println("\nSaved Article Object\n");
                showArticleSaved(view);
                // this.alreadySaved = true;
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isAlreadySaved(View view, String json)
    {
        try
        {
            Scanner scanner = new Scanner(openFileInput(this.saveFilePath));
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

    public void showBadInputAlert(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Article Already Saved").
                setMessage("You have already saved this article.")
                .setNeutralButton("OK", null);;

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showArticleSaved(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Article Saved").
                setMessage("You have saved this article for future reading.")
                .setNeutralButton("OK", null);;

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
