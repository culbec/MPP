package ro.mpp;

import Model.RaceORM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp.Clients.TemplateClient;

public class StartRESTClient {
    private static final TemplateClient raceClient = new TemplateClient();
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        final RaceORM[] raceORM = {new RaceORM(1000)};

        try {
            // Race creation.
            logger.info("Race creation.");
            show(() -> {
                try {
                    RaceORM created = raceClient.create(raceORM[0]);
                    raceORM[0] = created;
                    logger.info("Race created: {}", created);
                } catch (Exception e) {
                    logger.error("Error creating the race: {}", e.getMessage());
                }
            });

            // All race retrieval.
            logger.info("All race retrieval.");
            show(() -> {
                try {
                    logger.info("Retrieving all the races...");
                    RaceORM[] raceORMS = raceClient.getAll();

                    logger.info("The retrieved races: {}", (Object) raceORMS);
                } catch (Exception e) {
                    logger.error("Error retrieving the races: {}", e.getMessage());
                }
            });

            // Race update.
            logger.info("Race update.");
            show(() -> {
               try {
                   RaceORM newRaceORM = new RaceORM(999);
                   newRaceORM.setId(raceORM[0].getId());
                   raceClient.update(newRaceORM);
                   logger.info("The new race: {}", newRaceORM);
                   raceORM[0] = newRaceORM;
               } catch (Exception e) {
                   logger.error("Error updating the race: {}", e.getMessage());
               }
            });

            // Get one race.
            logger.info("One race finding.");
            show(() -> {
                try {
                    RaceORM found = raceClient.getById(raceORM[0].getId());
                    logger.info("The race with id {}: {}", raceORM[0].getId(), found);
                } catch (Exception e) {
                    logger.error("Error finding the race: {}", e.getMessage());
                }
            });

            // Deleting the race.
            logger.info("Deleting a race.");
            show(() -> {
                try {
                    raceClient.delete(raceORM[0].getId());
                    logger.info("The deleted race: {}", raceORM[0]);
                } catch (Exception e) {
                    logger.error("Error finding the race: {}", e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
}
