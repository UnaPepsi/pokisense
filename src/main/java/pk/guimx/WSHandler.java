package pk.guimx;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WSHandler extends WebSocketClient{
    private List<UUID> playersUsingPoki;
    private UUID playerUUID;
    private JsonParser jsonParser;
    private String token;
    private PokiSense pokiSense;
    public WSHandler(UUID playerUUID, PokiSense pokiSense) throws URISyntaxException {
        super(new URI("wss://poki.guimx.me"));
        playersUsingPoki = new ArrayList<>();
        this.playerUUID = playerUUID;
        jsonParser = new JsonParser();
        this.pokiSense = pokiSense;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Bukkit.getLogger().info("PokiSenseWS: connected to WS");
        send(playerUUID.toString());
        Bukkit.getScheduler().runTaskTimer(pokiSense, () -> {
            send("Heartbeat");
        },0,5*20);
    }

    @Override
    public void onMessage(String message) {
        JsonObject jsonObject = jsonParser.parse(message).getAsJsonObject();
        if ("connected_players".equalsIgnoreCase(jsonObject.get("type").getAsString())) {
            JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("players");
            jsonArray.forEach(element -> {
                UUID uuid = UUID.fromString(element.getAsString());
                if (!playersUsingPoki.contains(uuid) && !uuid.equals(playerUUID)) {
                    playersUsingPoki.add(uuid);
                }
            });
        }else if ("broadcast_message".equalsIgnoreCase(jsonObject.get("type").getAsString())){
            JsonObject messageObject = jsonObject.getAsJsonObject("data");
            String from = messageObject.get("from").getAsString();
            String messageReceived = messageObject.get("message").getAsString();
            Bukkit.broadcastMessage(Utils.colorTranslate("&d(P) %s&r: %s",from,messageReceived));
        }else if ("auth".equalsIgnoreCase(jsonObject.get("type").getAsString())){
            this.token = jsonObject.getAsJsonObject("data").get("token").getAsString();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        playersUsingPoki.clear();
        //this = null; //awh :(
        this.pokiSense.setWsHandler(null); //LMFAOOO
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public List<UUID> getPlayersUsingPoki() {
        return playersUsingPoki;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getToken() {
        return token;
    }
}
