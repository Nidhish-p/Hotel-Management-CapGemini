package com.example.HotelManagement.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.RoomRepository;

@Component
@RepositoryEventHandler
public class RoomTypeDeleteGuard {

    @Autowired
    private RoomRepository roomRepository;

    @HandleBeforeDelete
    public void handleRoomTypeDelete(RoomType roomType) {
        boolean exists = roomRepository.existsByRoomType(roomType);
        if (exists) {
            throw new RoomTypeLinkedException("RoomType is linked to a Room and cannot be deleted");
        }
    }
}