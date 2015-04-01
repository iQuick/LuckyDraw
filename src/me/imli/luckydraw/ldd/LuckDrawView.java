package me.imli.luckydraw.ldd;

import java.util.ArrayList;
import java.util.List;

import me.imli.luckdraw.R;
import me.imli.luckydraw.App;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LuckDrawView extends SurfaceView implements Callback, Runnable {
	
	protected final String TAG = getClass().getName();
	
	protected static final int DEF_NORMAL_COLOR = 0xff00ffff;
	protected static final int DEF_SELECT_COLOR = 0xffff00ff;
	protected static final int DEF_TEXT_SIZE = 14;
	protected static final int DEF_BLOCK_SPACE = 8;
	
	/** SurfaceHolder */
	private SurfaceHolder mSurfaceHolder;
	
	/** 画布 */
	private Canvas mCanvas;
	
	/** 用于绘制的线程  */
	private Thread mThread;
	
	/** 线程的控制开关  */  
    private boolean isRunning;
    
    /** 控件的宽、高 */
    private int mWidth, mHeight;
    private float mBlockSize;
    
    public static List<LuckDrawModel> mLuckDrawModels = new ArrayList<LuckDrawView.LuckDrawModel>();
    
    /** 绘制块的范围 */
    private RectF mRange;
    /** 画笔 */
    private Paint mPaint;
    
    /** 文字画笔 */
    private TextPaint mTextPaint;
    
    private int mBlockCount = 3;;
    

	public LuckDrawView(Context context) {
		this(context, null);
	}

	public LuckDrawView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public LuckDrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		Log.i(TAG, "LuckDrawView");
		
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		
		// 设置可以获取焦点
		setFocusable(true);
		setFocusableInTouchMode(true);
		// 设置常亮
		setKeepScreenOn(true);
		
		// 初始化画笔
		mPaint = new Paint();
		mPaint.setStyle(Style.FILL);
		mPaint.setAntiAlias(true);
		
		mTextPaint = new TextPaint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setTextSize(dp2px(DEF_TEXT_SIZE));
		mTextPaint.setColor(0xff333333);
		
		mRange = new RectF(getPaddingLeft(), getPaddingTop(), mWidth + getPaddingLeft() - getPaddingRight(), mHeight + getPaddingTop() - getPaddingBottom());
	
		mThread = new Thread(this);
		mThread.start();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 获取控件的宽高
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		int size = mWidth = mHeight = mWidth < mHeight ? mWidth : mHeight;
		mBlockSize = (size - getPaddingLeft() - getPaddingRight() - (mBlockCount-1)*dp2px(DEF_BLOCK_SPACE)) / mBlockCount;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");
		isRunning = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		isRunning = false;

	}

	@Override
	public void run() {
		while (true) {
			long start = System.currentTimeMillis();  
			draw();  
			long end = System.currentTimeMillis();  
			try {  
				if (end - start < 50) {  
					Thread.sleep(50 - (end - start));  
				}
			} catch (InterruptedException e) {  
				e.printStackTrace();
			} 
		} 
	}
	
	private void draw() {
		mCanvas = mSurfaceHolder.lockCanvas();
		try {
			if (mCanvas != null) {
				for (int i = 0; i < mLuckDrawModels.size(); i++) {
					drawBlock(mLuckDrawModels.get(i));
				}
			} else {
				Log.i(TAG, "canvas null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCanvas != null) {
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}
	
	private void drawBlock(LuckDrawModel model) {
		drawBlockBg(model);
		drawIcon(model);
		drawText(model);
	}
	
	private void drawBlockBg(LuckDrawModel model) {
		mPaint.setColor(DEF_NORMAL_COLOR);
		RectF rect = new RectF(dp2px(DEF_BLOCK_SPACE) + model.row*mBlockSize, dp2px(DEF_BLOCK_SPACE) +model.line*mBlockSize, 
				(model.row + 1)*mBlockSize, (model.line + 1)*mBlockSize);
		mCanvas.drawRect(rect, mPaint);
	}
	
	private void drawIcon(LuckDrawModel model) {
		int width = model.image.getWidth();
		int height = model.image.getHeight();
		float left = dp2px(DEF_BLOCK_SPACE) + model.row*mBlockSize + mBlockSize / 2 - width / 2;
		float top = dp2px(DEF_BLOCK_SPACE) +model.line*mBlockSize + mBlockSize / 2 - height / 2;
		mCanvas.drawBitmap(model.image, left, top, mPaint);
	}
	
	private void drawText(LuckDrawModel model) {
		float textWidth = mTextPaint.measureText(model.text);
		float left = dp2px(DEF_BLOCK_SPACE) + model.row*mBlockSize;
		float top = dp2px(DEF_BLOCK_SPACE) + model.line*mBlockSize;
		mCanvas.drawText(model.text, left, top, mTextPaint);
	}
	
	
	/**
	 * 
	 * @param dp
	 * @return
	 */
	public int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
	}
	
	static {
		mLuckDrawModels.add(new LuckDrawModel("111"));
		mLuckDrawModels.add(new LuckDrawModel("222"));
		mLuckDrawModels.add(new LuckDrawModel("333"));
		mLuckDrawModels.add(new LuckDrawModel("444"));
		mLuckDrawModels.add(new LuckDrawModel("555"));
		mLuckDrawModels.add(new LuckDrawModel("666"));
		mLuckDrawModels.add(new LuckDrawModel("777"));
		mLuckDrawModels.add(new LuckDrawModel("888"));
		int count = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if ((i > 0 && i < 3-1) && (j > 0 && j < 3-1)) {
					continue;
				}
				LuckDrawModel model = mLuckDrawModels.get(count++);
				model.row = i;
				model.line = j;
			}
		}
	}

	
	/**
	 * 
	 * @author Doots
	 *
	 */
	public static class LuckDrawModel {
		public String text;
		public Bitmap image;
		
		public int row;
		public int line;
		
		public LuckDrawModel(String text) {
			this.text = text;
			this.image = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.ic_launcher);
		}
	}
}
