package dev.grcq.permiplus.profile;

import com.google.common.collect.Lists;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.database.MySQL;
import dev.grcq.permiplus.group.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Profile {

    @NotNull
    private final String uuidString;
    @NotNull
    private String username;

    @Nullable
    private String prefix = null;
    @Nullable
    private String suffix = null;

    @NotNull
    private final List<String> parents = new ArrayList<>();
    @NotNull
    private final List<String> permissions = new ArrayList<>();

    public List<String> getFullPermissions() {
        List<String> perms = Lists.newArrayList(this.permissions);

        for (String parent : this.parents) {
            Group group = PermiPlus.getInstance().getGroupHandler().getGroup(parent);
            for (String perm : group.getFullPermissions()) {
                if (!perms.contains(perm)) perms.add(perm);
            }
        }

        return perms;
    }

    @NotNull
    public UUID getUUID() {
        return UUID.fromString(this.uuidString);
    }

    public void save() {
        MySQL mySQL = PermiPlus.getInstance().getMySQL();

        mySQL.update(
                "UPDATE profiles SET username='%s', prefix=%s, suffix=%s WHERE uuid='%s'"
                        .formatted(username, (prefix == null ? "NULL" : "'" + prefix + "'"), (suffix == null ? "NULL" : "'" + suffix + "'"), uuidString)
        );

        mySQL.update("DELETE FROM profile_permissions WHERE uuid='%s'".formatted(uuidString));
        mySQL.update("DELETE FROM profile_parents WHERE uuid='%s'".formatted(uuidString));

        for (String parent : parents) {
            mySQL.update("INSERT INTO profile_parents (uuid, parent) VALUES ('%s', '%s')".formatted(uuidString, parent));
        }

        for (String perm : permissions) {
            mySQL.update("INSERT INTO profile_permissions(uuid, permission) VALUES ('%s', '%s')".formatted(uuidString, perm));
        }
    }
}
