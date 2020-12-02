package com.android.moviestreamer.media_player;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.moviestreamer.R;

public class WebViewPlayerActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_player);
        String  VIDEO_URL = getIntent().getStringExtra("Url");

        String HTML = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "\n" +
                "\n" +
                "<body style=\"margin:0px;padding:0px;overflow:hidden\">\n" +
                "    <iframe src=\"https://drive.google.com/file/d/1oIeQs_YhhnFHvD-HFUOIXUy-kjpouwn4/preview\" frameborder=\"0\" style=\"overflow:hidden;overflow-x:hidden;overflow-y:hidden;height:100%;width:100%;position:absolute;top:0px;left:0px;right:0px;bottom:0px\" height=\"100%\" width=\"100%\"></iframe>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        webView = findViewById(R.id.wv_webView);

        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.135 Mobile Safari/537.36");

//                webView .loadUrl("https://www.youtube.com/watch?v=GWG9aaGUAVA");

//        webView .loadUrl("https://drive.google.com/file/d/1jx4rASBA1A0B7Xi-EK3gGDuJ_vCn_9FT/preview");
        webView.loadData(HTML, "text/html; charset=utf-8", "UTF-8");

    }
}