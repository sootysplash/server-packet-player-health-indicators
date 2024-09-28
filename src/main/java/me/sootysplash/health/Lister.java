package me.sootysplash.health;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Lister extends PacketListenerCommon {
    private static final String objectiveName = "HP";
    private static final Component displayName = Component.text(ChatColor.RED + "♥");
    private static final Map<String, Integer> last = Collections.synchronizedMap(new HashMap<>());
    private static final Map<UUID, User> registeredUsers = Collections.synchronizedMap(new HashMap<>());
    private static final Runnable hpUpdate = () -> {
        ArrayList<Player> player1 = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player rec : player1) {
            User user = registeredUsers.get(rec.getUniqueId());
            if (user != null) {
                ArrayList<Player> player2 = new ArrayList<>(Bukkit.getOnlinePlayers());
                for (Player other : player2) {
                    String name = other.getName();
                    int hp = (int) Math.floor(other.getHealth() + other.getAbsorptionAmount());
                    if (hp != last.getOrDefault(name+"/"+rec.getName(), -1)) {
                        user.sendPacket(new WrapperPlayServerUpdateScore(name, null, objectiveName, Optional.of(hp)));
                        last.put(name+"/"+rec.getName(), hp);
                    }
                }
            }
        }
    };

    public static void initIndicators() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), hpUpdate, 0, 1);
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> initForUser(event.getUser()), 20);
    }
    public void initForUser(User user) {
        user.sendPacket(new WrapperPlayServerScoreboardObjective(objectiveName, WrapperPlayServerScoreboardObjective.ObjectiveMode.CREATE, displayName, null));
        user.sendPacket(new WrapperPlayServerDisplayScoreboard(2, objectiveName));
        registeredUsers.put(user.getUUID(), user);
    }
    public void cleanUpForUser(User user) {
        user.sendPacket(new WrapperPlayServerScoreboardObjective(objectiveName, WrapperPlayServerScoreboardObjective.ObjectiveMode.REMOVE, displayName, null));
        registeredUsers.remove(user.getUUID(), user);
    }
}
