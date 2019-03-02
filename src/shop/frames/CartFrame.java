package shop.frames;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import shop.classes.MyCart;

public class CartFrame extends JFrame {
	
	private MyCart cart;
	private double finalSum = 0;
	
	public CartFrame(MyCart cart) {
		
		//GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); // �� �� ������ ��������� �� �������
		//int width = gd.getDisplayMode().getWidth();
		//int height = gd.getDisplayMode().getHeight();
		
		this.cart = cart;
		
		this.setTitle("Model window");
		this.setLayout(new BorderLayout());
		
		Object[][] data = new Object[cart.items.size()][4]; // 4 -> Product name, number, price, overall
		double sum = 0;
		for(int i=0; i<cart.items.size(); i++) {
			data[i][0] = cart.items.get(i).getName();
			data[i][1] = new Integer(cart.quantity.get(i));
			data[i][2] = new DecimalFormat("#.00").format(new Double(cart.items.get(i).getPrice())); // ����������� ���� �� 2 ����� ���� ���������
			data[i][3] = new DecimalFormat("#.00").format(new Double(cart.quantity.get(i)*cart.items.get(i).getPrice())); // ����*����
			
			sum+=cart.quantity.get(i)*cart.items.get(i).getPrice();
		}
		finalSum = sum; // �� �� ����� �� �� ������� � ����� ��������
		
		JTable table = new JTable(data, new String[]{"Product name", "Number", "Price", "Overall"});
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		this.add(BorderLayout.CENTER, scrollPane);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(2,1)); // 2 ����, 1 ������
		
		JLabel totalPrice = new JLabel("Overall " + new DecimalFormat("#.00").format(finalSum) + " ��.", SwingConstants.RIGHT); // � �����
		bottomPanel.add(totalPrice); // ����� �� ������ ���
		
		JPanel buttonsWrapper = new JPanel();
		buttonsWrapper.setLayout(new GridLayout(1,2)); // 1 ���, 2 ������
		
		JButton payCard = new JButton("Pay via Card");
		JButton payPal = new JButton("Pay via PayPal");
		
		buttonsWrapper.add(payCard); // ������ payCard ����� � ������� ������ �� buttonsWrapper
		buttonsWrapper.add(payPal); // ������ payPal ����� ��� ������� ������ �� buttonsWrapper
		
		bottomPanel.add(buttonsWrapper); // buttonWrapper ����� �� ������ ��� �� bottomPanel
		
		this.add(BorderLayout.PAGE_END, bottomPanel); // ������ � �������� ����� ���-���� ��� ������
		
		this.pack();
		this.setLocationRelativeTo(null); // �� �� �� ���������� �� ������
		this.setVisible(true);
		
		//MouseEventListeners
		payCard.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
        		performOrder();
            }
        	@Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                payCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e); 
                payCard.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
		
		payPal.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
        		performOrder();
            }
        	@Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                payPal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e); 
                payPal.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
		
	}// end CartFrame()
	
	private void performOrder() {
		
		//������ �� ������ �� ��������� � ����������� ��� ����� bill.txt, ����� ����� � ������� �� �������
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("bill.txt", "UTF-8"); // ������ ����� � [<workspace dir>/CampingShop/]
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return;
		}

		String name="Name", number="Number", price = "Price", overall="Overall";
		writer.println("Products:");
		writer.printf("%-50s %-10s %-20s %-20s\n", name, number, price, overall);
		//writer.println("\nName\tQuantity\tPrice\tOverall");
		for(int i=0; i<cart.items.size(); i++) {
			writer.printf("%-50s %-10s %-20s %-20s\n", cart.items.get(i).getName(), cart.quantity.get(i).toString(), new DecimalFormat("#.00").format(cart.items.get(i).getPrice()), new DecimalFormat("#.00").format(cart.quantity.get(i)*cart.items.get(i).getPrice()));
		}
		writer.println("\n\nOverall: " + new DecimalFormat("#.00").format(finalSum) + " ��.");
		writer.close();
		
		//���������� �� ��������� � ��������� �� ���������
		cart.clearCart();
		CartFrame.this.dispatchEvent(new WindowEvent(CartFrame.this, WindowEvent.WINDOW_CLOSING));
	}

}//end class