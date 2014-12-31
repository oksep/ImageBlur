package com.npi.blureffect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main2);
		img = (ImageView) findViewById(R.id.test);
		
		Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.bbb); //获取Bitmap图片
		RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), src); //创建RoundedBitmapDrawable对象
		roundedBitmapDrawable.setCornerRadius(500); //设置圆角半径（根据实际需求）
		roundedBitmapDrawable.setAntiAlias(true); //设置反走样
		
		Drawable dr = getResources().getDrawable(R.drawable.aaaa);
		img.setImageDrawable(roundedBitmapDrawable);
	}

	ImageView img;

	private void ninePathable() throws IOException {
		String path = getFilesDir() + "/theme_children_focus.9.png";
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);

		InputStream is = getAssets().open("theme_children_focus.9.png");
		Bitmap bitmap = BitmapFactory.decodeStream(is);

		final byte[] chunk = bitmap.getNinePatchChunk();
		if (NinePatch.isNinePatchChunk(chunk)) {
			Rect mPaddings = NinePatchChunk.deserialize(chunk).mPaddings;
			img.setBackgroundDrawable(new NinePatchDrawable(getResources(), bitmap, chunk, mPaddings, null));
		}

	}

	static class NinePatchChunk {

		public static final int NO_COLOR = 0x00000001;
		public static final int TRANSPARENT_COLOR = 0x00000000;

		public final Rect mPaddings = new Rect();

		public int mDivX[];
		public int mDivY[];
		public int mColor[];

		private static void readIntArray(final int[] data, final ByteBuffer buffer) {
			for (int i = 0, n = data.length; i < n; ++i)
				data[i] = buffer.getInt();
		}

		private static void checkDivCount(final int length) {
			if (length == 0 || (length & 0x01) != 0)
				throw new RuntimeException("invalid nine-patch: " + length);
		}

		public static NinePatchChunk deserialize(final byte[] data) {
			final ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder());

			if (byteBuffer.get() == 0)
				return null; // is not serialized

			final NinePatchChunk chunk = new NinePatchChunk();
			chunk.mDivX = new int[byteBuffer.get()];
			chunk.mDivY = new int[byteBuffer.get()];
			chunk.mColor = new int[byteBuffer.get()];

			checkDivCount(chunk.mDivX.length);
			checkDivCount(chunk.mDivY.length);

			// skip 8 bytes
			byteBuffer.getInt();
			byteBuffer.getInt();

			chunk.mPaddings.left = byteBuffer.getInt();
			chunk.mPaddings.right = byteBuffer.getInt();
			chunk.mPaddings.top = byteBuffer.getInt();
			chunk.mPaddings.bottom = byteBuffer.getInt();

			// skip 4 bytes
			byteBuffer.getInt();

			readIntArray(chunk.mDivX, byteBuffer);
			readIntArray(chunk.mDivY, byteBuffer);
			readIntArray(chunk.mColor, byteBuffer);

			return chunk;
		}
	}

}
