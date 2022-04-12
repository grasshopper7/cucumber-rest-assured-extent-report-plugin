package tech.grasshopper.processor.deserializer;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tech.grasshopper.pojo.Result;

public class ResultDeserializer implements JsonDeserializer<Result> {

	@Override
	public Result deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();

		Result result = new Result();
		result.setStatus(jsonObject.get("status").getAsString());

		if (jsonObject.has("duration"))
			result.setDuration(jsonObject.get("duration").getAsLong());

		if (jsonObject.has("error_message")) {
			String exception = jsonObject.get("error_message").getAsString();

			Matcher matcher = Pattern.compile("\\R\\tat").matcher(exception);
			if (matcher.find()) {
				String msg = exception.substring(0, matcher.start());
				
				// Hack for exception parsing logic to work!!
				msg = msg + System.lineSeparator();
				result.setErrorMessage(msg);
			}
		}
		return result;
	}
}
