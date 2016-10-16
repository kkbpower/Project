package manager;


import javax.swing.JOptionPane;
import model.Global;
import model.Version;

/**
 * 업데이트에 관한 작업을 관리하는 클래스
 * 각 버전을 가져와 비교하고 업데이트를 할 수 있도록 한다.
 * @author kkb
 *
 */
public class UpdateManager {
	static UpdateManager instance;
	
	String currentVersion;
	String latestVersion;
	
	
	
	private UpdateManager(){}
	
	public static UpdateManager getInstance(){
		if(instance == null){
			instance = new UpdateManager();
		}
		return instance;
	}


	/**
	 * 파일로부터 버전 정보를 얻어오는 함수
	 * @return
	 */
	private String getCurrentVersion(){
		return FileManager.readVersionInfo();
	}
	
	/**
	 * 서버로부터 최신 버전의 정보를 얻어오는 함수
	 * @return
	 */
	private String getLatestVersion(){
		return HttpRequestManager.getInstance().getLatestVersion();
	}
	
	
	/**
	 * 최신버전과 현재 버전을 비교하여 업데이트 작업을 수행할지, 일반적인 실행을 할지를 체크하는 함수
	 */
	public Boolean checkUpdate(){
		try {
			Version currentVersion = new Version(getCurrentVersion());
			Version latestVersion = new Version(getLatestVersion());
			if(currentVersion.compareTo(latestVersion) == Global.RIGHT_VERSION_GREATER){
				String message = "업데이트가 있습니다.\r 업데이트 하시겠습니까? \n" + currentVersion.get() + " > " + latestVersion.get();
				int dialogResult = JOptionPane.showConfirmDialog (null, message,"업데이트", JOptionPane.YES_NO_OPTION);
				return dialogResult == Global.UPDATE_START; 
			}else{
				return false;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "최신버전과의 비교에 실패하였습니다.");
			return false;
		}
		
	}
	
	/**
	 * 업데이트를 수행한다.
	 */
	public void update(){
		Boolean result = HttpRequestManager.getInstance().downloadLatestVersion();
		if(result){
			JOptionPane.showMessageDialog(null, "업데이트를 완료하였습니다.");
			FileManager.updateVersionInfo();
		}else{
			JOptionPane.showMessageDialog(null, "업데이트를 실패하였습니다. 다시 시도해주세요.");
		}
	}
	
}
