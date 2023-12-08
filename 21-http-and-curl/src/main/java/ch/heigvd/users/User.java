package ch.heigvd.users;

public class User {

    public Integer id;
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    public User() {
        // Empty constructor for deserialization
    }

    public User(Integer id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}