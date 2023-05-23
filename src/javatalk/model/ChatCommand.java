package javatalk.model;

public enum ChatCommand { // 채팅창에서 보내는 명령어 
	WHISPER("/w"), SEARCH("#"), KICK("/k");
	
	private final String command;
	
	private ChatCommand(final String command) {
		this.command = command;
	}
	
	public String toString() {
		return command;
	}
}
