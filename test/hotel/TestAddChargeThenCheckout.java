package hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import hotel.checkout.CheckoutCTL;
import hotel.checkout.CheckoutUI;
import hotel.credit.CreditCard;
import hotel.credit.CreditCardType;
import hotel.entities.Booking;
import hotel.entities.Guest;
import hotel.entities.Hotel;
import hotel.entities.Room;
import hotel.entities.RoomType;
import hotel.entities.ServiceType;
import hotel.service.RecordServiceCTL;
import hotel.service.RecordServiceCTL.State;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestAddChargeThenCheckout {
	
	Hotel hotel = new Hotel();
	RecordServiceCTL recordServiceControl;
	//CheckoutCTL checkoutControl;
	Booking booking;
	
	
	Guest guest;
	Room room;
	Date arrivalDate;
	int stayLength; 
	int numberOfOccupants;
	CreditCard creditCard;
	
	ServiceType serviceType;
	int cost;
	
	int roomId;
	
	@Mock CheckoutUI mockCheckoutUI;
	
	@InjectMocks CheckoutCTL checkoutControl = new CheckoutCTL(hotel);
	
	@BeforeEach
	void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
		
		recordServiceControl = new RecordServiceCTL(hotel);
		checkoutControl = new CheckoutCTL(hotel);
		
		
		guest = new Guest("Abe", "123 Fake st", 12345678);
		room = new Room(101, RoomType.SINGLE);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		arrivalDate = format.parse("11-11-2011");
		stayLength = 3;
		numberOfOccupants = 1;
		creditCard = new CreditCard(CreditCardType.MASTERCARD, 1, 1);
		booking = new Booking(guest, room, arrivalDate, stayLength, numberOfOccupants, creditCard);
		
		serviceType = ServiceType.BAR_FRIDGE;
		cost = 100;
		
		roomId = room.getId();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	
	
	@Test
	void test() {
		//arrange
		hotel.activeBookingsByRoomId.put(booking.getRoom().getId(), booking);
		
		recordServiceControl.state = State.SERVICE;
		recordServiceControl.booking = booking;
		recordServiceControl.roomNumber = roomId;
		
		checkoutControl.state = CheckoutCTL.State.ROOM;
		checkoutControl.checkoutUI = mockCheckoutUI;
		checkoutControl.roomId = roomId;
		
		ArgumentCaptor<String> checkoutCaptor = ArgumentCaptor.forClass(String.class);
		
		
		//act
		recordServiceControl.serviceDetailsEntered(serviceType, cost);
		checkoutControl.roomIdEntered(roomId);
		
		
		//assert
		
		verify(mockCheckoutUI).displayMessage(checkoutCaptor.capture());
		String message = checkoutCaptor.getValue();
		String[] splited = message.split("\\s+");
		assertEquals("$100.00", splited[splited.length-1]); 
		
	}
	 
}
