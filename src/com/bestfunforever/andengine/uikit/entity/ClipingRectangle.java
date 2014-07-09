package com.bestfunforever.andengine.uikit.entity;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.Constants;
import org.andengine.util.color.Color;

import android.opengl.GLES20;

import com.bestfunforever.andengine.uikit.util.Util;

public class ClipingRectangle extends Rectangle{

	public ClipingRectangle(float pX, float pY,float width,float height,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY,width,height, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onManagedDraw(GLState pGLState, Camera pCamera) {
		// TODO Auto-generated method stub
		final boolean wasScissorTestEnabled = pGLState.enableScissorTest();

		final int surfaceHeight = pCamera.getSurfaceHeight();
		/*
		 * In order to apply clipping, we need to determine the the axis aligned
		 * bounds in OpenGL coordinates.
		 */

		/* Determine clipping coordinates of each corner in surface coordinates. */
		final float[] lowerLeftSurfaceCoordinates = this
				.convertLocalToSceneCoordinates(0, 0);
		final int lowerLeftX = (int) Math
				.round(lowerLeftSurfaceCoordinates[Constants.VERTEX_INDEX_X]);
		final int lowerLeftY = surfaceHeight
				- (int) Math
						.round(lowerLeftSurfaceCoordinates[Constants.VERTEX_INDEX_Y]);

		final float[] upperLeftSurfaceCoordinates = this
				.convertLocalToSceneCoordinates(0, this.mHeight);
		final int upperLeftX = (int) Math
				.round(upperLeftSurfaceCoordinates[Constants.VERTEX_INDEX_X]);
		final int upperLeftY = surfaceHeight
				- (int) Math
						.round(upperLeftSurfaceCoordinates[Constants.VERTEX_INDEX_Y]);

		final float[] upperRightSurfaceCoordinates = this
				.convertLocalToSceneCoordinates(this.mWidth, this.mHeight);
		final int upperRightX = (int) Math
				.round(upperRightSurfaceCoordinates[Constants.VERTEX_INDEX_X]);
		final int upperRightY = surfaceHeight
				- (int) Math
						.round(upperRightSurfaceCoordinates[Constants.VERTEX_INDEX_Y]);

		final float[] lowerRightSurfaceCoordinates = this
				.convertLocalToSceneCoordinates(this.mWidth, 0);
		final int lowerRightX = (int) Math
				.round(lowerRightSurfaceCoordinates[Constants.VERTEX_INDEX_X]);
		final int lowerRightY = surfaceHeight
				- (int) Math
						.round(lowerRightSurfaceCoordinates[Constants.VERTEX_INDEX_Y]);

		/* Determine minimum and maximum x clipping coordinates. */
		final int minClippingX = Util.min(lowerLeftX, upperLeftX, upperRightX,
				lowerRightX);
		final int maxClippingX = Util.max(lowerLeftX, upperLeftX, upperRightX,
				lowerRightX);

		/* Determine minimum and maximum y clipping coordinates. */
		final int minClippingY = Util.min(lowerLeftY, upperLeftY, upperRightY,
				lowerRightY);
		final int maxClippingY = Util.max(lowerLeftY, upperLeftY, upperRightY,
				lowerRightY);

		/* Determine clipping width and height. */
		final int clippingWidth = maxClippingX - minClippingX;
		final int clippingHeight = maxClippingY - minClippingY;

		/* Finally apply the clipping. */
		GLES20.glScissor(minClippingX-1, minClippingY+1, clippingWidth+2,
				clippingHeight+3);

		/* Draw children, etc... */
		super.onManagedDraw(pGLState, pCamera);
		//
		// /* Revert scissor test to previous state. */
		pGLState.setScissorTestEnabled(wasScissorTestEnabled);
	}
	
}
