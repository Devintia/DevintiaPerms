package net.devintia.permissions.bukkit.listener;

import lombok.AllArgsConstructor;
import net.devintia.permissions.bukkit.PermissionsPlugin;
import net.devintia.permissions.core.data.PermissionContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Fabian on 09.05.2016.
 */
@AllArgsConstructor
public class PlayerListener implements Listener {

    private final PermissionsPlugin permissionsPlugin;

    @EventHandler
    public void onPlayerPreJoin( AsyncPlayerPreLoginEvent event ) {
        PermissionContainer permissions = this.permissionsPlugin.getCore().getPermissionContainer( event.getUniqueId() );
        if ( permissions == null ) {
            event.disallow( AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Could not load permissions" );
        }
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerLogin( PlayerLoginEvent event ) {
        this.permissionsPlugin.setBukkitPermissions( event.getPlayer() );
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onPlayerLoginMonitor( PlayerLoginEvent event ) {
        if ( event.getResult() != PlayerLoginEvent.Result.ALLOWED ) {
            this.permissionsPlugin.removeBukkitPermissions( event.getPlayer() );
        }
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerJoin( PlayerJoinEvent event ) {
        this.permissionsPlugin.setBukkitPermissions( event.getPlayer() );
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onPlayerQuit( PlayerQuitEvent event ) {
        this.permissionsPlugin.removeBukkitPermissions( event.getPlayer() );
    }

}
