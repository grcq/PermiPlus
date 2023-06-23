package dev.grcq.permiplus.group;

import com.google.gson.JsonObject;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.database.MySQL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class Group {

    private int id;
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
    private final List<String> parentIds = new ArrayList<>();


    public void save() {
        MySQL mySQL = PermiPlus.getInstance().getMySQL();

        mySQL.update("UPDATE groups SET displayName='" + displayName + "', name='" + name + "', prefix='" + prefix +
                "', suffix='" + suffix + "', colour=" + (colour != null ? "'" + colour + "'" : "null") + ", priority=" + priority + " WHERE id=" + id);
    }

}
