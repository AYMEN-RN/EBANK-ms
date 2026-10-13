package com.aymen.ebankbot.discord;
import com.aymen.ebankbot.agents.EbankAIAgent;

@DiscordController
public class DiscordBot {
    private EbankAIAgent ebankAIAgent;

    public DiscordBot(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }

    @DiscordMapping
    private void perform(MessageReceivedEvent event){
        if (event.getAuthor().isBot()) return;
        String query = event.getMessage().getContentRaw();
        String response = ebankAIAgent.chat(query, "DiscordBot");
        event.getChannel().sendMessage(response).queue();
    }

}
