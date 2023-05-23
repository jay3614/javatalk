package javatalk.login;
/**
 * @author ALL
 */
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * DB에 접근하는 메서드를 정의한 클래스
 */
public class ModelDAO {

	private static Connection conn;
	private static ModelDAO dao = new ModelDAO();
	private Object fileName;
	private File file, folder,folder2;
	private int result;
	private String userId, sql;
	private String path = "C:\\userData";
	private PreparedStatement pstmt;

	private ModelDAO() {
	}

	public static ModelDAO getInstance() {
		return dao;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int sendEmail(String email) { // 비밀번호 찾기 시 이메일을 보내는 메서드
		result = 0; // 0 = 전송실패, 1 = 전송성공, 2 = 이메일이 없음.
		boolean flag = true;
		String password = null;
		sql = "Select userpassword from chatusers where useremail = ?";

		conn = getConnection();
		pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				password = rs.getString("userpassword");
			}
			rs.close();
			pstmt.close();
			closer(conn);

		} catch (Exception e) {
			System.out.println("비밀번호 조회 중 예외 발생 : " + e.getMessage());
		}

		if (password == null) {
			result = 2;
			return result;
		}

		// 오픈API를 활용하여 가입 시 입력했던 이메일 주소로 비밀번호를 메일 보내주는 기능을 정의
		final String serviceUser = "yourid"; // 아이디 (gmail 기준)
		final String servicePass = "yourpassword"; // 비밀번호, 2단계인증 활성화, 인증키-> 이메일, IMAP 켜기

