import Clients.TemplateClient;
import Model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartRESTClient {
    private static final TemplateClient userClient = new TemplateClient();
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            logger.info("Testing the REST client.");
            show(() -> {
                try {
                    logger.info("Getting all users: {}", (Object) userClient.getAll());
                } catch (Exception e) {
                    logger.error(e);
                }
            });
            show(() -> {
                try {
                    logger.info("Getting user with id 1: {}", userClient.getById(1));
                } catch (Exception e) {
                    logger.error(e);
                }
            });
            show(() -> {
                try {
                    logger.info("Creating a user: {}", userClient.create(new User("test", "test", "test")));
                } catch (Exception e) {
                    logger.error(e);
                }
            });
            show(() -> {
                try {
                    logger.info("Update user.");

                    User user = userClient.getById(1);
                    user.setUsername("test2");
                    user.setFirstName("RESTUPDATEF");
                    user.setLastName("RESTUPDATEL");
                    userClient.update(user);
                    logger.info("Updated user: {}", user);
                } catch (Exception e) {
                    logger.error(e);
                }
            });
            show(() -> {
                try {
                    logger.info("Deleting user with id 1.");
                    userClient.delete(1);
                } catch (Exception e) {
                    logger.error(e);
                }
            });
            show(() -> {
                try {
                    logger.info("Final get all: {}", (Object) userClient.getAll());
                } catch (Exception e) {
                    logger.error(e);
                }

            });
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
