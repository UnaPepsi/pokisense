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
    public WSHandler(UUID playerUUID) throws URISyntaxException {
        super(new URI("wss://poki.guimx.me"));
        playersUsingPoki = new ArrayList<>();
        this.playerUUID = playerUUID;
        jsonParser = new JsonParser();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Bukkit.getLogger().info("PokiSenseWS: connected to WS");
        send(playerUUID.toString());
    }

    @Override
    public void onMessage(String message) {
        JsonObject jsonObject = jsonParser.parse(message).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("players");
        jsonArray.forEach(element -> {
            UUID uuid = UUID.fromString(element.getAsString());
            if (!playersUsingPoki.contains(uuid) && !uuid.equals(playerUUID)){
                playersUsingPoki.add(uuid);
            }
        });
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        playersUsingPoki.clear();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public List<UUID> getPlayersUsingPoki() {
        return playersUsingPoki;
    }
}
