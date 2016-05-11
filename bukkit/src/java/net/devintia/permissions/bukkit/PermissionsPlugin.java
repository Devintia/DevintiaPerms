package net.devintia.permissions.bukkit;

import lombok.Getter;
import net.devintia.commons.bukkit.command.CommandHandler;
import net.devintia.permissions.bukkit.command.AssignGroupCommand;
import net.devintia.permissions.bukkit.listener.PlayerListener;
import net.devintia.permissions.core.PermissionsCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

/**
 * Created by Fabian on 09.05.2016.
 */
public class PermissionsPlugin extends JavaPlugin {

    @Getter
    private PermissionsCore core;

    @Override
    public void onEnable() {
        CommandHandler commandHandler = new CommandHandler( this );
        commandHandler.register( new AssignGroupCommand( this ) );

        try {
            this.core = new PermissionsCore( new File( getDataFolder(), "config.yml" ) );

            // Attach listeners
            Bukkit.getPluginManager().registerEvents( new PlayerListener( this ), this );

            // Reload all online players
            Bukkit.getScheduler().runTaskTimer( this, new Runnable() {
                @Override
                public void run() {
                    for ( Player player : Bukkit.getOnlinePlayers() ) {
                        getCore().refresh( player.getUniqueId() );
                    }
                }
            }, 5 * 60 * 20, 5 * 60 * 20 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void setBukkitPermissions( Player player ) {
        Map<String, Boolean> permissions = this.getCore().getPermissionContainer( player.getUniqueId() ).getPermissions();

        String permName = "devintiaPermissions_player." + player.getUniqueId().toString();
        Permission perm = Bukkit.getPluginManager().getPermission( permName );
        boolean hasPermissionAttachment = player.isPermissionSet( permName ) && player.hasPermission( permName );

        if ( perm == null ) {
            perm = new Permission( permName, PermissionDefault.FALSE, permissions );
            Bukkit.getPluginManager().addPermission( perm );
        } else {
            perm.getChildren().clear();
            perm.getChildren().putAll( permissions );
        }

        perm.recalculatePermissibles();

        if ( !hasPermissionAttachment ) {
            player.addAttachment( this, perm.getName(), true );
        }
    }

    public void removeBukkitPermissions( Player player ) {
        final String permName = "devintiaPermissions_player." + player.getUniqueId().toString();
        Bukkit.getPluginManager().removePermission( permName );
    }

}
