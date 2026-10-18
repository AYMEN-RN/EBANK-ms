package com.aymen.ebankbot.telegram;

import com.aymen.ebankbot.agents.EbankAIAgent;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${TELEGRAM-TOKEN}")
    private String telegramBotToken;
    private EbankAIAgent ebankAIAgent;

    public TelegramBot(EbankAIAgent ebankAIAgent) {this.ebankAIAgent = ebankAIAgent;}

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        }catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update telegraRequest){
        try {
            if(!telegraRequest.hasMessage()) return;
            String messageText = telegraRequest.getMessage().getText();
            Long chatId = telegraRequest.getMessage().getChatId();
            List<PhotoSize> photos = telegraRequest.getMessage().getPhoto();
            List<Media> mediaList = new ArrayList<>();
            String caption =null;
            if (photos != null && !photos.isEmpty()){
                caption = telegraRequest.getMessage().getCaption();
                if (caption == null) caption = "What do you see in this image";

                PhotoSize photo = photos.get(photos.size() - 1);
                String fileId = photo.getFileId();
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                File file = execute(getFile);
                String filePath = file.getFilePath();
                String textUrl = "https://api.telegram.org/file/bot/" + getBotToken() + "/" + filePath;
                URL url = new URL(textUrl);

                mediaList.add(Media.builder()
                        .id(fileId)
                        .mimeType(MimeTypeUtils.IMAGE_PNG)
                        .data(new UrlResource(url))
                        .build());
            }

            String query = messageText != null?messageText: caption;
            UserMessage userMessage = UserMessage.builder()
                    .text (query)
                    .media(mediaList)
                    .build();
            sendTypingQuestion(chatId);
            String answer = ebankAIAgent.chat(new Prompt(userMessage), String.valueOf(chatId));
            sendTextMessage(chatId, answer);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
