package ru.boldyrev.otus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.boldyrev.otus.exception.AlreadyExistsException;
import ru.boldyrev.otus.exception.NotFoundException;
import ru.boldyrev.otus.model.AppUser;
import ru.boldyrev.otus.repo.AppUserRepo;

import java.util.Map;
import java.util.Optional;

@Service
public class AppUserService {
    @Autowired
    AppUserRepo appUserRepo;

    public AppUser getUserById(String username) throws NotFoundException {
        Optional<AppUser> user = appUserRepo.findById(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User not found");
        }

    }

    public AppUser createUser(AppUser user) throws AlreadyExistsException {
        if (appUserRepo.findById(user.getUsername()).isEmpty())
            return appUserRepo.save(user);
        else
            throw new AlreadyExistsException("User already exists");

    }

    public AppUser updateUser(String username, AppUser userDetails) throws NotFoundException {
        Optional<AppUser> user = appUserRepo.findById(username);
        if (user.isPresent()) {
            AppUser existingUser = user.get();

            if (userDetails.getFirstName() != null)
                existingUser.setFirstName(userDetails.getFirstName());

            if (userDetails.getLastName() != null)
                existingUser.setLastName(userDetails.getLastName());

            if (userDetails.getEmail() != null)
                existingUser.setEmail(userDetails.getEmail());

            if (userDetails.getPhone() != null)
                existingUser.setPhone(userDetails.getPhone());

            if (userDetails.getPassword() != null)
                existingUser.setPassword(userDetails.getPassword());


            return appUserRepo.save(existingUser);
        } else
            throw new NotFoundException("User not found");
    }

    public AppUser deleteUser(String username) throws NotFoundException {
        Optional<AppUser> user = appUserRepo.findById(username);
        if (user.isPresent()) {
            appUserRepo.deleteById(username);
            return null;
        } else
            throw new NotFoundException("User not found");
    }

    public AppUser getUserByCredentials(String username, String password) throws NotFoundException {
        Optional<AppUser> appUser = appUserRepo.findByUsernameAndPassword(username, password);
        if (appUser.isPresent()){
            return appUser.get();
        }
        else throw new NotFoundException("Invalid login/password");
    }
}
