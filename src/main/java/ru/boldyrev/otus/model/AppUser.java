package ru.boldyrev.otus.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "APP_USERS")
public class AppUser {
    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;


    @Override
    public String toString(){
        return new StringBuilder()
                .append(", username = ").append(username)
                .append(", firstName = ").append(firstName)
                .append(", lastName = ").append(lastName)
                .append(", email = ").append(email)
                .append(", phone = ").append(phone)
                .append(", password = ").append(password)
                .toString();
    }

    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        map.put("username", this.getUsername());
        map.put("firstName", this.getFirstName());
        map.put("lastName", this.getLastName());
        map.put("email", this.getEmail());
        map.put("phone", this.getPhone());
        map.put("password", this.getPassword());
        return map;
    }
}
