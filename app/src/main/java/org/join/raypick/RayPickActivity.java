package org.join.raypick;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class RayPickActivity extends Activity implements
		OnSurfacePickedListener {

	private GLSurfaceView mGLSurfaceView ;
//Android OpenGL射线拾取&手势旋转 http://vaero.blog.51cto.com/4350852/790620
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGLSurfaceView = new MyGLSurfaceView(this, this);
		mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(RayPickActivity.this, "选中了" + msg.what + "面",
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onSurfacePicked(int which) {
		myHandler.sendEmptyMessage(which);
	}

}