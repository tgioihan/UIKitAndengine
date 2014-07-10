package com.bestfunforever.andengine.uikit.listview.test;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.bestfunforever.andengine.uikit.listview.ListView;
import com.bestfunforever.andengine.uikit.listview.ListView.OnItemClickListenner;
import com.bestfunforever.andengine.uikit.util.RatioUtils;

public class TestActivity extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 480;
	private static int CAMERA_HEIGHT = 320;
	
	public static int LIST_WIDTH = 400;
	public static int LIST_HEIGHT = 600;
	
	public static int item_toscroll = 49 ;
	
	private Font mFont;
	
	private float ratio ;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		CAMERA_WIDTH = metrics.widthPixels;
		CAMERA_HEIGHT = metrics.heightPixels;
		
		ratio  = RatioUtils.calculatorRatioScreen(this, true);
		LIST_WIDTH = (int)(LIST_WIDTH * ratio);
		LIST_HEIGHT =(int)( LIST_HEIGHT * ratio);
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), (int)(256*ratio), (int)(256*ratio), Typeface.create(Typeface.DEFAULT, Typeface.BOLD), (int)(32*ratio));
		this.mFont.load();
	}
	
	Random rand = new Random();
	private ListView mListView;
	private Text mText;

	@Override
	protected Scene onCreateScene() {
		Scene scene = new Scene();
		scene.setBackground(new Background(Color.WHITE));
		mListView = new ListView(this, CAMERA_WIDTH/2 -LIST_WIDTH/2, CAMERA_HEIGHT/2 -LIST_HEIGHT/2, LIST_WIDTH, LIST_HEIGHT, getVertexBufferObjectManager());
		TestAdapter testAdapter = new TestAdapter(getVertexBufferObjectManager(), mFont);
		mListView.setAdapter(testAdapter);
		mListView.setSelection(5);
		mListView.setOnItemClickListenner(new OnItemClickListenner() {
			
			@Override
			public void onClick(final IAreaShape view,final int position) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(TestActivity.this, "click item "+ position +" with view "+view, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		mText = new Text(0,+10, mFont, "click to scroll to item "+ item_toscroll,50, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// TODO Auto-generated method stub
				if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP){
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(TestActivity.this, "scroll list to position  "+ item_toscroll , Toast.LENGTH_SHORT).show();
							mListView.setSelectionFromTop(item_toscroll, 20, true);
							item_toscroll = rand.nextInt(50);
							(TestActivity.this.mText).setText("click to scroll to item "+ item_toscroll);
						}
					});
					
				}
				return true;
			}
		};
		
		
		scene.attachChild(mText);
		scene.registerTouchArea(mText);
		scene.attachChild(mListView);
		scene.registerTouchArea(mListView);
		return scene;
	}

}
