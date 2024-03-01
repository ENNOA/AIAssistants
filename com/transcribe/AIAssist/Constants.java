package com.transcribe.AIAssist;

/*
 * todo list: change file locations
 */

public class Constants {
	static  String ASSEMBLY_API_KEY = null;
	static String GPT_API_KEY = null;
	static String G_API_KEY = null;
	
	static String serviceKeyPath = null;
    static String TRANSCRIPTION_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    static String TRANSLATION_ENDPOINT = "https://api.openai.com/v1/audio/translations";
	
	
	public static String getTRANSCRIPTION_ENDPOINT() {
		return TRANSCRIPTION_ENDPOINT;
	}
	public static String getTRANSLATION_ENDPOINT() {
		return TRANSLATION_ENDPOINT;
	}
	public static String getServiceKeyPath() {
		return serviceKeyPath;//serviceKeyRelativePath;
	}
	public static String getG_API_KEY() {
		return G_API_KEY;
	}
	public static void setG_API_KEY(String g_API_KEY) {
		G_API_KEY = g_API_KEY;
	}
	public static  String MODEL = "gpt-3.5-turbo";

	public static String getRECORDING_LOCATION() {
		return RECORDING_LOCATION;
	}

	public static String getMODEL() {
		return MODEL;
	}
	public static String getGPT_API_KEY() {
		return GPT_API_KEY;
	}

	public static String getAPI_KEY() {
		return ASSEMBLY_API_KEY;
	}

}
