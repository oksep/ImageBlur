package com.demo.blur;

import android.graphics.Bitmap;

public class Blur {
	static {
		System.loadLibrary("ImageBlur");
	}

	// 传递像素点集合进行模糊
	public static native void blurIntArray(int[] pImg, int w, int h, int r);

	// 直接传递位图进行模糊
	public static native void blurBitMap(Bitmap bitmap, int r);

}
