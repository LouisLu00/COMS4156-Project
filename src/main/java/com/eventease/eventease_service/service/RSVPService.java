package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.RSVPExistsException;
import com.eventease.eventease_service.exception.RSVPNotExistException;
import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.eventease.eventease_service.repository.RSVPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class RSVPService {

  @Autowired
  private RSVPRepository rsvpRepository;

  @Autowired
  UserService userService;

  @Autowired
  EventService eventService;

  public RSVP createRSVP(String eventId, String userId, RSVP rsvp) {
    Event event = eventService.findById(Long.parseLong(eventId));
    User user = userService.findUserById(Long.parseLong(userId));
    Optional<RSVP> rsvpCheck= rsvpRepository.findByUserAndEvent(user, event);
    if(rsvpCheck.isPresent()) {
      throw (new RSVPExistsException("RSVP Already Exists"));
    }
    rsvp.setEvent(event);
    rsvp.setUser(user);
    return rsvpRepository.save(rsvp);
  }


  public List<RSVP> getAttendeesByEvent(String eventId) {
    Event event = eventService.findById(Long.parseLong(eventId));
    return rsvpRepository.findByEvent(event);
  }


  public void cancelRSVP(String eventId, String userId) {
    Event event = eventService.findById(Long.parseLong(eventId));
    User user = userService.findUserById(Long.parseLong(userId));
    Optional<RSVP> optionalRSVP = rsvpRepository.findByUserAndEvent(user, event);
    if (optionalRSVP.isPresent()) {
      RSVP rsvp = optionalRSVP.get();
      rsvpRepository.delete(rsvp);
    } else {
      throw new RSVPNotExistException("RSVP not found");
    }
  }
}
