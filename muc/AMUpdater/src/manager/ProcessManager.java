package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessManager {
	static ProcessManager instance;
	
	private ProcessManager(){}
	
	public static ProcessManager getInstance(){
		if(instance == null){
			instance = new ProcessManager();
		}
		return instance;
	}
	
	
	@SuppressWarnings("finally")
	private Process start(String []cmd){
		Process process = null;
		try {
			process = new ProcessBuilder(cmd).start();
		} catch (IOException e) {
			FileManager.writeErrorLog(e.toString());
		}finally{
			return process;
		}
	}
	
	
	/**
	 * AM 프로세스를 실행한다.
	 * @return
	 */
	public Process startAM(){
		String[] cmd = new String[] {"am.exe"};
		
		return start(cmd);
	}
	
	/**
	 * AM 프로세스를 종료한다.
	 */
	public void killAMAndMUC(){
		try {
			String []cmd = new String[] {"tasklist", "/v"};
			
			BufferedReader reader;
			Process tasklistCMD = start(cmd);
			if(tasklistCMD != null){
				reader = new BufferedReader(new InputStreamReader(tasklistCMD.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					if(line.contains("muc.exe") || line.contains("Alternative MUC")){
						System.out.println(line);
						Pattern pattern = Pattern.compile("[0-9]{1,10}");
						Matcher matcher = pattern.matcher(line);
						if(matcher.find()){
							String []killCMD = new String[] {"taskkill", "/PID", matcher.group(0)};
							start(killCMD);
						}
					}
				}
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
