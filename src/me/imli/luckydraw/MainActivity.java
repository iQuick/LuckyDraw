package me.imli.luckydraw;

import me.imli.luckdraw.R;
import me.imli.luckydraw.ld.LuckyDrawView;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button mStopBtn;
	private LuckyDrawView luckDrawView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		luckDrawView = (LuckyDrawView) findViewById(R.id.luck_draw);
		mStopBtn = (Button) findViewById(R.id.stop);
		mStopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int end = (int) (Math.random() * 7) ;
				Log.i("MainActivity", "" + end);
				luckDrawView.stop(end);
				
			}
		});
		
		
		
//		setContentView(new LuckDrawView(this));
		
//		mLuckyPanView = (LuckyPanView) findViewById(R.id.id_luckypan);
//		mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
//
//		mStartBtn.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				if (!mLuckyPanView.isStart())
//				{
//					mStartBtn.setImageResource(R.drawable.stop);
//					mLuckyPanView.luckyStart(1);
//				} else
//				{
//					if (!mLuckyPanView.isShouldEnd())
//
//					{
//						mStartBtn.setImageResource(R.drawable.start);
//						mLuckyPanView.luckyEnd();
//					}
//				}
//			}
//		});
	}

}
