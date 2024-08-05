package Clients;

import Model.User;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

public class TemplateClient {
    public static final String URL = "http://localhost:8080/template/users";
    private final RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) throws RESTException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RESTException(e.getMessage());
        }
    }

    public User[] getAll() throws RESTException {
        return execute(() -> restTemplate.getForObject(URL, User[].class));
    }

    public User getById(Integer id) throws RESTException {
        return execute(() -> restTemplate.getForObject(String.format("%s/%s", URL, id), User.class));
    }

    public User create(User user) throws RESTException {
        return execute(() -> restTemplate.postForObject(URL, user, User.class));
    }

    public void update(User user) throws RESTException {
        execute(() -> {
            restTemplate.put(String.format("%s", URL), user);
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
