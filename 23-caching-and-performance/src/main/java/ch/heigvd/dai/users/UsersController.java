package ch.heigvd.dai.users;

import io.javalin.http.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsersController {
  private final ConcurrentHashMap<Integer, User> users;
  private final AtomicInteger userId = new AtomicInteger(0);
  private final ConcurrentHashMap<Integer, Integer> usersCache = new ConcurrentHashMap<>();
  private final Integer ALL_USERS_ID = -1;

  public UsersController(ConcurrentHashMap<Integer, User> users) {
    this.users = users;
  }

  public void create(Context ctx) {
    Integer etag = ctx.headerAsClass("If-None-Match", Integer.class).getOrDefault(null);

    if (etag != null && usersCache.containsValue(etag)) {
      throw new NotModifiedResponse();
    }

    User newUser =
        ctx.bodyValidator(User.class)
            .check(obj -> obj.firstName != null, "Missing first name")
            .check(obj -> obj.lastName != null, "Missing last name")
            .check(obj -> obj.email != null, "Missing email")
            .check(obj -> obj.password != null, "Missing password")
            .get();

    for (User user : users.values()) {
      if (user.email.equalsIgnoreCase(newUser.email)) {
        throw new ConflictResponse();
      }
    }

    User user = new User();

    user.id = userId.getAndIncrement();
    user.firstName = newUser.firstName;
    user.lastName = newUser.lastName;
    user.email = newUser.email;
    user.password = newUser.password;

    users.put(user.id, user);

    Integer userHash = user.hashCode();

    usersCache.put(user.id, userHash);
    usersCache.remove(ALL_USERS_ID);

    ctx.status(HttpStatus.CREATED);
    ctx.header("ETag", String.valueOf(userHash));
    ctx.json(user);
  }

  public void getOne(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();
    Integer etag = ctx.headerAsClass("If-None-Match", Integer.class).getOrDefault(null);

    if (etag != null && usersCache.get(id).equals(etag)) {
      throw new NotModifiedResponse();
    }

    User user = users.get(id);

    if (user == null) {
      throw new NotFoundResponse();
    }

    Integer userHash = user.hashCode();
    usersCache.put(user.id, userHash);

    ctx.header("ETag", String.valueOf(userHash));
    ctx.json(user);
  }

  public void getMany(Context ctx) {
    Integer etag = ctx.headerAsClass("If-None-Match", Integer.class).getOrDefault(null);

    if (etag != null
        && usersCache.contains(ALL_USERS_ID)
        && usersCache.get(ALL_USERS_ID).equals(etag)) {
      throw new NotModifiedResponse();
    }

    String firstName = ctx.queryParam("firstName");
    String lastName = ctx.queryParam("lastName");

    List<User> users = new ArrayList<>();

    for (User user : this.users.values()) {
      if (firstName != null && !user.firstName.equalsIgnoreCase(firstName)) {
        continue;
      }

      if (lastName != null && !user.lastName.equalsIgnoreCase(lastName)) {
        continue;
      }

      users.add(user);
    }

    Integer usersHash = users.hashCode();
    usersCache.put(ALL_USERS_ID, usersHash);

    ctx.header("ETag", String.valueOf(usersHash));
    ctx.json(users);
  }

  public void update(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();
    Integer etag = ctx.headerAsClass("If-Match", Integer.class).getOrDefault(null);

    if (etag != null && !usersCache.get(id).equals(etag)) {
      throw new PreconditionFailedResponse();
    }

    User updateUser =
        ctx.bodyValidator(User.class)
            .check(obj -> obj.firstName != null, "Missing first name")
            .check(obj -> obj.lastName != null, "Missing last name")
            .check(obj -> obj.email != null, "Missing email")
            .check(obj -> obj.password != null, "Missing password")
            .get();

    User user = users.get(id);

    if (user == null) {
      throw new NotFoundResponse();
    }

    user.firstName = updateUser.firstName;
    user.lastName = updateUser.lastName;
    user.email = updateUser.email;
    user.password = updateUser.password;

    users.put(id, user);

    Integer userHash = user.hashCode();
    usersCache.put(user.id, userHash);
    usersCache.remove(ALL_USERS_ID);

    ctx.header("ETag", String.valueOf(userHash));
    ctx.json(user);
  }

  public void delete(Context ctx) {
    Integer id = ctx.pathParamAsClass("id", Integer.class).get();
    Integer etag = ctx.headerAsClass("If-Match", Integer.class).getOrDefault(null);

    if (etag != null && !usersCache.get(id).equals(etag)) {
      throw new PreconditionFailedResponse();
    }

    if (!users.containsKey(id)) {
      throw new NotFoundResponse();
    }

    users.remove(id);
    usersCache.remove(id);
    usersCache.remove(ALL_USERS_ID);

    ctx.status(HttpStatus.NO_CONTENT);
  }
}
