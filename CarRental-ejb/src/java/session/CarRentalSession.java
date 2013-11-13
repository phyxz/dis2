package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
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
    
    @Resource 
    javax.ejb.SessionContext ctx; 
    
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
       List<CarType> cartypes = new ArrayList<CarType>();
        List<CarRentalCompany> companies = (List<CarRentalCompany>) em.createNamedQuery("rental.CarRentalCompany.getAllCompanies").getResultList();
        for(CarRentalCompany com: companies){
            cartypes.addAll(com.getAvailableCarTypes(start, end));
        }
        
        return cartypes;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        CarRentalCompany crc = (CarRentalCompany) em.createNamedQuery("rental.CarRentalCompany.getCompany")
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

    @TransactionAttribute(REQUIRED) 
    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {

        List<Reservation> done = new ArrayList<Reservation>();
        try {
            for (Quote quote : quotes) {
                String crcname = quote.getRentalCompany();
                
                CarRentalCompany c = (CarRentalCompany) em.createNamedQuery("rental.CarRentalCompany.getCompany")
                        .setParameter("name", crcname)
                        .getSingleResult();
               c.confirmQuote(quote);
            }
        } catch (Exception e) {
            throw new EJBException("Transaction Failed" + e.getMessage());
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