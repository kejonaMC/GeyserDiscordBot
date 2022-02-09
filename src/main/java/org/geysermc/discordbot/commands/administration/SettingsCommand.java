package org.geysermc.discordbot.commands.administration;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.geysermc.discordbot.GeyserBot;
import org.geysermc.discordbot.storage.ServerSettings;
import org.geysermc.discordbot.util.BotColors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsCommand extends Command {

    public SettingsCommand() {
        this.name = "settings";
        this.hidden = true;
        this.userPermissions = new Permission[] { Permission.MESSAGE_MANAGE };
    }

    @Override
    protected void execute(CommandEvent event) {
        List<String> args = new ArrayList<>(Arrays.asList(event.getArgs().split(" ")));

        String title;
        String key = args.remove(1);
        String value;

        String action = args.remove(0);
        switch (action) {
            case "get" -> {
                title = "Setting value";
                value = GeyserBot.storageManager.getServerPreference(event.getGuild().getIdLong(), key);
            }
            case "set" -> {
                title = "Updated setting";
                value = String.join(" ", args);
                GeyserBot.storageManager.setServerPreference(event.getGuild().getIdLong(), key, value);
            }
            case "add" -> {
                title = "Updated setting";

                List<String> list = ServerSettings.getList(event.getGuild().getIdLong(), key);
                list.add(String.join(" ", args));

                ServerSettings.setList(event.getGuild().getIdLong(), key, list);

                value = GeyserBot.storageManager.getServerPreference(event.getGuild().getIdLong(), key);
            }
            case "remove" -> {
                title = "Updated setting";

                List<String> list = ServerSettings.getList(event.getGuild().getIdLong(), key);
                list.remove(String.join(" ", args));

                ServerSettings.setList(event.getGuild().getIdLong(), key, list);

                value = GeyserBot.storageManager.getServerPreference(event.getGuild().getIdLong(), key);
            }
            default -> {
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Invalid action specified")
                        .setDescription("Unknown action `" + args.get(0) + "`, it can be one of: `get`, `set`")
                        .setTimestamp(Instant.now())
                        .setColor(BotColors.FAILURE.getColor())
                        .build()).queue();
                return;
            }
        }

        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(title)
                .addField("Key", "`" + key + "`", false)
                .addField("Value", "`" + value + "`", false)
                .setTimestamp(Instant.now())
                .setColor(BotColors.SUCCESS.getColor())
                .build()).queue();
    }
}
