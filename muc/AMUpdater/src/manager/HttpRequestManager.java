package manager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JOptionPane;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HttpRequestManager {
	final static String LATEST_VERSION_URL = "http://112.175.243.6:5555/get_latest_muc_version";
	final static String LATEST_DOWNLOAD_URL = "http://112.175.243.6:5555/download_latest_muc";
	
	static HttpRequestManager instance;
	private HttpRequestManager(){}
	
	public static HttpRequestManager getInstance(){
		if(instance == null){
			instance = new HttpRequestManager();
		}
		return instance;
	}
	
	public String getLatestVersion(){
		HttpClient client = new DefaultHttpClient();
		String latestVersion = null;
		try {
			HttpGet get = new HttpGet(LATEST_VERSION_URL);
			HttpResponse response = client.execute(get);
			InputStream is = response.getEntity().getContent();
			StringBuffer sb = new StringBuffer();
		     byte[] b = new byte[64];
		     for (int n; (n = is.read(b)) != -1;) {
		         sb.append(new String(b, 0, n));
		     }
		     
		     JSONObject json = (JSONObject) new JSONParser().parse(sb.toString());
		     latestVersion = (String) json.get("latest_version");
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "최신 버전 정보를 가져오지 못했습니다.");
		}
		client.getConnectionManager().shutdown();
		return latestVersion;
	}
	
	/**
	 * 최신 버전의 am을 다운로드한다.
	 */
	public Boolean downloadLatestVersion(){
		try {
			URL binURL = new URL(LATEST_DOWNLOAD_URL);
			InputStream in = new BufferedInputStream(binURL.openStream());
			
//			JOptionPane.showMessageDialog(null, response.length);
//			ReadableByteChannel rbc = Channels.newChannel(binURL.openStream());
//			FileOutputStream fos = new FileOutputStream("am.exe");
//			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			return FileManager.saveLatestBinary("am.exe", binURL);	// 파일 저장이 정상 완료된 경우
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "최신 버전을 다운로드하지 못했습니다.");
			return false;
		}
	}
	
	
}
