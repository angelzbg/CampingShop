package shop.frames;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import shop.classes.*;
import shop.layouts.ModifiedFlowLayout;

public class ShopFrame extends JFrame {
	
	WindowListener exitListener = new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e)
	    {
	    	//следим ивента натискане бутона Х и при натискането му правим проверка дали количката е празна - > ако не е ще попитаме потребителя дали е сигурен
	    	if(shop.cart.items.size() > 0) {
	    		int confirm = JOptionPane.showOptionDialog(null, "Сигурни ли сте, че искате да излезете?\nИмате " + shop.cart.items.size() + " продукт(и) в количката.", "Потвърждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		        if (confirm == 0) System.exit(0);
	    	} else {
	    		System.exit(0);
	    	}
	    }
	    
	};
	
	private MyShop shop;
	
	private JPanel leftPanel = new JPanel(), contentPanel = new JPanel();
	private JPanel cartPanel;
	private JLabel cartInfo;
	private JScrollPane menuScroll;
	
	private int width, height;
	public ShopFrame() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); // за да вземем метриките на дисплея
		width = gd.getDisplayMode().getWidth();
		height = gd.getDisplayMode().getHeight();
		
		// с this посочваме сегашния клас (фрейм)
		this.setSize(0, 0); // ще бъде променена от this.setMinimumSize при зареждане на шопа
		
		this.setTitle("Camping Shop"); // името на прозореца
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // да не прави нищо при натискане на X (без това при изскачащия прозорец като натиснем NO пак ще затвори прозореца)
		this.addWindowListener(exitListener); // при натискане на бутона X да изскочи прозорче за потвърждение
		this.setLayout(new BorderLayout());
		//Относно къде, защо и какъв вид лейаут съм използвал -> https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#flow
		
		/* ----- leftPanel ----- */
		leftPanel.setBackground(Color.BLUE);
		this.add(BorderLayout.LINE_START,leftPanel);
		leftPanel.setPreferredSize(new Dimension(width/10, 0));
		leftPanel.setLayout(new BorderLayout());
		
		/* ----- ContentPanel ----- */
		contentPanel.setBackground(Color.LIGHT_GRAY);
		contentPanel.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, contentPanel);
		
		JLabel welcomeMessage = new JLabel("<html><center>WELCOME<br>Please choose a category!</center></html>", SwingConstants.CENTER);
		contentPanel.add(BorderLayout.CENTER, welcomeMessage);
		
		//cartPanel
		cartPanel = new JPanel();
		cartPanel.setLayout(new GridLayout(1,1));
		
		leftPanel.add(BorderLayout.PAGE_START, cartPanel);
			
		cartInfo = new JLabel(new ImageIcon(new ImageIcon("src/cart_background.png").getImage().getScaledInstance(width/10, width/10, Image.SCALE_DEFAULT)));
		cartInfo.setText("<html><center>Имате 0 продукти в количката.</center></html>");
		cartInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		cartInfo.setPreferredSize(new Dimension(width/10, width/10));
		cartInfo.setForeground(Color.WHITE); // setForeground се използва за промяна на цвета на текста
		cartPanel.add(cartInfo);
		
		cartInfo.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) { // клик в/у количката
        		
        		ShopFrame.this.disable(); // замразяваме текущият прозорец
        		CartFrame modal = new CartFrame(shop.cart);
        		
        		modal.addWindowListener(new WindowAdapter() { // следим за затваряне на прозореца modal
					@Override
					public void windowClosing(WindowEvent e) {
						ShopFrame.this.enable(); // при затваряне на прозореца можем отново да използваме магазина (текущия прозорец)
						updateCartInfo(); // нужно е обновяване на количката, понеже ако е извършено плащане в модалния количката се е изпразнила
					}
				});
            }
        	@Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                cartInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e); 
                cartInfo.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
		
		//-----
		openShop();
		
		this.addComponentListener(new ComponentAdapter() { // bug fix понеже при рисайзване скрола на менюто в дясно се чупи
			@Override
			public void componentResized(ComponentEvent arg0) {
				super.componentResized(arg0);
				menuScroll.setPreferredSize(new Dimension(width/10, ShopFrame.this.height-width/10)); // ширина колкото напанела с количката, височина колкото фрейма минус количката
				menuScroll.revalidate();
				menuScroll.repaint();
			}
		});
		
	}// край на конструктора
	
	
	private void openShop() {
		shop = new MyShop();
		
		File file = new File("src/data.txt");
        Scanner sc = null;
		try { sc = new Scanner(file); }
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * ! = категория
		 * @ = подкатегория
		 *   = продукт 
		 * $ = разделител м/у име на продукта, цената и името на картинката
		 */
		
		JPanel list = new JPanel(); // тук ще се заредят текстовете на категории и подкатегории
		while(sc.hasNextLine()) {
        	String line = sc.nextLine();
            //System.out.println(line);
           
        	if(line.charAt(0) == '!') // ако първият символ е удивителен значи е категория
        	{
        		String name = line.substring(1); // без първия символ
        		shop.categories.add(new Category(name)); //добавяме категорията
        		
        		JLabel labelCategory = new JLabel("<html>" + name + "</html>");
        		labelCategory.setForeground(Color.decode("#7d877e"));
        		labelCategory.setFont(new Font(labelCategory.getFont().getName(), Font.BOLD, labelCategory.getFont().getSize()));
        		list.add(labelCategory);
        		list.add(Box.createRigidArea(new Dimension(0, 5)));
        	}
        	else if(line.charAt(0) == '@') // ако първият символ е @ имаме подкатегория
        	{
        		String name = line.substring(1); // без първия символ
        		shop.categories.get(shop.categories.size()-1).subs.add(new Subcategory(name));
        	
        		JLabel labelSub = new JLabel("<html>" + name + "</html>");
        		labelSub.setForeground(Color.decode("#09260a")); // много тъмно зелен цвят на текста
        		list.add(labelSub);
        		list.add(Box.createRigidArea(new Dimension(0, 5)));
                
        		labelSub.addMouseListener(new MouseAdapter() {
                	@Override
                    public void mouseClicked(MouseEvent e) {
                		showListFromSubcategory(name); // при натискане на JLabel-а с текст на подкатегорията отиваме в метода, подавайки му име на подкатегорията
                    }
                	@Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        labelSub.setForeground(Color.decode("#995521")); // кафеникаво докато мишката е в/у категорията в меню-то
                        labelSub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e); 
                        labelSub.setForeground(Color.decode("#09260a")); // връщаме на тъмно зелен след като мишката е махната от категорията в меню-то
                        labelSub.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                });
        	}
        	else // първият символ не е нито ! нито @ -> значи е продукт
        	{
        		String[] parts = line.split("<split>"); //разделяме стринга при комбинация <split> и получаваме 3 части
        		String NAME = parts[0];
        		Double PRICE = Double.parseDouble(parts[1]);
        		String IMAGE = parts[2];
        		final int indexCategory = shop.categories.size() - 1, indexSubcategory = shop.categories.get(indexCategory).subs.size() - 1; //предполага се че файлът ще е попълнен правилно и ще има категория и подкатегория преди първия продукт за да няма ArrayIndexOutOfBoundsException xD
        		shop.categories.get( indexCategory ).subs.get(indexSubcategory).items.add( new Item(NAME, PRICE, IMAGE) ); // добавяме предмета в последната_категория.последната_подкатегория
        	}
        }//end of While Loop
		        
		BoxLayout boxLayout = new BoxLayout(list, BoxLayout.Y_AXIS);
		list.setLayout(boxLayout);
		list.setBackground(Color.decode("#6ed38a")); // средно зелен бекграунд
		menuScroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // скрола ще скролва по панела list
		menuScroll.getVerticalScrollBar().setUnitIncrement(16);
		leftPanel.add(BorderLayout.CENTER, menuScroll);// скрола се добавя в панела, а не листа
		
		this.setVisible(true);
		this.setMinimumSize(new Dimension(width/10+width/6+this.getInsets().left+this.getInsets().right+20, (width/12)*3+this.getInsets().top*2)); // минамалното оразмеряване е с ширина менюто+ширината на един панел с информация за продукт, а височината е поне 3 продукта
		// insets са рамките на прозореца, + ~20px заради ширината на скролера, който влиза вътре
		this.setLocation((width-this.getWidth())/2, (height-this.getHeight())/2); // местим прозореца централно на екрана
		
		leftPanel.remove(cartPanel); // нека си стои скрита по начало, ще се показва като се добавят продукти и ще се премахва когато количката се чисти
		
	}// края на openShop()
	
	private void showListFromSubcategory(String subcategoryName) {
		contentPanel.removeAll(); // чистим Content Panela от неговите предишни компоненти (визуални елементи)
		
		this.setTitle("Camping Shop: " + subcategoryName);
		
		JPanel panelITEMS = new JPanel(); // тук ще отиват панелите, в които ще зареждаме информацията за продуктите
		panelITEMS.setBackground(Color.LIGHT_GRAY);
		panelITEMS.setLayout(new ModifiedFlowLayout());
		
		ArrayList<Item> items = null;
		
		// Ще търсим подкатегорията
		for(Category c : shop.categories) {
			for(Subcategory s : c.subs) {
				if(s.getName().equals(subcategoryName)) { // намерихме къде се намира подкатегорията
					items = s.items; // взимаме арейлиста с продуктите на подкатегорията
					break; // приключваме цикъла, за да не забвяме изпълнението -> така или иначе няма да намерим категорията по-надолу, вече е намерена
				}
			}
		}
		
		for(Item item : items) { // цикъл foreach (минаваме през всички елементи на арейлиста в тази подкатегория)

			JPanel panelItem = new JPanel(); // тук идват картинката + инфото за продукта
			//panelItem.setSize(height/12, height/6);
			panelItem.setSize(width/6, width/12);
			panelItem.setLayout(new GridLayout(1, 2)); // 1 ред : 2 колони
			
			JLabel imageLabelLeft = new JLabel(new ImageIcon(new ImageIcon("src/" + item.getImg()).getImage().getScaledInstance(panelItem.getHeight(), panelItem.getHeight(), Image.SCALE_DEFAULT)));
			imageLabelLeft.setSize(panelItem.getHeight(), panelItem.getHeight());
			imageLabelLeft.setBorder(BorderFactory.createLineBorder(Color.decode("#6d9957"))); // тъмно зелен бордър на картинката
			panelItem.add(imageLabelLeft);
			
			JPanel rightPanel = new JPanel();
			rightPanel.setSize(panelItem.getWidth()/2, panelItem.getHeight());
			rightPanel.setBackground(Color.decode("#cef2bc")); // светло зелен
			rightPanel.setLayout(new GridLayout(3,1)); // 3 реда : 1 колона
			panelItem.add(rightPanel);
			
			JLabel productName = new JLabel("<html>" + item.getName() + "</html>", SwingConstants.CENTER);
			productName.setForeground(Color.BLACK);
			productName.setPreferredSize(new Dimension(panelItem.getWidth()/2, 0));
			productName.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.decode("#6d9957"))); // тъмнозелен бордър около името, но само горе,долу и дясно
			rightPanel.add(productName);
			
			
			JLabel productPrice = new JLabel("<html>Цена: " + (new DecimalFormat("#.00")).format(item.getPrice()) + " лв.</html>", SwingConstants.CENTER); // до 2 цифри след запетаята
			productPrice.setForeground(Color.decode("#3b6d40")); // зелен нюанс
			productPrice.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#6d9957"))); // тъмнозелен бордър около цената, но само долу и дясно
			rightPanel.add(productPrice);
			
			JPanel bottomPanel = new JPanel();
			bottomPanel.setBackground(Color.decode("#e58c27")); // нюанс на оранжевото rgb(229, 140, 39)
			bottomPanel.setLayout(new GridLayout(1,2)); // 1 ред : 2 колони
			bottomPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#6d9957"))); // тъмнозелен бордър около долния панел, но само долу и дясно
			rightPanel.add(bottomPanel);
			
			JPanel SpinnerWrap = new JPanel();
			SpinnerWrap.setBackground(Color.decode("#e58c27"));; // нюанс на оранжевото rgb(229, 140, 39)
			SpinnerWrap.setLayout(new GridBagLayout());
			
			JSpinner quantitySpinner = new JSpinner(); // поленце с две бутончета за увеличаване и намаляне на числото в него
			quantitySpinner.setModel(new SpinnerNumberModel(1,1,100,1)); // максималната стойност е 100, минималната е 1, при антискане +-1 и започва от 1
			GridBagConstraints gbc = new GridBagConstraints(); // за да центрираме продуктите в лейаута
			SpinnerWrap.add(quantitySpinner, gbc);
			
			bottomPanel.add(SpinnerWrap);
			
			JLabel imageLabelAdd = new JLabel(new ImageIcon(new ImageIcon("src/add_to_cart.png").getImage().getScaledInstance(imageLabelLeft.getHeight()/3, imageLabelLeft.getHeight()/3, Image.SCALE_DEFAULT)));
			bottomPanel.add(imageLabelAdd);
			
			imageLabelAdd.addMouseListener(new MouseAdapter() { // маус ивент в/у картинката за добавяне
            	@Override
                public void mouseClicked(MouseEvent e) { // трябва да добавим/обновим предмет в кошницата
            		Integer selectedQuantity = (Integer) quantitySpinner.getValue();
                	
                	for(int i=0; i<shop.cart.items.size(); i++) { // трябва да се провери дали продукта вече не го има в кошницата
                		if(shop.cart.items.get(i).equals(item)) { // ако продукта вече е в кошницата
                			int newQuantity = shop.cart.quantity.get(i)+selectedQuantity;
                			shop.cart.quantity.set(i, newQuantity); // обновяваме бройката на продукта
                			updateCartInfo();
                			return; // добавено е количеството, можем да излезем от функцията без да продължаваме изпълнение надолу
                		}
                	}
                	
                	//щом не сме излезли от цикъла, значи просто трябва да добавим продукта + количеството:
                	shop.cart.items.add(item);
                	shop.cart.quantity.add(selectedQuantity);
                	updateCartInfo();
                }
            	@Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    imageLabelAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e); 
                    imageLabelAdd.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            });
			
			panelITEMS.add(panelItem); // добавяне в panelITEMS
		}
		
		JScrollPane scrollContent = new JScrollPane(panelITEMS, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // скрола ще скролва по панела lanelITEMS
		scrollContent.getVerticalScrollBar().setUnitIncrement(16);
		contentPanel.add(BorderLayout.CENTER, scrollContent);
		contentPanel.revalidate();
		contentPanel.repaint();
		
	}// края на showListFromSubcategory()
	
	private boolean isCartShown = false;
	private void updateCartInfo() {
		if(shop.cart.items.size() == 0) {
			if(isCartShown) {
				leftPanel.remove(cartPanel);
				isCartShown = false;
			}
		} else {
			if(!isCartShown) {
				leftPanel.add(BorderLayout.PAGE_START, cartPanel);
				isCartShown = true;
			}
			//тук трябва да обновим информацията за това колко продукта има потребителя
			cartInfo.setText("<html><center>Имате " + shop.cart.items.size() + " продукт(и)<br>в количката.</center></html>");
		}
		//Фиксваме бъгването -> въпреки че сме му казали да добави или премахне лейбъла с количката, трябва да прерисуваме панела, понеже визуално не се обновява
		leftPanel.revalidate();
		leftPanel.repaint();
	}

}// края на класа
