package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class FileManager {
	final static String USER_INFO_FILE_NAME = "userinfo.txt";
	
	static FileManager instance;
	
	private FileManager(){}
	
	public static FileManager getInstance(){
		if(instance == null){
			instance = new FileManager();
		}
		return instance;
	}
	
	/**
	 * 패킷 중 취급하지 않은 나머지 종류의 패킷들의 로그를 write한다.
	 */
	public void writeETCPacketToFile(String packet){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(getLogFileName(), true));
			writer.write("date : " + getCurrentDate() + " etc packet : "+  packet + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 유저 정보를 파일에 write한다.
	 */
	public void writeUserInfoToFile(String phoneIP, String myPhoneNum, Boolean isLogFlag){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(USER_INFO_FILE_NAME));
			writer.write("[Server]" + "\n");
			writer.write("IP=" + phoneIP + "\n");
			writer.write("MYPHONENUM=" + myPhoneNum + "\n");
			writer.write("[Log]" + "\n");
			writer.write("LOG=" + isLogFlag + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 포트를 변경했을때의 HTTP response를 파일에 write한다.
	 * @param response
	 */
	public void writeChangePortLogToFile(String response) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(getLogFileName(), true));
			writer.write("date : " + getCurrentDate() + " response : "+  response + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 들어오는 모든 패킷을 로그로 남긴다.
	 * @param packetStr
	 * @param hex
	 */
	public void writeLogToFile(String packetStr, String hex) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(getLogFileName(), true));
			writer.write("date : " + getCurrentDate() + "packet : " + packetStr + "\n");
			writer.write("date : " + getCurrentDate() + "packet : " + hex + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
			try {
				writer = new BufferedWriter(new FileWriter(getLogFileName(), true));
				writer.write("==========Exception==========\n" );
				writer.write(e.toString());
				writer.close();
				
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, e1.toString());
				e1.printStackTrace();
			}
			
		}
	}
	
	/**
	 * userinfo 파일을 읽어온다.
	 * @return 유저 정보 string 배열
	 */
	public HashMap<String, String> readFile(){
		File file = new File(USER_INFO_FILE_NAME);
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			
			String line = null;
			HashMap<String, String> userInfo = new HashMap<String, String>();
			while((line = reader.readLine()) != null){
				
				if(line.contains("IP=")){
					userInfo.put("ip", line.substring(3, line.length()));
				}else if(line.contains("MYPHONENUM=")){
					userInfo.put("myPhoneNum", line.substring(11, line.length()));
				}else if(line.contains("LOG=")){
					userInfo.put("log", line.substring(3, line.length()));
				}
			}
			reader.close();
			return userInfo;
			
		} catch (FileNotFoundException e) {
			System.out.println("파일이 존재하지 않음.");
		} catch (IOException e) {
			System.out.println("파일 읽기 실패");
		} 
		return null;
		
	}
	
	private String getCurrentDate(){
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return df.format(date);
	}
	private String getCurrentYMD(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return df.format(date);
	}
	/**
	 * 로그 파일의 이름을 얻어온다. 
	 * 이미 로그파일이 생성되어있으면 기존 로그파일의 이름을 넘겨주고, 없으면 새로운 로그파일 이름을 만든다.
	 * @return
	 */
	private String getLogFileName(){
		File currentDirectory = new File(".");
		File[] files = currentDirectory.listFiles();
		for (File file : files) {
			if(file.getName().contains(getCurrentYMD())){
				return file.getName();
			}
		}
		
		
		File logDir = new File("log");
		if(!logDir.exists()){
			logDir.mkdir();
		}
		String fileName = "log/" + getCurrentYMD() + ".log";
		File logFile = new File(fileName);
		if(!logFile.exists()){
			System.out.println("로그 파일 없어서 만든다.");
			try {
				logFile.createNewFile();
				System.out.println("만들기 성공");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("로그 파일 만들기 ㅅ;ㄹ패");
			}
		}
		return fileName;
	}
}
