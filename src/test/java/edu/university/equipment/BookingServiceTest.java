package edu.university.equipment;
import static org.junit.jupiter.api.Assertions.*; import java.util.NoSuchElementException; import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test;
class BookingServiceTest {
 private BookingService service; @BeforeEach void setup(){service=new BookingService();}
 @Test void successfulBookingReducesInventory(){service.create("STU1001","LAPTOP01",2);assertEquals(8,service.getEquipment("LAPTOP01").getAvailableQuantity());}
 @Test void zeroQuantityRejected(){assertThrows(IllegalArgumentException.class,()->service.create("STU1001","LAPTOP01",0));assertEquals(10,service.getEquipment("LAPTOP01").getAvailableQuantity());}
 @Test void blankBorrowerRejected(){assertThrows(IllegalArgumentException.class,()->service.create(" ","LAPTOP01",1));}
 @Test void unknownEquipmentRejected(){assertThrows(NoSuchElementException.class,()->service.create("STU1001","UNKNOWN",1));}
 @Test void ordinaryOverbookingRejected(){assertThrows(IllegalStateException.class,()->service.create("STU1001","PROJECTOR01",6));assertEquals(4,service.getEquipment("PROJECTOR01").getAvailableQuantity());}
 @Test void cancellationRestoresInventoryOnce(){Booking b=service.create("STU1001","TABLET01",3);service.cancel(b.getId());assertEquals(8,service.getEquipment("TABLET01").getAvailableQuantity());assertThrows(IllegalStateException.class,()->service.cancel(b.getId()));}

@Test
void bookingExactlyAvailableInventoryIsAccepted() {
    Booking booking = service.create("STU1001", "LAPTOP01", 10);

    assertEquals(0, service.getEquipment("LAPTOP01").getAvailableQuantity());
    assertEquals("CONFIRMED", booking.getStatus());
}

@Test
void bookingOneMoreThanAvailableIsRejectedWithoutChanges() {
    assertThrows(IllegalStateException.class,
            () -> service.create("STU1001", "LAPTOP01", 11));

    assertEquals(10, service.getEquipment("LAPTOP01").getAvailableQuantity());
    assertTrue(service.listBookings().isEmpty());
}

@Test
void sequentialBookingsWithinInventoryAreAccepted() {
    Booking first = service.create("STU1001", "LAPTOP01", 4);
    Booking second = service.create("STU1002", "LAPTOP01", 6);

    assertEquals(0, service.getEquipment("LAPTOP01").getAvailableQuantity());
    assertEquals("CONFIRMED", first.getStatus());
    assertEquals("CONFIRMED", second.getStatus());
    assertEquals(2, service.listBookings().size());
}


@Test
void sequentialBookingsExceedingInventoryAreRejected() {
    Booking first = service.create("STU1001", "LAPTOP01", 6);

    assertThrows(IllegalStateException.class,
            () -> service.create("STU1002", "LAPTOP01", 5));

    assertEquals(4, service.getEquipment("LAPTOP01").getAvailableQuantity());
    assertEquals(1, service.listBookings().size());

    Booking stored = service.getBooking(first.getId());
    assertEquals("CONFIRMED", stored.getStatus());
    assertEquals(6, stored.getQuantity());
}


}