package com.dy.learnokhttp.util;

/**
 * Created by Dyan on 2016/3/29.
 * 使用说明：
 * 1、application中定义
 */
public class CacheType {
	public static final int ONLY_NETWORK=0;//  只查询网络数据
	public static final int ONLY_CACHED=1;//只查询本地缓存
	public static final int CACHED_ELSE_NETWORK=2;//  先查询本地缓存，如果本地没有，再查询网络数据
	public static final int NETWORK_ELSE_CACHED=3;//  先查询网络数据，如果没有，再查询本地缓存
	public static final int CACHED_THEN_NETWORK=4;// 先查询本地缓存，再查询网络数据 , 网络更新本地的效果

}
