// Inside your Activity (e.g., MainActivity.java)

public void callDeepSeekAPI() {
    String prompt = "Return ONLY plain text, e.g., Hello DeepSeek 👋";

    DeepSeekAPI.askDeepSeek(prompt, new DeepSeekAPI.DeepSeekCallback() {
        @Override
        public String onResponse(String answer) {
            runOnUiThread(() -> {
                // Update your UI here
                textViewResult.setText(answer);
            });
            return answer;
        }

        @Override
        public String onError(String error) {
            runOnUiThread(() -> {
                // Handle error here
                textViewResult.setText("Error: " + error);
            });
            return "";
        }
    });
}
