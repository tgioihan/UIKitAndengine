package com.bestfunforever.andengine.uikit.entity;

public interface ISelector {
	public int getId();
	public void setId(int id);

	public void onSelectedState();

	public void onNormalState();
	
	public void onPressState();

}
