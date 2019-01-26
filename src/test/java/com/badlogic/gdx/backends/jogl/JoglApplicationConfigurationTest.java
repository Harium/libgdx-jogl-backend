package com.badlogic.gdx.backends.jogl;

import com.badlogic.gdx.backends.jogamp.JoglApplicationConfiguration;
import com.badlogic.gdx.backends.jogamp.JoglAwtApplicationConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class JoglApplicationConfigurationTest {

    @Test
    public void testInit() {
        JoglApplicationConfiguration configuration = new JoglAwtApplicationConfiguration();
        Assert.assertNotNull(configuration);
    }

}
