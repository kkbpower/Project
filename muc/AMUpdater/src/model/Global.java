package model;

/**
 * 어플리케이션 내에서 전체적으로 사용되는 변수를 담은 클래스
 * @author kkb
 *
 */
public class Global {
	public static int RIGHT_VERSION_GREATER = 1;	// 파라미터의 버전이 더 클 때.
	public static int LEFT_VERSION_GREATER = -1;	// 함수를 call한 버전가이더 클 때.
	public static int EQUAL_VERSION = 0;			// 두 버전이 같을 때.
	
	public static int UPDATE_START = 0;
	public static int UPDATE_CANCLE = 1;
	
	public static String SERVER_IP = "112.175.243.6";
	public static int SERVER_PORT = 9999;
	
	public static String versionXML = "version.xml";
	
	
}
