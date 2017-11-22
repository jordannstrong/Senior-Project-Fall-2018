package prototype_MinimalFunctionality;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.mindrot.BCrypt;

import com.alee.laf.WebLookAndFeel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class LoginWindow {

	private JFrame frame;
	private JTextField textField;
	private JPasswordField passwordField;
	private boolean loginSuccess = false;
	private static Connection connection;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConnectionPool.instantiate();
					connection = ConnectionPool.getConnection();
					WebLookAndFeel.install ();
					LoginWindow window = new LoginWindow();
					window.frame.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginWindow() {
		initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		frame = new JFrame("Login Window");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 	  	
					if(new SQLQueryBuilder().checkPassword(textField.getText(), passwordField.getPassword()))
					{
						new PrototypeWindow(textField.getText());
						frame.dispose();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Invalid Username or Password." + "\n" + "Please Try Again.");
						textField.setText("");
						passwordField.setText("");
					}
			  }} );
		
		JLabel lblUsername = new JLabel("Username: ");
		
		JLabel lblPassword = new JLabel("Password: ");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  new Registration();
			  }} );
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(83)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblUsername)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
								.addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnRegister)
								.addPreferredGap(ComponentPlacement.RELATED))
							.addGroup(gl_panel.createSequentialGroup()
								.addGap(13)
								.addComponent(lblPassword)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(57, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(61)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(26)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPassword))
					.addGap(38)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLogin)
						.addComponent(btnRegister))
					.addGap(59))
		);
		panel.setLayout(gl_panel);
	}
	
	public static byte[] charToByte(char[] array) {

		  byte[] result = new byte[array.length];

		  for(int i = 0; i < array.length; i++) {
		    result[i] = (byte) array[i];
		  }
		  return result;
		}
}
