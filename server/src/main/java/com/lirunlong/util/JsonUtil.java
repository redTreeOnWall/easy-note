package com.lirunlong.util;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtil {

  public static JsonNode getFieldFromJsonNode (JsonNode node, String propertyName) {
    // TODO unit test
    if(node == null) {
      return null;
    }

    var propertyNameChain = propertyName.split("\\.");
    var jsonNode = node;
    for(var i = 0; i< propertyNameChain.length; i++){
      var fieldName = propertyNameChain[i];
      jsonNode = node.get(fieldName);

      if(jsonNode == null) {
        return null;
      }
    }
    
    return jsonNode;
  }
  
  public static String getFiledFromJsonNode(JsonNode node, String propertyName, String defaultText) {
    var field = JsonUtil.getFieldFromJsonNode(node, propertyName);

    if(field == null || (! field.isTextual())) {
      return defaultText;
    } else {
      return field.asText(defaultText);
    }
  }

  public static double getFiledFromJsonNode(JsonNode node, String propertyName, double defaultNumber) {
    var field = JsonUtil.getFieldFromJsonNode(node, propertyName);

    if(field == null || (! field.isNumber())) {
      return defaultNumber;
    } else {
      return field.asDouble(defaultNumber);
    }
  }


  public static boolean getFiledFromJsonNode(JsonNode node, String propertyName, boolean defaultValue) {
    var field = JsonUtil.getFieldFromJsonNode(node, propertyName);

    if(field == null || (! field.isBoolean())) {
      return defaultValue;
    } else {
      return field.asBoolean(defaultValue);
    }
  }
}
