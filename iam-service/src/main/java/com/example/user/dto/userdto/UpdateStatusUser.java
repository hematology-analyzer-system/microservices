package com.example.user.dto.userdto;

import lombok.Data;

@Data
public class UpdateStatusUser {
    private boolean Lock;
    UpdateStatusUser(boolean lock) {
        this.Lock = lock;
    }

}
