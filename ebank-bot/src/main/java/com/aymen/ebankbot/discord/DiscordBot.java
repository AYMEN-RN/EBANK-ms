package com.aymen.ebankbot.discord;
import com.aymen.ebankbot.agents.EbankAIAgent;
import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
            if (query == null || query.trim().isEmpty()) {
            return;
        }

        String channelId = event.getChannel().getId();
        String response = ebankAIAgent.chat(query, channelId);
        event.getChannel().sendMessage(response).queue();
    }

}
