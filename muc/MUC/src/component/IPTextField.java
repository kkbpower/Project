package component;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class IPTextField extends JTextField{

	//0.0.0.0 ~ 255.255.255.255
	int min = 7;
	public IPTextField(String t){
		super(15);
		setText(t);
		setPreferredSize(new Dimension(100, 35));
		initListeners();
	}

	public void initListeners(){
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				String string = getText();
				Pattern pattern = Pattern.compile("([.])"); //case insensitive, use [g] for only lower
				Matcher matcher = pattern.matcher(string);
				int count = 0;
				while (matcher.find()) count++;
				
				if(count > 3){	// .이 3개가 넘을 때 알림
					setText(getText().substring(0, getText().length()-1));
					JOptionPane.showMessageDialog(IPTextField.this, "올바르지 않습니다.");
					return;
				}
				
				int column = getColumns();
				int length = getText().length();
				if(length>column){
					setText(getText().substring(0,column));
					JOptionPane.showMessageDialog(IPTextField.this, "올바르지 않습니다.");
					return;
				}
			}
			public void keyPressed(KeyEvent e) {}
		});
	}
}