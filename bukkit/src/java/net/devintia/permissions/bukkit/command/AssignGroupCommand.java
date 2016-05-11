package net.devintia.permissions.bukkit.command;

import com.google.common.base.Charsets;
import lombok.AllArgsConstructor;
import net.devintia.commons.bukkit.command.CommandArguments;
import net.devintia.commons.bukkit.command.CommandInfo;
import net.devintia.permissions.bukkit.PermissionsPlugin;
import net.devintia.permissions.core.dao.PermissionGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Created by Fabian on 09.05.2016.
 */
@AllArgsConstructor
public class AssignGroupCommand {
    private final PermissionsPlugin plugin;

    @CommandInfo( name = "dperms.setgroup", perm = "dperms.command.setgroup", usage = "/dperms setgroup <player> <group>", allowConsole = false )
    public void assignGroup( CommandArguments arguments ) {
        // Check for valid arguments
        if ( arguments.getNumArgs() != 2 ) {
            arguments.getPlayer().sendMessage( "/dperms setgroup <player> <group>" );
            return;
        }

        // Check if we can resolve the players uuid
        String name = arguments.getArg( 0 );
        UUID predictedOfflineUUID = UUID.nameUUIDFromBytes( ( "OfflinePlayer:" + name ).getBytes( Charsets.UTF_8 ) );
        OfflinePlayer playerToAssignTo = Bukkit.getOfflinePlayer( name );
        if ( playerToAssignTo == null || playerToAssignTo.getUniqueId().equals( predictedOfflineUUID ) ) {
            arguments.getPlayer().sendMessage( "Player given is invalid." );
            return;
        }

        // Check for special case "-"
        if ( arguments.getArg( 1 ).equals( "-" ) ) {
            this.plugin.getCore().assignGroup( playerToAssignTo.getUniqueId(), null );
            arguments.getPlayer().sendMessage( "Set player to default group" );
            return;
        }

        // Assign group
        if ( !this.plugin.getCore().assignGroup( playerToAssignTo.getUniqueId(), arguments.getArg( 1 ) ) ) {
            arguments.getPlayer().sendMessage( "Could not change group. Please check your groupname" );
        }
    }
}
