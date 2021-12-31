package me.polishkrowa.BetterMsgPlus;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReplyCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't reply to messages !");
            return true;
        }
        Player player = (Player) sender;

        if (!MsgPlus.lastReceived.containsKey(player.getUniqueId()) || MsgPlus.lastReceived.get(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You have no one to reply to !");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "No message was entered !");
            return true;
        }


        String message = StringUtils.join(args, " ");
        message = message.trim();

        TextComponent output = new TextComponent();
        String [] parts = message.split("\\s+");

        for (int i = 0; i < parts.length; i++) {
            try {
                URL url = new URL(parts[i]);
                TextComponent link = new TextComponent(parts[i]);
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, parts[i]));
                output.addExtra(link);

            } catch (MalformedURLException e) {
                // If there was an URL that was not it!...
                output.addExtra(parts[i]);
            }
            if (i != parts.length - 1)
                output.addExtra(" ");
        }

        //  "commands.message.display.outgoing": "You whisper to %s: %s",
        //  "commands.message.display.incoming": "%s whispers to you: %s",
        Player to = Bukkit.getPlayer(MsgPlus.lastReceived.get(player.getUniqueId()));

        TranslatableComponent outgoing = new TranslatableComponent( "commands.message.display.outgoing" );
        outgoing.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        outgoing.addWith(to.getName());
        outgoing.addWith(output);
        sender.spigot().sendMessage(outgoing);

        TranslatableComponent incoming = new TranslatableComponent( "commands.message.display.incoming" );
        incoming.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        incoming.addWith(sender.getName());
        incoming.addWith(output);
        to.spigot().sendMessage(incoming);


        MsgPlus.lastReceived.put(player.getUniqueId(), to.getUniqueId());
        MsgPlus.lastReceived.put(to.getUniqueId(), player.getUniqueId());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> returns = new ArrayList<>();
        return returns;
    }
}
