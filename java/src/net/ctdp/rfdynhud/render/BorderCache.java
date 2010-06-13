package net.ctdp.rfdynhud.render;

import java.io.File;
import java.util.HashMap;

import org.jagatoo.util.errorhandling.ParsingException;
import org.jagatoo.util.ini.AbstractIniParser;

import net.ctdp.rfdynhud.gamedata.GameFileSystem;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.util.Logger;
import net.ctdp.rfdynhud.util.TextureManager;

/**
 * The {@link BorderCache} is used to load borders only once.
 * 
 * @author Marvin Froehlich
 */
public class BorderCache
{
    private static final HashMap<String, Object[]> CACHE = new HashMap<String, Object[]>();
    
    private static String parseTypeFromIni( File iniFile )
    {
        final String[] type = { null };
        
        try
        {
            new AbstractIniParser()
            {
                @Override
                protected boolean onSettingParsed( int lineNr, String group, String key, String value, String comment ) throws ParsingException
                {
                    if ( "General".equals( group ) )
                    {
                        if ( key.equals( "Type" ) )
                        {
                            type[0] = value;
                            
                            return ( false );
                        }
                    }
                    
                    return ( true );
                }
            }.parse( iniFile );
        }
        catch ( Throwable t )
        {
            Logger.log( t );
            
            return ( type[0] );
        }
        
        return ( type[0] );
    }
    
    private static String parseImageFromIni( File iniFile )
    {
        final String[] image = { null };
        
        try
        {
            new AbstractIniParser()
            {
                @Override
                protected boolean onSettingParsed( int lineNr, String group, String key, String value, String comment ) throws ParsingException
                {
                    if ( "General".equals( group ) )
                    {
                        if ( key.equals( "Image" ) )
                        {
                            image[0] = value;
                            
                            return ( false );
                        }
                    }
                    
                    return ( true );
                }
            }.parse( iniFile );
        }
        catch ( Throwable t )
        {
            Logger.log( t );
            
            return ( image[0] );
        }
        
        return ( image[0] );
    }
    
    private static String parseRendererFromIni( File iniFile )
    {
        final String[] renderer = { null };
        
        try
        {
            new AbstractIniParser()
            {
                @Override
                protected boolean onSettingParsed( int lineNr, String group, String key, String value, String comment ) throws ParsingException
                {
                    if ( "General".equals( group ) )
                    {
                        if ( key.equals( "RendererClass" ) )
                        {
                            renderer[0] = value;
                            
                            return ( false );
                        }
                    }
                    
                    return ( true );
                }
            }.parse( iniFile );
        }
        catch ( Throwable t )
        {
            Logger.log( t );
            
            return ( renderer[0] );
        }
        
        return ( renderer[0] );
    }
    
    private static BorderMeasures parseMeasuresFromIni( File iniFile )
    {
        final BorderMeasures measures = new BorderMeasures();
        
        try
        {
            new AbstractIniParser()
            {
                @Override
                protected boolean onSettingParsed( int lineNr, String group, String key, String value, String comment ) throws ParsingException
                {
                    if ( "Measures".equals( group ) )
                    {
                        if ( key.equals( "LeftWidth" ) )
                            measures.setLeftWidth( Integer.parseInt( value ) );
                        else if ( key.equals( "RightWidth" ) )
                            measures.setRightWidth( Integer.parseInt( value ) );
                        else if ( key.equals( "TopHeight" ) )
                            measures.setTopHeight( Integer.parseInt( value ) );
                        else if ( key.equals( "BottomHeight" ) )
                            measures.setBottomHeight( Integer.parseInt( value ) );
                        
                        else if ( key.equals( "InnerLeftWidth" ) )
                            measures.setInnerLeftWidth( Integer.parseInt( value ) );
                        else if ( key.equals( "InnerRightWidth" ) )
                            measures.setInnerRightWidth( Integer.parseInt( value ) );
                        else if ( key.equals( "InnerTopHeight" ) )
                            measures.setInnerTopHeight( Integer.parseInt( value ) );
                        else if ( key.equals( "InnerBottomHeight" ) )
                            measures.setInnerBottomHeight( Integer.parseInt( value ) );
                        
                        else if ( key.equals( "OpaqueLeftWidth" ) )
                            measures.setOpaqueLeftWidth( Integer.parseInt( value ) );
                        else if ( key.equals( "OpaqueRightWidth" ) )
                            measures.setOpaqueRightWidth( Integer.parseInt( value ) );
                        else if ( key.equals( "OpaqueTopHeight" ) )
                            measures.setOpaqueTopHeight( Integer.parseInt( value ) );
                        else if ( key.equals( "InnerBottomHeight" ) )
                            measures.setOpaqueBottomHeight( Integer.parseInt( value ) );
                    }
                    
                    return ( true );
                }
            }.parse( iniFile );
        }
        catch ( Throwable t )
        {
            Logger.log( t );
            
            return ( measures );
        }
        
        return ( measures );
    }
    
