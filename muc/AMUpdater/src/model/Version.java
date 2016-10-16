package model;

/**
 * Version 모델
 * Version 객체간의 비교(버전 비교로 현재 버전보다 높은 버전이 있는지 알 수 있다.)
 * @author kkb
 *
 */
public class Version implements Comparable<Version>{

	private String version;
	
	public String get(){
		return this.version;
	}
	
	public Version(String version){
		if(version == null)
            throw new IllegalArgumentException("Version can not be null");
        if(!version.matches("[0-9]+(\\.[0-9]+)*"))
            throw new IllegalArgumentException("Invalid version format");
        this.version = version;
	}
	
	
	/**
	 * Version 객체간의 비교를 한다.
	 * 
	 * @param 비교 될 Version 객체
	 */
	@Override
	public int compareTo(Version o) {
		if(o == null)
            return 1;
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = o.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart)
            	return Global.RIGHT_VERSION_GREATER;
            if(thisPart > thatPart)
            	return Global.LEFT_VERSION_GREATER;
        }
        return Global.EQUAL_VERSION;
	}
}
