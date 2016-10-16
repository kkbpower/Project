package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import component.IPTextField;
import component.PlaceholderTextField;


public class AMMain extends JFrame{
	final static String DEFAULTPORT = "7531";
	final static String NEWPORT = "7532";
	static String id;
	static String pw;
	static String phoneIP;
	static String myPhoneNum;
	static Boolean isLogFlag;
	
	PlaceholderTextField idTextField;
	PlaceholderTextField pwTextField;
	IPTextField phoneIPTextField;
	JCheckBox isLogCheckBox;
	static JButton autoLogin;
	JButton login;
	boolean autoLoginFlag;
	
	public AMMain(){
		super("Alternative MUC");
		setLayout(new GridLayout(1,1));
		setBounds(100, 100, 300, 270);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = this.getContentPane();
		
		JPanel pane = new JPanel();
		JPanel idPanel = new JPanel();
		JPanel pwPanel = new JPanel();
		JPanel ipPanel = new JPanel();
		JPanel autoLoginPanel = new JPanel();
		JLabel ipLabel = new JLabel("IP 주소    ");

		idTextField = new PlaceholderTextField();
		pwTextField = new PlaceholderTextField();
		phoneIPTextField = new IPTextField("");
		autoLogin = new JButton("기존 정보로 로그인");
		isLogCheckBox = new JCheckBox("로그남기기");
		login = new JButton("로그인");
		
		try{
			new File("userinfo.txt");
			isLogCheckBox.setSelected(Boolean.valueOf(FileManager.getInstance().readFile().get("log")));
		}catch (Exception e){
			isLogCheckBox.setSelected(false);
		}
		
		
		
		pane.setLayout(new GridLayout(5,1));
		idTextField.setColumns(15);
		pwTextField.setColumns(15);
		idTextField.setPlaceholder("아이디(전화번호 숫자만) ");
		pwTextField.setPlaceholder("비밀번호");
		idTextField.setPreferredSize(new Dimension(250, 35));
		pwTextField.setPreferredSize(new Dimension(250, 35));
		

		pane.setBackground(Color.WHITE);
		idPanel.setLayout(new FlowLayout());
		pwPanel.setLayout(new FlowLayout());
		autoLoginPanel.setLayout(new FlowLayout());
		ipPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		login.setForeground(Color.white);
		autoLogin.setForeground(Color.white);
		idPanel.setBackground(Color.white);
		pwPanel.setBackground(Color.white);
		ipPanel.setBackground(Color.white);
		autoLoginPanel.setBackground(Color.white);
		login.setBackground(new Color(113, 92, 94));
		autoLogin.setBackground(new Color(113, 92, 94));
		login.setCursor(new Cursor(Cursor.HAND_CURSOR));
		autoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
		login.setPreferredSize(new Dimension(250, 40));
		idPanel.setPreferredSize(new Dimension(300, 40));
		pwPanel.setPreferredSize(new Dimension(300, 40));
		autoLoginPanel.setPreferredSize(new Dimension(300, 20));
		phoneIPTextField.setHorizontalAlignment(JTextField.CENTER);
		idTextField.setBorder(BorderFactory.createCompoundBorder(idTextField.getBorder(), BorderFactory.createEmptyBorder(5,5,5,5)));
		pwTextField.setBorder(BorderFactory.createCompoundBorder(pwTextField.getBorder(), BorderFactory.createEmptyBorder(5,5,5,5)));
		
		/**
		 * muc를 실행하고 로그인하는 함수
		 */
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				id = idTextField.getText();
				pw = pwTextField.getText();
				phoneIP = phoneIPTextField.getText();
				myPhoneNum = id;
				FileManager.getInstance().writeUserInfoToFile(phoneIP, myPhoneNum, isLogFlag);
				
				if (isLogCheckBox.isSelected())
					isLogFlag = true;
				
				if(id == null || id.equals("") || pw == null || pw.equals("") || myPhoneNum == null || myPhoneNum.equals("") ||  phoneIP == null || phoneIP.equals("")){
					JOptionPane.showMessageDialog(new JFrame(), "빈 칸이 있습니다.");
				}else{
					
					if(HttpRequestManager.getInstance().changePort(DEFAULTPORT, phoneIP)){
						MUCLoader loader;
						try {
							loader = new MUCLoader();
							Thread.sleep(5000);
							MUCManager manager = new MUCManager();
							manager.setUserId(id);
							manager.setUserPwd(pw);
							manager.setUserIp(phoneIP); 
							if(manager.startLogin()){
								if(HttpRequestManager.getInstance().changePort(NEWPORT, phoneIP)){
									Server server = Server.getSingletonInstance();
									server.start();
									login.setText("프로그램 실행중..");
									login.setEnabled(false);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		});
		autoLogin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (isLogCheckBox.isSelected())
					isLogFlag = true;
				
				String ip = null;
				String phoneNumber = null;
				try{
					HashMap<String, String> userInfo = FileManager.getInstance().readFile();
					ip = userInfo.get("ip");
					phoneNumber = userInfo.get("myPhoneNum");
				}catch (NullPointerException e){
					e.printStackTrace();
				}
				
				if(ip == null || ip.equals("") || phoneNumber == null || phoneNumber.equals("")){
					JOptionPane.showMessageDialog(new JFrame(), "기존 정보가 없습니다");
				}else{
					phoneIP = ip;
					myPhoneNum = phoneNumber;
					
					if(HttpRequestManager.getInstance().changePort(DEFAULTPORT, phoneIP)){
						MUCLoader loader;
						try {
							loader = new MUCLoader();
							Thread.sleep(5000);
							MUCManager manager = new MUCManager();
							
							if(manager.defaultStartLogin()){
								if(HttpRequestManager.getInstance().changePort(NEWPORT, phoneIP)){
									Server server = Server.getSingletonInstance();
									server.start();
									login.setText("프로그램 실행중..");
									login.setEnabled(false);
								}
							}
						}  catch (Exception e) {
							e.printStackTrace();
							FileManager.getInstance().writeChangePortLogToFile(e.toString());
						}
					}
				}
			}
		});
		

        idPanel.add(idTextField, "East");
        pwPanel.add(pwTextField);
        ipPanel.add(ipLabel);
        ipPanel.add(phoneIPTextField);
        autoLoginPanel.add(autoLogin);
//        autoLoginPanel.add(isLogCheckBox);
        
        pane.add(idPanel);
        pane.add(pwPanel);
        pane.add(ipPanel);
        pane.add(autoLoginPanel);
        pane.add(login);
		
        contentPane.add(pane);
		setVisible(true);
	}
	
	public static void main(String[] args){
		new AMMain();
		String[] cmd = new String[] {"cmd",  "/c", "am_updater.exe"};
		try {
			new ProcessBuilder(cmd).start();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "업데이터를 실행하지 못했습니다.");
		} catch (Exception e1){
			JOptionPane.showMessageDialog(null, "업데이터를 실행하지 못했습니다.");
		}
		
		
		
		

	}
}