package pk.guimx;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.event.ApolloListener;
import com.lunarclient.apollo.event.EventBus;
import com.lunarclient.apollo.event.Listen;
import com.lunarclient.apollo.event.player.ApolloRegisterPlayerEvent;
import com.lunarclient.apollo.module.combat.CombatModule;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.recipients.Recipients;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class EventListener implements ApolloListener {
    public EventListener(){
        EventBus.getBus().register(this);
    }
    @Listen
    public void onJoin(ApolloRegisterPlayerEvent e){
        ApolloPlayer player = e.getPlayer();
        Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(),player.getUniqueId(), Nametag.builder()
                .lines(Lists.newArrayList( //no idea why I have to build it upside down
                        Component.text()
                                .content("MeeZoid")
                                .color(NamedTextColor.DARK_RED)
                                .build(),
                        Component.text()
                                .content("[Mod Mode]")
                                .color(NamedTextColor.GRAY)
                                .build()
                ))
                .build()
        );
        Apollo.getModuleManager().getModule(CombatModule.class).getOptions().set(CombatModule.DISABLE_MISS_PENALTY,true);
        player.sendMessage(Component.text("PokiSense", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(":",NamedTextColor.WHITE))
                .append(Component.text(" successfully registered :3",NamedTextColor.GREEN)));
        player.sendMessage(Component.text("PokiSense",NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(":",NamedTextColor.WHITE))
                .append(Component.text(" made by guimx :)",NamedTextColor.GREEN)));
    }
}
