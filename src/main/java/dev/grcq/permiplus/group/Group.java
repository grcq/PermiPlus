package dev.grcq.permiplus.group;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.database.MySQL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class Group {

    @NotNull
    private String displayName;
    @NotNull
    private String name;

    @NotNull
    private String prefix;
    @NotNull
    private String suffix;
    @Nullable
    private String colour;

    private int priority;
    @NotNull
    private final List<String> permissions = new ArrayList<>();
    @NotNull
    private final List<String> parents = new ArrayList<>();

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

    @SneakyThrows
    public void save() {
        MySQL mySQL = PermiPlus.getInstance().getMySQL();

        mySQL.update("UPDATE groups SET displayName='" + displayName + "', prefix='" + prefix +
                "', suffix='" + suffix + "', colour=" + (colour != null ? "'" + colour + "'" : "NULL") + ", priority=" + priority +
                " WHERE name='" + name + "'");

        mySQL.update("DELETE FROM group_permissions WHERE group='%s'".formatted(name));
        mySQL.update("DELETE FROM group_parents WHERE group='%s'".formatted(name));

        for (String parent : parents) {
            mySQL.update("INSERT INTO group_parents (group, parent) VALUES ('%s', '%s')".formatted(name, parent));
        }

        for (String perm : permissions) {
            mySQL.update("INSERT INTO group_permissions(group, permission) VALUES ('%s', '%s')".formatted(name, perm));
        }

    }

}
