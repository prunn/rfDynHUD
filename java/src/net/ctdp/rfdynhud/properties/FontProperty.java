/**
 * Copyright (C) 2009-2010 Cars and Tracks Development Project (CTDP).
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.ctdp.rfdynhud.properties;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.widgets.WidgetsConfiguration;
import net.ctdp.rfdynhud.widgets.widget.Widget;
import net.ctdp.rfdynhud.widgets.widget.__WPrivilegedAccess;

/**
 * The {@link FontProperty} serves for customizing a font value.
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class FontProperty extends Property
{
    public static final String STANDARD_FONT_NAME = "StandardFont";
    public static final String STANDARD_FONT2_NAME = "StandardFont2";
    public static final String STANDARD_FONT3_NAME = "StandardFont3";
    public static final String SMALLER_FONT_NAME = "SmallerFont";
    public static final String SMALLER_FONT3_NAME = "SmallerFont3";
    public static final String BIGGER_FONT_NAME = "BiggerFont";
    
    private static final BufferedImage METRICS_PROVIDER_IMAGE = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_BGR );
    private static final Graphics2D METRICS_PROVIDER = METRICS_PROVIDER_IMAGE.createGraphics();
    
    private final WidgetsConfiguration widgetsConf;
    
    private String fontKey;
    private Font font = null;
    private Boolean antiAliased = null;
    
    private FontMetrics metrics = null;
    
    public static String getDefaultNamedFontValue( String name )
    {
        if ( name.equals( STANDARD_FONT_NAME ) )
            return ( "Monospaced-BOLD-13va" );
        
        if ( name.equals( STANDARD_FONT2_NAME ) )
            return ( "Monospaced-BOLD-12va" );
        
        if ( name.equals( STANDARD_FONT3_NAME ) )
            return ( "Monospaced-BOLD-11va" );
        
        if ( name.equals( SMALLER_FONT_NAME ) )
            return ( "Monospaced-BOLD-13va" );
        
        if ( name.equals( SMALLER_FONT3_NAME ) )
            return ( "Monospaced-BOLD-9va" );
        
        if ( name.equals( BIGGER_FONT_NAME ) )
            return ( "Monospaced-BOLD-14va" );
        
        return ( null );
    }
    
    /**
     * 
     * @param oldValue
     * @param newValue
     */
    protected void onValueChanged( String oldValue, String newValue )
    {
    }
    
    public void refresh()
    {
        this.font = null;
    }
    
    public void setFont( String fontKey )
    {
        if ( ( fontKey == null ) && ( this.fontKey == null ) )
            return;
        
        final WidgetsConfiguration widgetsConf = ( widget != null ) ? widget.getConfiguration() : this.widgetsConf;
        
        if ( widgetsConf == null )
        {
            this.fontKey = fontKey;
            this.font = null;
            this.antiAliased = null;
            this.metrics = null;
        }
        else
        {
            String oldValue = widgetsConf.getNamedFontString( this.fontKey );
            if ( oldValue == null )
                oldValue = this.fontKey;
            
            this.fontKey = fontKey;
            this.font = null;
            this.antiAliased = null;
            this.metrics = null;
            
            String newValue = widgetsConf.getNamedFontString( this.fontKey );
            if ( newValue == null )
                newValue = this.fontKey;
            
            if ( ( newValue == null ) || !newValue.equals( oldValue ) )
            {
                if ( widget != null )
                    widget.forceAndSetDirty();
                
                onValueChanged( oldValue, fontKey );
                
                if ( widget != null )
                    __WPrivilegedAccess.onPropertyChanged( this, oldValue, fontKey, widget );
            }
        }
    }
    
    public final void setFont( Font font, boolean virtual, boolean antiAliased )
    {
        setFont( FontUtils.getFontString( font, virtual, antiAliased ) );
    }
    
    public final String getFontKey()
    {
        return ( fontKey );
    }
    
    public final Font getFont()
    {
        if ( fontKey == null )
            return ( null );
        
        if ( font == null )
        {
            font = widget.getConfiguration().getNamedFont( fontKey );
            if ( font == null )
            {
                String fontStr = widget.getDefaultNamedFontValue( fontKey );
                if ( fontStr != null )
                {
                    widget.getConfiguration().addNamedFont( fontKey, fontStr );
                    font = widget.getConfiguration().getNamedFont( fontKey );
                }
                else
                {
                    font = FontUtils.parseFont( fontKey, widget.getConfiguration().getGameResolution().getViewportHeight(), false, true );
                }
            }
        }
        
        return ( font );
    }
    
    public final boolean isAntiAliased()
    {
        if ( antiAliased == null )
        {
            String fontStr = widget.getConfiguration().getNamedFontString( fontKey );
            if ( fontStr == null )
            {
                fontStr = widget.getDefaultNamedFontValue( fontKey );
                if ( fontStr != null )
                    widget.getConfiguration().addNamedFont( fontKey, fontStr );
                else
                    fontStr = fontKey;
            }
            
            antiAliased = FontUtils.parseAntiAliasFlag( fontStr, false, true );
        }
        
        return ( antiAliased );
    }
    
    public final FontMetrics getMetrics()
    {
        if ( metrics == null )
        {
            metrics = METRICS_PROVIDER.getFontMetrics( getFont() );
        }
        
        return ( metrics );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue( Object value )
    {
        setFont( ( value == null ) ? null : String.valueOf( value ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue()
    {
        return ( fontKey );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadValue( String value )
    {
        setValue( value );
    }
    
    /**
     * 
     * @param widgetsConf
     * @param widget
     * @param name the technical name used internally. See {@link #getName()}.
     * @param nameForDisplay the name displayed in the editor. See {@link #getNameForDisplay()}. If <code>null</code> is passed, the value of the name parameter is used.
     * @param defaultValue
     * @param readonly
     */
    private FontProperty( WidgetsConfiguration widgetsConf, Widget widget, String name, String nameForDisplay, String defaultValue, boolean readonly )
    {
        super( widget, name, nameForDisplay, readonly, PropertyEditorType.FONT, null, null );
        
        this.widgetsConf = widgetsConf;
        this.fontKey = defaultValue;
    }
    
    /**
     * 
     * @param widgetsConf
     * @param name the technical name used internally. See {@link #getName()}.
     * @param nameForDisplay the name displayed in the editor. See {@link #getNameForDisplay()}. If <code>null</code> is passed, the value of the name parameter is used.
     * @param defaultValue
     * @param readonly
     */
    public FontProperty( WidgetsConfiguration widgetsConf, String name, String nameForDisplay, String defaultValue, boolean readonly )
    {
        this( widgetsConf, null, name, nameForDisplay, defaultValue, readonly );
    }
    
    /**
     * 
     * @param widgetsConf
     * @param name the technical name used internally. See {@link #getName()}.
     * @param nameForDisplay the name displayed in the editor. See {@link #getNameForDisplay()}. If <code>null</code> is passed, the value of the name parameter is used.
     * @param defaultValue
     */
    public FontProperty( WidgetsConfiguration widgetsConf, String name, String nameForDisplay, String defaultValue )
    {
        this( widgetsConf, name, nameForDisplay, defaultValue, false );
    }
    
    /**
     * 
     * @param widgetsConf
     * @param name the technical name used internally. See {@link #getName()}. 'nameForDisplay' is set to the same value.
     * @param defaultValue
     * @param readonly
     */
    public FontProperty( WidgetsConfiguration widgetsConf, String name, String defaultValue, boolean readonly )
    {
        this( widgetsConf, name, null, defaultValue, readonly );
    }
    
    /**
     * 
     * @param widgetsConf
     * @param name the technical name used internally. See {@link #getName()}. 'nameForDisplay' is set to the same value.
     * @param defaultValue
     */
    public FontProperty( WidgetsConfiguration widgetsConf, String name, String defaultValue )
    {
        this( widgetsConf, name, defaultValue, false );
    }
    
    /**
     * 
     * @param widget
     * @param name the technical name used internally. See {@link #getName()}.
     * @param nameForDisplay the name displayed in the editor. See {@link #getNameForDisplay()}. If <code>null</code> is passed, the value of the name parameter is used.
     * @param defaultValue
     * @param readonly
     */
    public FontProperty( Widget widget, String name, String nameForDisplay, String defaultValue, boolean readonly )
    {
        this( null, widget, name, nameForDisplay, defaultValue, readonly );
    }
    
    /**
     * 
     * @param widget
     * @param name the technical name used internally. See {@link #getName()}.
     * @param nameForDisplay the name displayed in the editor. See {@link #getNameForDisplay()}. If <code>null</code> is passed, the value of the name parameter is used.
     * @param defaultValue
     */
    public FontProperty( Widget widget, String name, String nameForDisplay, String defaultValue )
    {
        this( widget, name, nameForDisplay, defaultValue, false );
    }
    
    /**
     * 
     * @param widget
     * @param name the technical name used internally. See {@link #getName()}. 'nameForDisplay' is set to the same value.
     * @param defaultValue
     * @param readonly
     */
    public FontProperty( Widget widget, String name, String defaultValue, boolean readonly )
    {
        this( widget, name, null, defaultValue, readonly );
    }
    
    /**
     * 
     * @param widget
     * @param name the technical name used internally. See {@link #getName()}. 'nameForDisplay' is set to the same value.
     * @param defaultValue
     */
    public FontProperty( Widget widget, String name, String defaultValue )
    {
        this( widget, name, defaultValue, false );
    }
    
    /**
     * 
     * @param w2pf
     * @param name the technical name used internally. See {@link #getName()}.
     * @param nameForDisplay the name displayed in the editor. See {@link #getNameForDisplay()}. If <code>null</code> is passed, the value of the name parameter is used.
     * @param defaultValue
     * @param readonly
     */
    public FontProperty( WidgetToPropertyForwarder w2pf, String name, String nameForDisplay, String defaultValue, boolean readonly )
    {
        this( (Widget)null, name, nameForDisplay, defaultValue, readonly );
        
        w2pf.addProperty( this );
    }
    
    /**
     * 
     * @param w2pf
     * @param name the technical name used internally. See {@link #getName()}.
     * @param nameForDisplay the name displayed in the editor. See {@link #getNameForDisplay()}. If <code>null</code> is passed, the value of the name parameter is used.
     * @param defaultValue
     */
    public FontProperty( WidgetToPropertyForwarder w2pf, String name, String nameForDisplay, String defaultValue )
    {
        this( w2pf, name, nameForDisplay, defaultValue, false );
    }
    
    /**
     * 
     * @param w2pf
     * @param name the technical name used internally. See {@link #getName()}. 'nameForDisplay' is set to the same value.
     * @param defaultValue
     * @param readonly
     */
    public FontProperty( WidgetToPropertyForwarder w2pf, String name, String defaultValue, boolean readonly )
    {
        this( w2pf, name, null, defaultValue, readonly );
    }
    
    /**
     * 
     * @param w2pf
     * @param name the technical name used internally. See {@link #getName()}. 'nameForDisplay' is set to the same value.
     * @param defaultValue
     */
    public FontProperty( WidgetToPropertyForwarder w2pf, String name, String defaultValue )
    {
        this( w2pf, name, defaultValue, false );
    }
}
