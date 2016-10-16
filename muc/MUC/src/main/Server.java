package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;


public class Server extends Thread {
	DatagramSocket socket;
	static Server server = null;
	HashMap<String, Long> phoneMap;
	PacketManager packetManager;
	
	/**
	 * 생성자 함수
	 * DatagramSocket에 해당 포트번호에 대한 DatagramSocket 객체를 할당 한다.
	 * @throws SocketException
	 */
	private Server() throws SocketException{
		socket = new DatagramSocket(Integer.parseInt(AMMain.NEWPORT));
		phoneMap = new HashMap<String, Long>();
		packetManager = new PacketManager();
	}
	
	/**
	 * 싱글턴객체를 반환해주는 static 함수.
	 * @return server 객체
	 * @throws SocketException
	 */
	public static Server getSingletonInstance() throws SocketException{
		if(server == null){
			server = new Server();
		}
		return server;
	}
	/**
	 * MUC에 패킷을 보낸다.
	 * @param value
	 * @throws IOException
	 */
	public void sendPacket(String value) throws IOException{
		byte []data = new byte[value.length()/2];
		for(int i=0; i<data.length; i++){
			data[i] = (byte)Integer.parseInt(value.substring(2*i, 2*i+2),16);
		}
		
		try {
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(AMMain.phoneIP), Integer.parseInt(AMMain.NEWPORT));
			socket.send(sendPacket);
		} catch (UnknownHostException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			JOptionPane.showMessageDialog(null, errors.toString());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * string을 hexstring으로 변환하는 함수
	 * @param originData string값
	 * @return hexstring 문자열
	 */
	public String convertStringToHexString(String originData){
		String hexToString = "";
		for (int i = 0; i < originData.length(); i++) {
			hexToString += String.format("%02X ", (int)originData.charAt(i));
	    }
		hexToString = hexToString.replace(" ", "");
		return hexToString;
	}
	
	/**
	 * 지정한 url요청을 통해 웹 사이트를 보여주는 브라우저를 호출한다.
	 * @param phoneNum
	 */
	public void showBrower(String phone, String viaPhone){
		long currentTime = System.currentTimeMillis();
		if(phoneMap.get(phone) != null){
			Long beforeTime = phoneMap.get(phone);
			if((currentTime - beforeTime) < 3000){
				return;
			}
		}
		phoneMap.put(phone, currentTime);
		String addr = "http://agent.1577-0005.com/call_notice/call_notice.php?send=" + phone + "&reci=" + AMMain.myPhoneNum + "&t=" + currentTime + "&thru=" + viaPhone;
		
		try {
			String[] cmd = new String[] {"rundll32", "url.dll", "FileProtocolHandler", addr};
			new ProcessBuilder(cmd).start();
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			JOptionPane.showMessageDialog(null, errors.toString());
		}
	}
	
	
	/**
	 * 쓰레드를 실행한다.
	 */
	public void run(){
		while(true){
			try {
				byte[] buf = new byte[566];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				DatagramPacket responsePacket = packetManager.getResponsePacket(packet);
				String dataStr = new String(packet.getData(), 0, packet.getLength());
				
				if(dataStr.contains("RECEIVE")){
					String packetStr = new String(packet.getData());
					String regEx1 = "[01[016789]{1}|02|0[3-9]({1}[0-9]{1})]{2,3}+[0-9]{3,4}?[0-9]{4,5}";	// 모든전화번호 rege7x
					Matcher matcher = Pattern.compile(regEx1).matcher(packetStr);
					ArrayList<String> phoneNums = new ArrayList<String>();
					while(matcher.find()){
						String matchString = matcher.group().toString();
						String dateRegex = "(19[7-9][0-9]|20\\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])";
						Matcher dateMatcher = Pattern.compile(dateRegex).matcher(matchString);
						
						if(dateMatcher.find()) continue;							// 정규표현식으로 찾은 문자열이 날짜형식이면 다음으로 넘어간다.
						
						phoneNums.add(matcher.group().toString());
					}
					if(phoneNums.size() > 0){
						String phone;
						if(phoneNums.size() == 1){			// 직접 전화온 경우
							phone = phoneNums.get(0);
							showBrower(phone, "");
						}else if(phoneNums.size() == 2){		// 경유된 경우
							String viaPhone = phoneNums.get(0);
							phone = phoneNums.get(1);
							showBrower(phone, viaPhone);
						}
					}
					socket.send(responsePacket);
				}else if(dataStr.contains("TIMEOUT")){
					AMMain.autoLogin.doClick();
				}else{
					FileManager.getInstance().writeETCPacketToFile(dataStr);
					socket.send(responsePacket);
				}
			} catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				JOptionPane.showMessageDialog(null, errors.toString());
				FileManager.getInstance().writeChangePortLogToFile(errors.toString());
				e.printStackTrace();
			}
		}
	}
	
}
