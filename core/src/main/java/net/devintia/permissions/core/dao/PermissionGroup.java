package net.devintia.permissions.core.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geNAZt
 * @version 1.0
 */
@NoArgsConstructor
@Entity( value = "permissionGroup", noClassnameStored = true )
public class PermissionGroup {
    @Id @Getter
    private ObjectId id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private Map<String, Boolean> permissions = new HashMap<>();
    @Reference @Getter @Setter
    private List<PermissionGroup> inheritance = new ArrayList<>();
    @Getter @Setter
    private int priority;
    @Getter @Setter
    private Map<String, String> metadata = new HashMap<>();

    public PermissionGroup( String name ) {
        this.name = name;
    }
}
