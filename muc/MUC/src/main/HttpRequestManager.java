package main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpRequestManager {
	static HttpRequestManager instance;
	
	
	private HttpRequestManager(){}
	
	public static HttpRequestManager getInstance(){
		if(instance == null){
			instance = new HttpRequestManager();
		}
		return instance;
	}
	/**
	 * 포트를 바꾼다.
	 * @param port 번호
	 * @return 포트변경 성공 / 실패
	 */
	public Boolean changePort(String port, String phoneIP){
		
		DefaultHttpClient client = new DefaultHttpClient();
		String urlStr = "http://" + phoneIP+ ":8080"  + "/sipservice_set.html";
		
		String userName = "ktvoip";
		String password = "voipph";
		String host = phoneIP;
		String realm = phoneIP;
		try {
			HttpPost post = new HttpPost(urlStr);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("dialPlan", "(X|*|#)X+#<"));
			params.add(new BasicNameValuePair("unset", "0X+|15X+|16X+|*X+|#X+|XXXXXX|XXXXX|XXXX|XXX|XX|X"));
			params.add(new BasicNameValuePair("pickupCode", "*98"));
			params.add(new BasicNameValuePair("vmsCode", "13"));
			params.add(new BasicNameValuePair("holdType", "1"));
			params.add(new BasicNameValuePair("xferType", "2"));
			params.add(new BasicNameValuePair("eventTalk", "1"));
			params.add(new BasicNameValuePair("eventHold", "1"));
			params.add(new BasicNameValuePair("callEventType", "0"));
			params.add(new BasicNameValuePair("smsType", "12"));
			params.add(new BasicNameValuePair("prsType", "0"));
			params.add(new BasicNameValuePair("smsPrefix", ""));
			params.add(new BasicNameValuePair("prsTo", "0"));
			params.add(new BasicNameValuePair("muc_enable", "1"));
			params.add(new BasicNameValuePair("muc_port", port));
			params.add(new BasicNameValuePair("Muc1timer", "500"));
			params.add(new BasicNameValuePair("Muc2timer", "2000"));
			params.add(new BasicNameValuePair("MucMaxTranstimer", "4000"));
			params.add(new BasicNameValuePair("muc_media_port", "7533"));
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			client = new DefaultHttpClient();
			Credentials upc = new UsernamePasswordCredentials(userName, password);
			AuthScope as = new AuthScope(host, 8080, realm);
			client.getCredentialsProvider().setCredentials(as, upc);
			HttpResponse response = client.execute(post);
			FileManager.getInstance().writeChangePortLogToFile(response.toString());
			
			return true;
		}catch(Exception e){
			
			e.printStackTrace();
			return false;
		}
	}
}
