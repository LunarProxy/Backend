package me.lcproxy.jb;

import lombok.SneakyThrows;
import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.player.Rank;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.packets.WSPacketForceCrash;
import me.lcproxy.jb.server.packets.WSPacketSendEmote;
import me.lcproxy.jb.server.packets.WSSendChatMessage;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Discord extends ListenerAdapter {
    @SneakyThrows
    public void initialize() {
        // Obtain your discord token from https://discord.com/developers
        JDABuilder.createLight("discord bot token here", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Discord())
                .setActivity(Activity.playing("with the Lunar websocket"))
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();

        if (msg.getContentRaw().equals("l!online")) {
            msg.reply("**" + WebServer.getInstance().getPlayerManager().getTotalOnline() + "** users currently online.").queue();
        }

        if (!msg.getMember().getRoles().contains(msg.getGuild().getRoleById("change to your role id here"))) return;

        if (msg.getContentRaw().startsWith("l!announce")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;
            String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
            PlayerManager.getPlayerMap().forEach((uuid, player) -> {
                if (player.isOnline()) {
                    WebServer.getInstance().getServerHandler().sendPacket(player.getConn(), new WSSendChatMessage(String.join(" ", realArgs)));
                }
            });
            msg.reply("Successfully announced message.").queue();
        }

        if (msg.getContentRaw().startsWith("l!crash")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;
            PlayerManager.getPlayerMap().forEach((uuid, player) -> {
                if (player.isOnline()) {
                    if (player.getUsername().equalsIgnoreCase(args[1])) {
                        WebServer.getInstance().getServerHandler().sendPacket(player.getConn(), new WSPacketForceCrash());
                        msg.reply("Crashed " + args[1] + ".").queue();
                    }
                }
            });
        }

        if (msg.getContentRaw().startsWith("l!rank")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 3) return;
            PlayerManager.getPlayerMap().forEach((uuid, player) -> {
                if (player.isOnline()) {
                    if (player.getUsername().equalsIgnoreCase(args[1])) {
                        Rank rank = Rank.getRankById(Integer.parseInt(args[2]));
                        player.setRank(rank);
                        msg.reply("Gave " + args[1] + " the rank " + rank.getName() + ".").queue();
                    }
                }
            });
        }

        if (msg.getContentRaw().startsWith("l!players")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;
            String responseMsg = "Players on **" + args[1] + "**:";
            for (Player player : PlayerManager.getPlayerMap().values()) {
                if (player != null && player.isOnline() && player.getServer() != null) {
                    if (player.getServer().toLowerCase().contains(args[1])) {
                        responseMsg += "\n- `" + player.getUsername() + "`";
                    }
                }
            }

            List<String> strings = new ArrayList<String>();
            int index = 0;
            while (index < responseMsg.length()) {
                strings.add(responseMsg.substring(index, Math.min(index + 1999, responseMsg.length())));
                index += 1999;
            }

            for(String message : strings) {
                msg.reply(message).queue();
            }
        }

        if (msg.getContentRaw().startsWith("l!color")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 3) return;
            for (Player player : PlayerManager.getPlayerMap().values()) {
                if (player.getUsername().equalsIgnoreCase(args[1])) {
                    player.setCustomColor(Integer.parseInt(args[2]));
                    msg.reply("Set **" + args[1] + "**'s color to **" + args[2] + "**.").queue();
                }
            }
        }

        if(msg.getContentRaw().startsWith("l!emote")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;

            ServerHandler serverHandler = WebServer.getInstance().getServerHandler();

            for(Player player : PlayerManager.getPlayerMap().values()) {
                for(Player online : PlayerManager.getPlayerMap().values()) {
                    if (online != null && online.isOnline() && online.getServer() != null && player.getServer() != null) {
                        if (args.length == 2 ? online.getServer().toLowerCase().contains(player.getServer().toLowerCase()) : online.getServer().toLowerCase().contains(args[2].toLowerCase())) {
                            serverHandler.sendPacket(player.getConn(), new WSPacketSendEmote(online.getPlayerId(), Integer.parseInt(args[1])));
                        }
                    }
                }
            }

            if(args.length == 2) {
                msg.reply("Playing **" + args[1] + "** for all online users.").queue();
            } else {
                msg.reply("Playing **" + args[1] + "** for all users on **" + args[2] + "**.").queue();
            }
        }
    }
}
