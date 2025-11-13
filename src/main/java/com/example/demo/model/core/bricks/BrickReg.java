package com.example.demo.model.core.bricks;

import com.example.demo.model.core.factory.BrickFactory;
import com.example.demo.model.core.factory.bricks.NormalBrickFactory;
import com.example.demo.model.core.factory.bricks.SteelBrickFactory;
import com.example.demo.utils.var.GameVar;

import java.util.ArrayList;
import java.util.List;

public class BrickReg {
    private static final List<BrickFactory> factories = new ArrayList<>();

    static {
        double w = GameVar.WIDTH_OF_BRICK;
        double h = GameVar.HEIGHT_OF_BRICK;

        factories.add(new NormalBrickFactory(1, w, h));
        factories.add(new NormalBrickFactory(2, w, h));
        factories.add(new NormalBrickFactory(3, w, h));
        factories.add(new NormalBrickFactory(4, w, h));
        factories.add(new NormalBrickFactory(5, w, h));
        factories.add(new SteelBrickFactory());
    }

    public static BrickFactory getRandomFactory() {
        int index = (int) (Math.random() * factories.size());
        return factories.get(index);
    }
}
