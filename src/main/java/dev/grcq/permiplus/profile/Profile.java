package dev.grcq.permiplus.profile;

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

    @NotNull
    public UUID getUUID() {
        return UUID.fromString(this.uuidString);
    }
}