		// Property에 SMTP 서버 정보 설정
		Properties prop = new Properties();
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 587);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.tls.enable", "true");
		prop.put("mail.smtp.tls.trust", "smtp.gmail.com");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

		System.out.println(prop.toString());

		Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(serviceUser, servicePass);
			}
		});
		while (flag) {
			MimeMessage message = new MimeMessage(session);
			try {
				try {
					message.setFrom(new InternetAddress(serviceUser, "ADMIN", "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					System.out.println("Message 설정 중 예외 발생 : " + e.getMessage());
					e.printStackTrace();
				}

				message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
				message.setSubject("비밀번호 찾기 요청을 받았습니다.");
				message.setText("회원님의 비밀번호는 " + password + " 입니다.");
				Transport.send(message);

				flag = false; // 성공적으로 수행했다면 false로 변경하여 while 중단.
				result = 1; // 리턴값 1로 설정
				return result;
			} catch (AddressException e) {
				System.out.println("주소 예외 발생 : " + e.getMessage());
				e.getStackTrace();
			} catch (MessagingException e) {
				System.out.println("메시지 예외 발생 : " + e.getMessage());
				e.getStackTrace();
			}
		}
		return result;
	}

	public String returnPass(String email) { // 비밀번호 조회 메서드
		String password = null;
		sql = "Select userpassword from chatusers where useremail = ?";
		conn = getConnection();
		pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				password = rs.getString("userpassword");
			}
			rs.close();
			pstmt.close();
			closer(conn);

		} catch (Exception e) {
			System.out.println("비밀번호 조회 중 예외 발생 : " + e.getMessage());
		}
		return password;
	}

	// 회원가입 메서드
	public int regChat(MemberDTO dto) { // result 1 = 가입성공 0 = 실패
		result = 0;
		sql = "Insert into CHATUSERS (USERID, USERNAME, USERPASSWORD, USEREMAIL) values (?, ?, ?, ?)";
		conn = getConnection();
		pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getUserName());
			pstmt.setString(3, dto.getUserPassword());
			pstmt.setString(4, dto.getUserEmail());
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("회원가입 중 예외 발생. " + e.getMessage());

		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
					closer(conn);
				} catch (Exception e) {
					System.out.println("회원가입 후 DB Connection 닫는 중 예외 발생 " + e.getMessage());
				}
			}
		}
		return result;
	}

	public int findEmail(String email) { // 회원인지 아닌지 이메일을 통해 찾는 메서드
		result = 0;
		sql = "Select count(useremail) from chatusers where useremail = ?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			closer(conn);
		} catch (Exception e) {
			System.out.println("회원 이메일 조회 중 예외 발생 : " + e.getMessage());
		}
		return result;
	}

	// ID가 존재하는지 여부에 대한 validation 메서드 정의
	public int isMember(String id) {
		result = 0;
		sql = "Select count(userid) from chatUsers Where userid = ?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			// 조회(Select)는 executeQuery(sql)을 실행해야 한다.
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			return result;
		} catch (Exception e) {
			System.out.println("회원 조회 시 예외 발생" + e.getMessage());
		}
		return result;
	}

	public boolean checkPass(String txtName, String password) { // 비밀번호가 맞는지 체크
		boolean result = false;
		sql = "Select userpassword from chatUsers Where userid = ?"; // 고른 userid의 password select
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, txtName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String pass = rs.getString(1); // users table에서 가져온 password
				if (pass.equals(password)) { // users table에서 가져온 password와 입력한 password 비교
					result = true;
				} else {
					result = false;
				}
			}
			rs.close();
			pstmt.close();
			closer(conn);
			return result;
		} catch (Exception e) {
			System.out.println("비밀번호 조회 시 예외 발생" + e.getMessage());
		}
		return result;
	}

	public void closer(Connection conn) { // Connection 닫기
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println("DB close 시 예외 발생함" + e.getMessage());
			}
		}
	}

	// 내부에서 사용한 Connection 리턴 메서드 정의
	private Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "yourid", "yourpassword");	// 오라클 db 계정 정보 입력
			System.out.println("커넥션 OK..정보 --> " + conn);
		} catch (Exception e) {
			System.out.println("Connection 생성 시 예외 발생함.");
			System.out.println("예외 내용 : " + e.getMessage());
		}
		return conn;
	}

	public void changePw(String id, String rePassword) { // password 변경
		sql = "Update Chatusers SET userpassword = ?" + "where userid = ?";
		conn = getConnection();
		pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, rePassword);
			pstmt.setString(2, id);
			pstmt.executeUpdate();

			pstmt.close();
			closer(conn);

		} catch (Exception e) {
			System.out.println("예외발생" + e.getMessage());
		}

	}

	public int dbChatlog(String user, String message) { // 채팅로그를 DB에 저장

		result = 0;
		sql = "Insert into chatlog (AT_TIME, CHATUSER ,CHATDATA) values (sysdate, ?, ?)";
		conn = getConnection();
		pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user);
			pstmt.setString(2, message);
			result = pstmt.executeUpdate();
			pstmt.close();
			closer(conn);
		} catch (Exception e) {
			System.out.println("채팅 로그 기록 중 예외 발생 : " + e.getMessage());
		}
		deleteFile();
		return result;
	}

	public void deleteFile() { // DB에 저장 후 id.dat 파일 삭제하여 다음 로그인 시 채팅로그 초기화
		File outputFile = new File(path + "\\" + userId + ".dat");
		try {
			outputFile.delete();

		} catch (Exception e) {
			System.out.println("로그파일 삭제 중 예외 : " + e.getMessage());
		}
	}

	public int saveChatlog(String user, String message) throws IOException { // 파일로 채팅로그 저장하기

		int result = 0;

		fileName = user + ".dat";
		file = new File(folder, (String) fileName);
		folder = new File("C:\\userData");

		try {

			PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(file, true)));

			ps.println("chat: " + message);
			ps.flush();

			ps.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public void makeFile(String id) { // 중복된 ID가 없을 때 파일 생성하는 메서드

		fileName = id + ".dat";
		folder = new File("C:\\userData");
		folder2 = new File("download");
		file = new File(folder, (String) fileName);

		try {
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);

			if (!folder.exists()) // userData폴더가 없다면 폴더 생성
				System.out.println(folder.mkdir());
			
			if (!folder2.exists()) // download폴더가 없다면 폴더 생성
				System.out.println(folder2.mkdir());
			
			ps.close();
		} catch (FileNotFoundException e) {

		}

	}

	/*
	 * 생성된 채팅로그 파일을 읽는 메서드 userData 폴더 내에 로그인 시 생성된 유저의 id.dat 파일 내 채팅로그를 한줄씩 읽어 배열에
	 * 담아서 리턴한다.
	 */
	public String[] dbReader(String user) {
		ArrayList<String> strList = new ArrayList<String>();
		String[] inputArr = new String[strList.size()];
		String str;

		try {
			BufferedReader reader = new BufferedReader(new FileReader("c:\\userData\\" + user + ".dat"));
			while ((str = reader.readLine()) != null) { // 한줄씩 읽기
				strList.add(str); // ArrayList에 읽은 내용 추가
			}
			inputArr = strList.toArray(inputArr); // 읽은 내용 저장
			reader.close();
		} catch (IOException e) {
			System.out.println("채팅로그 읽는 중 예외 발생 : " + e.getMessage());
		}
		return inputArr;
	}

}
