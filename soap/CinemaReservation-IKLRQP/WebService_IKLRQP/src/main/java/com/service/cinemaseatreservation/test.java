package com.service.cinemaseatreservation;

import seatreservation.ICinemaGetSeatStatusCinemaException;
import seatreservation.ICinemaInitCinemaException;
import seatreservation.Seat;

public class test {
		public static void main(String[] args ) throws ICinemaInitCinemaException, ICinemaGetSeatStatusCinemaException {
			Cinema myObject=new Cinema();
			myObject.init(5, 7);
			
			Seat seat=new Seat();
			seat.setRow("B");
			seat.setColumn("1");
			
			myObject.getSeatStatus(seat);
		}
}
