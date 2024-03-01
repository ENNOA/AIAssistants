package com.transcribe.AIAssist;

import com.google.protobuf.ByteString;

public class GUI_BackEnd {

	static String fileLocation;
	static String transcript;
	static String answer;
	static ByteString verbal;
	static String words;
	static String sentinel;
	static int selection;

	public static String getFileLocation() {
		return fileLocation;
	}

	public static void setFileLocation(String fileLocation) {
		GUI_BackEnd.fileLocation = fileLocation;
	}

	public static String getTranscript() {
		return transcript;
	}

	public static void setTranscript(String transcript) {
		GUI_BackEnd.transcript = transcript;
	}

	public static String getAnswer() {
		return answer;
	}

	public static void setAnswer(String answer) {
		GUI_BackEnd.answer = answer;
	}

	public static String getWords() {
		return words;
	}

	public static void setWords(String words) {
		GUI_BackEnd.words = words;
	}
	public static ByteString getVerbal() {
		return verbal;
	}
	
	public static void setVerbal(ByteString verbal) {
		GUI_BackEnd.verbal = verbal;
	}

	public static String getSentinel() {
		return sentinel;
	}

	public static void setSentinel(String sentinel) {
		GUI_BackEnd.sentinel = sentinel;
	}

	public static int getSelection() {
		return selection;
	}

	public static void setSelection(int selection) {
		GUI_BackEnd.selection = selection;
	}


	/*
	 * @SuppressWarnings("static-access") public static void main(String[] args)
	 * throws Exception { //variables
	 * 
	 * 
	 * do { //user prompts
	 * 
	 * System.out.println(menu()); selection= input.nextInt(); switch(selection) {
	 * case 0: // takes audio and saves as .wav fileLocation = audio.audioIn(); //
	 * sends the .wav to whisper and saves response as string transcript =
	 * agents.wsprAgent(fileLocation, selection); //sends saved string to gpt for
	 * processing answer =agents.gptAgent(transcript); System.out.println(answer);
	 * //sends answer to google for base64 string file creation verbal =
	 * agents.gSpeechAgent(answer); // plays the answer received
	 * audio.Audio_out(verbal); break;
	 * 
	 * case 1: // takes audio and saves as .wav fileLocation = audio.audioIn(); //
	 * sends the .wav to whisper and saves response as string transcript =
	 * agents.wsprAgent(fileLocation, selection); //sends answer to google for
	 * base64 string file creation verbal = agents.gSpeechAgent(transcript); //
	 * plays the answer received audio.Audio_out(verbal); break; }
	 * 
	 * System.out.print("Do you have a another request? Y/N: ");
	 * sentinel=input.next(); }while(sentinel.equalsIgnoreCase("Y"));
	 * 
	 * input.close(); System.exit(0);
	 * 
	 * }
	 */
	public static String menu() {
		String menu ="   Menu\n----------"
					+"0: Request an answer to a question."
					+ "\n1: Request a translation."
					+"\nSelection: ";
		return menu;
	}
	
	public static String Question() throws Exception {
		// takes audio and saves as .wav
		fileLocation = getFileLocation();
		// sends the .wav to  whisper and saves response as string
		transcript = AIAgents.wsprAgent(fileLocation, selection);
		//sends saved string to gpt for processing
		answer =AIAgents.gptAgent(transcript);
		System.out.println(answer);
		//sends answer to google for base64 string file creation
		onHold(answer);
		verbal = AIAgents.localSpeechAgent(answer);
		//words = AIAgents.gSpeechAgent(answer);
		// plays the answer received
		Audio_IO.Audio_out(verbal);
		//Audio_IO.Audio_out(words);
	
		return answer;
	}
	
	public static String Translation() throws Exception{
		// takes audio and saves as .wav
		fileLocation = Audio_IO.audioIn();
		// sends the .wav to  whisper and saves response as string
		transcript = AIAgents.wsprAgent(fileLocation, selection);
		verbal = AIAgents.localSpeechAgent(answer);
		// plays the answer received
		Audio_IO.Audio_out(verbal);
		return answer;
	}
	
	public static boolean onHold(String x) throws InterruptedException {
		final Object lock = new Object(); 

	    synchronized (lock) {
	        while (x == null) {
	            lock.wait(500); // Wait for 500 milliseconds or until another thread calls lock.notify()
	            return true;
	        }
	    }
	    return false;
	}
	
}
