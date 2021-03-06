package net.devintia.permissions.core.dao;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.UUID;

/**
 * @author geNAZt
 * @version 1.0
 */
@Entity( value = "permissionUser", noClassnameStored = true )
public class PermissionUser {
    @Id
    private ObjectId id;
    @Getter @Setter
    private UUID uuid;
    @Reference @Getter @Setter
    private PermissionGroup group;
}
