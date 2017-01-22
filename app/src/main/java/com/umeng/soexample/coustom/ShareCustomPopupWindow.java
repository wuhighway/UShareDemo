package com.umeng.soexample.coustom;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.facebook.share.model.ShareModel;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;
import com.umeng.soexample.BuildConfig;
import com.umeng.soexample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JH
 * on 2017/1/22.
 * 自定义样式分享
 */

public class ShareCustomPopupWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    public static final SharePlatform WEIXIN = new SharePlatform(Platform.WEIXIN, "微信", R.drawable.umeng_socialize_wechat);
    public static final SharePlatform WEIXIN_CIRCLE = new SharePlatform(Platform.WEIXIN_CIRCLE, "朋友圈", R.drawable.umeng_socialize_wxcircle);
    public static final SharePlatform QQ = new SharePlatform(Platform.QQ, "QQ", R.drawable.umeng_socialize_qq);
    public static final SharePlatform QZONE = new SharePlatform(Platform.QZONE, "QQ空间", R.drawable.umeng_socialize_qzone);
    public static final SharePlatform SMS = new SharePlatform(Platform.SMS, "短信", R.drawable.umeng_socialize_sms);
    public static final SharePlatform COPYLINK = new SharePlatform(Platform.COPYLINK, "复制链接", R.drawable.umeng_socialize_copy);
    private List<SharePlatform> mPlatformList = new ArrayList<>();
    private Activity activity;
    private ShareModel shareModel;
    private ShareItemAdapter adapter;
    private OnPlatformClickListener listener;

    public ShareCustomPopupWindow(Activity activity) {
        this(activity, null);
    }

    public ShareCustomPopupWindow(Activity activity, ShareModel shareModel) {
        super(activity);
        this.activity = activity;
        this.shareModel = shareModel;
        View rootView = inflaterContentView();
        initPopWindow(rootView);
        initPlatform();
        initPlatformConfig();
        initContentView(rootView);
    }

    private View inflaterContentView() {
        return LayoutInflater.from(activity).inflate(R.layout.layout_custom_share, null, false);
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopWindow(View view) {
        this.setContentView(view);//设置PopupWindow的View
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);//设置PopupWindow弹出窗体的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT); //设置PopupWindow弹出窗体的高
        this.setAnimationStyle(R.style.BottomUpStyle); //设置PopupWindow弹出窗体动画效果
        this.setFocusable(true); //设置PopupWindow可获得焦点
        this.setTouchable(true); //设置PopupWindow可触摸
        this.setOutsideTouchable(true); //设置非PopupWindow区域可触摸
        this.setBackgroundDrawable(new ColorDrawable(0xb0000000)); //设置PopupWindow的背景
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
    }

    public static final String QQ_SHARE_APPKEY = "100424468";
    public static final String QQ_SHARE_SECRET = "c7394704798a158208a74ab60104f0ba";
    public static final String WX_KEY = "wxdc1e388c3822c80b";
    public static final String WX_SECRET = "3baf1193c85774b3fd9d18447d76cab0";

    /**
     * 初始化分享平台
     */
    private void initPlatform() {
        mPlatformList.add(WEIXIN);
        mPlatformList.add(WEIXIN_CIRCLE);
        mPlatformList.add(QQ);
        mPlatformList.add(QZONE);
        mPlatformList.add(SMS);
        mPlatformList.add(COPYLINK);
    }

    /**
     * 初始化ContentView
     */
    private void initContentView(View rootView) {
        GridView mGridView = (GridView) rootView.findViewById(R.id.share_platform_grid);
        rootView.findViewById(R.id.share_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mGridView.setOnItemClickListener(this);
        adapter = new ShareItemAdapter();
        mGridView.setAdapter(adapter);
    }

    /**
     * 初始化PlatformConfig
     */
    private void initPlatformConfig() {
        Config.OpenEditor = true;
        Log.LOG = true;
        Config.IsToastTip = false;
//        PlatformConfig.setWeixin(WX_KEY, WX_SECRET);
//        PlatformConfig.setQQZone(QQ_SHARE_APPKEY, QQ_SHARE_SECRET);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (shareModel == null) {
            String targetUrl = null;
            String appName = activity.getString(R.string.app_name);
            String title = "最近在用" + appName + "，好用到没朋友！"; // 分享标题
            String content = "不仅彩种多、提款快，还可以看比赛分析、天天推球、比分直播、走势图。"; // 分享内容
            UMImage umImage = new UMImage(activity, R.drawable.umeng_app_icon);
            shareModel = new ShareModel();
            shareModel.appName = appName;
            shareModel.title = title;
            shareModel.content = content;
            shareModel.umImage = umImage;
            shareModel.targetUrl = targetUrl;
            shareModel.urlPrefix = "下载地址：";
        }
        SharePlatform platform = (SharePlatform) parent.getAdapter().getItem(position);
        if (platform != COPYLINK) {
            share(platform, shareModel);
        } else {
            copyToClipboard(activity, shareModel.targetUrl);// 复制分享链接
        }
        if (listener != null) {
            listener.onPlatformClick(platform, shareModel);
        }
    }


    public static boolean copyToClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // api level < 11
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setText(text);
                return true;
            }
        } else {
            // api level >= 11
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setText(text);
                return true;
            }
        }
        return false;
    }

    public void setShareModel(ShareModel shareModel) {
        this.shareModel = shareModel;
    }

    public void setOnPlatformClickListener(OnPlatformClickListener listener) {
        this.listener = listener;
    }

    public void setSharePlatformList(SharePlatform... platforms) {
        List<SharePlatform> list = Arrays.asList(platforms);
        adapter.setSharePlatformList(list);
    }

    public void share(SharePlatform platform, ShareModel shareModel) {
        if (platform == null) {
            return;
        }
        UMImage umImage = shareModel.umImage == null ? new UMImage(activity, R.drawable.logo) : shareModel.umImage;
        ShareAction shareAction = new ShareAction(activity)
                .setPlatform(platform.media)
                .setCallback(mUMShareListener)
                .withTargetUrl(shareModel.targetUrl);
        if (platform.media == SHARE_MEDIA.WEIXIN || platform.media == SHARE_MEDIA.QQ || platform.media == SHARE_MEDIA.QZONE) {
            shareAction.withTitle(shareModel.title).withText(shareModel.content).withMedia(umImage);
        } else if (platform.media == SHARE_MEDIA.WEIXIN_CIRCLE) {
            shareAction.withTitle(shareModel.title + shareModel.content).withText(shareModel.targetUrl).withMedia(umImage);
        } else if (platform.media == SHARE_MEDIA.SINA) {
            shareAction.withText(shareModel.title + shareModel.content + shareModel.targetUrl).withMedia(umImage);
        } else if (platform.media == SHARE_MEDIA.SMS) {
            shareAction.withText(shareModel.title + shareModel.content + shareModel.urlPrefix + shareModel.targetUrl);
        }
        shareAction.share();
    }

    public void show() {
        updateWindowAlpha(1.0f, 0.7f);
        showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void dismiss() {
        updateWindowAlpha(0.7f, 1.0f);
        super.dismiss();
    }

    private void updateWindowAlpha(float fromValue, float toValue){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromValue, toValue);
        valueAnimator.setDuration(350);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                setWindowAlpha(currentValue);
            }
        });
        valueAnimator.start();
    }

    private void setWindowAlpha(float alpha) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = alpha;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(params);
    }

    UMShareListener mUMShareListener = new UMShareListener() {

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            showToast("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            showToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            showToast("分享取消");
        }
    };

    private void showToast(String message) {
//        ((BaseActivity) activity).showToast(message);
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public interface OnPlatformClickListener {
        void onPlatformClick(SharePlatform platform, ShareModel shareModel);
    }

    /**
     * 分享平台
     */
    public enum Platform {
        WEIXIN, WEIXIN_CIRCLE, QQ, QZONE, SINA, SMS, COPYLINK
    }

    /**
     * 分享数据
     */
    public static class ShareModel{
        /** APP名称 */
        public String appName;
        /** 分享标题 */
        public String title;
        /** 分享内容 */
        public String content;
        /** 分享链接前缀 */
        public String urlPrefix;
        /** 分享链接 */
        public String targetUrl;
        /** 分享图片 */
        public UMImage umImage;
    }

    /**
     * 分享平台信息
     */
    public static class SharePlatform {

        public SharePlatform(Platform platform, String name, int logoRes) {
            this.platform = platform;
            this.name = name;
            this.logoRes = logoRes;
            setShareMedia();
        }

        /** 分享平台 */
        public Platform platform;
        /** 分享平台(友盟) */
        public SHARE_MEDIA media;
        /** 分享平台名称 */
        public String name;
        /** 分享平台Logo */
        public int logoRes;

        private void setShareMedia() {
            switch (this.platform) {
                case WEIXIN:
                    this.media = SHARE_MEDIA.WEIXIN;
                    break;
                case WEIXIN_CIRCLE:
                    this.media = SHARE_MEDIA.WEIXIN_CIRCLE;
                    break;
                case QQ:
                    this.media = SHARE_MEDIA.QQ;
                    break;
                case QZONE:
                    this.media = SHARE_MEDIA.QZONE;
                    break;
                case SINA:
                    this.media = SHARE_MEDIA.SINA;
                    break;
                case SMS:
                    this.media = SHARE_MEDIA.SMS;
                    break;
                default:
                    this.media = null;
                    break;
            }
        }
    }

    class ShareItemAdapter extends BaseAdapter {

        void setSharePlatformList(List<SharePlatform> list) {
            mPlatformList.clear();
            mPlatformList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPlatformList == null ? 0 : mPlatformList.size();
        }

        @Override
        public SharePlatform getItem(int position) {
            return mPlatformList == null ? null : mPlatformList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_share, parent, false);
                viewHolder.ivShareLogo = (ImageView) convertView.findViewById(R.id.share_item_logo);
                viewHolder.tvShareName = (TextView) convertView.findViewById(R.id.share_item_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SharePlatform sharePlatform = getItem(position);
            if (sharePlatform != null) {
                viewHolder.ivShareLogo.setImageResource(sharePlatform.logoRes);
                viewHolder.tvShareName.setText(sharePlatform.name);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView ivShareLogo;
            TextView tvShareName;
        }
    }
}
