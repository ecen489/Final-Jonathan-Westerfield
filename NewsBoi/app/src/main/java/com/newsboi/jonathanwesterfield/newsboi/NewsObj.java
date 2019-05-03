package com.newsboi.jonathanwesterfield.newsboi;

import java.io.Serializable;

public class NewsObj implements Serializable
{
    private String status;
    private String numResults;
    private Article[] articles;


    /**
     * This class must be setup this way in order to get the nested json information
     * with Gson
     */
    public NewsObj()
    {
        // No args constructor for Gson
    }

    public static class Article implements Serializable
    {
         String author;
         String title;
         String description;
         String url;
         String urlToImage;
         String publishedAt;
         String content;

         public Article()
         {
             // no args constructor for GSON
         }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            return url;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public String toString()
        {
            return String.format("\n\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n", this.title,
                    this.description, this.author, this.content, this.urlToImage,
                    this.urlToImage, this.publishedAt);
        }
    }

    public String toString()
    {
        String result = String.format("{\n\tStatus: %s\n\ttotalResults: %s\n\t- articles: [\n",
                status, numResults);

        for (int i = 0; i < articles.length; i++)
        {
            result += String.format("-{\n\t\tauthor: %s,\n\t\ttitle: \"%s\",\n\t\tdescription: \"%s\",\n\t\t",
                articles[i].author, articles[i].title, articles[i].description);
            result += String.format("url: %s,\n\t\turlToImage: %s,\n\t\tpublishedAt: %s,\n\t\t",
                    articles[i].url, articles[i].urlToImage, articles[i].publishedAt);
            result += String.format("content: \"%s\",\n\t\t},\n\t\t", articles[i].content);
        }

        result += "}\n\t]\n}";
        return result;
    }

    public Article[] getArticles() {
        return articles;
    }
}
