package pk.guimx;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.glow.GlowModule;
import com.lunarclient.apollo.module.staffmod.StaffMod;
import com.lunarclient.apollo.module.staffmod.StaffModModule;
import com.lunarclient.apollo.recipients.Recipients;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class MainCommand implements CommandExecutor {
    private PokiSense pokiSense;
    private Cache cache;
    private ArrayList<UUID> playersToGlow;
    private BukkitRunnable glowPlayersLoop;
    public MainCommand(PokiSense pokiSense){
        this.pokiSense = pokiSense;
        cache = new Cache();
        playersToGlow = new ArrayList<>();
        glowPlayersLoop = new BukkitRunnable(){
            @Override
            public void run() {
                for (UUID uuid : playersToGlow) {
                    Apollo.getModuleManager().getModule(GlowModule.class).overrideGlow(Recipients.ofEveryone(),
                            uuid, Color.RED);
                }
            }
        };
        glowPlayersLoop.runTaskTimer(pokiSense,0,20);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args){
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        sender.sendMessage(command.getName());
        switch (command.getName().toLowerCase()){
            case "headed":
                players.forEach(p -> {
                    if (p.getGameMode() == GameMode.SURVIVAL){
                        p.setGameMode(GameMode.CREATIVE);
                        p.sendMessage(Utils.colorTranslate("&dPokiSense&r: &aIn this video we do everything for entertainment purposes guys"));
                        Utils.displayNotification(Component.text("Headed!!!",NamedTextColor.GREEN),Component.text("sub 2 headed"));
                    new BukkitRunnable(){
                      @Override
                      public void run(){
                          if (p.getGameMode() == GameMode.SURVIVAL){
                              cancel();
                          }
                          PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(Utils.colorTranslate("&a¯\\_(ツ)_/¯")), (byte)2);
                          ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                      }
                    }.runTaskTimer(pokiSense,0,1);
                    }else{
                        p.setGameMode(GameMode.SURVIVAL);
                        p.sendMessage(Utils.colorTranslate("&dPokiSense&r: &cI fell in the void are you kidding me"));
                        Utils.displayNotification(Component.text("no more headed :(",NamedTextColor.RED),Component.text("im still a fan"));
                    }
                    p.setAllowFlight(false);
                });
                break;
            case "fly":
                players.forEach(p -> {
                    if (args.length == 0) {
                        if (p.getAllowFlight()) {
                            p.setAllowFlight(false);
                            p.setFlying(false);
                            p.sendMessage(Utils.colorTranslate("&dPokiSense&r: &cflying disabled"));
                        }else{
                            p.setAllowFlight(true);
                            p.sendMessage(Utils.colorTranslate("&dPokiSense&r: &aflying enabled"));
                        }
                    }else{
                        float flySpeed = Float.parseFloat(args[0]);
                        if (flySpeed/10 > 1){
                            flySpeed = 10.0f;
                        }
                        p.setAllowFlight(true);
                        p.setFlySpeed(flySpeed/10);
                        p.sendMessage(Utils.colorTranslate("&dPokiSense&r: &aflying enabled with speed %.1f woo!",flySpeed));
                    }
                });
                break;
            case "connect":
                if (args.length == 0){
                    sender.sendMessage(Utils.colorTranslate("&dPokiSense&r: &cyou must provide a server address"));
                    return false;
                }
                long delay;
                if (args.length == 1){
                    delay = 10;
                }else{
                    delay = Long.parseLong(args[1]);
                }
                players.forEach(p -> p.kickPlayer(":v"));
                try {
                    Runtime.getRuntime().exec("cmd /c start lunarclient://play?serverAddress=localhost");
                    Bukkit.getScheduler().runTaskLater(pokiSense,() -> {
                        try {
                            Runtime.getRuntime().exec("cmd /c start lunarclient://play?serverAddress=" + args[0]);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    },delay);
                }catch (IOException e){
                    e.printStackTrace();
                }
                sender.sendMessage(Utils.colorTranslate("&dPokiSense&r: if you didn't properly connect, disconnect from every server and try again." +
                        "\notherwise lunar might just crash (lol)"));
                break;
            case "glow":
                if (args.length == 0){
                    sender.sendMessage(Utils.colorTranslate("&dPokiSense&r: &cyou must provide players separated with a comma"));
                    return false;
                }
                for (String p : args){
                    Bukkit.getScheduler().runTaskAsynchronously(pokiSense,() -> {
                        try {
                            UUID uuid;
                            if (cache.getCachedUUIDs().containsKey(p)){
                                uuid = cache.getCachedUUIDs().get(p);
                            }else {
                                uuid = Utils.getUUIDFromName(p);
                            }
                            sender.sendMessage(uuid.toString());
                            playersToGlow.add(uuid);
                            Bukkit.broadcastMessage("done "+p);
                            Utils.displayNotification(Component.text("Glow applied", NamedTextColor.GREEN),
                                    Component.text("If "+p+" is in render distance, they should glow",NamedTextColor.WHITE));
                        }catch (IOException e){
                            Utils.displayNotification(Component.text("Player fetch failed", NamedTextColor.RED),
                                    Component.text("Couldn't fetch UUID of player "+p+". Are they premium?",NamedTextColor.WHITE));
                            Bukkit.broadcastMessage("couldn't fetch "+p);
                        }
                    });
                }
                break;
            case "cglow":
                String data;
                try {
                    data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                try {
                    UUID uuid;
                    if (cache.getCachedUUIDs().containsKey(data)){
                        uuid = cache.getCachedUUIDs().get(data);
                    }else {
                        uuid = Utils.getUUIDFromName(data);
                    }
                    playersToGlow.add(uuid);
                    Utils.displayNotification(Component.text("Glow applied", NamedTextColor.GREEN),
                            Component.text("If "+data+" is in render distance, they should glow",NamedTextColor.WHITE));
                }catch (IOException e) {
                    Utils.displayNotification(Component.text("Player fetch failed", NamedTextColor.RED),
                            Component.text("Couldn't fetch UUID of player " + data + ". Are they premium?", NamedTextColor.WHITE));
                    Bukkit.broadcastMessage("couldn't fetch " + data);
                }
                break;
            case "staff":
                Apollo.getModuleManager().getModule(StaffModModule.class).enableStaffMods(Recipients.ofEveryone(), Collections.singletonList(StaffMod.XRAY));
                Bukkit.broadcastMessage(Utils.colorTranslate("&dPokiSense&r: &aenabled staff modules"));
                Utils.displayNotification(Component.text("X-Ray",NamedTextColor.GREEN),Component.text("Enabled x-ray"));
                break;
            case "panic":
                Apollo.getModuleManager().getModule(StaffModModule.class).disableStaffMods(Recipients.ofEveryone(), Collections.singletonList(StaffMod.XRAY));
                players.forEach(p -> {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setFlying(false);
                    p.setAllowFlight(false);
                    p.setFlySpeed(0.1f);
                    playersToGlow.clear();
                    p.sendMessage(Utils.colorTranslate("&dPokiSense&r: gl"));
                });
                glowPlayersLoop.cancel();
                Apollo.getModuleManager().getModule(GlowModule.class).resetGlow(Recipients.ofEveryone());

        }
        return true;
    }
}