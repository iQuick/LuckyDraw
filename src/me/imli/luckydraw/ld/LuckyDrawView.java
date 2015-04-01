package me.imli.luckydraw.ld;

import java.util.List;

import me.imli.luckdraw.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LuckyDrawView extends RelativeLayout {
	
	protected static final String TAG = "LuckyDrawView";
	
	private static final int DEF_MIN_WIDTH = 200;
	private static final int DEF_MIN_HEIGHT = 200;
	private static final int PRIZE_COUNT = 8;
	
	private Handler mHandler = new Handler(getContext().getMainLooper());
	private LuckyDrawRunnable mLuckyDrawRunnable;
	
	/** 开始按钮 */
	private ImageView mStart;
	
	/** 奖品Item */
	private LuckyDrawItem[] mLuckyDrawItem = new LuckyDrawItem[PRIZE_COUNT];
	
	private int mWidth, mHeight;

	public LuckyDrawView(Context context) {
		this(context, null);
	}

	public LuckyDrawView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressWarnings("deprecation")
	public LuckyDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
		
		// 属性
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LuckyDrawView);
		Drawable startSrc = a.getDrawable(R.styleable.LuckyDrawView_startSrc);
		Drawable start_backgroud = a.getDrawable(R.styleable.LuckyDrawView_startBackgroud);
		Drawable prize_backgroud = a.getDrawable(R.styleable.LuckyDrawView_prizeBackgroud);
		a.recycle();
		
		mStart.setImageDrawable(startSrc);
		if (start_backgroud == null) {
			mStart.setBackgroundResource(R.drawable.lucky_draw_btn_selector);
		} else {
			mStart.setBackgroundDrawable(start_backgroud);
		}
		for (int i = 0; i < mLuckyDrawItem.length; i++) {
			if (prize_backgroud == null) {
				mLuckyDrawItem[i].setBackgroundResource(R.drawable.lucky_draw_item_selector);
			} else {
				mLuckyDrawItem[i].setBackgroundDrawable(prize_backgroud);
			}
		}
	}
	
	private void init() {
		RelativeLayout.inflate(getContext(), R.layout.layout_lucky_draw_view, this);

		mLuckyDrawRunnable = new LuckyDrawRunnable(mHandler, mLuckyDrawItem, luckyDrawRotation());
		
		mStart = (ImageView) findViewById(R.id.v_lucky_draw_view_start);
		mStart.setOnClickListener(onClickStart());

		mLuckyDrawItem[0] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_1);
		mLuckyDrawItem[1] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_2);
		mLuckyDrawItem[2] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_3);
		mLuckyDrawItem[3] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_4);
		mLuckyDrawItem[4] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_5);
		mLuckyDrawItem[5] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_6);
		mLuckyDrawItem[6] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_7);
		mLuckyDrawItem[7] = (LuckyDrawItem) findViewById(R.id.ld_lucky_draw_view_8);
	}
	
	private void initView(int width, int height) {
		mWidth = width;
		mHeight = height;
		
		// 如果大小小于最小值，则重新设置大小为最小值
		if (width < DEF_MIN_WIDTH || height < DEF_MIN_HEIGHT) {
			if (width < DEF_MIN_WIDTH) {
				mWidth = DEF_MIN_WIDTH;
			}
			if (height < DEF_MIN_HEIGHT) {
				mHeight = DEF_MIN_HEIGHT;
			}
			setMeasuredDimension(mWidth, mHeight);
			return;
		}
		
		// 设置 奖品Imte及开始按钮的大小
		float size = (float) (((width < height ? width : height) - dp2px(8) * 2.0) / 3.0);
		ViewGroup.LayoutParams lp = mStart.getLayoutParams();
		lp.width = (int) size;
		lp.height = (int) size;
		mStart.setLayoutParams(lp);
		for (int i = 0; i < mLuckyDrawItem.length; i++) {
			lp = mLuckyDrawItem[i].getLayoutParams();
			lp.width = (int) size;
			lp.height = (int) size;
			mLuckyDrawItem[i].setLayoutParams(lp);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getMeasuredWidth() != 0 && getMeasuredHeight() != 0) {
			initView(getMeasuredWidth(), getMeasuredHeight());
		}
	}
	
	/**
	 * Sets a drawable as the content of this ImageView.
	 * @param resId the resource identifier of the drawable
	 */
	public void setStartResource(int resId) {
		if (mStart != null) {
			mStart.setImageResource(resId);
		}
	}
	
	/**
	 * Sets a Bitmap as the content of this ImageView.
	 * @param bm The bitmap to set
	 */
	public void setStartBitmap(Bitmap bm) {
		if (mStart != null) {
			mStart.setImageBitmap(bm);
		}
	}
	
	/**
	 * Set the background to a given resource. The resource should refer to a Drawable object or 0 to remove the background.
	 * @param resid The identifier of the resource.
	 */
	public void setStartBackgroudResource(int resid) {
		if (mStart != null) {
			mStart.setBackgroundResource(resid);
		}
	}
	
	/**
	 * Sets the background color for this view.
	 * @param color the color of the background
	 */
	public void setStartBackgroudColor(int color) {
		if (mStart != null) {
			mStart.setBackgroundColor(color);
		}
	}
	
	/**
	 * 设置奖品图片
	 * @param res
	 */
	public void setPrizeResources(List<Integer> res) {
		if (mLuckyDrawItem != null) {
			for (int i = 0; i < (mLuckyDrawItem.length < res.size() ? mLuckyDrawItem.length : res.size()); i++) {
				mLuckyDrawItem[i].setImageResource(res.get(i));
			}
		}
	}
	
	/**
	 * 设置奖品图片
	 * @param bitmaps
	 */
	public void setPrizeBitmaps(List<Bitmap> bitmaps) {
		if (mLuckyDrawItem != null) {
			for (int i = 0; i < (mLuckyDrawItem.length < bitmaps.size() ? mLuckyDrawItem.length : bitmaps.size()); i++) {
				mLuckyDrawItem[i].setImageBitmap(bitmaps.get(i));
			}
		}
	}
	
	/**
	 * 设置奖品背景
	 * @param resid
	 */
	public void setPrizeBackgroudResource(int resid) {
		if (mLuckyDrawItem != null) {
			for (int i = 0; i < mLuckyDrawItem.length; i++) {
				mLuckyDrawItem[i].setBackgroundResource(resid);
			}
		}
	}
	
	/**
	 * 设置奖品的背景颜色
	 * @param color
	 */
	public void setPrizeBackgroudColor(int color) {
		if (mLuckyDrawItem != null) {
			for (int i = 0; i < mLuckyDrawItem.length; i++) {
				mLuckyDrawItem[i].setBackgroundColor(color);
			}
		}
	}
	
	/**
	 * 开始抽奖
	 */
	public void start() {
		new Thread(mLuckyDrawRunnable).start();
	}
	
	/**
	 * 停止抽奖并指定奖品
	 * @param end
	 */
	public void stop(int end) {
		mLuckyDrawRunnable.stop(end);
	}
	
	/**
	 * 
	 */
	public void clean() {
		for (int i = 0; i < mLuckyDrawItem.length; i++) {
			mLuckyDrawItem[i].setSelected(false);
		}
		mStart.setEnabled(false);
		setKeepScreenOn(false);
	}
	
	/**
	 * 点击开始按钮
	 * @return
	 */
	private View.OnClickListener onClickStart() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				start();
			}
		};
	}
	
	/**
	 * 
	 * @return
	 */
	private LuckyDrawView.OnLuckyDrawRotationListener luckyDrawRotation() {
		return new OnLuckyDrawRotationListener() {
			
			@Override
			public void startRotation() {
				mStart.setEnabled(false);
				setKeepScreenOn(true);
				if (mOnLuckyDrawListener != null) {
					mOnLuckyDrawListener.stop();
				}
			}
			
			@Override
			public void stopRotation() {
				mStart.setEnabled(true);
				setKeepScreenOn(false);
				if (mOnLuckyDrawListener != null) {
					mOnLuckyDrawListener.start();
				}
			}
		};
	}
	

	/**
	 * 
	 * @param dp
	 * @return
	 */
	public int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
	}
	
	
	/**
	 * 
	 * @author Doots
	 *
	 */
	private class LuckyDrawRunnable implements Runnable {
		
		private final int MIN_TIME_INTERVAL = 50;		// 最小时间间隔
		private final int MAX_TIME_INTERVAL = 1000;		// 最大时间间隔

		/** 时间间隔 */
		private int mTimeInterval = 1000;
		
		/** 停止位置 */
		private int mEnd;
		
		/**
		 * 高速旋转的圈数
		 */
		private final int ROTATION_COUNT = 10;
		
		private Handler mMainHandelr;
		
		private OnLuckyDrawRotationListener mDrawStopListener;
		
		/** 是否开始减速*/
		private boolean isDeceleration;
		/** 是否停止 */
		private boolean isStop;
		
		/** Items */
		private View[] mItems;
		
		
		public LuckyDrawRunnable(Handler handler, View[] items, OnLuckyDrawRotationListener l) {
			this.mMainHandelr = handler;
			this.mItems = items;
			this.mDrawStopListener = l;
			this.mTimeInterval = MAX_TIME_INTERVAL;
			this.isDeceleration = false;
		}

		@Override
		public void run() {
			start();
			stop();
		}
		
		/**
		 * 开始旋转
		 */
		private void start() {
			isStop = false;
			fristRotationPrize();
			while (!isStop) {
				rotationPrize();
			}
		}
		
		/**
		 * 停止操作
		 */
		private void stop() {
			for (int i = 0; i < ROTATION_COUNT; i++) {
				rotationPrize();
			}
			// 停止旋转
			isDeceleration = true;
			int count = 0;
			while (true) {
				nextPrize(mItems[count++%mItems.length]);
				if (mTimeInterval >= (MAX_TIME_INTERVAL / 2)) {
					break;
				}
			}
			lastRotationPrize(mEnd);
		}
		
		/**
		 * 停止操作
		 * @param end
		 */
		public void stop(int end) {
			isStop = true;
			mEnd = end;
		}
		
		/**
		 * 刚开始首轮转动
		 */
		private void fristRotationPrize() {
			if (mDrawStopListener != null) {
				mMainHandelr.post(new Runnable() {
					
					@Override
					public void run() {
						mDrawStopListener.startRotation();
					}
				});
			}
			int position = -1;
			for (int i = 0; i < mItems.length; i++) {
				if (mItems[i].isSelected()) {
					position = i;
					break;
				}
			}
			for (int i = (position == -1 ? 0 : position); i < mItems.length; i++) {
				nextPrize(mItems[i]);
			}
		}
		
		/**
		 * 最后一轮转动，并转到指定的项上
		 */
		private void lastRotationPrize(int end) {
			// 获取当前所在位置
			int position = 0;
			for (int i = 0; i < mItems.length; i++) {
				if (mItems[i].isSelected()) {
					position = i;
					break;
				}
			}
			if (position < end) {
				// 如果当前位置在停止位置后，则旋转到停止位置停止
				for (int i = ++position; i <= (end < mItems.length-1 ? end : mItems.length-1); i++) {
					nextPrize(mItems[i]);
				}
			} else {
				// 如新当前位置在停止位置前，则先旋转到0,再继续旋转到停止位置停止
				for (int i = ++position; i < mItems.length; i++) {
					nextPrize(mItems[i]);
				}
				for (int i = 0; i <= (end < mItems.length-1 ? end : mItems.length-1); i++) {
					nextPrize(mItems[i]);
				}
			}
			if (mDrawStopListener != null) {
				mMainHandelr.post(new Runnable() {
					
					@Override
					public void run() {
						clean();
						mDrawStopListener.stopRotation();
					}
				});
			}
		}
		
		/**
		 * 旋转
		 */
		private void rotationPrize() {
			for (int i = 0; i < mItems.length; i++) {
				nextPrize(mItems[i]);
			}
		}
		
		private void nextPrize(final View v) {
			if (!isDeceleration) {
				mTimeInterval -= mTimeInterval / 8;
				if (mTimeInterval < MIN_TIME_INTERVAL) {
					mTimeInterval = MIN_TIME_INTERVAL;
				}
			} else {
				mTimeInterval += mTimeInterval / 8;
				if (mTimeInterval > 2 * MAX_TIME_INTERVAL) {
					mTimeInterval = 2 * MAX_TIME_INTERVAL;
				}
			}
			// 线程休眠，造成
			try {
				Thread.sleep(mTimeInterval);  
			} catch (InterruptedException e) {  
				e.printStackTrace();
			}
			// 旋转到下一个item
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					cleanSelected();
					v.setSelected(true);
				}
			});
		}
		
		/**
		 * 
		 */
		private void cleanSelected() {
			for (int i = 0; i < mItems.length; i++) {
				mItems[i].setSelected(false);
			}
		}
		
		private void clean() {
			isDeceleration = false;
			isStop = false;
			mTimeInterval = MAX_TIME_INTERVAL;
		}
	}
	
	
	private OnLuckyDrawListener mOnLuckyDrawListener;
	
	public void setOnLuckyDrawListener(OnLuckyDrawListener listener) {
		mOnLuckyDrawListener = listener;
	}
	
	/**
	 * 
	 * @author Doots
	 *
	 */
	public interface OnLuckyDrawListener {
		public void start();
		public void stop();
	}
	
	/**
	 * 
	 * @author Doots
	 *
	 */
	private interface OnLuckyDrawRotationListener {
		public void startRotation();
		public void stopRotation();
	}

}
