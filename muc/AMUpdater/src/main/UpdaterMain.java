package main;

import manager.ProcessManager;
import manager.UpdateManager;

public class UpdaterMain {
	String currentVersion;
	String latestVersion;
	
	public static void main(String[] args) {
		Boolean needUpdate = UpdateManager.getInstance().checkUpdate();
		
		if(needUpdate){	// 업데이트
			ProcessManager.getInstance().killAMAndMUC();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UpdateManager.getInstance().update();		
		}
	}
}
