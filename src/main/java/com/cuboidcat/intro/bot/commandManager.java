package com.cuboidcat.intro.bot;

import com.cuboidcat.intro.mongoManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class commandManager extends ListenerAdapter {

    private JDA bot;
    public commandManager(JDA bot) {
        this.bot = bot;
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("intro")) {
            OptionMapping channelOption = event.getOption("channel");
           Channel channel = channelOption.getAsChannel();
            event.reply("Intro channel set to: " + channel.getAsMention()).queue();
            mongoManager.setIntroChannel(event.getGuild().getId(), channel.getId());
        }
        else if(command.equals("notification")) {
            OptionMapping channelOption = event.getOption("channel");
            Channel channel = channelOption.getAsChannel();
            event.reply("Notification channel set to: " + channel.getAsMention()).queue();
            mongoManager.setMessageChannel(event.getGuild().getId(), channel.getId());
        }
        else if(command.equals("temp")) {
            OptionMapping channelOption = event.getOption("role");
            Role role = channelOption.getAsRole();
            event.reply("Temp role set to: " +role.getAsMention()).queue();
            mongoManager.setTemp(event.getGuild().getId(), role.getId());
        }
        else if(command.equals("ping")) {
            OptionMapping channelOption = event.getOption("role");
            Role role = channelOption.getAsRole();
            event.reply("Ping role set to: " +role.getAsMention()).queue();
            mongoManager.setPing(event.getGuild().getId(), role.getId());
        }
        else if(command.equals("role")) {
            OptionMapping channelOption = event.getOption("role");
            Role role = channelOption.getAsRole();
            event.reply("Member role set to: " +role.getAsMention()).queue();
            mongoManager.setRole(event.getGuild().getId(), role.getId());
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        commands(event.getGuild());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        commands(event.getGuild());
        mongoManager.testIfNew(bot.getGuilds());
    }

    private void commands(Guild guild) {
        List<CommandData> commandData = new ArrayList<CommandData>();
        OptionData data1 = new OptionData(OptionType.CHANNEL, "channel", "The channel you would like to select.", true).setChannelTypes(ChannelType.TEXT);
        OptionData data2 = new OptionData(OptionType.ROLE, "role", "The role you would like to select.", true);
        commandData.add(Commands.slash("intro", "Set an intro channel.").addOptions(data1).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        commandData.add(Commands.slash("notification", "Set a notification channel.").addOptions(data1).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        commandData.add(Commands.slash("temp", "Set a temporary role").addOptions(data2).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        commandData.add(Commands.slash("role", "Set a member role.").addOptions(data2).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        commandData.add(Commands.slash("ping", "Set a notification role.").addOptions(data2).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        guild.updateCommands().addCommands(commandData).queue();
    }
}
