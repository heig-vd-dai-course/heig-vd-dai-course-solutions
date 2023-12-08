package ch.heigvd.users;

import io.javalin.http.*;
import io.javalin.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsersController {

    private final ConcurrentHashMap<Integer, User> users;
    private final AtomicInteger userId = new AtomicInteger();

    public UsersController(ConcurrentHashMap<Integer, User> users) {
        this.users = users;
    }

    public void createUser(Context ctx) {
        User newUser = ctx.bodyValidator(User.class)
                .check(obj -> obj.firstName != null, "Missing first name")
                .check(obj -> obj.lastName != null, "Missing last name")
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .getOrThrow(message -> new BadRequestResponse());

        for (User user : users.values()) {
            if (user.email.equals(newUser.email)) {
                throw new ConflictResponse();
            }
        }

        newUser.id = userId.getAndIncrement();

        users.put(newUser.id, newUser);

        ctx.status(HttpStatus.CREATED);
        ctx.json(newUser);
    }

    public void getOne(Context ctx) {
        Integer id = ctx.pathParamAsClass("id", Integer.class)
                .check(userId -> users.get(userId) != null, "User not found")
                .getOrThrow(message -> new NotFoundResponse());

        User user = users.get(id);

        ctx.json(user);
    }

    public void getMany(Context ctx) {
        String firstName = ctx.queryParam("firstName");
        String lastName = ctx.queryParam("lastName");

        List<User> users = new ArrayList<>();

        for (User user : this.users.values()) {
            if (firstName != null && !user.firstName.equals(firstName)) {
                continue;
            }

            if (lastName != null && !user.lastName.equals(lastName)) {
                continue;
            }

            users.add(user);
        }

        ctx.json(users);
    }

    public void updateUser(Context ctx) {
        Integer id = ctx.pathParamAsClass("id", Integer.class)
                .check(userId -> users.get(userId) != null, "User not found")
                .getOrThrow(message -> new NotFoundResponse());

        User updatedUser = ctx.bodyValidator(User.class)
                .check(obj -> obj.firstName != null, "Missing first name")
                .check(obj -> obj.lastName != null, "Missing last name")
                .check(obj -> obj.email != null, "Missing email")
                .check(obj -> obj.password != null, "Missing password")
                .getOrThrow(message -> new BadRequestResponse());

        users.put(id, updatedUser);

        ctx.json(updatedUser);
    }

    public void deleteUser(Context ctx) {
        Integer id = ctx.pathParamAsClass("id", Integer.class)
                .check(userId -> users.get(userId) != null, "User not found")
                .getOrThrow(message -> new NotFoundResponse());

        users.remove(id);

        ctx.status(HttpStatus.NO_CONTENT);
    }
}