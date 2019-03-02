package shop.classes;

import java.util.ArrayList;

public class Subcategory {
	
	public ArrayList<Item> items; // ����� ������������ �� ��� ������ � ��������
	private String name;
	
	public Subcategory(String name) {
		this.name = name;
		items = new ArrayList<Item>();
	}

	public String getName() { return name; }

}
