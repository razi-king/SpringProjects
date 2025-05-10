package com.example.chatApplication.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.chatApplication.dto.Message;
import com.example.chatApplication.dto.Room;
import com.example.chatApplication.payload.MessageRequest;
import com.example.chatApplication.service.RoomService;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {

    @Autowired
    RoomService roomService;

    // For Sending And Receiving Message
    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Message sendMessage(
            @DestinationVariable String roomId,
            @RequestBody MessageRequest request
    ) {
        if (request.getSender() == null || request.getSender().isEmpty()) {
            throw new InvalidRequestException("Sender cannot be empty");
        }
        if (request.getContent() == null || request.getContent().isEmpty()) {
            throw new InvalidRequestException("Message content cannot be empty");
        }

        Room room = roomService.findByRoomID(roomId);
        if (room == null) {
            throw new RoomNotFoundException("Room with id " + roomId + " not found");
        }

        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(request.getSender());
        message.setTimeStamp(LocalDateTime.now());
        room.getMessage().add(message);
        roomService.saveRoom(room);
        
        return message;
    }
}

// Custom Exception for Room Not Found
@ResponseStatus(HttpStatus.NOT_FOUND)
class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}

// Custom Exception for Invalid Requests
@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}

// Global exception handler
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(RoomNotFoundException.class)
    public String handleRoomNotFound(RoomNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidRequestException.class)
    public String handleInvalidRequest(InvalidRequestException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex) {
        return "An unexpected error occurred: " + ex.getMessage();
    }
}
