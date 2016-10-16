package main;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketManager {
	PacketManager instance;
	
	public DatagramPacket getResponsePacket(DatagramPacket receivedPacket){
		String packetStr = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
		String hex = convertStringToHexString(packetStr);
		String response = hex.substring(0,  152) + "4f4b" + hex.substring(156, hex.length());
		
		byte[] buf = new byte[566];
		DatagramPacket result = new DatagramPacket(buf, buf.length);
		result.setData(new BigInteger(response, 16).toByteArray());
		try {
			result.setAddress(InetAddress.getByName(AMMain.phoneIP));
			result.setPort(Integer.parseInt(AMMain.NEWPORT));
			result.setLength(new BigInteger(response, 16).toByteArray().length);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileManager.getInstance().writeLogToFile(packetStr, hex);	// 받은 패킷을 로그로 남긴다.
		return result;
	}
	
	
	
	/**
	 * string을 hexstring으로 변환하는 함수
	 * @param originData string값
	 * @return hexstring 문자열
	 */
	private String convertStringToHexString(String originData){
		String hexToString = "";
		for (int i = 0; i < originData.length(); i++) {
			hexToString += String.format("%02X ", (int)originData.charAt(i));
	    }
		hexToString = hexToString.replace(" ", "");
		return hexToString;
	}
	
}
