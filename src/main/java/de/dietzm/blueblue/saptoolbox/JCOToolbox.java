package de.dietzm.blueblue.saptoolbox;

import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Primitives;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class JCOToolbox {

	public static void addRangeTableEntryEQ(JCoFunction function, String tableName, String value) {

		JCoParameterList tableList = function.getTableParameterList();
		JCoTable table = tableList.getTable(tableName);
		table.appendRow();
		table.setValue("SIGN", "I");
		table.setValue("OPTION", "EQ");
		table.setValue("LOW", value);

	}

	public static void addRangeTableEntry(JCoFunction function, String tableName, String operator,
			String valueFieldPrefix, String value) {

		JCoParameterList tableList = function.getTableParameterList();
		JCoTable table = tableList.getTable(tableName);
		table.appendRow();
		table.setValue("SIGN", "I");
		table.setValue("OPTION", operator);
		table.setValue(valueFieldPrefix + "LOW", value);

	}

	public static JsonArray convertTable(JCoFunction function, String tableName) {
		JsonArray resultList = new JsonArray();

		JCoTable table = function.getTableParameterList().getTable(tableName);

		for (int i = 0; i < table.getNumRows(); i++) {
			table.setRow(i);
			JsonObject entry = convertRecord(table);
			resultList.add(entry);
		}

		return resultList;

	}

	public static JsonObject convertExportParameter(JCoFunction function, String tableName) {
		JCoStructure struct = function.getExportParameterList().getStructure(tableName);
		return convertRecord(struct);
	}

	public static JsonObject convertRecord(JCoRecord table) {

		JsonObject entry = new JsonObject();

		for (int i = 0; i < table.getMetaData().getFieldCount(); i++) {

			String type = table.getMetaData().getTypeAsString(i);
			String name = table.getMetaData().getName(i);

			if (type.equals("CHAR")) {
				String value = table.getString(i);
				entry.addProperty(name, value);

			} else {
				String value = table.getString(i);
				entry.addProperty(name, value);
			}

		}

		return entry;
	}

	public static <T> T convertTableToObjectArray(JCoFunction function, String tableName, Class<T> classOfT) {
		JsonArray materialListJ = JCOToolbox.convertTable(function, tableName);
		Object object = new Gson().fromJson(materialListJ, classOfT);
		return Primitives.wrap(classOfT).cast(object);
	}

	public static void enterJsonArrayToTable(JCoFunction function, String tableName, JsonArray jsonArray) {

		JCoTable table = function.getTableParameterList().getTable(tableName);
		JCoMetaData meta = table.getMetaData();

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonEntry = jsonArray.get(i).getAsJsonObject();
			table.appendRow();

			Iterator<String> keyIt = jsonEntry.keySet().iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				if (meta.hasField(key)) {
					String type = meta.getTypeAsString(key);
					if (type.equals("CHAR")) {
						table.setValue(key, jsonEntry.get(key).getAsString());
					} else if (type.equals("NUM")) {
						table.setValue(key, jsonEntry.get(key).getAsInt());
					} else if (type.equals("BCD")) {
						table.setValue(key, jsonEntry.get(key).getAsDouble());
					} else {
						System.err.println("ABAP Type " + type + " of field " + key + " unhandeled in enterJsonArrayToTable");
					}
				}

			}

		}

	}
}
