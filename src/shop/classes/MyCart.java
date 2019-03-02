package shop.classes;

import java.util.ArrayList;

public class MyCart {
	
	// ��������� ��� ������ � �������� ����� � ������ ��� ������������ �� ���������� ������ �� ������� � ��������
	
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
