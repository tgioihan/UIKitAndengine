package com.bestfunforever.andengine.uikit.entity;


public interface ISelector {
	public int getId();

	public void setId(int id);

	public void onSelectedState();

	public void onNormalState();

	public void onPressState();

	public void setClickListenner(IClick mClickListenner);

	public void setEnable(boolean enable);

	public void setState(State mState);
}
