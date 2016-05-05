package net.devintia.permissions.core.dao;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
@Entity( value = "permissionGroup", noClassnameStored = true )
public class PermissionGroup {
    @Id @Getter
    private ObjectId id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private Map<String, Boolean> permissions;
    @Reference @Getter @Setter
    private List<PermissionGroup> inheritance;
    @Getter @Setter
    private int priority;

    public PermissionGroup( String name ) {
        this.name = name;
        this.permissions = new HashMap<>();
    }
}
