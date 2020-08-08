package com.quas.mesozoicisland.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.ShopType;
import com.quas.mesozoicisland.util.Pair;

public class ShopItem {

	private int id;
	private String name;
	private Item buyitem;
	private long buycount;
	private Item payitem;
	private long paycount;
	private boolean player;
	private long totalstock;
	private long playerstock;
	private boolean visible;
	private ShopType type;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Item getBuyItem() {
		return buyitem;
	}
	
	public long getBuyCount() {
		return buycount;
	}
	
	public Item getPayItem() {
		return payitem;
	}
	
	public long getPayCount() {
		return paycount;
	}
	
	public boolean isPlayerSpecific() {
		return player;
	}
	
	public long getTotalStock() {
		return totalstock;
	}
	
	public long getPlayerStock(long player) {
		if (isPlayerSpecific()) {
			long purchase = 0;

			try (ResultSet res = JDBC.executeQuery("select * from purchases where shopid = %d and player = %d;", id, player)) {
				if (res.next()) {
					purchase = res.getInt("count");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if (getTotalStock() == -1) return playerstock - purchase;
			if (playerstock == -1) return -1;
			return Math.min(playerstock - purchase, getTotalStock());
		}
		
		return getTotalStock();
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public ShopType getShopType() {
		return type;
	}
	
	////////////////////////////////////
	
	public static ShopItem of(int id) {
		try (ResultSet res = JDBC.executeQuery("select * from shop where shopid = %d;", id)) {
			if (res.next()) {
				ShopItem i = new ShopItem();
				i.id = res.getInt("shopid");
				i.name = res.getString("name");
				i.buyitem = Item.getItem(new Pair<Integer, Long>(res.getInt("buyitemid"), res.getLong("buyitemdmg")));
				i.buycount = res.getInt("buycount");
				i.payitem = Item.getItem(new Pair<Integer, Long>(res.getInt("payitemid"), res.getLong("payitemdmg")));
				i.paycount = res.getInt("paycount");
				i.player = res.getBoolean("player");
				i.totalstock = res.getLong("totalstock");
				i.playerstock = res.getLong("playerstock");
				i.visible = res.getBoolean("visible");
				i.type = ShopType.of(res.getInt("shop"));
				return i;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ShopItem[] values() {
		ArrayList<ShopItem> shopitems = new ArrayList<ShopItem>();
		
		try (ResultSet res = JDBC.executeQuery("select shopid from shop order by shopid;")) {
			while (res.next()) {
				shopitems.add(of(res.getInt("shopid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return shopitems.toArray(new ShopItem[0]);
	}
}
