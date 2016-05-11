package net.devintia.permissions.core.data;

import lombok.Getter;
import net.devintia.permissions.core.dao.PermissionGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PermissionContainer {

    @Getter private Map<String, Boolean> permissions = new HashMap<>();
    private final PermissionGroup group;

    public PermissionContainer( PermissionGroup startGroup ) {
        this.group = startGroup;

        // Sort all inheritance
        List<PermissionGroup> groups = new ArrayList<>();
        groups.add( startGroup );

        // Check for inheritance
        if ( startGroup.getInheritance().size() > 0 ) {
            resolveInheritance( startGroup, groups );
        }

        // Reverse the list and calc effective permissions
        Collections.reverse( groups );
        for ( PermissionGroup group : groups ) {
            for ( Map.Entry<String, Boolean> stringBooleanEntry : group.getPermissions().entrySet() ) {
                this.permissions.put( stringBooleanEntry.getKey(), stringBooleanEntry.getValue() );
            }
        }
    }

    private void resolveInheritance( PermissionGroup group, List<PermissionGroup> groups ) {
        List<PermissionGroup> unsorted = new ArrayList<>( group.getInheritance() );
        Collections.sort( unsorted, new Comparator<PermissionGroup>() {
            @Override
            public int compare( PermissionGroup o1, PermissionGroup o2 ) {
                return Integer.compare( o1.getPriority(), o2.getPriority() );
            }
        } );

        // Iterate over all groups
        for ( PermissionGroup permissionGroup : unsorted ) {
            groups.add( permissionGroup );

            // Check if group has inheritance
            if ( permissionGroup.getInheritance().size() > 0 ) {
                resolveInheritance( permissionGroup, groups );
            }
        }
    }

    public String getMetadata( String key ) {
        return this.group.getMetadata().get( key );
    }

    public boolean hasPermission( String permission ) {
        Boolean perm = permissions.get( permission );
        return perm == null ? false : perm;
    }

}
