package dev.grcq.permiplus.permission;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PermissionNode {

    private final String node;

    public boolean match(String permission) {
        if (node.equals(permission)) {
            return true;
        }

        String[] nodeParts = node.split("\\.");
        String[] permissionParts = permission.split("\\.");

        if (nodeParts.length != permissionParts.length) {
            return false;
        }

        for (int i = 0; i < nodeParts.length; i++) {
            String nodePart = nodeParts[i];
            String permissionPart = permissionParts[i];

            if (!nodePart.equals("*") && !nodePart.equals(permissionPart)) {
                return false;
            }
        }

        return true;
    }


}
