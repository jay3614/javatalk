package javatalk.login;
/**
 * @author 강철민, 박선영
 * 해당 클래스의 내용은 필요에 의해 ModelDAO 메서드로 통합되었습니다.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * 생성된 채팅로그 파일을 읽는 클래스
 * userData 폴더 내에 로그인 시 생성된 유저의 id.dat 파일 내 채팅로그를 한줄씩 읽어 배열에 담아서 리턴한다. 
 */
public class DbReader {
	
	public static ArrayList<String> strList = new ArrayList<>();
	public static String[] input_arr = new String[strList.size()];
	
	public static String[] reader(String user) throws IOException {
		String str;
		BufferedReader reader = new BufferedReader(new FileReader("c:\\userData\\" + user + ".dat"));
		
		while ((str = reader.readLine()) != null) { // 한줄씩 읽기
			strList.add(str); // ArrayList에 읽은 내용 추가
		}
		input_arr = strList.toArray(input_arr); // 읽은 내용 저장
		reader.close();
		
		return input_arr;
	}

}
