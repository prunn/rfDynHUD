package net.ctdp.rfdynhud.widgets.controls;

import java.io.IOException;

import net.ctdp.rfdynhud.editor.hiergrid.FlaggedList;
import net.ctdp.rfdynhud.editor.properties.BooleanProperty;
import net.ctdp.rfdynhud.editor.properties.ColorProperty;
import net.ctdp.rfdynhud.editor.properties.IntegerProperty;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.TelemetryData;
import net.ctdp.rfdynhud.input.InputAction;
import net.ctdp.rfdynhud.render.Texture2DCanvas;
import net.ctdp.rfdynhud.render.TransformableTexture;
import net.ctdp.rfdynhud.widgets._util.Size;
import net.ctdp.rfdynhud.widgets._util.WidgetsConfigurationWriter;
import net.ctdp.rfdynhud.widgets.widget.Widget;

/**
 * The {@link ControlsWidget} displays clutch, brake and throttle.
 * 
 * @author Marvin Froehlich
 */
public class ControlsWidget extends Widget
{
    private final BooleanProperty displayClutch = new BooleanProperty( this, "displayClutch", true )
    {
        @Override
        protected void onValueChanged( boolean newValue )
        {
            texClutch = null;
            texBrake = null;
            texThrottle = null;
        }
    };
    private final BooleanProperty displayBrake = new BooleanProperty( this, "displayBrake", true )
    {
        @Override
        protected void onValueChanged( boolean newValue )
        {
            texClutch = null;
            texBrake = null;
            texThrottle = null;
        }
    };
    private final BooleanProperty displayThrottle = new BooleanProperty( this, "displayThrottle", true )
    {
        @Override
        protected void onValueChanged( boolean newValue )
        {
            texClutch = null;
            texBrake = null;
            texThrottle = null;
        }
    };
    
    private final ColorProperty clutchColor = new ColorProperty( this, "clutchColor", "#0000FF" );
    private final ColorProperty brakeColor = new ColorProperty( this, "brakeColor", "#FF0000" );
    private final ColorProperty throttleColor = new ColorProperty( this, "throttleColor", "#00FF00" );
    
    private TransformableTexture texClutch = null;
    private TransformableTexture texBrake = null;
    private TransformableTexture texThrottle = null;
    
    private final IntegerProperty gap = new IntegerProperty( this, "gap", 10 );
    
    private int initSubTextures( int widgetInnerWidth, int widgetInnerHeight )
    {
        int numBars = 0;
        if ( displayClutch.getBooleanValue() )
            numBars++;
        if ( displayBrake.getBooleanValue() )
            numBars++;
        if ( displayThrottle.getBooleanValue() )
            numBars++;
        
        if ( numBars == 0 )
        {
            texClutch = null;
            texBrake = null;
            texThrottle = null;
            
            return ( 0 );
        }
        
        final int gap = this.gap.getIntegerValue();
        final int w = ( widgetInnerWidth - 6 + gap ) / numBars - gap;
        final int h = widgetInnerHeight - 6;
        
        int left = 3;
        if ( displayClutch.getBooleanValue() && ( ( texClutch == null ) || ( texClutch.getWidth() != w ) || ( texClutch.getHeight() != h ) ) )
        {
            texClutch = new TransformableTexture( w, h, left, 3, 0, 0, 0f, 1f, 1f );
            left += w + gap;
        }
        
        if ( displayBrake.getBooleanValue() && ( ( texBrake == null ) || ( texBrake.getWidth() != w ) || ( texBrake.getHeight() != h ) ) )
        {
            texBrake = new TransformableTexture( w, h, left, 3, 0, 0, 0f, 1f, 1f );
            left += w + gap;
        }
        
        if ( displayThrottle.getBooleanValue() && ( ( texThrottle == null ) || ( texThrottle.getWidth() != w ) || ( texThrottle.getHeight() != h ) ) )
        {
            texThrottle = new TransformableTexture( w, h, left, 3, 0, 0, 0f, 1f, 1f );
            left += w + gap;
        }
        
        return ( numBars );
    }
    
