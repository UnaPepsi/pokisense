package pk.guimx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.notification.Notification;
import com.lunarclient.apollo.module.notification.NotificationModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.recipients.Recipients;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Utils {
    public static String colorTranslate(String message, Object... args){
        return ChatColor.translateAlternateColorCodes('&',String.format(message,args));
    }

    public static UUID getUUIDFromName(String premiumPlayer) throws IOException {
        //this is blocking
        URL url = new URL("https://playerdb.co/api/player/minecraft/"+premiumPlayer);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept","application/json");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        if (conn.getResponseCode() != 200){
            throw new IOException("Invalid User");
        }
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();
        JsonObject jsonResponse = new JsonParser().parse(content.toString()).getAsJsonObject();
        return UUID.fromString(jsonResponse.getAsJsonObject("data")
                .getAsJsonObject("player")
                .get("id").getAsString());
    }
    public static void displayNotification(Component title, Component description) {
        Apollo.getModuleManager().getModule(NotificationModule.class).displayNotification(Recipients.ofEveryone(), Notification.builder()
                .titleComponent(title)
                .descriptionComponent(description)
                .resourceLocation("icons/herbert.png")
                .displayTime(Duration.ofSeconds(5))
                .build());
    }
}


