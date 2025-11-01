package com.example.demo.model.utils;

import com.example.demo.utils.Vector2D;
import org.junit.jupiter.api.Test;

import com.example.demo.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2DTest {

    @Test
    void testAdd() {
        Vector2D a = new Vector2D(1, 2);
        Vector2D b = new Vector2D(3, 4);
        Vector2D result = a.add(b);
        assertEquals(4, result.x);
        assertEquals(6, result.y);
    }
}
