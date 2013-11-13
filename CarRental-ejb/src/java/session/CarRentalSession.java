package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    @PersistenceContext
    EntityManager em;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> companies = null;
        companies = (List<String>) em.createNamedQuery("rental.CarRentalCompany.getAllCompanies").getResultList();
        return new HashSet<String>(companies);
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
       List<CarType> cartypes = null;
        //companies = (List<CarType>) em.createNamedQuery("rental.CarRentalCompany.getAllCompanies").getResultList();
        return cartypes;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        CarRentalCompany crc = (CarRentalCompany) em.createNamedQuery("rental.CarRentalCompany.getAllCompanies")
                                                    .setParameter("name", company)
                                                    .getSingleResult();
        Quote out = crc.createQuote(constraints, renter);
        quotes.add(out);
        return out;
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
               // done.add(Loader.getRental(quote.getRentalCompany()).confirmQuote(quote));
            }
        } catch (Exception e) {
            //for(Reservation r:done)
              //  Loader.getRental(r.getRentalCompany()).cancelReservation(r);
            throw new ReservationException();
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }
}