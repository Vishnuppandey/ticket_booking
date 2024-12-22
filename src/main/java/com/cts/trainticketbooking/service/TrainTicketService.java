package com.cts.trainticketbooking.service;

import com.cts.trainticketbooking.dto.TicketBookingRequest;
import com.cts.trainticketbooking.entity.*;
import com.cts.trainticketbooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TrainTicketService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketBookingRepository ticketBookingRepository;

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public List<Train> searchTrains(String source, String destination) {
        return trainRepository.findBySourceAndDestination(source, destination);
    }

    public Train addTrain(Train train) {
        return trainRepository.save(train);
    }

    public List<TicketBooking> getBookingsByUser(Long userId) {
        return ticketBookingRepository.findByUserId(userId);
    }

    public TicketBooking bookTicket(TicketBookingRequest bookingRequest) {
        Train train = trainRepository.findById(bookingRequest.getTrainId())
            .orElseThrow(() -> new RuntimeException("Train not found"));

        User user = userRepository.findById(bookingRequest.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (train.getTotalSeats() < bookingRequest.getNumberOfSeats()) {
            throw new RuntimeException("Not enough seats available");
        }

        train.setTotalSeats(train.getTotalSeats() - bookingRequest.getNumberOfSeats());
        trainRepository.save(train);

        TicketBooking ticketBooking = new TicketBooking();
        ticketBooking.setTrain(train);
        ticketBooking.setUser(user);
        ticketBooking.setTravelDate(bookingRequest.getTravelDate());
        ticketBooking.setNumberOfSeats(bookingRequest.getNumberOfSeats());

        return ticketBookingRepository.save(ticketBooking);
    }

    public void cancelBooking(Long id) {
        TicketBooking booking = ticketBookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        Train train = booking.getTrain();
        train.setTotalSeats(train.getTotalSeats() + booking.getNumberOfSeats());
        trainRepository.save(train);

        ticketBookingRepository.deleteById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}