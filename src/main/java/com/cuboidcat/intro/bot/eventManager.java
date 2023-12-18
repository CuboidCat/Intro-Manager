package com.cuboidcat.intro.bot;

import com.cuboidcat.intro.mongoManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Map;

public class eventManager extends ListenerAdapter {
    private JDA bot;
    public eventManager(JDA bot) {
        this.bot = bot;
    }



    @Override




    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().getId().equals("1175699863229440050")) {
            return;
        }
        if (mongoManager.getIntro(e.getGuild().getId()).equals("0")) {
            return;
        }
        TextChannel intro = bot.getTextChannelById(mongoManager.getIntro(e.getGuild().getId()));
        if (!e.getChannel().equals(intro)) {
            return;
        }
        if (mongoManager.getRole(e.getGuild().getId()).equals("0")) {
            bot.getTextChannelById(mongoManager.getNotifcation(e.getGuild().getId())).sendMessage("You have not defined a member role. Please use the command /role and input the channel ID you wish to use.").queue();
            return;
        }
        if (mongoManager.getTemp(e.getGuild().getId()).equals("0")) {
            return;
        }
        Member member = e.getMember();
        String message = "";
        if (!mongoManager.getPing(e.getGuild().getId()).equals("0")) {
            message += bot.getRoleById(mongoManager.getPing(e.getGuild().getId())).getAsMention() + ": ";
        }
                message += (member.getAsMention() + " has sent their introduction:\n\n"  + e.getMessage().getContentDisplay() + "\n\nConfirm: :white_check_mark: Deny: :x:");
        TextChannel channel = bot.getTextChannelById(mongoManager.getNotifcation(e.getGuild().getId()));
        channel.sendMessage(message).queue((msg) -> {
            msg.addReaction(Emoji.fromUnicode("✅")).queue();
            msg.addReaction(Emoji.fromUnicode("❌")).queue();
            mongoManager.addMessage(e.getGuild().getId(), msg.getId(), e.getAuthor().getId());
        });
        e.getGuild().addRoleToMember(e.getAuthor(), e.getGuild().getRoleById(mongoManager.getTemp(e.getGuild().getId()))).queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (e.getUserId().equals("1175699863229440050")) {
            return;
        }
        Map<String, String> map = mongoManager.getMessages(e.getGuild().getId());
        if (!map.containsKey(e.getMessageId())) {
            return;
        }
        if (e.getEmoji().getName().equals("✅")) {
            e.getGuild().addRoleToMember(e.getGuild().getMemberById(map.get(e.getMessageId())), e.getGuild().getRoleById(mongoManager.getRole(e.getGuild().getId()))).queue();
            mongoManager.removeMessage(e.getGuild().getId(), e.getMessageId());
            e.getChannel().sendMessage(bot.getUserById(map.get(e.getMessageId())).getAsMention() + " has been confirmed!").queue();
            e.getChannel().deleteMessageById(e.getMessageId()).queue();
            e.getGuild().removeRoleFromMember(e.getGuild().getMemberById(map.get(e.getMessageId())), e.getGuild().getRoleById(mongoManager.getTemp(e.getGuild().getId()))).queue();
        }
        else if (e.getEmoji().getName().equals("❌")) {
            mongoManager.removeMessage(e.getGuild().getId(), e.getMessageId());
            e.getChannel().sendMessage(bot.getUserById(map.get(e.getMessageId())).getAsMention() + " has been denied! To have them send another intro remove all of their roles.").queue();
            e.getChannel().deleteMessageById(e.getMessageId()).queue();
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        new mongoManager();
        super.onReady(event);
    }
}
