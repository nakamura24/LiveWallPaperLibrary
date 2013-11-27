/*
 * Copyright (C) 2012,2013 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */
package jp.library.livewallpaper;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class LiveWallPaper extends WallpaperService {
	private static String Tag = "LiveWallPaper";
	protected Bitmap BitmapImage = null;
	protected int[] ImageResources = {};
	protected int BackgroundColor = Color.BLACK;
	protected long DelayMillis = 0;
	protected int Offset = 2;
	protected float Scaled;
	protected int WidthPixels;
	protected int HeightPixels;
	protected boolean UseTimeTick = false;
	protected boolean UseBatteryChanged = false;
	protected int BatteryLevel = 0;
	protected int BatteryScale = 100;
	protected boolean UseSensor = false;

	public LiveWallPaper() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 画面サイズを取得
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		DisplayMetrics Metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(Metrics);
		Scaled = (float) Metrics.widthPixels
				/ (float) DisplayMetrics.DENSITY_DEFAULT;
		WidthPixels = Metrics.widthPixels;
		HeightPixels = Metrics.heightPixels;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new LiveEngine();
	}

	// 表示状態変更時に呼び出される
	protected void VisibilityChanged(boolean visible) {
	}

	// サーフェイス変更時に呼び出される
	protected void SurfaceChanged(int format, int width, int height) {
	}

	// オフセット変更時に呼び出される
	protected boolean OffsetsChanged(float xOffset, float yOffset, float xStep,
			float yStep, int xPixels, int yPixels) {
		int select = (int) Math.round(xOffset / xStep);
		if (Offset != select) {
			Log.d(Tag, "xOffset=" + xOffset + " xStep=" + xStep + " xPixels="
					+ xPixels);
			Offset = select;
			return true;
		}
		return false;
	}

	// 描画する画像を変更
	protected void ChangeImage() {
		if (ImageResources == null)
			return;
		// 　画像が5枚以上
		if (ImageResources.length / 5 > 0) {
			BitmapImage = BitmapFactory.decodeResource(getResources(),
					ImageResources[Offset]);
		} else {
			// 　画像が1枚以上
			if (ImageResources.length > 0) {
				Offset %= ImageResources.length;
				BitmapImage = BitmapFactory.decodeResource(getResources(),
						ImageResources[Offset]);
			}
		}
	}

	// キャンバスに描画を行う
	protected void DrawCanvas(Canvas canvas) {
		// 背景色を設定
		canvas.drawColor(BackgroundColor);

		if (BitmapImage == null)
			return;
		// 画像のリサイズ
		float scale_x = (float) BitmapImage.getWidth() / (float) WidthPixels;
		float scale_y = (float) BitmapImage.getHeight() / (float) HeightPixels;
		float scale = Math.max(scale_x, scale_y);
		int new_x = (int) (BitmapImage.getWidth() / scale);
		int new_y = (int) (BitmapImage.getHeight() / scale);
		BitmapImage = Bitmap.createScaledBitmap(BitmapImage, new_x, new_y,
				false);
		// キャンバスに画像を描画
		canvas.drawBitmap(BitmapImage, 0, 0, null);
	}

	// 再度描画が行われる前に呼び出される
	protected void DrawDelay() {
	}

	// ダブルタップした時の処理
	protected boolean DoubleTap(MotionEvent event) {
		return false;
	}

	protected boolean DoubleTapEvent(MotionEvent event) {
		// 描画処理を呼び出さない
		return false;
	}

	protected boolean Down(MotionEvent event) {
		// 描画処理を呼び出さない
		return false;
	}

	// フリックした時の処理
	protected boolean Fling(MotionEvent event1, MotionEvent event2,
			float velocityX, float velocityY) {
		// 描画処理を呼び出さない
		return false;
	}

	// 長押しした時の処理
	protected boolean LongPress(MotionEvent event) {
		// 描画処理を呼び出さない
		return false;
	}

	// スクロールした時の処理
	protected boolean Scroll(MotionEvent event1, MotionEvent event2,
			float distanceX, float distanceY) {
		// 描画処理を呼び出さない
		return false;
	}

	public boolean ShowPress(MotionEvent event) {
		// 描画処理を呼び出さない
		return false;
	}

	// シングルタップした時の処理
	protected boolean SingleTapConfirmed(MotionEvent event) {
		// 描画処理を呼び出さない
		return false;
	}

	public boolean SingleTapUp(MotionEvent event) {
		// 描画処理を呼び出さない
		return false;
	}

	// 進行中のジェスチャーに対するスケーリングイベントの処理
	protected boolean Scale(ScaleGestureDetector detector) {
		// 描画処理を呼び出さない
		return false;
	}

	// スケーリングジェスチャーの開始の処理
	protected boolean ScaleBegin(ScaleGestureDetector detector) {
		// 描画処理を呼び出さない
		return false;
	}

	// スケールジェスチャの終了の処理
	protected boolean ScaleEnd(ScaleGestureDetector detector) {
		// 描画処理を呼び出さない
		return false;
	}

	// センサが変化した時の処理
	protected boolean SensorChanged(int type, float[] values) {
		// 描画処理を呼び出さない
		return false;
	}

	// 描画を行うEngineクラス
	public class LiveEngine extends Engine {
		private final Handler mHandler = new Handler();
		private boolean mVisible;
		private boolean OffsetsChangeEnable = false;
		private GestureDetector mGestureDetector;
		private ScaleGestureDetector mScaleGestureDetector;
		private SensorManager mSensorManager;
		private float OrientationValues[] = new float[3];
		private float accelerometerValues[] = null;
		private float geomagneticMatrix[] = null;

		public LiveEngine() {
		}

		// Engine生成時に呼び出される
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			Log.i(Tag, "onCreate");
			// タッチイベントを有効
			setTouchEventsEnabled(true);
			// GestureDetecotorクラスのインスタンス生成
			mGestureDetector = new GestureDetector(LiveWallPaper.this,
					onGestureListener);
			// ScaleGestureDetectorクラスのインスタンス生成
			mScaleGestureDetector = new ScaleGestureDetector(
					LiveWallPaper.this, onSimpleOnScaleGestureListener);
			// Receiver登録
			IntentFilter filter = new IntentFilter();
			if (UseTimeTick)
				filter.addAction(Intent.ACTION_TIME_TICK);
			if (UseBatteryChanged)
				filter.addAction(Intent.ACTION_BATTERY_CHANGED);
			registerReceiver(mBroadcastReceiver, filter);
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		}

		// Engine破棄時に呼び出される
		@Override
		public void onDestroy() {
			super.onDestroy();
			Log.i(Tag, "onDestroy");
			// Receiver解除
			unregisterReceiver(mBroadcastReceiver);
			if (BitmapImage != null) {
				// Bitmapデータの解放
				BitmapImage.recycle();
				BitmapImage = null;
			}
			mHandler.removeCallbacks(drawRunnable);
			OffsetsChangeEnable = false;
		}

		// サーフェイス生成時に呼び出される
		@Override
		public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
			super.onSurfaceCreated(surfaceHolder);
			Log.i(Tag, "onSurfaceCreated");
		}

		// サーフェイス変更時に呼び出される
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			Log.i(Tag, "onSurfaceChanged");
			SurfaceChanged(format, width, height);
			if (mVisible) {
				drawFrame();
			}
		}

		// サーフェイス破棄時に呼び出される
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			Log.i(Tag, "onSurfaceDestroyed");
			mVisible = false;
			if (BitmapImage != null) {
				// Bitmapデータの解放
				BitmapImage.recycle();
				BitmapImage = null;
			}
			mHandler.removeCallbacks(drawRunnable);
			OffsetsChangeEnable = false;
		}

		// 表示状態変更時に呼び出される
		@Override
		public void onVisibilityChanged(boolean visible) {
			Log.i(Tag, "onVisibilityChanged=" + String.valueOf(visible));
			mVisible = visible;
			VisibilityChanged(visible);
			if (mVisible) {
				drawFrame();
				if (UseSensor) {
					List<Sensor> deviceSensors = mSensorManager
							.getSensorList(Sensor.TYPE_ALL);
					for (int i = 0; i < deviceSensors.size(); i++) {
						Log.d(Tag, "deviceSensors="
								+ deviceSensors.get(i).getName());
						mSensorManager.registerListener(mSensorEventListener,
								deviceSensors.get(i),
								SensorManager.SENSOR_DELAY_NORMAL);
					}
				}
			} else {
				mHandler.removeCallbacks(drawRunnable);
				if (UseSensor) {
					mSensorManager.unregisterListener(mSensorEventListener);
				}
			}
		}

		// オフセット変更時に呼び出される
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
			super.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels,
					yPixels);
			Log.i(Tag,
					"onOffsetsChanged - xOffset=" + String.valueOf(xOffset)
							+ "xStep=" + String.valueOf(xStep) + "xPixels="
							+ String.valueOf(xPixels));
			OffsetsChangeEnable = true;
			if (OffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels)) {
				drawFrame();
			}
		}

		// 　タッチイベント
		@Override
		public void onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			Log.d(Tag, "onTouchEvent - " + String.valueOf(event.getX()) + " "
					+ String.valueOf(event.getY()));
			// タッチイベントをGestureDetector#onTouchEventメソッドに
			if (event.getPointerCount() == 1) {
				mGestureDetector.onTouchEvent(event);
			} else if (event.getPointerCount() > 1) {
				mScaleGestureDetector.onTouchEvent(event);
			}
		}

		// キャンバスで描画を行う
		private void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas canvas = null;
			try {
				// キャンバスをロック
				canvas = holder.lockCanvas();
				if (canvas != null) {
					// 描画する画像を変更
					ChangeImage();
					// 画像を描画する
					DrawCanvas(canvas);
				}
			} catch (Exception e) {
				Log.e(Tag, e.getMessage());
			} finally {
				// Canvas アンロック
				if (canvas != null) {
					try {
						holder.unlockCanvasAndPost(canvas);
					} catch (Exception e) {
						Log.e(Tag, e.getMessage());
					}
				}
			}
			if (DelayMillis > 0) {
				mHandler.removeCallbacks(drawRunnable);
				if (mVisible) {
					// DelayMillis(ms)後に再描画
					mHandler.postDelayed(drawRunnable, DelayMillis);
				}
			}
		}

		private final Runnable drawRunnable = new Runnable() {
			public void run() {
				DrawDelay();
				// 描画メソッドを呼び出す
				drawFrame();
			}
		};

		// Receiver
		private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i(Tag, "onReceive - " + intent.getAction());
				String action = intent.getAction();
				// TIME_TICKを受け取った処理
				if (Intent.ACTION_TIME_TICK.equals(action)) {
					if (mVisible) {
						drawFrame();
					}
				}
				// BATTERY_CHANGEDを受け取った処理
				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					BatteryLevel = intent.getIntExtra("level", 0);
					BatteryScale = intent.getIntExtra("scale", 100);
					if (mVisible) {
						drawFrame();
					}
				}
			}
		};

		private final SensorEventListener mSensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent e) {
				// Log.i(Tag, "onSensorChanged");
				switch (e.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					Log.v(Tag, "TYPE_ACCELEROMETER X=" + e.values[0] + " Y="
							+ e.values[1] + " Z=" + e.values[2]);
					sensorChanged(e.sensor.getType(), e.values);
					break;
				case Sensor.TYPE_LIGHT:
					Log.v(Tag, "TYPE_LIGHT lx=" + e.values[0]);
					sensorChanged(e.sensor.getType(), e.values);
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					Log.v(Tag, "TYPE_MAGNETIC_FIELD X=" + e.values[0] + " Y="
							+ e.values[1] + " Z=" + e.values[2]);
					sensorChanged(e.sensor.getType(), e.values);
					break;
				case Sensor.TYPE_PROXIMITY:
					Log.v(Tag, "TYPE_PROXIMITY distance=" + e.values[0]);
					sensorChanged(e.sensor.getType(), e.values);
					break;
				case Sensor.TYPE_AMBIENT_TEMPERATURE:
					Log.v(Tag, "TYPE_AMBIENT_TEMPERATURE " + e.values[0] + "℃");
					sensorChanged(e.sensor.getType(), e.values);
					break;
				}
			}

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
			}

			// センサが変化した時の処理
			private boolean sensorChanged(int type, float[] values) {
				switch (type) {
				case Sensor.TYPE_ACCELEROMETER:
					accelerometerValues = values.clone();
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					geomagneticMatrix = values.clone();
					break;
				}
				if (geomagneticMatrix != null && accelerometerValues != null) {
					float[] R = new float[16];
					float[] I = new float[16];
					SensorManager.getRotationMatrix(R, I, accelerometerValues,
							geomagneticMatrix);
					SensorManager.getOrientation(R, OrientationValues);
					Log.v(Tag, "Orientation X=" + OrientationValues[0]
							/ Math.PI * 180 + " Y=" + OrientationValues[1]
							/ Math.PI * 180 + " Z=" + OrientationValues[2]
							/ Math.PI * 180);
					geomagneticMatrix = null;
					accelerometerValues = null;
					values = OrientationValues;
				}
				SensorChanged(type, values);
				// 描画処理を呼び出さない
				return false;
			}
		};

		// 複雑なタッチイベントを取得
		public SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent event) {
				Log.d(Tag, "onDoubleTap - " + String.valueOf(event.getX())
						+ " " + String.valueOf(event.getY()));
				// ダブルタップした時の処理
				if (DoubleTap(event)) {
					drawFrame();
				}
				return super.onDoubleTap(event);
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent event) {
				Log.d(Tag, "onDoubleTapEvent - " + String.valueOf(event.getX())
						+ " " + String.valueOf(event.getY()));
				if (DoubleTapEvent(event)) {
					drawFrame();
				}
				return super.onDoubleTapEvent(event);
			}

			@Override
			public boolean onDown(MotionEvent event) {
				Log.d(Tag, "onDown - " + String.valueOf(event.getX()) + " "
						+ String.valueOf(event.getY()));
				if (Down(event)) {
					drawFrame();
				}
				return super.onDown(event);
			}

			@Override
			public boolean onFling(MotionEvent event1, MotionEvent event2,
					float velocityX, float velocityY) {
				Log.d(Tag, "onFling - " + String.valueOf(velocityX) + " "
						+ String.valueOf(velocityY));
				// フリックした時の処理
				if (Fling(event1, event2, velocityX, velocityY)) {
					drawFrame();
				}
				// onOffsetsChangedが呼び出されない機種対応
				if (!OffsetsChangeEnable) {
					if (velocityX > 0) {
						Offset++;
						Offset %= 5;
					} else if (velocityX < 0) {
						Offset--;
						Offset = (Offset + 5) % 5;
					}
					drawFrame();
				}
				return super.onFling(event1, event2, velocityX, velocityY);
			}

			@Override
			public void onLongPress(MotionEvent event) {
				Log.d(Tag, "onLongPress - " + String.valueOf(event.getX())
						+ " " + String.valueOf(event.getY()));
				// 長押しした時の処理
				if (LongPress(event)) {
					drawFrame();
				}
				super.onLongPress(event);
			}

			@Override
			public boolean onScroll(MotionEvent event1, MotionEvent event2,
					float distanceX, float distanceY) {
				Log.d(Tag, "onScroll - " + String.valueOf(distanceX) + " "
						+ String.valueOf(distanceY));
				// スクロールした時の処理
				if (Scroll(event1, event2, distanceX, distanceY)) {
					drawFrame();
				}
				return super.onScroll(event1, event2, distanceX, distanceY);
			}

			@Override
			public void onShowPress(MotionEvent event) {
				Log.d(Tag, "onShowPress - " + String.valueOf(event.getX())
						+ " " + String.valueOf(event.getY()));
				if (ShowPress(event)) {
					drawFrame();
				}
				super.onShowPress(event);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {
				Log.d(Tag,
						"onSingleTapConfirmed - "
								+ String.valueOf(event.getX()) + " "
								+ String.valueOf(event.getY()));
				// シングルタップした時の処理
				if (SingleTapConfirmed(event)) {
					drawFrame();
				}
				return super.onSingleTapConfirmed(event);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent event) {
				Log.d(Tag, "onSingleTapUp - " + String.valueOf(event.getX())
						+ " " + String.valueOf(event.getY()));
				if (SingleTapUp(event)) {
					drawFrame();
				}
				return super.onSingleTapUp(event);
			}
		};

		// 複雑なタッチイベントを取得
		public SimpleOnScaleGestureListener onSimpleOnScaleGestureListener = new SimpleOnScaleGestureListener() {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				Log.d(Tag,
						"onScale - "
								+ String.valueOf(detector.getCurrentSpan()));
				// 進行中のジェスチャーに対するスケーリングイベントに応答します
				if (Scale(detector)) {
					drawFrame();
				}
				return super.onScale(detector);
			}

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				Log.d(Tag,
						"onScaleBegin - "
								+ String.valueOf(detector.getCurrentSpan()));
				// スケーリングジェスチャーの開始に応答します
				if (ScaleBegin(detector)) {
					drawFrame();
				}
				return super.onScaleBegin(detector);
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				Log.d(Tag,
						"onScaleEnd - "
								+ String.valueOf(detector.getCurrentSpan()));
				// スケールジェスチャの終了に応答します
				if (ScaleEnd(detector)) {
					drawFrame();
				}
				super.onScaleEnd(detector);
			}
		};
	}
}
