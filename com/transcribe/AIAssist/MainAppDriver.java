package com.transcribe.AIAssist;

import java.util.Scanner;

public class MainAppDriver {

	public static int selection;
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		//variables
		String fileLocation;
		String transcript;
		String answer;
		String verbal;
		String sentinel;
		
		//declarations
		Scanner input = new Scanner(System.in);
		Audio_IO audio = new Audio_IO();
		AIAgents agents = new AIAgents();
		
		do {
		//user prompts
		
		System.out.println(menu());
		selection= input.nextInt();
		switch(selection) {
		case 0:		// takes audio and saves as .wav
					fileLocation = audio.audioIn();
					// sends the .wav to  whisper and saves response as string
					transcript = agents.wsprAgent(fileLocation, selection);
					//sends saved string to gpt for processing
					answer =agents.gptAgent(transcript);
					System.out.println(answer);
					//sends answer to google for base64 string file creation
					verbal = agents.gSpeechAgent(answer);
					// plays the answer received
					audio.Audio_out(verbal);
					break;
					
		case 1:		// takes audio and saves as .wav
					fileLocation = audio.audioIn();
					// sends the .wav to  whisper and saves response as string
					transcript = agents.wsprAgent(fileLocation, selection);
					//sends answer to google for base64 string file creation
					verbal = agents.gSpeechAgent(transcript);
					// plays the answer received
					audio.Audio_out(verbal);
					break;
		}
		
		System.out.print("Do you have a another request? Y/N: ");
		sentinel=input.next();
		}while(sentinel.equalsIgnoreCase("Y"));
				
		input.close();
		System.exit(0);

	}
	public static String menu() {
		String menu ="   Menu\n----------"
					+"\n0: Request an answer to a question."
					+ "\n1: Request a translation."
					+"\nSelection: ";
		return menu;
	}
	
}
