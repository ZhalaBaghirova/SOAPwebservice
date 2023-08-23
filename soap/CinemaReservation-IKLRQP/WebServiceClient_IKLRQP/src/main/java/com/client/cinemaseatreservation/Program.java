package com.client.cinemaseatreservation;

import jakarta.xml.ws.BindingProvider;
import seatreservation.CinemaService;
import seatreservation.ICinema;
import seatreservation.ICinemaBuyCinemaException;
import seatreservation.ICinemaLockCinemaException;
import seatreservation.ICinemaReserveCinemaException;
import seatreservation.Seat;

public class Program {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url=args[0];
		String row=args[1];
		String column=args[2];
		String task=args[3];
		
		
		 CinemaService cinemaService = new CinemaService();
		 ICinema client = cinemaService.getICinemaHttpSoap11Port();
		 BindingProvider bp = (BindingProvider) client;
		 bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,url);
		
		 try {
			 String lockId;
			 Seat seat = new Seat();
			 seat.setRow(row);
			 seat.setColumn(column);
			 
			 switch(task) {
			 	case "Lock":
			 		lockId=client.lock(seat,1);
			 		System.out.println("Seat is locked.");
			 		break;
			 		
			 	case "Reserve":
			 		lockId=client.lock(seat, 1);
			 		client.reserve(lockId);
			 		System.out.println("Seat is reserved.");
			 		break;
			 		
			 	case "Buy":
			 		lockId=client.lock(seat, 1);
			 		client.buy(lockId);
			 		System.out.println("Seat is bought.");
			 		break;
			 		
			 } 
		 } catch (ICinemaLockCinemaException e) {
			 e.printStackTrace();
		 } catch (ICinemaReserveCinemaException e) {
			 e.printStackTrace();
		 } catch (ICinemaBuyCinemaException e) {
			 e.printStackTrace();
		 }
		 
	}

}
