package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        Train train = new Train();
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());
        train.setDepartureTime(trainEntryDto.getDepartureTime());
        List<Station> stationRoute = trainEntryDto.getStationRoute();
        String stationRoute1 = "";
        for(Station s : stationRoute){
            stationRoute1+=s+",";
        }
        train.setRoute(stationRoute1);
        trainRepository.save(train);
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        return train.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans

        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.

       return null;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{
        boolean isStationPresent = false;
        Train train = trainRepository.findById(trainId).get();
        String arr[] = train.getRoute().split(",");
        for(String s: arr){
            if(s==station.toString()){
                isStationPresent = true;
                break;
            }
        }
        if(!isStationPresent){
            throw new Exception("Train is not passing from this station");
        }
        int count = 0;
        List<Ticket> list = train.getBookedTickets();
        for(Ticket t: list){
            if(t.getFromStation()==station) count+=t.getPassengersList().size();
        }
        return count;




        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.


    }

    public Integer calculateOldestPersonTravelling(Integer trainId){
        Train train = trainRepository.findById(trainId).get();
        int age = 0;
        List<Ticket> list = train.getBookedTickets();
        for(Ticket t : list){
            List<Passenger> p = t.getPassengersList();
            for(Passenger p1 : p){
                age = Math.max(age, p1.getAge());
            }
        }
        return age;

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0

    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){
        List<Integer> ans = new ArrayList<>();
        String start = startTime.toString();
        int st = Integer.parseInt(start.substring(0,2));
        int et = Integer.parseInt(start.substring(3,5));
        st = st*60;
        int startT = st+et;
        String end = endTime.toString();
        int sta = Integer.parseInt(start.substring(0,2));
        int eta = Integer.parseInt(start.substring(3,5));
        sta = sta*60;
        int endT = sta+eta;

        List<Train> trains = trainRepository.findAll();
        for(Train train : trains){
            String route = train.getRoute();
            String arr[] = route.split(",");
            for(int i=0;i<arr.length;i++) {
                if (arr[i] == station.toString()) {
                    int cal = (i + 1) * 60;
                    if (cal >= startT && cal <= endT) {
                        ans.add(train.getTrainId());
                    }
                }
            }
        }
        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.
        return ans;
    }
}
