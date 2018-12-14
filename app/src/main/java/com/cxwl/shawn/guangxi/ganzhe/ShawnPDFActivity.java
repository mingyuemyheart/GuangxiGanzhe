package com.cxwl.shawn.guangxi.ganzhe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.util.AuthorityUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * PDF文档
 */
public class ShawnPDFActivity extends ShawnBaseActivity implements OnClickListener {

	private TextView tvPercent;
	private PDFView pdfView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_pdf);
		checkAuthority();
	}

	private void init() {
		initWidget();
		initPDFView();
	}

	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvPercent = findViewById(R.id.tvPercent);

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}
	}

	// 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    // 完整的判断中文汉字和符号
    private String isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
            	try {
					strName = strName.replace(c+"", URLEncoder.encode(c+"", "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
        }
		return strName;
    }

	private void initPDFView() {
		pdfView = findViewById(R.id.pdfView);
		pdfView.enableDoubletap(true);
		pdfView.enableSwipe(true);

		String pdfUrl = getIntent().getStringExtra(CONST.WEB_URL);
		if (TextUtils.isEmpty(pdfUrl)) {
			return;
		}else {
			pdfUrl = isChinese(pdfUrl);
		}

		OkHttpFile(pdfUrl);
	}

	private void OkHttpFile(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						InputStream is = null;
						FileOutputStream fos = null;
						try {
							is = response.body().byteStream();//获取输入流
							float total = response.body().contentLength();//获取文件大小
							if(is != null){
								File files = new File(Environment.getExternalStorageDirectory()+"/GuangxiGanzhe");
								if (!files.exists()) {
									files.mkdirs();
								}
								String filePath = files.getAbsolutePath()+"/"+"1.pdf";
								fos = new FileOutputStream(filePath);
								byte[] buf = new byte[1024];
								int ch = -1;
								int process = 0;
								while ((ch = is.read(buf)) != -1) {
									fos.write(buf, 0, ch);
									process += ch;

									int percent = (int) Math.floor((process / total * 100));
									Log.e("percent", process+"--"+total+"--"+percent);
									Message msg = handler.obtainMessage(1001);
									msg.what = 1001;
									msg.obj = filePath;
									msg.arg1 = percent;
									handler.sendMessage(msg);
								}

							}
							fos.flush();
							fos.close();// 下载完成
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (is != null) {
								is.close();
							}
							if (fos != null) {
								fos.close();
							}
						}

					}
				});
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1001) {
				if (tvPercent == null || pdfView == null) {
					return;
				}
				int percent = msg.arg1;
				tvPercent.setText(percent+getString(R.string.unit_percent));
				if (percent >= 100) {
					tvPercent.setVisibility(View.GONE);
					String filePath = msg.obj+"";
					if (!TextUtils.isEmpty(filePath)) {
						File file = new File(msg.obj+"");
						if (file.exists()) {
							pdfView.fromFile(file)
									.defaultPage(0)
									.scrollHandle(new DefaultScrollHandle(ShawnPDFActivity.this))
									.load();
						}
					}
				}
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;

		default:
			break;
		}
	}

	//需要申请的所有权限
	private String[] allPermissions = new String[] {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
	};

	//拒绝的权限集合
	public static List<String> deniedList = new ArrayList<>();
	/**
	 * 申请定位权限
	 */
	private void checkAuthority() {
		if (Build.VERSION.SDK_INT < 23) {
			init();
		}else {
			deniedList.clear();
			for (String permission : allPermissions) {
				if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
					deniedList.add(permission);
				}
			}
			if (deniedList.isEmpty()) {//所有权限都授予
				init();
			}else {
				String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
				ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_STORAGE);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case AuthorityUtil.AUTHOR_STORAGE:
				if (grantResults.length > 0) {
					boolean isAllGranted = true;//是否全部授权
					for (int gResult : grantResults) {
						if (gResult != PackageManager.PERMISSION_GRANTED) {
							isAllGranted = false;
							break;
						}
					}
					if (isAllGranted) {//所有权限都授予
						init();
					}else {//只要有一个没有授权，就提示进入设置界面设置
						AuthorityUtil.intentAuthorSetting(this, "\""+getString(R.string.app_name)+"\""+"需要使用您的存储权限，是否前往设置？");
					}
				}else {
					for (String permission : permissions) {
						if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
							AuthorityUtil.intentAuthorSetting(this, "\""+getString(R.string.app_name)+"\""+"需要使用您的存储权限，是否前往设置？");
							break;
						}
					}
				}
				break;
		}
	}

}
