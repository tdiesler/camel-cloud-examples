package org.apache.camel.oauth.vertx;

import java.text.ParseException;
import java.util.Map;

import com.nimbusds.jose.util.JSONObjectUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import org.apache.camel.oauth.UserProfile;

public class UserProfileVertx extends UserProfile {

    private final User vtxUser;

    public UserProfileVertx(User vtxUser) {
        super(deepMap(vtxUser.attributes()), deepMap(vtxUser.principal()));
        this.vtxUser = vtxUser;
    }

    public User getVertxUser() {
        return this.vtxUser;
    }

    private static Map<String, Object> deepMap(JsonObject obj) {
        try {
            return JSONObjectUtils.parse(obj.encode());
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

}
