package com.aymen.ebankbot.telegram;

import com.aymen.ebankbot.agents.EbankAIAgent;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String telegramBotToken;
    private final EbankAIAgent ebankAIAgent;

    public TelegramBot(
            EbankAIAgent ebankAIAgent,
            @Value("${telegram.token}") String telegramBotToken) {
        this.ebankAIAgent = ebankAIAgent;
        this.telegramBotToken = telegramBotToken;
    }

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du bot Telegram", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update){
        try {
            if (!update.hasMessage()) return;

            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            List<PhotoSize> photos = update.getMessage().getPhoto();
            List<Media> mediaList = new ArrayList<>();
            String caption = null;

            if (photos != null && !photos.isEmpty()) {
                caption = update.getMessage().getCaption();
                if (caption == null) caption = "What do you see in this image";

                PhotoSize photo = photos.get(photos.size() - 1); // Meilleure résolution
                String fileId = photo.getFileId();
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                String filePath = file.getFilePath();

                String textUrl = "https://api.telegram.org/file/bot/" + getBotToken() + "/" + filePath;
                URL url = new URL(textUrl);

                mediaList.add(Media.builder()
                        .id(fileId)
                        .mimeType(MimeTypeUtils.IMAGE_PNG)
                        .data(new UrlResource(url))
                        .build());
            }

            String query = messageText != null ? messageText : caption;
            if (query == null) return;

            UserMessage userMessage = UserMessage.builder()
                    .text(query)
                    .media(mediaList)
                    .build();

            sendTypingQuestion(chatId);

            String answer = ebankAIAgent.chat(new Prompt(userMessage), String.valueOf(chatId));

            sendTextMessage(chatId, answer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Ebank_r4n_1bot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    private void sendTextMessage(long chatId, String text)throws TelegramApiException{
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId),text);
        execute(sendMessage);
    }
    private void sendTypingQuestion(Long chatId) throws TelegramApiException{
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        execute(sendChatAction);
    }
}
