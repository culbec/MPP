package Controllers;

import Model.User;
import Repository.RepositoryException;
import Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("template/users")
public class UsersController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    public String test() {
        return "TEST!";
    }

    @GetMapping
    public Collection<User> getAll() throws RepositoryException {
        return (Collection<User>) userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws RepositoryException {
        Optional<User> userOptional = userRepository.findOne(id);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) throws RepositoryException {
        Optional<User> userOptional = userRepository.save(user);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not created", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userOptional.get(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.delete(id);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody User user) throws RepositoryException {
        Optional<User> userOptional = userRepository.update(user);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not updated", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
    }
}
