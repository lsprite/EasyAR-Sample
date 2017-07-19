package com.lxb.easyar;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import cn.easyar.engine.EasyAR;

/**
 * Created by Joker on 2017-03-16.
 */

public class SweepRedPackageActivity extends Activity implements
		View.OnClickListener {

	private FrameLayout redEnvelopeFragment;
	private String SDPath = "";
	private final String path1 = "1.jpg";
	private final String path2 = "2.jpg";
	private Handler handler = new Handler() {
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题�?
		setContentView(R.layout.activity_main);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		SDPath = FileUtil.getSDPath(this);
		//
		try {
			FileUtil.copyFilesFromAssets(this, path1, SDPath);
			boolean b = FileUtil.copyFilesFromAssets(this, path2, SDPath);//
			// 将两个文件考到sd卡
			if (b) {
				init();
			} else {
				Toast.makeText(this, "SD连接失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void init() {
		EasyARUtil.getInstance().prepare(this);
		EasyARUtil.getInstance().setStatus(true);
		initView();
	}

	private void initView() {
		redEnvelopeFragment = (FrameLayout) findViewById(R.id.redEnvelopeFragment);
		GLView glView = new GLView(this);
		glView.setRenderer(new Renderer(this, showRedEnvelopeInterFace));
		glView.setZOrderMediaOverlay(true);

		((ViewGroup) findViewById(R.id.preview)).addView(glView,
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
		EasyARUtil
				.getInstance()
				.rotationChange(
						getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
		File file = new File(SDPath + "/" + path1);
		if (file.exists()) {
			EasyARUtil.getInstance().loadSDPath(SDPath + "/" + path1);
		} else {
			Toast.makeText(this, "SD卡异常，请稍后再试", Toast.LENGTH_SHORT).show();
		}
		file = new File(SDPath + "/" + path2);
		if (file.exists()) {
			EasyARUtil.getInstance().loadSDPath(SDPath + "/" + path2);
		} else {
			Toast.makeText(this, "SD卡异常，请稍后再试", Toast.LENGTH_SHORT).show();
		}
		initListeners();
	}

	private void initListeners() {
		redEnvelopeFragment.setOnClickListener(this);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		EasyARUtil
				.getInstance()
				.rotationChange(
						getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EasyARUtil.getInstance().destory();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EasyAR.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EasyAR.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.redEnvelopeFragment:
			showDialog();
			break;
		default:
			break;

		}

	}

	private ShowRedEnvelopeInterFace showRedEnvelopeInterFace = new ShowRedEnvelopeInterFace() {
		@Override
		public void isShowRedEnvelope(boolean isShowRedEnvelope,
				final String picName) {
			if (isShowRedEnvelope) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						System.out.println("---扫描到的图片:" + picName);
						redEnvelopeFragment.setVisibility(View.VISIBLE);
					}
				});
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						redEnvelopeFragment.setVisibility(View.GONE);
					}
				});
			}

		}
	};

	public void showDialog() {
		EasyARUtil.getInstance().setStatus(false);
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("恭喜");
		builder.setMessage("收到红包啦");
		builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EasyARUtil.getInstance().setStatus(true);
				redEnvelopeFragment.setVisibility(View.GONE);
				dialog.dismiss();
			}
		});
		builder.show();
	}
}
