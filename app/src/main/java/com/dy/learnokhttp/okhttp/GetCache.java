package com.dy.learnokhttp.okhttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.okhttp.internal.DiskLruCache;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.io.FileSystem;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.Scanner;

import okio.BufferedSource;
import okio.Okio;

/**
 * Created by Dyan on 2016/3/30.
 */
public class GetCache {
	public static String getFromCache(String url) throws Exception {
				DiskLruCache cache = DiskLruCache.create(FileSystem.SYSTEM, OkHttpUtil.CACHE_DIRECTORY,
				201105, 2, OkHttpUtil.SIZE_OF_CACHE);
		cache.flush();
		String key = Util.md5Hex(url);
		final DiskLruCache.Snapshot snapshot;
		try {
			snapshot = cache.get(key);
			if (snapshot == null) {
				return null;
			}
		} catch (IOException e) {
			return null;
		}
		okio.Source source = snapshot.getSource(1) ;
		BufferedSource metadata = Okio.buffer(source);
		FilterInputStream bodyIn = new FilterInputStream(metadata.inputStream()) {
			@Override
			public void close() throws IOException {
				snapshot.close();
				super.close();
			}
		};

		Scanner sc = null;
		try {
			sc = new Scanner(bodyIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuilder str= new StringBuilder();
		String s;
		while(sc.hasNext() && (s=sc.nextLine())!=null) {
			str.append(s);
		}
		return str.toString() ;
	}

	/**
	 * Created by Dyan on 2016/3/30.
	 */
	public static class Utils {
		public  static boolean isNetworkAvailable(Context context)
		{
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			NetworkInfo network = cm.getActiveNetworkInfo();
			if (network != null) {
				return network.isAvailable();
			}
			return false;
		}

	}
}
