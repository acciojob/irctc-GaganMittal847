package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
        int bookedSeates = 0;
        List<Ticket> BookedTicket = train.getBookedTickets();
        for(Ticket t : BookedTicket){
            bookedSeates+=t.getPassengersList().size();
        }
        if(bookedSeates+bookTicketEntryDto.getNoOfSeats()>train.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }
        List<Passenger> passengers = new ArrayList<>();
        List<Integer> id = bookTicketEntryDto.getPassengerIds();
        for(int x : id){
            passengers.add(passengerRepository.findById(x).get());
        }

        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        Ticket ticket = new Ticket();
        ticket.setPassengersList(passengers);
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        String []stationsList = train.getRoute().split(",");
        int start = -1;
        int end = -1;
        for(int i=0;i<stationsList.length;i++){
            if(stationsList[i] == bookTicketEntryDto.getFromStation().toString()){
                start = i;
                break;
            }
        }
        for(int i=0;i<stationsList.length;i++){
            if(stationsList[i] == bookTicketEntryDto.getToStation().toString()){
                end = i;
                break;
            }
        }
        if(start==-1 || end == -1 || end-start<0){
            throw new Exception("Invalid stations");
        }
        int totalFare = 300*(end-start);
        ticket.setTotalFare(totalFare);
        ticket.setTrain(train);
        train.getBookedTickets().add(ticket);
        train.setNoOfSeats(train.getNoOfSeats()-bookTicketEntryDto.getNoOfSeats());

        Passenger passenger1 = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        passenger1.getBookedTickets().add(ticket);
        trainRepository.save(train);
        ticketRepository.save(ticket);
        return ticket.getTicketId();




        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

    }
}
