package com.kosa.moimeasy.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomDTO {
    private String roomName;
    private Long createdBy;
    private List<Long> memberIds;
    private List<String> memberNicknames;
}