    @Override
    public TransformableTexture[] getSubTextures( boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight )
    {
        int numBars = initSubTextures( widgetInnerWidth, widgetInnerHeight );
        
        if ( numBars == 0 )
            return ( null );
        
        TransformableTexture[] texs = new TransformableTexture[ numBars ];
        
        int i = 0;
        if ( displayClutch.getBooleanValue() )
            texs[i++] = texClutch;
        if ( displayBrake.getBooleanValue() )
            texs[i++] = texBrake;
        if ( displayThrottle.getBooleanValue() )
            texs[i++] = texThrottle;
        
        return ( texs );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onBoundInputStateChanged( boolean isEditorMode, InputAction action, boolean state, int modifierMask )
    {
    }
    
    @Override
    public void onRealtimeExited( boolean isEditorMode, LiveGameData gameData )
    {
        super.onRealtimeExited( isEditorMode, gameData );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkForChanges( boolean isEditorMode, boolean clock1, boolean clock2, LiveGameData gameData, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize( boolean isEditorMode, boolean clock1, boolean clock2, LiveGameData gameData, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        initSubTextures( width, height );
        
        if ( displayClutch.getBooleanValue() )
            texClutch.getTexture().clear( clutchColor.getColor(), true, null );
        
        if ( displayBrake.getBooleanValue() )
            texBrake.getTexture().clear( brakeColor.getColor(), true, null );
        
        if ( displayThrottle.getBooleanValue() )
            texThrottle.getTexture().clear( throttleColor.getColor(), true, null );
    }
    
    @Override
    protected void drawWidget( boolean isEditorMode, boolean clock1, boolean clock2, boolean needsCompleteRedraw, LiveGameData gameData, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        final TelemetryData telemData = gameData.getTelemetryData();
        float uClutch = isEditorMode ? 0.5f : telemData.getUnfilteredClutch();
        float uBrake = isEditorMode ? 0.2f : telemData.getUnfilteredBrake();
        float uThrottle = telemData.getUnfilteredThrottle();
        
        final int h = displayThrottle.getBooleanValue() ? texThrottle.getHeight() : ( displayBrake.getBooleanValue() ? texBrake.getHeight() : ( displayClutch.getBooleanValue() ? texClutch.getHeight() : 0 ) );
        int clutch = (int)( h * uClutch );
        int brake = (int)( h * uBrake );
        int throttle = (int)( h * uThrottle );
        
        if ( displayClutch.getBooleanValue() )
            texClutch.setClipRect( 0, h - clutch, texClutch.getWidth(), clutch, true );
        if ( displayBrake.getBooleanValue() )
            texBrake.setClipRect( 0, h - brake, texBrake.getWidth(), brake, true );
        if ( displayThrottle.getBooleanValue() )
            texThrottle.setClipRect( 0, h - throttle, texThrottle.getWidth(), throttle, true );
        
        if ( isEditorMode )
        {
            if ( displayClutch.getBooleanValue() )
                texClutch.drawInEditor( texCanvas, offsetX, offsetY );
            if ( displayBrake.getBooleanValue() )
                texBrake.drawInEditor( texCanvas, offsetX, offsetY );
            if ( displayThrottle.getBooleanValue() )
                texThrottle.drawInEditor( texCanvas, offsetX, offsetY );
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveProperties( WidgetsConfigurationWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( displayClutch, "Display the clutch bar?" );
        writer.writeProperty( clutchColor, "The color used for the clutch bar in the format #RRGGBB (hex)." );
        writer.writeProperty( displayBrake, "Display the brake bar?" );
        writer.writeProperty( brakeColor, "The color used for the brake bar in the format #RRGGBB (hex)." );
        writer.writeProperty( displayThrottle, "Display the throttle bar?" );
        writer.writeProperty( throttleColor, "The color used for the throttle bar in the format #RRGGBB (hex)." );
        writer.writeProperty( gap, "Gap between the bars" );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadProperty( String key, String value )
    {
        super.loadProperty( key, value );
        
        if ( displayClutch.loadProperty( key, value ) );
        else if ( clutchColor.loadProperty( key, value ) );
        else if ( displayBrake.loadProperty( key, value ) );
        else if ( brakeColor.loadProperty( key, value ) );
        else if ( displayThrottle.loadProperty( key, value ) );
        else if ( throttleColor.loadProperty( key, value ) );
        else if ( gap.loadProperty( key, value ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getProperties( FlaggedList propsList )
    {
        super.getProperties( propsList );
        
        FlaggedList props = new FlaggedList( "Specific", true );
        
        props.add( displayClutch );
        props.add( clutchColor );
        props.add( displayBrake );
        props.add( brakeColor );
        props.add( displayThrottle );
        props.add( throttleColor );
        props.add( gap );
        
        propsList.add( props );
    }
    
    @Override
    protected boolean hasText()
    {
        return ( false );
    }
    
    public ControlsWidget( String name )
    {
        super( name, Size.PERCENT_OFFSET + 0.099f, Size.PERCENT_OFFSET + 0.165f );
    }
}