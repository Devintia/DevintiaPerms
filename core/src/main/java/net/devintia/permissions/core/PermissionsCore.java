package net.devintia.permissions.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import net.devintia.permissions.core.config.Config;
import net.devintia.permissions.core.dao.PermissionGroup;
import net.devintia.permissions.core.dao.PermissionUser;
import net.devintia.permissions.core.data.PermissionContainer;
import net.devintia.permissions.core.exception.UserNotFoundException;
import net.devintia.permissions.core.repository.PermissionGroupRepository;
import net.devintia.permissions.core.repository.PermissionUserRepository;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PermissionsCore {

    private final Config config;
    private final Datastore datastore;
    private final PermissionGroupRepository groupRepository;
    private final PermissionUserRepository userRepository;
    private final LoadingCache<UUID, PermissionContainer> cache = CacheBuilder.newBuilder()
            .expireAfterAccess( 10, TimeUnit.MINUTES )
            .refreshAfterWrite( 5, TimeUnit.MINUTES )
            .build( new CacheLoader<UUID, PermissionContainer>() {
                @Override
                public PermissionContainer load( UUID uuid ) throws Exception {
                    PermissionUser permissionUser = userRepository.getUser( uuid );
                    if ( permissionUser == null ) {
                        // Use default group
                        PermissionGroup defaultGroup = groupRepository.getGroup( config.getDefaultGroup() );
                        if ( defaultGroup == null ) {
                            throw new UserNotFoundException();
                        } else {
                            return new PermissionContainer( defaultGroup );
                        }
                    } else {
                        PermissionGroup permissionGroup = permissionUser.getGroup();
                        if ( permissionGroup == null ) {
                            permissionGroup = groupRepository.getGroup( config.getDefaultGroup() );
                            if ( permissionGroup == null ) {
                                throw new UserNotFoundException();
                            }
                        }

                        return new PermissionContainer( permissionGroup );
                    }
                }
            } );

    /**
     * Construct a new PermissionsCore. The Core holds the caches and connections to Mongo
     *
     * @param configurationFile The file which holds all configuration values
     * @throws Exception Any Exception which interrupted the initial process of the core
     */
    public PermissionsCore( File configurationFile ) throws Exception {
        // First of all load the config
        this.config = new Config( configurationFile );
        this.config.init();

        // Connect to mongo
        ServerAddress serverAddress = new ServerAddress( this.config.getDatabaseHost(), this.config.getDatabasePort() );
        MongoClient mongoClient = new MongoClient( serverAddress, new ArrayList<MongoCredential>(){{
            add( MongoCredential.createCredential( config.getDatabaseUser(), config.getDatabaseDatabase(), config.getDatabasePass().toCharArray() ) );
        }} );

        // Setup ORM
        Morphia morphia = new Morphia();
        this.datastore = morphia.createDatastore( mongoClient, this.config.getDatabaseDatabase() );

        // Repository
        this.groupRepository = new PermissionGroupRepository( this.datastore );
        this.userRepository = new PermissionUserRepository( this.datastore );
    }

    // --------------------------- Permission data container

    public PermissionContainer getPermissionContainer( UUID playerUUID ) {
        try {
            return this.cache.get( playerUUID );
        } catch ( Exception e ) {
            return null;
        }
    }

    // --------------------------- Refresh

    public void refresh( UUID playerUUID ) {
        this.cache.refresh( playerUUID );
    }

    // --------------------------- Metadata

    public void setMetadata( String group, String metaKey, String metaValue ) {
        PermissionGroup permissionGroup = this.groupRepository.getGroup( group );
        if ( permissionGroup != null ) {
            permissionGroup.getMetadata().put( metaKey, metaValue );
            this.datastore.save( permissionGroup );
        }
    }

    // --------------------------- Groups

    public boolean assignGroup( UUID playerUUID, String group ) {
        PermissionGroup permissionGroup = ( group == null ) ? null : this.groupRepository.getGroup( group );
        PermissionUser permissionUser = this.userRepository.getUser( playerUUID );
        if ( permissionUser == null ) {
            permissionUser = new PermissionUser();
            permissionUser.setUuid( playerUUID );
        }

        if ( group != null && permissionGroup == null ) {
            return false;
        }

        permissionUser.setGroup(permissionGroup);
        this.datastore.save(permissionUser);
        return true;
    }

    // --------------------------- Permissions

    public void removePermission( String group, String permission ) {
        PermissionGroup permissionGroup = this.groupRepository.getGroup( group );
        if ( permissionGroup != null ) {
            permissionGroup.getPermissions().remove( permission );
            this.datastore.save( permissionGroup );
        }
    }

    public void setPermission( String group, String permission, boolean value ) {
        PermissionGroup permissionGroup = this.groupRepository.getGroup( group );
        if ( permissionGroup != null ) {
            permissionGroup.getPermissions().put( permission, value );
            this.datastore.save( permissionGroup );
        }
    }

    public static void main( String[] args ) {
        try {
            PermissionsCore permissionsCore = new PermissionsCore( new File( "config.yml" ) );
            permissionsCore.getPermissionContainer( UUID.randomUUID() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
