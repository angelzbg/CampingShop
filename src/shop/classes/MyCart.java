package shop.classes;

import java.util.ArrayList;

public class MyCart {
	
	// Количката има списък с продукти както и списък със количеството за съответния индекс на списъка с продукти
	
	public ArrayList<Item> items;
	public ArrayList<Integer> quantity;
	
	public MyCart() {
		items = new ArrayList<Item>();
		quantity = new ArrayList<Integer>();
	}
	
	public void clearCart() {
		items.clear();
		quantity.clear();
	}

}
