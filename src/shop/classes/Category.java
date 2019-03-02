package shop.classes;

import java.util.ArrayList;

public class Category {
	
	private String name;
	public ArrayList<Subcategory> subs; // ����� ��������� �� ��� ���� �� ������������
	
	public Category(String name) {
		this.name = name;
		subs = new ArrayList<Subcategory>();
	}

	public String getName() { return name; }

}
