package org.join.raypick;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	// private final float TOUCH_SCALE_FACTOR = 180.0f / 480;

	/**
	 * 具体实现的渲染器
	 */
	private RayPickRenderer mRenderer;
	/**
	 * 记录上次触屏位置的坐标
	 */
	private float mPreviousX, mPreviousY;

	public MyGLSurfaceView(Context context,
			OnSurfacePickedListener onSurfacePickedListener) {
		super(context);
		// 设置渲染器
		mRenderer = new RayPickRenderer(context);

		// 透视上一个View
		setZOrderOnTop(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		// 透视上一个Activity
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		setRenderer(mRenderer);
		// 设置渲染模式为主动渲染
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		mRenderer.setOnSurfacePickedListener(onSurfacePickedListener);
	}

	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		super.onResume();
	}

	/**
	 * 响应触屏事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		AppConfig.setTouchPosition(x, y);
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:

			// 经过中心点的手势方向逆时针旋转90°后的坐标
			float dx = y - mPreviousY;
			float dy = x - mPreviousX;
			// 手势距离
			float d = (float) (Math.sqrt(dx * dx + dy * dy));
			// 旋转轴单位向量的x,y值（z=0）
			mRenderer.mfAngleX = dx;
			mRenderer.mfAngleY = dy;
			// 手势距离
			mRenderer.gesDistance = d;

			// float dx = x - mPreviousX;
			// float dy = y - mPreviousY;
			// mRenderer.mfAngleY += dx * TOUCH_SCALE_FACTOR;
			// mRenderer.mfAngleX += dy * TOUCH_SCALE_FACTOR;

			// PickFactory.update(x, y);
			AppConfig.gbNeedPick = false;
			break;
		case MotionEvent.ACTION_DOWN:
			AppConfig.gbNeedPick = false;
			break;
		case MotionEvent.ACTION_UP:
			AppConfig.gbNeedPick = true;
			break;
		case MotionEvent.ACTION_CANCEL:
			AppConfig.gbNeedPick = false;
			break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}
}
