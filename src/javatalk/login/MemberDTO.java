package javatalk.login;

/*
 * 멤버의 정보를 간직하는 DTO 객체
 */
public class MemberDTO {
	
	private String userId;
	private String userName;
	private String userPassword;
	private String userEmail;
	private String date;
	
	public MemberDTO() {
		
	}	// 기본 생성자의 끝
	
	public String getReg_Date() {
		return date;
	}

	public void setdate(String date) {
		this.date = date;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	
}
