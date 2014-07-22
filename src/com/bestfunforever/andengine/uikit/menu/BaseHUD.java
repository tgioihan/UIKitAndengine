package com.bestfunforever.andengine.uikit.menu;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;

public abstract class BaseHUD extends HUD {

	public abstract void onLoadResource();

	public abstract void onCreate();

	public abstract void onDestroy();
	
	public void attackScene(Scene scene){
		scene.setChildScene(this);
		onAttackScene();
	}

	public void onAttackScene() {
		
	}

}
