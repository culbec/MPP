package ro.mpp.Controllers;

import Hibernate.RaceRepositoryHibernate;
import Model.RaceORM;
import Repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("motorcycle-contest/races")
public class RacesController {
    @Autowired
    private RaceRepositoryHibernate raceRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    @GetMapping
    public Collection<RaceORM> getAll() throws RepositoryException {
        return (Collection<RaceORM>) raceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) throws RepositoryException {
        Optional<RaceORM> raceORMOptional = raceRepository.findOne(id);
        if (raceORMOptional.isEmpty()) {
            return new ResponseEntity<String>("Race not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<RaceORM>(raceORMOptional.get(), HttpStatus.OK);
    }

    @PostMapping
    public RaceORM create(@RequestBody RaceORM raceORM) throws RepositoryException {
        Optional<RaceORM> raceORMOptional = raceRepository.save(raceORM);
        return raceORMOptional.orElse(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Optional<RaceORM> raceORMOptional = raceRepository.delete(id);
        if (raceORMOptional.isEmpty()) {
            return new ResponseEntity<String>("The race doesn't exist", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<RaceORM>(raceORMOptional.get(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody RaceORM raceORM) throws RepositoryException {
        Optional<RaceORM> raceORMOptional = raceRepository.update(raceORM);
        if (raceORMOptional.isEmpty()) {
            return new ResponseEntity<>("The race doesn't exist!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(raceORMOptional.get(), HttpStatus.OK);
    }
}
