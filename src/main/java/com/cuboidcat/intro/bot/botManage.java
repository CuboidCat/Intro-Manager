package com.cuboidcat.intro.bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.List;

public class botManage {
    private static JDA bot;
    public botManage() {
        bot = JDABuilder.createDefault("insert connection string here")
                .setActivity(Activity.customStatus("Managing your intros!"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
        bot.addEventListener(new eventManager(bot));
        bot.addEventListener(new commandManager(bot));
    }

    public static JDA getBot() {
        return bot;
    }
}
