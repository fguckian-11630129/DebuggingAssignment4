package hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class TestAddChargeAfterCheckout {

	Hotel hotel = new Hotel();
	RecordServiceCTL recordServiceControl;
	RecordServiceCTL recordServiceControl2;
	CheckoutCTL checkoutControl;
	Booking booking;
	
	
	Guest guest;
	Room room;
	Date arrivalDate;
	int stayLength; 
	int numberOfOccupants;
	CreditCard creditCard;
	
	ServiceType serviceType;
	int cost;
	int cost2;
	
	int roomId;
	
	//@Mock CheckoutUI mockCheckoutUI;
	
	//@InjectMocks CheckoutCTL checkoutControl = new CheckoutCTL(hotel);
	
	@BeforeEach
	void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
		
		recordServiceControl = new RecordServiceCTL(hotel);
		recordServiceControl2 = new RecordServiceCTL(hotel);
		checkoutControl = new CheckoutCTL(hotel);
		
		
		guest = new Guest("Abe", "123 Fake st", 12345678);
		room = new Room(101, RoomType.SINGLE);
		SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
		arrivalDate = format.parse("11-11-2011");
		stayLength = 3;
		numberOfOccupants = 1;
		creditCard = new CreditCard(CreditCardType.MASTERCARD, 1, 1);
		booking = new Booking(guest, room, arrivalDate, stayLength, numberOfOccupants, creditCard);
		
		serviceType = ServiceType.BAR_FRIDGE;
		cost = 100;
		cost2 = 300;
		
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
		//checkoutControl.checkoutUI = mockCheckoutUI;
		checkoutControl.roomId = roomId;
		
		recordServiceControl2.state = State.SERVICE;
		recordServiceControl2.booking = booking;
		recordServiceControl2.roomNumber = roomId;
		
		booking.state = Booking.State.CHECKED_IN;
		
		room.state = Room.State.OCCUPIED;
		
		//ArgumentCaptor<String> checkoutCaptor = ArgumentCaptor.forClass(String.class);
		
		
		//act
		recordServiceControl.serviceDetailsEntered(serviceType, cost);
		checkoutControl.roomIdEntered(roomId);
		checkoutControl.chargesAccepted(true);
		checkoutControl.creditDetailsEntered(CreditCardType.MASTERCARD, 1, 1);
		assertTrue(hotel.activeBookingsByRoomId.size() == 0);
		
		Executable e = () -> recordServiceControl2.serviceDetailsEntered(serviceType, cost2);
		Throwable t = assertThrows(RuntimeException.class, e);
		
		//assert
		assertEquals("Cannot find booking in active bookings", t.getMessage());
		
	}

}
