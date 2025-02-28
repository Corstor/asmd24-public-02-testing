package OperationalSpes;

import devices.FailingPolicy;
import devices.RandomFailing;
import devices.StandardDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StandardDeviceTest {
    @Mock
    FailingPolicy failingPolicy;

    @Spy
    RandomFailing randomFailing;

    @Spy
    FailingPolicy spyFailingPolicy;

    StandardDevice sd;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.sd = new StandardDevice(failingPolicy);
    }

    @Test
    public void standardDeviceIsInitiallyOff() {
        assertFalse(sd.isOn());
    }

    @Test
    public void standardDeviceCanBeSwitchedOn() {
        when(failingPolicy.attemptOn()).thenReturn(true);
        when(failingPolicy.policyName()).thenReturn("policyName");
        sd.on();
        assertTrue(sd.isOn());
        assertEquals(sd.toString(), "StandardDevice{" +
                "policy=" + failingPolicy.policyName() +
                ", on=" + sd.isOn() +
                "}");
    }

    @Test
    public void standardDeviceCanBeSwitchedOff() {
        when(failingPolicy.attemptOn()).thenReturn(true);
        sd.on();
        sd.off();
        assertFalse(sd.isOn());
    }

    @Test
    public void standardDeviceCannotBeSwitchedOn() {
        when(failingPolicy.attemptOn()).thenReturn(false);
        assertThrows(IllegalStateException.class, sd::on);
    }

    @Test
    public void standardDeviceCanBeSwitchedMultipleTimes() {
        when(failingPolicy.attemptOn()).thenReturn(true, true, false);
        sd.on();
        sd.on();
        assertThrows(IllegalStateException.class, sd::on);
    }

    @Test
    public void standardDeviceCallsFailingPolicyAttemptOn() {
        StandardDevice sd = new StandardDevice(randomFailing);

        try {
            sd.on();
        } catch (IllegalStateException e) {}

        verify(randomFailing).attemptOn();
    }

    @Test
    public void standardDeviceCanBeReset() {
        when(failingPolicy.attemptOn()).thenReturn(true);
        sd.on();
        sd.reset();
        assertFalse(sd.isOn());
    }

    @Test
    public void standardDeviceCallsFailingPolicyReset() {
        StandardDevice sd = new StandardDevice(spyFailingPolicy);

        when(spyFailingPolicy.attemptOn()).thenReturn(true);
        sd.on();
        sd.reset();
        verify(spyFailingPolicy).reset();
    }
}