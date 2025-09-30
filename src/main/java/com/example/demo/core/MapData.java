package com.example.demo.core;

import java.util.List;

/**
 * Record để đóng gói danh sách gạch và tường của một level.
 */
public record MapData(List<Brick> bricks, List<Wall> walls) {}