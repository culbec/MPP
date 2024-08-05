package ro.mpp2024.lab3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository {

    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        logger.traceEntry("finding by manufacturer {}", manufacturerN);
        List<Car> carList = new ArrayList<>();
        try (Connection connection = this.dbUtils.getConnection()) {
            String sql = "select * from cars where cars.manufacturer = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, manufacturerN);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                        int id = resultSet.getInt("cid");
                        String model = resultSet.getString("model");
                        int year = resultSet.getInt("year");

                        Car car = new Car(manufacturerN, model, year);
                        car.setId(id);

                        carList.add(car);
                    }

                    return carList;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        logger.traceExit();
        return carList;
    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {
        logger.traceEntry("finding between years {} and {}", min, max);
        List<Car> carList = new ArrayList<>();
        try (Connection connection = this.dbUtils.getConnection()) {
            String sql = "select * from cars where cars.year > ? and cars.year < ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, min);
                preparedStatement.setInt(2, max);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                        int id = resultSet.getInt("cid");
                        String manufacturer = resultSet.getString("manufacturer");
                        String model = resultSet.getString("model");
                        int year = resultSet.getInt("year");

                        Car car = new Car(manufacturer, model, year);
                        car.setId(id);

                        carList.add(car);
                    }

                    return carList;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        logger.traceExit();
        return carList;
    }

    @Override
    public void add(Car elem) {
        logger.traceEntry("adding entity: {}", elem);

        try (Connection connection = this.dbUtils.getConnection()) {
            String sql = "insert into cars(manufacturer, model, year) values (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, elem.getManufacturer());
                preparedStatement.setString(2, elem.getModel());
                preparedStatement.setInt(3, elem.getYear());

                int noRows = preparedStatement.executeUpdate();

                if (noRows == 0) {
                    logger.error("could not add the element {}", elem);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        logger.traceExit("added successfully {}", elem);
    }

    @Override
    public void update(Integer integer, Car elem) {
        logger.traceEntry("updating the element with id {} with the element {}", integer, elem);

        try (Connection connection = this.dbUtils.getConnection()) {
            String sql = "update cars set manufacturer = ?, model = ?, year = ? where cars.cid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, elem.getManufacturer());
                preparedStatement.setString(2, elem.getModel());
                preparedStatement.setInt(3, elem.getYear());
                preparedStatement.setInt(4, elem.getId());

                int noRows = preparedStatement.executeUpdate();

                if (noRows == 0) {
                    logger.error("nu exista entitatea cu id {}", integer);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        logger.traceExit("update cu succes pentru element cu id {}", integer);
    }

    @Override
    public Iterable<Car> findAll() {
        logger.traceEntry("cauta toate entitatile...");
        List<Car> cars = new ArrayList<>();

        try (Connection connection = this.dbUtils.getConnection()) {
            String sql = "select * from cars";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("cid");
                        String manufacturer = resultSet.getString("manufacturer");
                        String model = resultSet.getString("model");
                        int year = resultSet.getInt("year");

                        Car car = new Car(manufacturer, model, year);
                        car.setId(id);

                        cars.add(car);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        logger.traceExit("returnat cu succes toate entitatile");
        return cars;
    }
}
