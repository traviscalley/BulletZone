package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.test.mock.MockContext;
import android.widget.ImageView;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class ViewFactoryTest {

    @Mock
    ImageView iv;
    ViewFactory factory;
    Context c = new MockContext();

    @Test
    public void getInstance() {
    }

    @Test
    public void setMyID() {
    }

    @Test
    public void makeCellViewTank() {
        factory = ViewFactory.getInstance();
        factory.setContext( c );
        factory.makeCellView(iv, 10011000);
    }
}