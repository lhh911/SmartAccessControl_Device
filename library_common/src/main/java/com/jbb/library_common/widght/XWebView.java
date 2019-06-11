package com.jbb.library_common.widght;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jbb.library_common.R;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.download.AppDownloadService;
import com.jbb.library_common.utils.log.LogUtil;

import java.io.File;


public class XWebView extends WebView {
	private Activity context;
	private WebSettings webSettings;
	WebViewBackFace backFace;
	private String downUrl;
	public static final int GET_UNKNOWN_APP_SOURCES = 111;
	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mUploadCallbackAboveL;
	private TakePhotoUtil photoUtil;
	private File tempFile;

//	private boolean mIsPageLoading= false;

	public File getTempFile() {
		return tempFile;
	}

	public Uri getImageUri(){
		if(photoUtil != null){
			return photoUtil.getImageUri();
		}
		return null;
	}

	public XWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = (Activity) context;
		initView();
	}

	public XWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (Activity) context;
		initView();
	}

	public XWebView(Context context) {
		super(context);
		this.context = (Activity) context;
		initView();
	}




	private void initView() {
		webSettings = this.getSettings();


		// 设置javaScript可用
		webSettings.setJavaScriptEnabled(true);

//		webSettings.setDefaultTextEncodingName("UTF-8");
//		webSettings.setMinimumFontSize(12);//设置最小字体大小
//		webSettings.setTextZoom(100);
		//设置自适应屏幕，两者合用
		webSettings.setUseWideViewPort(true);//Webivew支持<meta>标签的viewport属性
		webSettings.setLoadWithOverviewMode(true);//缩放至屏幕的大小
		//启用地理定位  
//		webSettings.setGeolocationEnabled(true);
		webSettings.setDomStorageEnabled(true);
//		webSettings.setAppCacheEnabled(true);// 设置App的缓存
//		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存：
		webSettings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
		webSettings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
		// 是否允许通过file url加载的Javascript读取本地文件，默认值 false
		webSettings.setAllowFileAccessFromFileURLs(true);
//		webSettings.setBlockNetworkImage(false);//同步请求图片

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

		this.setWebViewClient(new MyWebViewClient());

		this.setWebChromeClient(new MyWebChromeClient());
		this.setDownloadListener(new MyDownLoadListeren());
	}

	public void loadURL(String url) {
		LogUtil.d("url = " +url);
		if (!TextUtils.isEmpty(url)) {
			this.loadUrl(url);
		} else {

		}
	}

    public void setTitle(String title) {

    }


    private class MyWebChromeClient extends WebChromeClient {


		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(backFace != null)
				backFace.progress(newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if(backFace != null)
				backFace.title(title);
		}


		// For Android 3.0-
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			LogUtil.d("openFileChoose(ValueCallback<Uri> uploadMsg)");
			if(backFace != null)
				backFace.faceOpenFileChooser(uploadMsg);

		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
			LogUtil.d("openFileChoose( ValueCallback uploadMsg, String acceptType )");
			if(backFace != null)
				backFace.faceOpenFileChooser(uploadMsg);
		}

		//For Android 4.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			LogUtil.d("openFileChoose(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
			if(backFace != null)
				backFace.faceOpenFileChooser(uploadMsg);
		}

		// For Android 5.0+
		public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
			LogUtil.d("onShowFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
			if(backFace != null)
				backFace.faceOnShowFileChooser(filePathCallback);
			return true;
		}

	}



	private class MyWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(backFace != null)
				backFace.onPageStart();
			if (url.startsWith("tmast://appdetails")) {
				try {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);
				} catch (Exception e) {
				}
				return true;
			} else if (url.startsWith("market://details?id=")) {//跳转应用市场
				LogUtil.d("url = " + url);
				toMarket(context, url, null);
				return true;
			}

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
//			mIsPageLoading = false;
			if(backFace != null)
				backFace.onPageFinished();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
//			mIsPageLoading = true;
			if(backFace != null)
				backFace.onPageStart();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

				LogUtil.w("error : url = " +  failingUrl + " " + description);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}
	}
	

	public interface WebViewBackFace{
		void title(String title);
		void progress(int progress);
		void faceOpenFileChooser(ValueCallback<Uri> uploadMsg);
		void faceOnShowFileChooser(ValueCallback<Uri[]> filePathCallback);
		void onPageFinished();
		void onPageStart();
	}


	public void setWebViewBackFace(WebViewBackFace backFace){
		this.backFace = backFace;
	}


	public class MyDownLoadListeren implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			showHintDialog(url);
			LogUtil.d("下载链接 = " + url);
			downUrl = url;
		}
	}

	void showHintDialog(final String url){
		final DoubleButtonDialog dialog = new DoubleButtonDialog(context,R.style.common_loading_dialog);
		dialog.setTitle("下载" + url);
		dialog.showDialog();
		dialog.getConfirmBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkIsInstalls();
				dialog.dismiss();
			}
		});
	}


	public void startDownServer(){
		Intent it = new Intent(context,AppDownloadService.class);
		it.putExtra(KeyContacts.KEY_URL,downUrl);
		it.putExtra(KeyContacts.KEY_TITLE,"下载");
		context.startService(it);

	}

	public void checkIsInstalls() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			boolean canInstall = context.getPackageManager().canRequestPackageInstalls();
			if(canInstall){
				startDownServer();
			}else{

				DoubleButtonDialog dialog = new DoubleButtonDialog(context,R.style.common_loading_dialog);
				dialog.setDatas("安装应用需要打开未知来源权限，请去设置中开启权限");
				dialog.getConfirmBtn().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Uri packageURI = Uri.parse("package:" + context.getPackageName());//设置包名，可直接跳转当前软件的设置页面
						Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
						context.startActivityForResult(intent, XWebView.GET_UNKNOWN_APP_SOURCES);
					}
				});
			}
		}else{
			startDownServer();
		}

	}



	//调起应用市场
	public static boolean toMarket(Context context, String url, String marketPkg) {
//		Uri uri = Uri.parse("market://details?id=" + appPkg);

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
			intent.setPackage(marketPkg);
		}
		try {
			context.startActivity(intent);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
