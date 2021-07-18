package com.nowinski.kamil.flighttracker.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class AngleArithmTest {

    @Test
    public void close_angles() {
        assertEquals(AngleArithm.close_angles(355, 346, 10), true);
        assertEquals(AngleArithm.close_angles(300, 281, 20), true);
        assertEquals(AngleArithm.close_angles(70, 60, 10), true);
        assertEquals(AngleArithm.close_angles(60, 40, 20), true);

        assertNotEquals(AngleArithm.close_angles(355, 344, 10), true);
        assertNotEquals(AngleArithm.close_angles(300, 279, 20), true);
    }

    @Test
    public void computeAngle() {
        long angle = AngleArithm.computeAngle(49.687392, 19.736616, 49.688398, 19.731530, 255);
    }

    @Test
    public void angleFromCoordinate() {
    }
}