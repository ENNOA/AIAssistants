package com.transcribe.AIAssist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;


public class AIAgents {
	static String API_KEY = Constants.getGPT_API_KEY();
	static String G_API_KEY = Constants.getG_API_KEY();
	static String fileLocation = Constants.getRECORDING_LOCATION();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    
    public static String gptAgent(String text) throws URISyntaxException, IOException, InterruptedException {
    	String gptReply = null;
    	String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
        String[] conversation = {
                "system", "You are a helpful assistant.",
                "user", text
            };

            JsonArray messages = new JsonArray();
            for (int i = 0; i < conversation.length; i += 2) {
                JsonObject messageObj = new JsonObject();
                messageObj.addProperty("role", conversation[i]);
                messageObj.addProperty("content", conversation[i + 1]);
                messages.add(messageObj);
            }

            JsonObject payload = new JsonObject();
            payload.add("messages", messages);
            payload.addProperty("model", Constants.getMODEL());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_ENDPOINT))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            JsonObject choice = choices.get(0).getAsJsonObject();
            gptReply = choice.getAsJsonObject("message").get("content").getAsString();
    		
            return gptReply;
        }
    
    public static String wsprAgent(String fileLocation, int x) throws Exception {
    	String transcriptionText=null;
    	String endPoint=null;
    	if (x==0) 
    		endPoint= Constants.getTRANSCRIPTION_ENDPOINT();
    	else
    		endPoint= Constants.getTRANSLATION_ENDPOINT();
    		
        

        File file = new File(fileLocation);

        HttpEntity wsprPayload = MultipartEntityBuilder.create()
                .addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName())
                .addTextBody("model", "whisper-1")
                .addTextBody("response_format", "json")
                .addTextBody("language", "en")
                .build();
        
        //if (x==1) {
        HttpPost httpPost = new HttpPost(endPoint);
        httpPost.setHeader("Authorization", "Bearer " + API_KEY);
        httpPost.setEntity(wsprPayload);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        @SuppressWarnings("deprecation")
		CloseableHttpResponse response = httpClient.execute(httpPost);

        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
        if (responseJson.has("text")) {
            transcriptionText = responseJson.get("text").getAsString();
        } else {
            transcriptionText = "Processing..."; 
        }
        return transcriptionText;
    }
    
    
    public static String gSpeechAgent(String aloud) {
    	System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", Constants.getServiceKeyPath());
    	String audio=null;
        try {
            String G_API_ENDPOINT = "https://texttospeech.googleapis.com/v1/text:synthesize";
            String serviceKeyPath = Constants.getServiceKeyPath(); 

            InputStream credentialsStream = new FileInputStream(serviceKeyPath);
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);

            List<String> scopes = ImmutableList.of("https://www.googleapis.com/auth/cloud-platform");
            AccessToken token = credentials.createScoped(scopes).refreshAccessToken();

            String requestBody = "{\n" +
                    "  \"input\": {\n" +
                    "    \"text\": \"" + aloud + "\"\n" +
                    "  },\n" +
                    "  \"voice\": {\n" +
                    "    \"languageCode\": \"en-gb\",\n" +
                    "    \"name\": \"en-GB-Standard-A\",\n" +
                    "    \"ssmlGender\": \"FEMALE\"\n" +
                    "  },\n" +
                    "  \"audioConfig\": {\n" +
                    "    \"audioEncoding\": \"LINEAR16\"\n" +
                    "  }\n" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(G_API_ENDPOINT))
                    .header("Authorization", "Bearer " + token.getTokenValue())
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonElement root = JsonParser.parseString(response.body());

            // Check if the "audioContent" key exists and is not null
            if (root.getAsJsonObject().has("audioContent") && !root.getAsJsonObject().get("audioContent").isJsonNull()) {
                audio = root.getAsJsonObject().get("audioContent").getAsString();
            } else {
            	audio = "No audio available";
            }

            return audio;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ByteString localSpeechAgent(String aloud) throws NullPointerException {
        try {
            
            ByteString audio;
            try (TextToSpeechClient TTSclient = TextToSpeechClient.create()) {
                SynthesisInput input = SynthesisInput.newBuilder().setText(aloud).build();
                VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                        .setLanguageCode("en-GB")
                        .setSsmlGender(SsmlVoiceGender.FEMALE)
                        .build();
                AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();
                SynthesizeSpeechResponse response = TTSclient.synthesizeSpeech(input, voice, audioConfig);
                audio = response.getAudioContent();
            }
            return audio;
        } catch (Exception e) {
           e.printStackTrace();
            return null;
        }
    }

    
}
    