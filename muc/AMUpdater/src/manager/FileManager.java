package manager;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import model.Global;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FileManager {
	
	/**
	 * 새로운 AM을 받고나서 version 정보럴 업데이트한다.
	 */
	public static void updateVersionInfo(){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(Global.versionXML);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xpath.evaluate("//ApplicationVersion", doc, XPathConstants.NODESET);
			nodes.item(0).setTextContent(HttpRequestManager.getInstance().getLatestVersion());
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(Global.versionXML));
            transformer.transform(source, result);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * version.xml 파일에서 현재 am의 버전 정보를 얻어온다.
	 * @return
	 */
	@SuppressWarnings("finally")
	public static String readVersionInfo(){
		String version = null;
		try {
			InputSource is = new InputSource(new FileReader("version.xml"));
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			version = xpath.evaluate("//ApplicationVersion", document);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return version;
		}
		
	}
	
	/**
	 * 에러로그를 작성한다.
	 */
	public static void writeErrorLog(String error){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter("log/error.log"));
			writer.write("date : " + getCurrentDate() + " error : "+  error + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 새로운 AM파일을 저장한다. 기존의 파일은 삭제한다.
	 * @return 파일 저장 완료/실패 flag
	 */
	public static Boolean saveLatestBinary(String filename, URL binURL){
		try {
			File beforeAM = new File("am.exe");
			Boolean isDelete = false;
			Boolean isExistAM = beforeAM.exists(); 
			if(beforeAM.exists()){
				beforeAM.setWritable(true);
				isDelete = beforeAM.delete();
			}
			
			if(isExistAM && isDelete){
				BufferedInputStream in = null;
				 FileOutputStream fout = null;
				 try {
					 in = new BufferedInputStream(binURL.openStream());
					 fout = new FileOutputStream(filename);

					 byte data[] = new byte[1024];
					 int count;
					 while ((count = in.read(data, 0, 1024)) != -1) {
						 fout.write(data, 0, count);
					 }
				 } finally {
					 if (in != null)
					 in.close();
				 if (fout != null)
					 fout.close();
				 }
				 return true;
			 
//				FileOutputStream fos = new FileOutputStream("am.exe");
//				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//				fos.close();
				
			}else if(isExistAM && !isDelete){
				writeErrorLog("am.exe 파일 존재, 파일 삭제만 실패");
				JOptionPane.showMessageDialog(null, "이전버전 삭제중 오류가 발생했습니다.");
				return false;
			}else{
				writeErrorLog("am 파일이 없음.(파일명 확인)");
				JOptionPane.showMessageDialog(null, "업데이트를 실패하였습니다. am파일을 확인해주세요");
				return false;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static String getCurrentDate(){
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return df.format(date);
	}
	
}
