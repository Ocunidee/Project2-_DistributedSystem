package chat.common;


import java.util.Map;


public abstract class AbstractCommandHandler implements CommandHandler {

	private final String TYPE_KEY= "abstract";
	@Override
	public boolean accept(Map<String, Object> in_message) {
        return getTYPE_KEY().equals((String) in_message.get("type"));
	}

	public String getTYPE_KEY() {
		return TYPE_KEY;
	}

}
