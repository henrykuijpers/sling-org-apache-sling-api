package org.apache.sling.api.wrappers;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SlingHttpServletRequestWrapperTest {
    @Mock
    private AdapterManager adapterManager;

    @Mock
    private SlingHttpServletRequest request;

    @Test
    public void adaptToShouldUseWrapperAsSourceForAdaption() {
        final SlingHttpServletRequest underTest = new SlingHttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return super.getRequestURI() + "/1234";
            }
        };

        when(adapterManager.getAdapter(any(SlingHttpServletRequest.class), eq(MyModel.class)))
            .thenAnswer(invocationOnMock ->
                new MyModel(((SlingHttpServletRequest)invocationOnMock.getArgument(0)).getRequestURI()));
        SlingAdaptable.setAdapterManager(adapterManager);
        when(request.adaptTo(MyModel.class)).thenAnswer(invocationOnMock ->
            adapterManager.getAdapter(invocationOnMock.getMock(), MyModel.class));
        when(request.getRequestURI()).thenReturn("/test");

        final MyModel result = underTest.adaptTo(MyModel.class);
        assertNotNull("A model instance should be returned", result);
        assertEquals("The model should have considered the overwritten path","/test/1234", result.getPath());
    }

    private static class MyModel {
        private final String path;

        public MyModel(final String path) {
            this.path = path;
        }

        public String getPath() {
            return this.path;
        }
    }
}