package net.ctdp.rfdynhud.widgets._util;

import java.io.IOException;

import net.ctdp.rfdynhud.editor.properties.Property;

public interface WidgetsConfigurationWriter
{
    public void writeProperty( String key, Object value, Boolean quoteValue, String comment ) throws IOException;
    
    public void writeProperty( String key, Object value, String comment ) throws IOException;
    
    public void writeProperty( Property property, Boolean quoteValue, String comment ) throws IOException;
    
    public void writeProperty( Property property, String comment ) throws IOException;
}