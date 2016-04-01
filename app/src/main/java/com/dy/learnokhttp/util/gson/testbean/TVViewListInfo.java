package com.dy.learnokhttp.util.gson.testbean;

//视频播放页视频对象
public class TVViewListInfo {
	private String title;
	private String description;
	private String vid;
	private int vip;
	private int mall_vip;
	private int quanji;
	private int shunxu;
	private String mall_url;
	private int id;
	private int catid;//与列表信息的catid不一定一致！！！！！！
	//用来标志列表信息的整体catid
	private int lcatid;

	private String url;
	private String thumb;

	//新添腾讯云视频数据
	private String qcloud_id;
	private String qcloud_url;
	private String qcloud_gaoqing_url;

	public void setQcloud_id(String qcloud_id) {
		this.qcloud_id = qcloud_id;
	}

	public String getQcloud_id() {
		return qcloud_id;
	}

	public void setQcloud_url(String qcloud_url) {
		this.qcloud_url = qcloud_url;
	}

	public String getQcloud_url() {
		return qcloud_url;
	}

	public void setQcloud_gaoqing_url(String qcloud_gaoqing_url) {
		this.qcloud_gaoqing_url = qcloud_gaoqing_url;
	}

	public String getQcloud_gaoqing_url() {
		return qcloud_gaoqing_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public int getLcatid() {
		return lcatid;
	}
	public void setLcatid(int lcatid) {
		this.lcatid = lcatid;
	}
	//用来标志列表信息的整体id
	private int lid;
	public int getLid() {
		return lid;
	}
	public void setLid(int lid) {
		this.lid = lid;
	}
	//用来标志列表信息的整体作者
	private String people_name;
	
	public String getPeople_name() {
		return people_name;
	}
	public void setPeople_name(String people_name) {
		this.people_name = people_name;
	}
	//是否收藏 与json数据无关 只是程序内部实现需要
	private boolean isCollected;
	
	public boolean isCollected() {
		return isCollected;
	}
	public void setCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getVip() {
		return vip;
	}
	public void setVip(int vip) {
		this.vip = vip;
	}
	public int getMall_vip() {
		return mall_vip;
	}
	public void setMall_vip(int mall_vip) {
		this.mall_vip = mall_vip;
	}
	public int getQuanji() {
		return quanji;
	}
	public void setQuanji(int quanji) {
		this.quanji = quanji;
	}
	public int getShunxu() {
		return shunxu;
	}
	public void setShunxu(int shunxu) {
		this.shunxu = shunxu;
	}
	public String getMall_url() {
		return mall_url;
	}
	public void setMall_url(String mall_url) {
		this.mall_url = mall_url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCatid() {
		return catid;
	}
	public void setCatid(int catid) {
		this.catid = catid;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	
}
