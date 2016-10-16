package main;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.jna.Native;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

public class MUCLoader {
	public MUCLoader() throws InterruptedException {
		try {
			new ProcessBuilder("C:\\Program Files\\MUC\\muc.exe").start();
		} catch (Exception e) {
			try {
				new ProcessBuilder("C:\\Program Files (x86)\\MUC\\muc.exe").start();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(new JFrame(), "MUC를 실행할 수 없습니다.");
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

}

/**
 * MUC 로그인을 수행하는 클래스
 * @author kkb
 *
 */
class MUCManager {
	final private int WM_COMMAND = 0x0111;
	final private int WM_SETTEXT = 0x000C;
	final private int BM_SETCHECK = 0x00F1;
	final private int BM_CLICK = 0x00F5;
	private String userId = null;
	private String userPwd = null;
	private String userIp = null;
	private String aClass = null;
	private String bClass = null;
	private String cClass = null;
	private String dClass = null;

	private interface User32 extends StdCallLibrary {
		final User32 instance = (User32) Native.loadLibrary("user32", User32.class);

		HWND FindWindowExA(HWND hwndParent, HWND childAfter, String className,
				String windowName);

		HWND FindWindowA(String className, String windowName);

		int SendMessageA(HWND hWnd, int msg, int wParam, int lParam);

		int SendMessageA(HWND hWnd, int msg, int wParam, String lParam);

		int PostMessageA(HWND hWnd, int msg, int wParam, String lParam);

		DWORD RegisterCallback(HANDLE handle, int type, User32 callback);

		interface WNDENUMPROC extends StdCallCallback {
			boolean callback(HWND hWnd, LPARAM arg);
		}

		boolean EnumWindows(WNDENUMPROC lpEnumFunc, LPARAM arg);

		int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public void setUserIp(String userIp) {
		if (userIp != null || !userIp.equals("")) {
			this.userIp = userIp;
			String[] spliteUserIp = userIp.split("\\.");
			if (spliteUserIp != null && spliteUserIp.length == 4) {
				this.aClass = spliteUserIp[0];
				this.bClass = spliteUserIp[1];
				this.cClass = spliteUserIp[2];
				this.dClass = spliteUserIp[3];
			}
		}
	}

	public boolean startLogin() {
		if (userIp == null || userId == null || userPwd == null
				|| userId.equals("") || userPwd.equals("") || userIp.equals("")) {
			System.err.println("userId or userPwd or userIp is null");
			return false;
		} else {
			startSetupAndLogin();
			return true;
		}
	}

	public boolean defaultStartLogin(){
		HWND muc = User32.instance.FindWindowA(null, "MUC");
		HWND hWnd = User32.instance.FindWindowExA(muc, null, "#32770", null);
		HWND MainDialog = User32.instance.FindWindowExA(muc, hWnd, "#32770",
				null);
		User32.instance.PostMessageA(MainDialog, WM_COMMAND, 0x000004B6, null);
		return true;
	}
	
	
	private boolean startSetupAndLogin() {
		HWND muc = User32.instance.FindWindowA(null, "MUC");
		HWND hWnd = User32.instance.FindWindowExA(muc, null, "#32770", null);
		HWND MainDialog = User32.instance.FindWindowExA(muc, hWnd, "#32770",
				null);
		HWND idInputText = User32.instance.FindWindowExA(MainDialog, null,
				"Edit", null);
		HWND pwdInputText = User32.instance.FindWindowExA(MainDialog,
				idInputText, "Edit", null);

		User32.instance.SendMessageA(idInputText, WM_SETTEXT, 0, userId);
		User32.instance.SendMessageA(pwdInputText, WM_SETTEXT, 0, userPwd);
		User32.instance.PostMessageA(MainDialog, WM_COMMAND, 0x00000416, null);

		User32.instance.EnumWindows(new User32.WNDENUMPROC() {
			@Override
			public boolean callback(HWND hWnd, LPARAM arg) {
				HWND cancel = User32.instance.FindWindowExA(hWnd, null,
						"Button", null);
				HWND save = User32.instance.FindWindowExA(hWnd, cancel,
						"Button", null);
				HWND checkBtn = User32.instance.FindWindowExA(hWnd, save,
						"Button", null);
				HWND label = User32.instance.FindWindowExA(hWnd, null,
						"Static", null);
				HWND sysIpAddress = User32.instance.FindWindowExA(hWnd, null,
						"SysIPAddress32", null);
				if (cancel != null && save != null && checkBtn != null
						&& label != null && sysIpAddress != null) {
					HWND dClassEdit = User32.instance.FindWindowExA(
							sysIpAddress, null, "Edit", null);
					HWND cClassEdit = User32.instance.FindWindowExA(
							sysIpAddress, dClassEdit, "Edit", null);
					HWND bClassEdit = User32.instance.FindWindowExA(
							sysIpAddress, cClassEdit, "Edit", null);
					HWND aClassEdit = User32.instance.FindWindowExA(
							sysIpAddress, bClassEdit, "Edit", null);
					User32.instance
							.PostMessageA(checkBtn, BM_SETCHECK, 1, null);
					User32.instance.SendMessageA(aClassEdit, WM_SETTEXT, 0,
							aClass);
					User32.instance.SendMessageA(bClassEdit, WM_SETTEXT, 0,
							bClass);
					User32.instance.SendMessageA(cClassEdit, WM_SETTEXT, 0,
							cClass);
					User32.instance.SendMessageA(dClassEdit, WM_SETTEXT, 0,
							dClass);
					User32.instance.PostMessageA(save, BM_CLICK, 1, null);
				}
				return true;
			}
		}, null);
		User32.instance.PostMessageA(MainDialog, WM_COMMAND, 0x000004B6, null);
		return true;
	}
}
