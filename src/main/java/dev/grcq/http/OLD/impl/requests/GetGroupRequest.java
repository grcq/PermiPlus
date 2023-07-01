package dev.grcq.http.OLD.impl.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import dev.grcq.http.OLD.IHttpRequest;
import dev.grcq.http.OLD.Path;
import dev.grcq.http.OLD.RequestType;
import dev.grcq.http.OLD.Type;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.group.Group;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Type(RequestType.GET)
@Path("/api/group/fetch")
public class GetGroupRequest implements IHttpRequest {

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        List<Group> groups = PermiPlus.getInstance().getGroupHandler().getGroups();
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        for (Group group : groups) {
            array.add(PermiPlus.GSON.toJson(group));
        }

        object.add("groups", array);

        OutputStream os = exchange.getResponseBody();
        os.write(object.toString().getBytes());
        os.close();
    }
}
