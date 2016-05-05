package net.devintia.permissions.core.repository;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.devintia.permissions.core.dao.PermissionGroup;
import net.devintia.permissions.core.exception.GroupNotFoundException;
import org.mongodb.morphia.Datastore;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PermissionGroupRepository {

    private final Datastore datastore;
    private final LoadingCache<String, PermissionGroup> groupCache = CacheBuilder.newBuilder()
            .refreshAfterWrite( 2, TimeUnit.MINUTES )
            .build( new CacheLoader<String, PermissionGroup>() {
                @Override
                public PermissionGroup load( String groupName ) throws Exception {
                    Iterator<PermissionGroup> iterator = datastore
                            .createQuery( PermissionGroup.class )
                            .field( "name" ).equal( groupName )
                            .fetch();

                    if ( !iterator.hasNext() ) {
                        throw new GroupNotFoundException();
                    }

                    return iterator.next();
                }
            } );

    public PermissionGroupRepository( Datastore datastore ) {
        this.datastore = datastore;
    }

    /**
     * Check if a group already exists
     *
     * @param groupName The groupName which should be checked
     * @return True when the group exists, false when it doesn't
     */
    public boolean checkIfExists( String groupName ) {
        try {
            return this.groupCache.get( groupName ) != null;
        } catch ( Exception e ) {
            return false;
        }
    }

    /**
     * Get a group from their name
     *
     * @param groupName The name of group which we want to lookup
     * @return The group which we found or null when we found nutting
     */
    public PermissionGroup getGroup( String groupName ) {
        try {
            return this.groupCache.get( groupName );
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

}
