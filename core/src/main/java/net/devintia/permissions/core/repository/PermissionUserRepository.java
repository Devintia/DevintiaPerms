package net.devintia.permissions.core.repository;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.devintia.permissions.core.dao.PermissionUser;
import net.devintia.permissions.core.exception.UserNotFoundException;
import org.mongodb.morphia.Datastore;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PermissionUserRepository {

    private final Datastore datastore;
    private final LoadingCache<UUID, PermissionUser> userCache = CacheBuilder.newBuilder()
            .expireAfterAccess( 10,TimeUnit.MINUTES )
            .build( new CacheLoader<UUID, PermissionUser>() {
                @Override
                public PermissionUser load( UUID playerUUID ) throws Exception {
                    Iterator<PermissionUser> iterator = datastore
                            .createQuery( PermissionUser.class )
                            .field( "uuid" ).equal( playerUUID )
                            .fetch();

                    if ( !iterator.hasNext() ) {
                        throw new UserNotFoundException();
                    }

                    return iterator.next();
                }
            } );

    public PermissionUserRepository( Datastore datastore ) {
        this.datastore = datastore;
    }

    public PermissionUser getUser( UUID playerUUID ) {
        try {
            return this.userCache.get( playerUUID );
        } catch ( Exception e ) {
            return null;
        }
    }

}
