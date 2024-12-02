package ch.heigvd.dai.auth;

import ch.heigvd.dai.users.User;
import io.javalin.http.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthController {
  private final ConcurrentHashMap<Integer, User> users;
  private final ConcurrentHashMap<Integer, Integer> usersCache = new ConcurrentHashMap<>();

  public AuthController(ConcurrentHashMap<Integer, User> users) {
    this.users = users;
  }

  public void login(Context ctx) {
    Integer etag = ctx.headerAsClass("If-None-Match", Integer.class).getOrDefault(null);

    if (etag != null && usersCache.containsValue(etag)) {
      throw new NotModifiedResponse();
    }

    User loginUser =
        ctx.bodyValidator(User.class)
            .check(obj -> obj.email != null, "Missing email")
            .check(obj -> obj.password != null, "Missing password")
            .get();

    for (User user : users.values()) {
      if (user.email.equalsIgnoreCase(loginUser.email)
          && user.password.equals(loginUser.password)) {
        Integer userHash = user.hashCode();
        usersCache.put(user.id, userHash);

        ctx.cookie("user", String.valueOf(user.id));
        ctx.header("ETag", String.valueOf(userHash));
        ctx.status(HttpStatus.NO_CONTENT);
        return;
      }
    }

    throw new UnauthorizedResponse();
  }

  public void logout(Context ctx) {
    ctx.removeCookie("user");
    ctx.status(HttpStatus.NO_CONTENT);
  }

  public void profile(Context ctx) {
    String userId = ctx.cookie("user");

    if (userId == null) {
      throw new UnauthorizedResponse();
    }

    Integer etag = ctx.headerAsClass("If-None-Match", Integer.class).getOrDefault(null);

    if (etag != null && usersCache.get(Integer.parseInt(userId)).equals(etag)) {
      throw new NotModifiedResponse();
    }
    User user = users.get(Integer.parseInt(userId));

    if (user == null) {
      throw new UnauthorizedResponse();
    }

    Integer userHash = user.hashCode();
    usersCache.put(user.id, userHash);

    ctx.header("ETag", String.valueOf(userHash));
    ctx.json(user);
  }
}
