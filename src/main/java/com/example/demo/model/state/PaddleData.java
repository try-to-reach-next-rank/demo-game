package com.example.demo.model.state;

import com.example.demo.model.core.Paddle;
import com.example.demo.model.state.gameobjectdata.GameObjectData;
import com.example.demo.model.state.gameobjectdata.ImageObjectData;

public class PaddleData extends ImageObjectData {

    public PaddleData(Paddle paddle) {
       super(paddle);
    }
}