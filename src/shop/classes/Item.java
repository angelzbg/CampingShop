package shop.classes;

public class Item {
	
	private String name, img;
	private double price;
	
	public Item(String name, double price, String img) { // Конструктор, който да приема всички параметри на продукта при създаване на инстанция
		this.name = name;
		this.img = img;
		this.price = price;
	}
	
	//Getters
	public String getName() { return name; }
	public String getImg() {return img; }
	public double getPrice() { return price; }

}
