package com.example.demo.engine;

public interface Updatable extends GameComponent{
    void update(double deltaTime);
    void clear();
}
