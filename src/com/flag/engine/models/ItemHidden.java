package com.flag.engine.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.flag.engine.utils.CalUtils;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "items_hidden")
public class ItemHidden extends BaseModel {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
	private Long id;

	@Persistent
	@Index
	private Long shopId;

	@Persistent
	private String name;

	@Persistent
	private String thumbnailUrl;

	@Persistent
	private String description;

	@Persistent
	private int sex;

	@Persistent
	private int type;

	@Persistent
	private int sale;

	@Persistent
	private String oldPrice;

	@Persistent
	private String price;

	@Persistent
	@Index
	private String barcodeId;

	@Persistent
	private int reward;

	@NotPersistent
	private boolean rewardable;

	@NotPersistent
	private boolean rewarded;

	@NotPersistent
	private int likes;

	@NotPersistent
	private boolean liked;

	public ItemHidden() {
		super();
	}

	public ItemHidden(String[] dataArray) {
		barcodeId = dataArray[0];
		name = dataArray[1];
		description = dataArray[2];
		if (dataArray[4].isEmpty()) {
			price = dataArray[3];
			oldPrice = dataArray[4];
		} else {
			price = dataArray[4];
			oldPrice = dataArray[3];
			sale = CalUtils.discountRate(oldPrice, price);
		}
		reward = (int) CalUtils.toNumber(dataArray[5]);
		thumbnailUrl = "";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSale() {
		return sale;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	public String getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBarcodeId() {
		return barcodeId;
	}

	public void setBarcodeId(String barcodeId) {
		this.barcodeId = barcodeId;
	}

	public int getReward() {
		return reward;
	}

	public boolean isRewardable() {
		return rewardable;
	}

	public void setRewardable(boolean rewardable) {
		this.rewardable = rewardable;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public boolean isRewarded() {
		return rewarded;
	}

	public void setRewarded(boolean rewarded) {
		this.rewarded = rewarded;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public void calSale() {
		if (oldPrice != null && !oldPrice.isEmpty())
			sale = CalUtils.discountRate(oldPrice, price);
	}

	public void update(ItemHidden item) {
		if (item.getName() != null && !item.getName().isEmpty())
			name = item.getName();
		if (item.getThumbnailUrl() != null && !item.getThumbnailUrl().isEmpty())
			thumbnailUrl = item.getThumbnailUrl();
		if (item.getDescription() != null && !item.getDescription().isEmpty())
			description = item.getDescription();
		if (item.getBarcodeId() != null && !item.getBarcodeId().isEmpty())
			barcodeId = item.getBarcodeId();
		if (item.getPrice() != null && !item.getPrice().isEmpty())
			price = item.getPrice();
		reward = item.getReward();

		if (item.getOldPrice() == null)
			oldPrice = "";
		else
			oldPrice = item.getOldPrice();

		if (oldPrice != null && !oldPrice.isEmpty())
			sale = CalUtils.discountRate(oldPrice, price);
		else
			sale = 0;
	}

	@Override
	public boolean equals(Object o) {
		try {
			ItemHidden target = (ItemHidden) o;
			return target.getId() == id
					|| (target.getBarcodeId() != null && !target.getBarcodeId().isEmpty() && barcodeId != null && !barcodeId.isEmpty() && target
							.getBarcodeId().equals(barcodeId));
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return name + " " + barcodeId;
	}
}
