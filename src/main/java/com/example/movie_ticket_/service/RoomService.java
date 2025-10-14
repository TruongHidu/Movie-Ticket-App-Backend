// PhongChieuService.java
package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.Room;
import com.example.movie_ticket_.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository phongChieuRepository;

    public List<Room> getAll() {
        return phongChieuRepository.findAll();
    }

    public Room getById(String id) {
        return phongChieuRepository.findById(id).orElse(null);
    }

    public Room save(Room phongChieu) {
        // Nếu chưa có ID thì tự sinh
        if (phongChieu.getIdPhongChieu() == null || phongChieu.getIdPhongChieu().isBlank()) {
            String lastId = phongChieuRepository.findLastId(); // VD: PC009
            String newId = generateNextId(lastId); // => PC010
            phongChieu.setIdPhongChieu(newId);
        }
        return phongChieuRepository.save(phongChieu);
    }

    public Room add(Room room) {
        // Luôn đảm bảo đây là bản ghi mới, không bị Hibernate nhầm
        String lastId = phongChieuRepository.findLastId(); // VD: PC002
        String newId = generateNextId(lastId);             // => PC003

        // Tạo một đối tượng hoàn toàn mới (tránh reuse object đã gắn session)
        Room newRoom = new Room();
        newRoom.setIdPhongChieu(newId);
        newRoom.setTenPhongChieu(room.getTenPhongChieu());
        newRoom.setStatus(room.getStatus());

        return phongChieuRepository.saveAndFlush(newRoom);
    }


    public Room update(Room room) {
        return phongChieuRepository.save(room);
    }

    private String generateNextId(String lastId) {
        if (lastId == null) return "PC001";
        int num = Integer.parseInt(lastId.substring(2)) + 1;
        return String.format("PC%03d", num);
    }


    public void delete(String id) {
        phongChieuRepository.deleteById(id);
    }

    public void changeRoomStatus(String id, int newStatus) {
        Room room = phongChieuRepository.findById(id).orElse(null);
        if (room != null) {
            room.setStatus(newStatus);
            phongChieuRepository.save(room);
        }
    }

    public List<Room> getActiveRooms() {
        return phongChieuRepository.findByStatus(1);
    }
}
