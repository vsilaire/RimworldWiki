package com.vsilaire.rimworldwiki;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Browser extends WebViewClient {

    private static Browser itself;

    private Context context;
    private Activity activity;
    private WebView webView;
    private String baseUrl = "https://www.rimworldwiki.com/wiki/";
    private String url;
    private String page = "Main_Page";
    private Document doc;

    private Browser(Activity activity){

        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        this.initMainWebView();
    }

    private void initMainWebView(){

        this.webView = this.activity.findViewById(R.id.main_webview);
        this.webView.setWebViewClient(this);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setUseWideViewPort(true);
        //this.webView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
        this.loadWithParameter(page);
    }

    public void loadWithParameter(String parameter){

        this.page = parameter;
        this.loadPage(baseUrl + this.page);
    }

    private void loadPage(String url){

        this.url = url;

        new  AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String title ="";
                try {
                    Document tempDoc = Jsoup.connect(params[0]).get();
                    Browser.itself.setDoc(tempDoc);
                    title = tempDoc.title();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return title;
            }

            @Override
            protected void onPostExecute(String result) {
                if(doc != null){

                    doc.head().getElementsByTag("link").remove();
                    doc.head().appendElement("link")    .attr("rel", "stylesheet")
                            .attr("type", "text.css")
                            .attr("href", "style.css");
                }
                //CSS Loading

            }
        }.execute(url);

        if(doc != null){

            String htmlData = doc.outerHtml();
            this.webView.loadDataWithBaseURL(   "file:///android_assets/.", htmlData,"text/html", "UTF-8", null);
        }

        /*else {
            this.webView.loadUrl(this.url);
        }*/
    }


    public static Browser getInstance(Activity activity){

        if(itself == null){
            itself = new Browser(activity);
        }
        return itself;
    }

    public void goBack(){

        if(this.webView.canGoBack()){
            this.webView.goBack();
            this.url = this.webView.getUrl();
        }
    }

    protected Document getDoc(){
        return this.doc;
    }

    protected void setDoc(Document doc){
        this.doc = doc;
    }



}
