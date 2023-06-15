package dev.grcq.permiplus.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

}
