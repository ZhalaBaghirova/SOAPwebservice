package com.service.cinemaseatreservation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.jws.WebService;
import jakarta.xml.ws.Endpoint;
import seatreservation.ArrayOfSeat;
import seatreservation.CinemaException;
import seatreservation.ICinema;
import seatreservation.ICinemaBuyCinemaException;
import seatreservation.ICinemaGetAllSeatsCinemaException;
import seatreservation.ICinemaGetSeatStatusCinemaException;
import seatreservation.ICinemaInitCinemaException;
import seatreservation.ICinemaLockCinemaException;
import seatreservation.ICinemaReserveCinemaException;
import seatreservation.ICinemaUnlockCinemaException;
import seatreservation.Seat;
import seatreservation.SeatStatus;

@WebService(endpointInterface = "seatreservation.ICinema")
public class Cinema implements ICinema {

	ArrayList<SeatExtended> seats = new ArrayList<SeatExtended>();
	ArrayList<LockExtended> lockedSeats = new ArrayList<LockExtended>();

	@Override
	public void init(int rows, int columns) throws ICinemaInitCinemaException {
		seats.clear();

		if (rows < 1 || rows > 26) {
			String message = "Number of rows is out of bound.";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaInitCinemaException(message, exception);
		}

		if (columns < 1 || columns > 100) {
			String message = "Number of columns is out of bound.";
			CinemaException exception = new CinemaException();
			exception.setErrorMessage(message);
			throw new ICinemaInitCinemaException(message, exception);
		}

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				SeatExtended seat = new SeatExtended();
				seat.setRow(Character.toString((char) (i + 65)));
				seat.setColumn(String.valueOf(j + 1));
				seat.availability = SeatStatus.FREE;

				seats.add(seat);
			}
		}
	}

	@Override
	public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
		ArrayOfSeat arrayofseat = new ArrayOfSeat();
		for (SeatExtended seat : seats) {
			arrayofseat.getSeat().add(seat);
		}

		return arrayofseat;
	}

	@Override
	public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
		for (SeatExtended currentSeat : seats) {
			if ((currentSeat.getRow().equals(seat.getRow())) && (currentSeat.getColumn().equals(seat.getColumn()))) {
				return currentSeat.availability;

			}

		}
		String message = "Bad seat number.";
		CinemaException exception = new CinemaException();
		exception.setErrorMessage(message);
		throw new ICinemaGetSeatStatusCinemaException(message, exception);

	}

	@Override
	public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
		List<SeatExtended> rowSeats = new ArrayList<SeatExtended>();

		for (SeatExtended currentSeat : seats) {
			if (currentSeat.getRow().equals(seat.getRow())) {
				rowSeats.add(currentSeat);
			}
		}

		for (SeatExtended currentSeat : seats) {
			if (currentSeat.getRow().equals(seat.getRow()) && currentSeat.getColumn().equals(seat.getColumn())) {
				int currentIndex = seats.indexOf(currentSeat);

				if (!currentSeat.availability.equals(SeatStatus.LOCKED)) {

					if (count <= rowSeats.size() - Integer.parseInt(currentSeat.getColumn()) + 1) {

						for (int i = 0; i < count; i++) {
							seats.get(currentIndex).availability = SeatStatus.LOCKED;
							currentIndex++;
						}

						LockExtended lock = new LockExtended();
						lock.setCount(count);
						lock.setSeat(seat);

						UUID uuid = UUID.randomUUID();
						lock.lockId = uuid.toString();

						lockedSeats.add(lock);

						return uuid.toString();

					} else {
						String message = "Not enough seats.";
						CinemaException exception = new CinemaException();
						exception.setErrorMessage(message);
						throw new ICinemaLockCinemaException(message, exception);
					}
				} else {
					String message = "Seat is not free.";
					CinemaException exception = new CinemaException();
					exception.setErrorMessage(message);
					throw new ICinemaLockCinemaException(message, exception);
				}
			}

		}
		return null;
	}

	@Override
	public void unlock(String lockId) throws ICinemaUnlockCinemaException {
		for (LockExtended lock : lockedSeats) {
			if (lock.lockId.equals(lockId)) {
				int currentIndex = -1;

				for (int i = 0; i < seats.size(); i++) {
					SeatExtended currentSeat = seats.get(i);
					if (currentSeat.getRow().equals(lock.getSeat().getRow())
							&& currentSeat.getColumn().equals(lock.getSeat().getColumn())) {
						currentIndex = i;
					}
				}

				for (int i = 0; i < lock.getCount(); i++) {
					if (seats.get(currentIndex).availability.equals(SeatStatus.LOCKED)) {
						seats.get(currentIndex).availability = SeatStatus.FREE;
						currentIndex = currentIndex + 1;
					} else {
						String message = "You cannot unlock reserved seat.";
						CinemaException exception = new CinemaException();
						exception.setErrorMessage(message);
						throw new ICinemaUnlockCinemaException(message, exception);
					}

				}
				lockedSeats.remove(lock);

				return;
			}
		}
		String message = "No lock found.";
		CinemaException exception = new CinemaException();
		exception.setErrorMessage(message);
		throw new ICinemaUnlockCinemaException(message, exception);

	}

	@Override
	public void reserve(String lockId) throws ICinemaReserveCinemaException {
		for (LockExtended lock : lockedSeats) {
			if (lock.lockId.equals(lockId)) {
				int currentIndex = -1;

				for (int i = 0; i < seats.size(); i++) {
					SeatExtended currentSeat = seats.get(i);
					if (currentSeat.getRow().equals(lock.getSeat().getRow())
							&& currentSeat.getColumn().equals(lock.getSeat().getColumn())) {
						currentIndex = i;
					}
				}

				for (int i = 0; i < lock.getCount(); i++) {

					seats.get(currentIndex).availability = SeatStatus.RESERVED;
					currentIndex = currentIndex + 1;
				}

				return;
			}

		}
		String message = "No lock found.";
		CinemaException exception = new CinemaException();
		exception.setErrorMessage(message);
		throw new ICinemaReserveCinemaException(message, exception);
	}

	@Override
	public void buy(String lockId) throws ICinemaBuyCinemaException {

		for (LockExtended lock : lockedSeats) {
			if (lock.lockId.equals(lockId)) {
				int currentIndex = -1;

				for (int i = 0; i < seats.size(); i++) {
					SeatExtended currentSeat = seats.get(i);
					if (currentSeat.getRow().equals(lock.getSeat().getRow())
							&& currentSeat.getColumn().equals(lock.getSeat().getColumn())) {

						currentIndex = i;
					}
				}

				for (int i = 0; i < lock.getCount(); i++) {
					if (seats.get(currentIndex).availability.equals(SeatStatus.SOLD)) {
						
						String message = "The seat has already been sold.";
						CinemaException exception = new CinemaException();
						exception.setErrorMessage(message);
						throw new ICinemaBuyCinemaException(message, exception);
						
					}
					else {
						seats.get(currentIndex).availability = SeatStatus.SOLD;
						currentIndex = currentIndex + 1;
					}
				}

				return;
			}

		}
		String message = "The identifier for the lock is invalid.";
		CinemaException exception = new CinemaException();
		exception.setErrorMessage(message);
		throw new ICinemaBuyCinemaException(message, exception);

	}

	public static void main(String[] args) {
		System.out.println("Service started...");
		Endpoint.publish("http://localhost:8080/WebService_IKLRQP/Cinema", new Cinema());
	}
}
