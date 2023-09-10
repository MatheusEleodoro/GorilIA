package com.eleodorodev.ia.chatgpt;
import com.eleodorodev.ia.config.*;
import com.eleodorodev.ia.config.Config.Log;
import com.eleodorodev.ia.voice.Speech;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

public class ChatIA {
    private static final String MODEL_API = "gpt-3.5-turbo";
    public void conversation(String userMessage, Speech speech){
        ChatMessage system = new ChatMessage("system","seu nome agora é GorilIA uma junção de Gorila com IA");
        ChatMessage message = new ChatMessage("user",userMessage);
        OpenAiService service = new OpenAiService(Config.API_KEY);
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(MODEL_API)
                .messages(List.of(system,message))
                .maxTokens(4000)
                .build();


        StringBuilder chatResponse = new StringBuilder();
        service.createChatCompletion(request).getChoices().forEach(n->chatResponse.append(n.getMessage().getContent()));
        speech.toSpeak(chatResponse.toString());
        Log.LOGGER.info("[Jarvis] - "+chatResponse);
    }
}

