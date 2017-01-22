package com.umeng.soexample.coustom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.umeng.socialize.media.UMImage;
import com.umeng.soexample.HomeActivity;
import com.umeng.soexample.R;

/**
 * Created by JH
 * on 2017/1/22.
 */

public class CoustomShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coustom_share_activity);

        //umeng
        findViewById(R.id.um_sys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoustomShareActivity.this, HomeActivity.class));
            }
        });

        //自定义
        findViewById(R.id.coustom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUsingUmeng(CoustomShareActivity.this, "title", "分享", null, "http://www.baidu.com");
            }
        });
    }

    public void shareUsingUmeng(final Activity activity, String title, final String content, UMImage umImage, String targetUrl) {
        ShareCustomPopupWindow.ShareModel shareModel = new ShareCustomPopupWindow.ShareModel();
        shareModel.appName = activity.getString(R.string.app_name);
        shareModel.title = title;
        shareModel.content = content;
        shareModel.umImage = umImage;
        shareModel.targetUrl = targetUrl;
        shareModel.urlPrefix = "";
        if (TextUtils.isEmpty(shareModel.title)) {
            shareModel.title = shareModel.appName;
        }
        ShareCustomPopupWindow shareCustomPopupWindow = new ShareCustomPopupWindow(activity);
        shareCustomPopupWindow.setShareModel(shareModel);
        shareCustomPopupWindow.setOnPlatformClickListener(new ShareCustomPopupWindow.OnPlatformClickListener() {
            @Override
            public void onPlatformClick(ShareCustomPopupWindow.SharePlatform platform, ShareCustomPopupWindow.ShareModel shareModel) {
                if (platform == ShareCustomPopupWindow.COPYLINK) {
                    boolean result = ShareCustomPopupWindow.copyToClipboard(activity, shareModel.targetUrl);// 复制分享链接
                    if (result) {
                        Toast.makeText(CoustomShareActivity.this, "复制成功", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        shareCustomPopupWindow.show();
    }
}
