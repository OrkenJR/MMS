package kz.iitu.orken.medical_managament_system.aop;

import org.junit.jupiter.api.Test;

class AspectPointcutsTest {
    AspectPointcuts aspectPointcuts = new AspectPointcuts();

    @Test
    void testServiceOperation() {
        aspectPointcuts.serviceOperation();
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme