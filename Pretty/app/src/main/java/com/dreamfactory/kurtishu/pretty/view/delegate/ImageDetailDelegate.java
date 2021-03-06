package com.dreamfactory.kurtishu.pretty.view.delegate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.dreamfactory.kurtishu.pretty.R;
import com.dreamfactory.kurtishu.pretty.event.NavigatorEvent;
import com.dreamfactory.kurtishu.pretty.view.activity.ImageViewerActivity;
import com.dreamfactory.kurtishu.pretty.widget.CustomWebView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.net.URI;

import de.greenrobot.event.EventBus;

/**
 * Created by kurtishu on 12/1/15.
 */
public class ImageDetailDelegate extends BaseAppDelegate implements Toolbar.OnClickListener {

    private CustomWebView mWebView;
    private SimpleDraweeView mPrettyImage;
    private String imageUrl;
    private String title;
    private int id;
    private Context mContext;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_image_detail;
    }

    @Override
    public void initViews(Context context, Intent mIntent) {
        mWebView = get(R.id.image_webview);
        mPrettyImage = get(R.id.detail_image_view);

        Toolbar toolbar = get(R.id.toolbar);
        toolbar.setTitle(R.string.title_image_detail);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolbar.setNavigationOnClickListener(this);

        imageUrl = mIntent.getStringExtra("img");
        title = mIntent.getStringExtra("title");
        id = mIntent.getExtras().getInt("id");
        mContext = context;

        mWebView.setWebViewCallback(callback);
        WebSettings webSettings = mWebView.getSettings();
        //webSettings.setSavePassword(false);
        //webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        mWebView.addJavascriptInterface(new JavascriptObject(), "obj");

        mWebView.loadUrl("http://kurtishu.github.io/remote/index.html");

        mPrettyImage.setImageURI(Uri.parse(imageUrl));
        mPrettyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoImageViewerScreen();
            }
        });
    }

    class JavascriptObject {

        @JavascriptInterface
        public void pageOnLoad() {

            rootView.post(new Runnable() {
                @Override
                public void run() {
                    String javascript = String.format("javascript:loadImg('%s', '%s')", "", title);
                    mWebView.loadUrl(javascript);
                }
            });
        }
    }

    private CustomWebView.WebViewCallback callback = new CustomWebView.WebViewCallback() {

        @Override
        public void loadingUrl(URI uri) {
            gotoImageViewerScreen();
        }

        @Override
        public void pageStarted() {

        }
    };

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new NavigatorEvent(null, true));
    }

    private void gotoImageViewerScreen() {
        Intent intent = new Intent(mContext, ImageViewerActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("img", imageUrl);
        intent.putExtra("title", title);
        mContext.startActivity(intent);
    }
}
