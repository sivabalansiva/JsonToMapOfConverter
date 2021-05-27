package com.plugin.mapofconverter;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class Utils {

    final String KOTLIN_FUNCTION_DECLARATION = "fun getInputData() : String {";

    final String KOTLIN_MAP_OF_RETURN_STATEMENT = "return Gson().toJson(inputData)\n}";

    final String KOTLIN_MAP_OF_WITH_SERIALIZE_NULLS_RETURN_STATEMENT = "return GsonBuilder().serializeNulls().create().toJson(inputData)\n}";

    final String INPUT_DATA_DECLARATION = "val inputData = ";

    final String mapOf = "mapOf(";

    final String arrayOf = "arrayOf(";

    final char closeParenthesis = ')';

    final char closeSquareBrackets = ']';

    final char newLine = '\n';

    final char tabCharacter = '\t';

    final char comma = ',';

    final String to = " to ";

    // lazy initialisation syntax for creating Singleton instance
    public static final Utils INSTANCE = new Utils();

    private Utils() {}

    public boolean isValidJson(String inputText) {
        try {
            new Gson().fromJson(inputText, JsonElement.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getMethodDeclarationSyntax(String methodName) {
        if(methodName == null || methodName.trim().isEmpty()) {
            return KOTLIN_FUNCTION_DECLARATION;
        } else {
            return "fun " + methodName + "(): String {";
        }
    }

    public String getMapOfCodeFromJsonString(@NotNull String jsonString, @Nullable String methodName, boolean serializeNulls) {
        StringBuilder sb = new StringBuilder();
        JsonElement jsonElement = new Gson().fromJson(jsonString, JsonElement.class);

        sb.append(getMethodDeclarationSyntax(methodName));
        sb.append(newLine);
        sb.append(INPUT_DATA_DECLARATION);

        jsonToMapOfRecursive(sb, jsonElement);

        sb.append(newLine);
        if(serializeNulls) {
            sb.append(KOTLIN_MAP_OF_WITH_SERIALIZE_NULLS_RETURN_STATEMENT);
        } else {
            sb.append(KOTLIN_MAP_OF_RETURN_STATEMENT);
        }

        return sb.toString();
    }

    private void jsonToMapOfRecursive(StringBuilder sb, JsonElement jsonValue) {
        if (jsonValue instanceof JsonObject) {
            JsonObject value = (JsonObject) jsonValue;
            sb.append(mapOf);
            Iterator<String> jsonKeySetIterator = value.keySet().iterator();
            while (jsonKeySetIterator.hasNext()) {
                String jsonKey =  jsonKeySetIterator.next();
                sb.append(newLine);
                sb.append('"');
                sb.append(jsonKey);
                sb.append('"');
                sb.append(to);
                jsonToMapOfRecursive(sb, value.get(jsonKey));
                if(jsonKeySetIterator.hasNext()) {
                    sb.append(comma);
                }
            }
            sb.append(newLine);
            sb.append(closeParenthesis);
        } else if(jsonValue instanceof JsonArray) {
            JsonArray value = (JsonArray) jsonValue;
            sb.append(arrayOf);
            int jsonArraySize = value.size();
            for(int index = 0; index < jsonArraySize; index++) {
                sb.append(newLine);
                jsonToMapOfRecursive(sb, value.get(index));
                if (index < jsonArraySize - 1) {
                    sb.append(comma);
                }
            }
            sb.append(newLine);
            sb.append(closeParenthesis);
        } else if (jsonValue instanceof JsonPrimitive) {
            JsonPrimitive value = (JsonPrimitive) jsonValue;
            sb.append(value);
        } else if(jsonValue instanceof JsonNull) {
            sb.append(JsonNull.INSTANCE);
        }
    }

}
