package pk.guimx;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PokiSense extends JavaPlugin {
    private WSHandler wsHandler;
    public void onEnable(){
        new EventListener(this);
        MainCommand mainCommand = new MainCommand(this);
        //I don't want prefixes
        getCommand("headed").setExecutor(mainCommand);
        getCommand("speed").setExecutor(mainCommand);
        getCommand("fly").setExecutor(mainCommand);
        getCommand("connect").setExecutor(mainCommand);
        getCommand("glow").setExecutor(mainCommand);
        getCommand("cglow").setExecutor(mainCommand);
        getCommand("staff").setExecutor(mainCommand);
        getCommand("panic").setExecutor(mainCommand);
        getCommand("block").setExecutor(mainCommand);
        getCommand("kb").setExecutor(mainCommand);
        Bukkit.getConsoleSender().sendMessage("____            _      _   ____                              \n" +
                " |  _ \\    ___   | | __ (_) / ___|    ___   _ __    ___    ___ \n" +
                " | |_) |  / _ \\  | |/ / | | \\___ \\   / _ \\ | '_ \\  / __|  / _ \\\n" +
                " |  __/  | (_) | |   <  | |  ___) | |  __/ | | | | \\__ \\ |  __/\n" +
                " |_|      \\___/  |_|\\_\\ |_| |____/   \\___| |_| |_| |___/  \\___|");
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (wsHandler != null){
                wsHandler.getPlayersUsingPoki().forEach(uuid -> {
                    Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(),uuid, Nametag.builder()
                            .lines(Lists.newArrayList(
                                    Component.text()
                                            .content("P >> ")
                                            .color(NamedTextColor.LIGHT_PURPLE)
                                            .append(Component.text(Bukkit.getOfflinePlayer(uuid).getName(),NamedTextColor.GREEN))
                                            .build()
                            ))
                            .build()
                    );
                });
            }
        },0,70);
    }

    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage(Utils.colorTranslate("&dPokiSense&r: bye~!"));
        if (wsHandler != null){
            wsHandler.close();
        }
    }

    public WSHandler getWsHandler() {
        return wsHandler;
    }

    public void setWsHandler(WSHandler wsHandler) {
        this.wsHandler = wsHandler;
    }
}
