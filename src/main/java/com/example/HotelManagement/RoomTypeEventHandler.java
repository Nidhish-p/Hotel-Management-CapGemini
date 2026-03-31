package com.example.HotelManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.RoomRepository;

@Component
@RepositoryEventHandler
public class RoomTypeEventHandler {

    @Autowired
    private RoomRepository roomRepo;

    @HandleBeforeDelete
    public void handleRoomTypeDelete(RoomType roomType) {
        boolean exists = roomRepo.existsByRoomType(roomType);
        if (exists) {
            throw new RuntimeException("RoomType is linked to a Room");
        }
    }
}