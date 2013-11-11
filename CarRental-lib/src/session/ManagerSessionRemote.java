package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public int getNumberOfReservationsBy(String renter);
    
    public void loadRentalCompany(String name, String datafile);
    
    public void addCarType(String name, int nbOfSeats, boolean smokingAllowed,
            double RentalPricePerDay, float trunkspace);
    
    // moet id nog gegeven worden ????
    public void addCar(String CarRentalCompanyName, int uid, CarType type);
}