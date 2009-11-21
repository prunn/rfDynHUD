package net.ctdp.rfdynhud.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;

import org.jagatoo.util.errorhandling.ParsingException;
import org.jagatoo.util.ini.AbstractIniParser;

public class RFactorTools
{
    private static final File stripDotDots( String pathname )
    {
        try
        {
            return ( new File( pathname ).getCanonicalFile() );
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        
        return ( new File( "." ) );
    }
    
    public static String extractRFactorPath()
    {
        String[] classPath = System.getProperty( "java.class.path" ).split( File.pathSeparator );
        
        for ( String s : classPath )
        {
            if ( s.contains( "rfdynhud.jar" ) )
                return ( stripDotDots( s ).getParentFile().getParentFile().getParent() );
            else if ( s.contains( "rfdynhud_editor.jar" ) )
                return ( stripDotDots( s ).getParentFile().getParentFile().getParentFile().getParent() );
        }
        
        return ( "" );
    }
    
    public static final String RFACTOR_PATH = extractRFactorPath();
    public static final File RFACTOR_FOLDER = new File( RFACTOR_PATH );
    public static final String PLUGIN_PATH = RFACTOR_PATH + File.separator + "Plugins" + File.separator + "rfDynHUD";
    public static final String CONFIG_PATH = ResourceManager.isJarMode() ? PLUGIN_PATH + File.separator + "config" : new java.io.File( "." ).getAbsoluteFile().getParent() + File.separator + "data" + File.separator + "config";
    public static final File IMAGES_FOLDER = new File( CONFIG_PATH + File.separator + "data" + File.separator + "images" );
    public static final String EDITOR_PATH = ResourceManager.isJarMode() ? PLUGIN_PATH + File.separator + "editor" : new java.io.File( "." ).getAbsoluteFile().getParent() + File.separator + "data";
    public static final String LOG_PATH = PLUGIN_PATH + File.separator + "log";
    
    private static File lastPLRFile = null;
    private static long lastPLRModified = -1L;
    
    private static String modName = null;
    private static String vehName = null;
    private static String lastUsedTrackFile = null;
    private static int trackRaceLaps = -1;
    private static Float multiRaceLength = null;
    
    public static File getProfileFile( File profileFolder )
    {
        File plr = new File( profileFolder, profileFolder.getName() + ".PLR" );
        
        if ( !plr.exists() )
            return ( null );
        
        return ( plr );
    }
    
    public static File getProfileFolder( File userDataFolder )
    {
        if ( !RFACTOR_FOLDER.exists() )
            return ( null );
        
        if ( userDataFolder == null )
            userDataFolder = new File( RFACTOR_PATH, "UserData" );
        
        File[] profileCandidates = userDataFolder.listFiles( DIRECTORY_FILE_FILTER );
        
        File profilePLR = null;
        for ( File p : profileCandidates )
        {
            File plr = getProfileFile( p );
            if ( plr != null )
            {
                if ( profilePLR == null )
                    profilePLR = plr;
                else if ( plr.lastModified() > profilePLR.lastModified() )
                    profilePLR = plr;
            }
        }
        
        if ( profilePLR == null )
            return ( null );
        
        return ( profilePLR.getParentFile() );
    }
    
    public static File getProfileFolder()
    {
        return ( getProfileFolder( null ) );
    }
    
    private static final FileFilter DIRECTORY_FILE_FILTER = new FileFilter()
    {
        public boolean accept( File pathname )
        {
            return ( pathname.isDirectory() );
        }
    };
    
    private static void updateProfileInformation( File userDataFolder, File profileFolder )
    {
        if ( profileFolder == null )
        {
            profileFolder = getProfileFolder( userDataFolder );
        }
        
        if ( profileFolder == null )
            return;
        
        File profilePLR = getProfileFile( profileFolder );
        
        if ( profilePLR == null )
            return;
        
        if ( ( lastPLRFile != null ) && profilePLR.equals( lastPLRFile ) && ( profilePLR.lastModified() <= lastPLRModified ) )
            return;
        
        modName = null;
        vehName = null;
        lastUsedTrackFile = null;
        multiRaceLength = null;
        
        try
        {
            new AbstractIniParser()
            {
                @Override
                protected boolean onSettingParsed( int lineNr, String group, String key, String value, String comment ) throws ParsingException
                {
                    if ( group == null )
                    {
                    }
                    else if ( group.equalsIgnoreCase( "SCENE" ) )
                    {
                        if ( key.equalsIgnoreCase( "Scene File" ) )
                        {
                            lastUsedTrackFile = value;
                        }
                    }
                    else if ( group.equalsIgnoreCase( "DRIVER" ) )
                    {
                        if ( key.equalsIgnoreCase( "Game Description" ) )
                        {
                            if ( value.length() > 4 )
                                modName = value.substring( 0, value.length() - 4 );
                            else
                                modName = value;
                        }
                        else if ( key.equalsIgnoreCase( "Vehicle File" ) )
                        {
                            vehName = value;
                        }
                    }
                    else if ( group.equalsIgnoreCase( "Game Options" ) )
                    {
                        if ( key.equalsIgnoreCase( "MULTI Race Length" ) )
                        {
                            multiRaceLength = Float.valueOf( value );
                        }
                    }
                    
                    return ( true );
                }
            }.parse( profilePLR );
        }
        catch ( Throwable t )
        {
            Logger.log( t );
            return;
        }
    }
    
    public static String getModName( File profileFolder )
    {
        updateProfileInformation( null, profileFolder );
        
        return ( modName );
    }
    
    public static String getPlayerVEHFile( File profileFolder )
    {
        updateProfileInformation( null, profileFolder );
        
        return ( vehName );
    }
    
    public static File getLastUsedTrackFile( File profileFolder )
    {
        updateProfileInformation( null, profileFolder );
        
        if ( lastUsedTrackFile == null )
            return ( null );
        
        return ( new File( RFACTOR_FOLDER, lastUsedTrackFile ) );
    }
    
    public static Float getRaceLengthMultiplier( File profileFolder )
    {
        updateProfileInformation( null, profileFolder );
        
        return ( multiRaceLength );
    }
    
    public static File getCCHFile( File profileFolder )
    {
        if ( profileFolder == null )
            profileFolder = getProfileFolder();
        
        if ( profileFolder == null )
            return ( null );
        
        String modName = getModName( profileFolder );
        
        return ( new File( profileFolder, modName + ".cch" ) );
    }
    
    private static boolean checkGDB( File gdb, String trackname )
    {
        BufferedReader br = null;
        
        try
        {
            boolean trackFound = false;
            boolean raceLapsFound = false;
            
            trackRaceLaps = -1;
            
            br = new BufferedReader( new FileReader( gdb ) );
            
            String line = null;
            while ( ( line = br.readLine() ) != null )
            {
                line = line.trim();
                if ( line.startsWith( "TrackName" ) )
                {
                    int idx = line.indexOf( '=', 9 );
                    if ( ( idx >= 0 ) && line.substring( idx + 1 ).trim().equals( trackname ) )
                    {
                        trackFound = true;
                    }
                }
                else if ( line.startsWith( "RaceLaps" ) )
                {
                    int idx = line.indexOf( '=', 8 );
                    if ( idx >= 0 )
                    {
                        try
                        {
                            trackRaceLaps = Integer.parseInt( line.substring( idx + 1 ).trim() );
                            raceLapsFound = true;
                        }
                        catch ( Throwable t )
                        {
                        }
                    }
                }
                
                if ( trackFound && raceLapsFound )
                    return ( true );
            }
        }
        catch ( Throwable t )
        {
            Logger.log( t );
        }
        finally
        {
            if ( br != null )
                try { br.close(); } catch ( Throwable t ) { Logger.log( t ); }
        }
        
        return ( false );
    }
    
    private static File searchTrackFolder( File parentDir, String trackname )
    {
        for ( File f : parentDir.listFiles() )
        {
            if ( f.isDirectory() )
            {
                File result = searchTrackFolder( f, trackname );
                if ( result != null )
                    return ( result );
            }
            else if ( f.getName().toLowerCase().endsWith( ".gdb" ) )
            {
                if ( checkGDB( f, trackname ) )
                    return ( f.getParentFile() );
            }
        }
        
        return ( null );
    }
    
    /**
     * <p>
     * Finds the folder from the GameData\Locations folder, in which a .gdb file
     * exists, that contains a line<br>
     *   TrackName = trackname
     * </p>
     * WARNING:<br>
     * This operation may take a long time.
     * 
     * @param trackname
     * 
     * @return the first matching folder (or null, if not found, but shouldn't happen).
     */
    public static File findTrackFolder( String trackname )
    {
        File locationsFolder = new File( new File( RFACTOR_PATH, "GameData" ), "Locations" );
        
        if ( ( locationsFolder == null ) || !locationsFolder.exists() )
            return ( null );
        
        return ( searchTrackFolder( locationsFolder, trackname ) );
    }
    
    /**
     * <p>
     * Gets the race laps from the current track reading it from the .gdb file
     * </p>
     * WARNING:<br>
     * This operation may take a long time.
     * 
     * @param trackname
     * 
     * @return the race laps from the current track reading it from the .gdb file
     */
    public static int updateTrackRaceLaps( String trackname )
    {
        findTrackFolder( trackname );
        
        return ( trackRaceLaps );
    }
    
    /**
     * Gets last read track race laps.
     * 
     * @return last read track race laps.
     */
    public static int getTrackRaceLaps()
    {
        return ( trackRaceLaps );
    }
    
    /**
     * <p>
     * Finds the AIW file for the given track.
     * </p>
     * WARNING:<br>
     * This operation may take a long time.
     * 
     * @param trackFolder
     * 
     * @return the AIW file for the given track.
     */
    public static File findAIWFile( File trackFolder )
    {
        if ( trackFolder == null )
            return ( null );
        
        File aiw = null;
        for ( File f : trackFolder.listFiles() )
        {
            if ( !f.isDirectory() && f.getName().toUpperCase().endsWith( ".AIW" ) )
            {
                aiw = f;
                break;
            }
        }
        
        return ( aiw );
    }
    
    /**
     * <p>
     * Finds the AIW file for the given track.
     * </p>
     * WARNING:<br>
     * This operation may take a long time.
     * 
     * @param trackname
     * 
     * @return the AIW file for the given track.
     */
    public static File findAIWFile( String trackname )
    {
        File trackFolder = findTrackFolder( trackname );
                
        return ( findAIWFile( trackFolder ) );
    }
}