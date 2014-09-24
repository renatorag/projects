/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.parse.json.parsejson;


import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class responsible for allowing a simple and efficient way of retrieving
 * content from JSON texts.
 *
 * @author Renato (renato.augusto.wow@gmail.com)
 */
public class JsonParser implements IParseable {

	private String mjsonData;

	/**
	 * Creates a new instance of JsonParser class.
	 * 
	 * @param jsonData
	 *            JSON text to be parsed.
	 * 
	 */
	public JsonParser(String jsonData) {
		mjsonData = jsonData;
	}
	
	/**
	 * Parse the initially given JSON text using a predicate to extract the
	 * desired information. The predicate must be compliance with the following
	 * pattern: {@code "root/a/[0]/b"}, where names followed by the slash character, like
	 * "root/", represents keys from JSON that usually become a
	 * JsonObject, and numbers inside parentheses represents the index within an
	 * JsonArray. Eg. 
	 * {"root":{
	 * 		"a":"valueA",
	 * 		"b":"valueB",
	 * 		"c":[
	 * 			{
	 * 				"d":"valueD1",
	 * 				"e":"valueE1"},
	 * 			{
	 * 				"d":"valueD2",
	 * 				"e":"valueE2"}
	 * 			]
	 * 		}
	 * }
	 * 
	 * For the given JSOn text, you can access the value from "e" 
	 * of the second array item using the calling parse method as follows:
	 * 	
	 * parse("root/c/[1]/e");
	 * 
	 * Which will result on the String "valueE2" as response.
	 * 	
	 * 
	 * @param jsonPredicate
	 *            predicate that indicates the parser the location of the
	 *            desired information.
	 */
	public Object parse(String jsonPredicate) throws ParserException {
		
		Object currentObject= null;

		List<String> list = Arrays.asList(jsonPredicate.split(File.separator));

		// if user starts mjsonPredicate with a "/", split will generate an empty first object.
		if (list != null && list.size() > 0 && list.get(0).isEmpty()) {
			throw new ParserException(
					"Invalid predicate! Predicates cannot start with slashs.");
		}

		Iterator<String> predicates = list.iterator();

		try {

			currentObject = new JSONObject(mjsonData);

			while (predicates.hasNext()) {
				
				String string = (String) predicates.next();
				
				if(string.trim().isEmpty()) {
					throw new ParserException("Invalid predicate! Check empty slashes.");
				}
				
				int indexOpen = string.indexOf("[");
				int indexClose = string.indexOf("]");

				if (indexOpen != -1 && indexClose != -1 && currentObject instanceof JSONArray) {
					
					int index = Integer.valueOf(string.substring(indexOpen + 1, indexClose));
					
					currentObject = ((JSONArray)currentObject).get(index);
					
				} else if(currentObject instanceof JSONObject) {
					
					currentObject = ((JSONObject) currentObject).get(string);
					
				} else {
					
					throw new ParserException(
							"Invalid predicate! Trying to access unknown object.");
					
				}

			}
			
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ParserException("Parser Error! " + e.getMessage());

		}

		return currentObject;
	}

}
