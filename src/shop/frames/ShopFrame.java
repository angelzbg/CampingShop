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
	    	//������ ������ ��������� ������ � � ��� ����������� �� ������ �������� ���� ��������� � ������ - > ��� �� � �� �������� ����������� ���� � �������
	    	if(shop.cart.items.size() > 0) {
	    		int confirm = JOptionPane.showOptionDialog(null, "������� �� ���, �� ������ �� ��������?\n����� " + shop.cart.items.size() + " �������(�) � ���������.", "������������", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
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
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); // �� �� ������ ��������� �� �������
		width = gd.getDisplayMode().getWidth();
		height = gd.getDisplayMode().getHeight();
		
		// � this ��������� �������� ���� (�����)
		this.setSize(0, 0); // �� ���� ��������� �� this.setMinimumSize ��� ��������� �� ����
		
		this.setTitle("Camping Shop"); // ����� �� ���������
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // �� �� ����� ���� ��� ��������� �� X (��� ���� ��� ���������� �������� ���� �������� NO ��� �� ������� ���������)
		this.addWindowListener(exitListener); // ��� ��������� �� ������ X �� ������� �������� �� ������������
		this.setLayout(new BorderLayout());
		//������� ����, ���� � ����� ��� ������ ��� ��������� -> https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html#flow
		
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
		cartInfo.setText("<html><center>����� 0 �������� � ���������.</center></html>");
		cartInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		cartInfo.setPreferredSize(new Dimension(width/10, width/10));
		cartInfo.setForeground(Color.WHITE); // setForeground �� �������� �� ������� �� ����� �� ������
		cartPanel.add(cartInfo);
		
		cartInfo.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) { // ���� �/� ���������
        		
        		ShopFrame.this.disable(); // ����������� �������� ��������
        		CartFrame modal = new CartFrame(shop.cart);
        		
        		modal.addWindowListener(new WindowAdapter() { // ������ �� ��������� �� ��������� modal
					@Override
					public void windowClosing(WindowEvent e) {
						ShopFrame.this.enable(); // ��� ��������� �� ��������� ����� ������ �� ���������� �������� (������� ��������)
						updateCartInfo(); // ����� � ���������� �� ���������, ������ ��� � ��������� ������� � �������� ��������� �� � ����������
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
		
		this.addComponentListener(new ComponentAdapter() { // bug fix ������ ��� ���������� ������ �� ������ � ����� �� ����
			@Override
			public void componentResized(ComponentEvent arg0) {
				super.componentResized(arg0);
				menuScroll.setPreferredSize(new Dimension(width/10, ShopFrame.this.height-width/10)); // ������ ������� �������� � ���������, �������� ������� ������ ����� ���������
				menuScroll.revalidate();
				menuScroll.repaint();
			}
		});
		
	}// ���� �� ������������
	
	
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
		 * ! = ���������
		 * @ = ������������
		 *   = ������� 
		 * $ = ���������� �/� ��� �� ��������, ������ � ����� �� ����������
		 */
		
		JPanel list = new JPanel(); // ��� �� �� ������� ���������� �� ��������� � ������������
		while(sc.hasNextLine()) {
        	String line = sc.nextLine();
            //System.out.println(line);
           
        	if(line.charAt(0) == '!') // ��� ������� ������ � ���������� ����� � ���������
        	{
        		String name = line.substring(1); // ��� ������ ������
        		shop.categories.add(new Category(name)); //�������� �����������
        		
        		JLabel labelCategory = new JLabel("<html>" + name + "</html>");
        		labelCategory.setForeground(Color.decode("#7d877e"));
        		labelCategory.setFont(new Font(labelCategory.getFont().getName(), Font.BOLD, labelCategory.getFont().getSize()));
        		list.add(labelCategory);
        		list.add(Box.createRigidArea(new Dimension(0, 5)));
        	}
        	else if(line.charAt(0) == '@') // ��� ������� ������ � @ ����� ������������
        	{
        		String name = line.substring(1); // ��� ������ ������
        		shop.categories.get(shop.categories.size()-1).subs.add(new Subcategory(name));
        	
        		JLabel labelSub = new JLabel("<html>" + name + "</html>");
        		labelSub.setForeground(Color.decode("#09260a")); // ����� ����� ����� ���� �� ������
        		list.add(labelSub);
        		list.add(Box.createRigidArea(new Dimension(0, 5)));
                
        		labelSub.addMouseListener(new MouseAdapter() {
                	@Override
                    public void mouseClicked(MouseEvent e) {
                		showListFromSubcategory(name); // ��� ��������� �� JLabel-� � ����� �� �������������� ������� � ������, ��������� �� ��� �� ��������������
                    }
                	@Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        labelSub.setForeground(Color.decode("#995521")); // ���������� ������ ������� � �/� ����������� � ����-��
                        labelSub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e); 
                        labelSub.setForeground(Color.decode("#09260a")); // ������� �� ����� ����� ���� ���� ������� � ������� �� ����������� � ����-��
                        labelSub.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                });
        	}
        	else // ������� ������ �� � ���� ! ���� @ -> ����� � �������
        	{
        		String[] parts = line.split("<split>"); //��������� ������� ��� ���������� <split> � ���������� 3 �����
        		String NAME = parts[0];
        		Double PRICE = Double.parseDouble(parts[1]);
        		String IMAGE = parts[2];
        		final int indexCategory = shop.categories.size() - 1, indexSubcategory = shop.categories.get(indexCategory).subs.size() - 1; //���������� �� �� ������ �� � �������� �������� � �� ��� ��������� � ������������ ����� ������ ������� �� �� ���� ArrayIndexOutOfBoundsException xD
        		shop.categories.get( indexCategory ).subs.get(indexSubcategory).items.add( new Item(NAME, PRICE, IMAGE) ); // �������� �������� � ����������_���������.����������_������������
        	}
        }//end of While Loop
		        
		BoxLayout boxLayout = new BoxLayout(list, BoxLayout.Y_AXIS);
		list.setLayout(boxLayout);
		list.setBackground(Color.decode("#6ed38a")); // ������ ����� ���������
		menuScroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // ������ �� ������� �� ������ list
		menuScroll.getVerticalScrollBar().setUnitIncrement(16);
		leftPanel.add(BorderLayout.CENTER, menuScroll);// ������ �� ������ � ������, � �� �����
		
		this.setVisible(true);
		this.setMinimumSize(new Dimension(width/10+width/6+this.getInsets().left+this.getInsets().right+20, (width/12)*3+this.getInsets().top*2)); // ����������� ������������ � � ������ ������+�������� �� ���� ����� � ���������� �� �������, � ���������� � ���� 3 ��������
		// insets �� ������� �� ���������, + ~20px ������ �������� �� ��������, ����� ����� �����
		this.setLocation((width-this.getWidth())/2, (height-this.getHeight())/2); // ������ ��������� ��������� �� ������
		
		leftPanel.remove(cartPanel); // ���� �� ���� ������ �� ������, �� �� ������� ���� �� ������� �������� � �� �� �������� ������ ��������� �� �����
		
	}// ���� �� openShop()
	
	private void showListFromSubcategory(String subcategoryName) {
		contentPanel.removeAll(); // ������ Content Panela �� �������� �������� ���������� (�������� ��������)
		
		this.setTitle("Camping Shop: " + subcategoryName);
		
		JPanel panelITEMS = new JPanel(); // ��� �� ������ ��������, � ����� �� ��������� ������������ �� ����������
		panelITEMS.setBackground(Color.LIGHT_GRAY);
		panelITEMS.setLayout(new ModifiedFlowLayout());
		
		ArrayList<Item> items = null;
		
		// �� ������ ��������������
		for(Category c : shop.categories) {
			for(Subcategory s : c.subs) {
				if(s.getName().equals(subcategoryName)) { // ��������� ���� �� ������ ��������������
					items = s.items; // ������� ��������� � ���������� �� ��������������
					break; // ����������� ������, �� �� �� ������� ������������ -> ���� ��� ����� ���� �� ������� ����������� ��-������, ���� � ��������
				}
			}
		}
		
		for(Item item : items) { // ����� foreach (�������� ���� ������ �������� �� ��������� � ���� ������������)

			JPanel panelItem = new JPanel(); // ��� ����� ���������� + ������ �� ��������
			//panelItem.setSize(height/12, height/6);
			panelItem.setSize(width/6, width/12);
			panelItem.setLayout(new GridLayout(1, 2)); // 1 ��� : 2 ������
			
			JLabel imageLabelLeft = new JLabel(new ImageIcon(new ImageIcon("src/" + item.getImg()).getImage().getScaledInstance(panelItem.getHeight(), panelItem.getHeight(), Image.SCALE_DEFAULT)));
			imageLabelLeft.setSize(panelItem.getHeight(), panelItem.getHeight());
			imageLabelLeft.setBorder(BorderFactory.createLineBorder(Color.decode("#6d9957"))); // ����� ����� ������ �� ����������
			panelItem.add(imageLabelLeft);
			
			JPanel rightPanel = new JPanel();
			rightPanel.setSize(panelItem.getWidth()/2, panelItem.getHeight());
			rightPanel.setBackground(Color.decode("#cef2bc")); // ������ �����
			rightPanel.setLayout(new GridLayout(3,1)); // 3 ���� : 1 ������
			panelItem.add(rightPanel);
			
			JLabel productName = new JLabel("<html>" + item.getName() + "</html>", SwingConstants.CENTER);
			productName.setForeground(Color.BLACK);
			productName.setPreferredSize(new Dimension(panelItem.getWidth()/2, 0));
			productName.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.decode("#6d9957"))); // ���������� ������ ����� �����, �� ���� ����,���� � �����
			rightPanel.add(productName);
			
			
			JLabel productPrice = new JLabel("<html>����: " + (new DecimalFormat("#.00")).format(item.getPrice()) + " ��.</html>", SwingConstants.CENTER); // �� 2 ����� ���� ���������
			productPrice.setForeground(Color.decode("#3b6d40")); // ����� �����
			productPrice.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#6d9957"))); // ���������� ������ ����� ������, �� ���� ���� � �����
			rightPanel.add(productPrice);
			
			JPanel bottomPanel = new JPanel();
			bottomPanel.setBackground(Color.decode("#e58c27")); // ����� �� ���������� rgb(229, 140, 39)
			bottomPanel.setLayout(new GridLayout(1,2)); // 1 ��� : 2 ������
			bottomPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#6d9957"))); // ���������� ������ ����� ������ �����, �� ���� ���� � �����
			rightPanel.add(bottomPanel);
			
			JPanel SpinnerWrap = new JPanel();
			SpinnerWrap.setBackground(Color.decode("#e58c27"));; // ����� �� ���������� rgb(229, 140, 39)
			SpinnerWrap.setLayout(new GridBagLayout());
			
			JSpinner quantitySpinner = new JSpinner(); // ������� � ��� ��������� �� ����������� � �������� �� ������� � ����
			quantitySpinner.setModel(new SpinnerNumberModel(1,1,100,1)); // ������������ �������� � 100, ����������� � 1, ��� ��������� +-1 � ������� �� 1
			GridBagConstraints gbc = new GridBagConstraints(); // �� �� ���������� ���������� � �������
			SpinnerWrap.add(quantitySpinner, gbc);
			
			bottomPanel.add(SpinnerWrap);
			
			JLabel imageLabelAdd = new JLabel(new ImageIcon(new ImageIcon("src/add_to_cart.png").getImage().getScaledInstance(imageLabelLeft.getHeight()/3, imageLabelLeft.getHeight()/3, Image.SCALE_DEFAULT)));
			bottomPanel.add(imageLabelAdd);
			
			imageLabelAdd.addMouseListener(new MouseAdapter() { // ���� ����� �/� ���������� �� ��������
            	@Override
                public void mouseClicked(MouseEvent e) { // ������ �� �������/������� ������� � ���������
            		Integer selectedQuantity = (Integer) quantitySpinner.getValue();
                	
                	for(int i=0; i<shop.cart.items.size(); i++) { // ������ �� �� ������� ���� �������� ���� �� �� ��� � ���������
                		if(shop.cart.items.get(i).equals(item)) { // ��� �������� ���� � � ���������
                			int newQuantity = shop.cart.quantity.get(i)+selectedQuantity;
                			shop.cart.quantity.set(i, newQuantity); // ���������� �������� �� ��������
                			updateCartInfo();
                			return; // �������� � ������������, ����� �� ������� �� ��������� ��� �� ������������ ���������� ������
                		}
                	}
                	
                	//��� �� ��� ������� �� ������, ����� ������ ������ �� ������� �������� + ������������:
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
			
			panelITEMS.add(panelItem); // �������� � panelITEMS
		}
		
		JScrollPane scrollContent = new JScrollPane(panelITEMS, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // ������ �� ������� �� ������ lanelITEMS
		scrollContent.getVerticalScrollBar().setUnitIncrement(16);
		contentPanel.add(BorderLayout.CENTER, scrollContent);
		contentPanel.revalidate();
		contentPanel.repaint();
		
	}// ���� �� showListFromSubcategory()
	
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
			//��� ������ �� ������� ������������ �� ���� ����� �������� ��� �����������
			cartInfo.setText("<html><center>����� " + shop.cart.items.size() + " �������(�)<br>� ���������.</center></html>");
		}
		//�������� ��������� -> ������� �� ��� �� ������ �� ������ ��� �������� ������� � ���������, ������ �� ����������� ������, ������ �������� �� �� ��������
		leftPanel.revalidate();
		leftPanel.repaint();
	}

}// ���� �� �����
