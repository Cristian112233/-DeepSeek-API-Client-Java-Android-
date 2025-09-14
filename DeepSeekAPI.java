
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

// Class to handle DeepSeek API requests in Java
public class DeepSeekAPI {
    // Base URL for DeepSeek API
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    // ⚠️ API Key (for demo purposes only)
    // Never hardcode your API key in production code.
    // Use BuildConfig, environment variables, or a backend proxy.
    private static final String API_KEY = "sk-YOUR_API_KEY_HERE"; 

    // Callback interface to handle asynchronous responses
    public interface DeepSeekCallback {
        String onResponse(String answer); // Called when API returns a successful response
        String onError(String error);     // Called when there is an error
    }

    // Method to send a question to DeepSeek
    public static void askDeepSeek(String question, DeepSeekCallback callback) {
        // OkHttp client to make HTTP requests
        OkHttpClient client = new OkHttpClient();

        try {
            // Build the JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "deepseek-chat");  // Model to use
            requestBody.put("temperature", 0.0);        // 0.0 = deterministic responses
            requestBody.put("max_tokens", 20);          // Limit response length (tokens)

            // Create the messages array (OpenAI-style format)
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "user")       // Role = user
                    .put("content", question)  // Content = user’s question
            );
            requestBody.put("messages", messages);

            // Build the HTTP request
            Request request = new Request.Builder()
                    .url(API_URL) // API endpoint
                    .post(RequestBody.create(
                            requestBody.toString(),
                            MediaType.parse("application/json")
                    ))
                    .addHeader("Authorization", "Bearer " + API_KEY) // Authorization header
                    .build();

            // Send the request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Connection or network error
                    callback.onError("Connection error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        // Get the raw response as a string
                        String rawResponse = response.body().string();

                        // Handle unsuccessful HTTP responses
                        if (!response.isSuccessful()) {
                            callback.onError("API error: " + response.code() + " - " + rawResponse);
                            return;
                        }

                        // Parse the JSON response
                        JSONObject json = new JSONObject(rawResponse);
                        String answer = json.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                                .trim();

                        // Send parsed answer to the callback
                        callback.onResponse(answer);
                    } catch (Exception e) {
                        // Handle parsing or processing errors
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            // Handle errors while building the request
            callback.onError("Error creating request: " + e.getMessage());
        }
    }
}