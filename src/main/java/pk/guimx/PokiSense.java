package pk.guimx;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PokiSense extends JavaPlugin {

    public void onEnable(){
        new EventListener();
        MainCommand mainCommand = new MainCommand(this);
        //I don't want prefixes
        getCommand("headed").setExecutor(mainCommand);
        getCommand("fly").setExecutor(mainCommand);
        getCommand("connect").setExecutor(mainCommand);
        getCommand("glow").setExecutor(mainCommand);
        getCommand("cglow").setExecutor(mainCommand);
        getCommand("staff").setExecutor(mainCommand);
        getCommand("panic").setExecutor(mainCommand);
        Bukkit.getConsoleSender().sendMessage("____            _      _   ____                              \n" +
                " |  _ \\    ___   | | __ (_) / ___|    ___   _ __    ___    ___ \n" +
                " | |_) |  / _ \\  | |/ / | | \\___ \\   / _ \\ | '_ \\  / __|  / _ \\\n" +
                " |  __/  | (_) | |   <  | |  ___) | |  __/ | | | | \\__ \\ |  __/\n" +
                " |_|      \\___/  |_|\\_\\ |_| |____/   \\___| |_| |_| |___/  \\___|");
    }

    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage(Utils.colorTranslate("&dPokiSense&r: bye~!"));
    }
}
