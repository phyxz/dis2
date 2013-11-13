package session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;
import rental.ReservationException;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    public static final Logger logger = Logger.getLogger(ManagerSession.class.getName());
    
    @PersistenceContext
    private EntityManager em;
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            List<CarType> results = null;
            results = em.createNamedQuery("rental.CarRentalCompany.getCarTypes")
                                                        .setParameter("name", company)
                                                        .getResultList();
            
            return new HashSet<CarType>(results);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            CarRentalCompany ccompany = (CarRentalCompany) 
                    em.createNamedQuery("rental.CarRentalCompany.getCompany").getSingleResult();
            for(Car c: ccompany.getCars(type)){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
             CarRentalCompany ccompany = (CarRentalCompany) 
                    em.createNamedQuery("rental.CarRentalCompany.getCompany").getSingleResult();
            return ccompany.getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
             CarRentalCompany ccompany = (CarRentalCompany) 
                    em.createNamedQuery("rental.CarRentalCompany.getCompany").getSingleResult();
            for(Car c: ccompany.getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        Set<Reservation> out = new HashSet<Reservation>();
        
        List<CarRentalCompany> companies = null;
        companies = (List<CarRentalCompany>) em.createNamedQuery("rental.CarRentalCompany.getAllCompanies").getResultList();
        for(CarRentalCompany d : companies) {
            out.addAll(d.getReservationsBy(renter));
    }
            return out.size();
    }

    @Override
    public void loadRentalCompany(String name, String datafile) {
        logger.log(Level.INFO, "loading {0} from file {1}", new Object[]{name, datafile});
        try {
            List<Car> cars = loadData(datafile);
            CarRentalCompany company = new CarRentalCompany(name, cars);
            em.persist(company);
        } catch (NumberFormatException ex) {
            logger.log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addCarType(String name, int nbOfSeats, boolean smokingAllowed, double rentalPricePerDay, float trunkspace) {
        //String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed 
        CarType carType = new CarType(name,nbOfSeats,trunkspace,rentalPricePerDay, smokingAllowed);
        em.persist(carType);//To change body of generated methods, choose Tools | Templates.
    }
    
     public static List<Car> loadData(String datafile)
            throws NumberFormatException, IOException {

        List<Car> cars = new LinkedList<Car>();

        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(ManagerSession.class.getClassLoader().getResourceAsStream(datafile)));
        //while next line exists
        while (in.ready()) {
            //read line
            String line = in.readLine();
            //if comment: skip
            if (line.startsWith("#")) {
                continue;
            }
            //tokenize on ,
            StringTokenizer csvReader = new StringTokenizer(line, ",");
            //create new car type from first 5 fields
            CarType type = new CarType(csvReader.nextToken(),
                    Integer.parseInt(csvReader.nextToken()),
                    Float.parseFloat(csvReader.nextToken()),
                    Double.parseDouble(csvReader.nextToken()),
                    Boolean.parseBoolean(csvReader.nextToken()));
            //create N new cars with given type, where N is the 5th field
            for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                cars.add(new Car(type));
            }
        }

        return cars;
    }

    @Override
    public void addCar(String CarRentalCompanyName, CarType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}