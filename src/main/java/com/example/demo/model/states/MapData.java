package com.example.demo.model.states;

import com.example.demo.model.core.Wall;
import com.example.demo.model.core.bricks.Brick;

import java.util.List;

/**
 * Record để đóng gói danh sách gạch và tường của một level.
 */
public record MapData(List<Brick> bricks, List<Wall> walls) {}