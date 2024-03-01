package com.transcribe.AIAssist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.google.protobuf.ByteString;


public class Audio_IO {
    private static boolean isRecording = false;
    private static File outputFile;
    private static TargetDataLine line;
    private static Thread recordingThread;

    public static String audioIn() throws IOException {
        final int durationInSeconds = 5;
        final File outputFile = new File("recorded_audio.wav");

        AudioFormat audioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                48000,     // Sample rate
                16,        // Sample size in bits
                2,         // Channels (1 for mono, 2 for stereo)
                4,         // Frame size
                48000,     // Frame rate
                false      // Big-endian (false for little-endian)
        );

        try {
            TargetDataLine line = AudioSystem.getTargetDataLine(audioFormat);
            line.open(audioFormat);
            line.start();

            System.out.println("Recording...");

            AudioInputStream audioInputStream = new AudioInputStream(line);
         // Create a thread to record audio for the specified duration
            Thread recordingThread = new Thread(() -> {
                try {
					AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
                
                System.out.println("Recording complete. Saved to " + outputFile.getAbsolutePath());
            });

            recordingThread.start();
            
            // Let the recording thread run for the specified duration
            Thread.sleep(durationInSeconds * 1000);

            // Stop recording and clean up
            line.stop();
            line.close();
            recordingThread.join(); // Wait for the recording thread to finish
        } catch (LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
        return outputFile.getAbsolutePath();
        
    }
    public static String startRecording() throws IOException {
        setRecording(true);

        outputFile = new File("recorded_audio.wav");
        AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            48000,     // Sample rate
            16,        // Sample size in bits
            2,         // Channels (1 for mono, 2 for stereo)
            4,         // Frame size
            48000,     // Frame rate
            false      // Big-endian (false for little-endian)
        );

        try {
            line = AudioSystem.getTargetDataLine(audioFormat);
            line.open(audioFormat);
            line.start();

            System.out.println("Recording...");

            AudioInputStream audioInputStream = new AudioInputStream(line);
            recordingThread = new Thread(() -> {
                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                System.out.println("Recording complete. Saved to " + outputFile.getAbsolutePath());
            });

            recordingThread.start();
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }

        return outputFile.getAbsolutePath();
    }
    
    public static void stopRecording() {
    	setRecording(false);
        
        if (line != null) {
            line.stop();
            line.close();
        }

        if (recordingThread != null) {
            try {
                recordingThread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    	
    }
    
    public static void Audio_out(String speech) {
    	try {
            byte[] audioData = Base64.getDecoder().decode(speech);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioData));
            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
                    line.write(buffer, 0, bytesRead);
                }

                line.drain();
                line.stop();
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
    
    public static void Audio_out(ByteString speech) {
        try {
            byte[] audioData = speech.toByteArray();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioData));
            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
                    line.write(buffer, 0, bytesRead);
                }

                line.drain();
                line.stop();
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
	public static boolean isRecording() {
		return isRecording;
	}
	public static void setRecording(boolean isRecording) {
		Audio_IO.isRecording = isRecording;
	}
}
    