    private static Object[] getFallback( String iniFilename )
    {
        Object[] bw = { null, null };
        
        CACHE.put( iniFilename, bw );
        
        return ( bw );
    }
    
    /**
     * Gets or creates a TexturedBorder with the given side widths.
     * 
     * @param texture
     */
    public static BorderWrapper getBorder( String iniFilename, IntProperty paddingTop, IntProperty paddingLeft, IntProperty paddingRight, IntProperty paddingBottom )
    {
        if ( iniFilename == null )
            return ( null );
        
        Object[] border = CACHE.get( iniFilename );
        
        if ( border != null )
            return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
        
        if ( File.separatorChar != '/' )
            iniFilename = iniFilename.replace( '/', File.separatorChar );
        if ( File.separatorChar != '\\' )
            iniFilename = iniFilename.replace( '\\', File.separatorChar );
        
        File iniFile = new File( GameFileSystem.INSTANCE.getBordersFolder(), iniFilename );
        
        if ( !iniFile.exists() || !iniFile.isFile() || !iniFile.getName().toLowerCase().endsWith( ".ini" ) )
        {
            Logger.log( "[Error] Border ini file invalid \"" + iniFilename + "\"." );
            
            border = getFallback( iniFilename );
            
            return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
        }
        
        String type = parseTypeFromIni( iniFile );
        if ( type == null )
        {
            Logger.log( "[Error] No \"Type\" setting found in \"" + iniFile.getAbsolutePath() + "\"." );
            
            border = getFallback( iniFilename );
            
            return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
        }
        
        if ( type.equals( "Image" ) )
        {
            String textureName = parseImageFromIni( iniFile );
            
            if ( textureName == null )
            {
                Logger.log( "[Error] No \"Image\" setting found in \"" + iniFile.getAbsolutePath() + "\"." );
                
                border = getFallback( iniFilename );
                
                return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
            }
            
            TextureImage2D texture = TextureManager.getImage( "borders" + File.separator + textureName, false ).getTextureImage();
            
            border = new Object[] { new ImageBorderRenderer( textureName, texture ), parseMeasuresFromIni( iniFile ) };
            
            BorderWrapper bw = new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom );
            
            CACHE.put( iniFilename, border );
            
            return ( bw );
        }
        
        if ( type.equals( "Renderer" ) )
        {
            String rendererClass = parseRendererFromIni( iniFile );
            
            if ( rendererClass == null )
            {
                Logger.log( "[Error] No \"RendererClass\" setting found in \"" + iniFile.getAbsolutePath() + "\"." );
                
                border = getFallback( iniFilename );
                
                return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
            }
            
            Class<?> clazz = null;
            try
            {
                clazz = (Class<?>)Class.forName( rendererClass );
            }
            catch ( Throwable t )
            {
                Logger.log( "[Error] Unable to load BorderRenderer class \"" + rendererClass + "\"." );
                
                border = getFallback( iniFilename );
                
                return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
            }
            
            if ( !BorderRenderer.class.isAssignableFrom( clazz ) )
            {
                Logger.log( "[Error] \"" + rendererClass + "\" is not a subclass of " + BorderRenderer.class.getName() + "." );
                
                border = getFallback( iniFilename );
                
                return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
            }
            
            BorderRenderer br = null;
            try
            {
                br = (BorderRenderer)clazz.newInstance();
            }
            catch ( Throwable t )
            {
                Logger.log( "[Error] Unable to instantiate " + clazz.getName() + " using default constructor." );
                
                border = getFallback( iniFilename );
                
                return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
            }
            
            border = new Object[] { br, parseMeasuresFromIni( iniFile ) };
            
            BorderWrapper bw = new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom );
            
            CACHE.put( iniFilename, border );
            
            return ( bw );
        }
        
        Logger.log( "[Error] Unknown border type \"" + type + "\" in border ini file \"" + iniFile.getAbsolutePath() + "\"." );
        
        border = getFallback( iniFilename );
        
        return ( new BorderWrapper( (BorderRenderer)border[0], (BorderMeasures)border[1], paddingTop, paddingLeft, paddingRight, paddingBottom ) );
    }
}
