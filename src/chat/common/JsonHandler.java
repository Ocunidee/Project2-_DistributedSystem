package chat.common;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class JsonHandler {

	public Map<String, Object> unmarshall(String in_json){
		Gson gson = new Gson();
		Type stringObjectMap = new TypeToken<Map<String,Object>>(){}.getType();
		Map<String, Object> in_message = gson.fromJson(in_json, stringObjectMap);
		return in_message;
	}
	
	
	public String marshall(Map<String, Object> out_message){
		Gson gson = new Gson();
		Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
		String out_json = gson.toJson(out_message, mapType);
		return out_json;
	}


}
