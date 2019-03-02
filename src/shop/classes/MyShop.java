package shop.classes;

import java.util.ArrayList;

public class MyShop {
	
	public ArrayList<Category> categories;
	public MyCart cart;
	
	public MyShop() {
		categories = new ArrayList<Category>();
		cart = new MyCart();
	}

}
