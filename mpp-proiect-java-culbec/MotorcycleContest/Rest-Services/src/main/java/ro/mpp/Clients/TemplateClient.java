package ro.mpp.Clients;

import Model.RaceORM;
import org.springframework.web.client.RestTemplate;
import ro.mpp.RESTException;

import java.util.concurrent.Callable;

public class TemplateClient {
    public static final String URL = "http://localhost:8080/motorcycle-contest/races";
    private final RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) throws RESTException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RESTException(e.getMessage());
        }
    }

    public RaceORM[] getAll() throws RESTException {
        return execute(() -> restTemplate.getForObject(URL, RaceORM[].class));
    }

    public RaceORM getById(Integer id) throws RESTException {
        return execute(() -> restTemplate.getForObject(String.format("%s/%s", URL, id), RaceORM.class));
    }

    public RaceORM create(RaceORM raceORM) throws RESTException {
        return execute(() -> restTemplate.postForObject(URL, raceORM, RaceORM.class));
    }

    public void update(RaceORM raceORM) throws RESTException {
        execute(() -> {
            restTemplate.put(String.format("%s", URL), raceORM);
            return null;
        });
    }

    public void delete(Integer id) throws RESTException {
        execute(() -> {
            restTemplate.delete(String.format("%s/%s", URL, id));
            return null;
        });
    }
}
