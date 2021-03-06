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
package net.ctdp.rfdynhud.gamedata.rfactor1;

import java.io.File;
import java.io.FileFilter;

import net.ctdp.rfdynhud.gamedata.GameFileSystem;
import net.ctdp.rfdynhud.gamedata.ProfileInfo;
import net.ctdp.rfdynhud.util.RFDHLog;

import org.jagatoo.util.errorhandling.ParsingException;
import org.jagatoo.util.ini.AbstractIniParser;

/**
 * Model of the current player's profile information
 * 
 * @author Marvin Froehlich
 */
public class _rf1_ProfileInfo extends ProfileInfo
{
    private static final FileFilter DIRECTORY_FILE_FILTER = new FileFilter()
    {
        @Override
        public boolean accept( File pathname )
        {
            return ( pathname.isDirectory() );
        }
    };
    
    private final GameFileSystem fileSystem;
    private final File USERDATA_FOLDER;
    
    private File profileFolder = null;
    private File plrFile = null;
    private long plrLastModified = -1L;
    
    private File lastUsedTrackFile = null;
    
    private Integer formationLapFlag = null;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isValid()
    {
        return ( plrFile != null );
    }
    
    private File findPLRFile()
    {
        File[] profileCandidates = USERDATA_FOLDER.listFiles( DIRECTORY_FILE_FILTER );
        
        if ( profileCandidates == null )
            return ( null );
        
        File plrFile = null;
        for ( File p : profileCandidates )
        {
            File plr = new File( p, p.getName() + ".PLR" );
            if ( plr.exists() && plr.isFile() )
            {
                if ( plrFile == null )
                    plrFile = plr;
                else if ( plr.lastModified() > plrFile.lastModified() )
                    plrFile = plr;
            }
        }
        
        return ( plrFile );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset()
    {
        super.reset();
        
        profileFolder = null;
        plrFile = null;
        
        lastUsedTrackFile = null;
        formationLapFlag = 1;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateImpl()
    {
        File plrFile = findPLRFile();
        
        if ( plrFile == null )
        {
            RFDHLog.error( "ERROR: No Profile with PLR file found under \"" + USERDATA_FOLDER.getAbsolutePath() + "\". Plugin unusable!" );
            
            reset();
            return ( false );
        }
        
        if ( ( this.plrFile != null ) && plrFile.equals( this.plrFile ) && ( plrFile.lastModified() == plrLastModified ) )
        {
            return ( true );
        }
        
        reset();
        
        RFDHLog.printlnEx( "INFO: Using PLR file \"" + plrFile.getAbsolutePath() + "\"" );
        
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
                            lastUsedTrackFile = new File( value );
                            
                            if ( !lastUsedTrackFile.isAbsolute() )
                                lastUsedTrackFile = new File( fileSystem.getGameFolder(), value );
                        }
                    }
                    else if ( group.equalsIgnoreCase( "DRIVER" ) )
                    {
                        if ( key.equalsIgnoreCase( "RaceCast Email" ) )
                        {
                            raceCastEmail = value;
                        }
                        else if ( key.equalsIgnoreCase( "RaceCast Password" ) )
                        {
                            raceCastPassword = value;
                        }
                        else if ( key.equalsIgnoreCase( "Vehicle File" ) )
                        {
                            vehFile = new File( fileSystem.getGameFolder(), value );
                        }
                        else if ( key.equalsIgnoreCase( "Team" ) )
                        {
                            teamName = value;
                        }
                        else if ( key.equalsIgnoreCase( "Nationality" ) )
                        {
                            nationality = value;
                        }
                        else if ( key.equalsIgnoreCase( "Birth Date" ) )
                        {
                            birthDate = value;
                        }
                        else if ( key.equalsIgnoreCase( "Location" ) )
                        {
                            location = value;
                        }
                        else if ( key.equalsIgnoreCase( "Game Description" ) )
                        {
                            value = value.trim();
                            if ( value.toLowerCase().endsWith( ".rfm" ) )
                                modName = value.substring( 0, value.length() - 4 );
                            else
                                modName = value;
                        }
                        else if ( key.equalsIgnoreCase( "Helmet" ) )
                        {
                            helmet = value;
                        }
                        else if ( key.equalsIgnoreCase( "Unique ID" ) )
                        {
                            uniqueID = Integer.parseInt( value );
                        }
                        else if ( key.equalsIgnoreCase( "Starting Driver" ) )
                        {
                            startingDriver = Integer.parseInt( value );
                        }
                        else if ( key.equalsIgnoreCase( "AI Controls Driver" ) )
                        {
                            aiControlsDriver = Integer.parseInt( value );
                        }
                        else if ( key.equalsIgnoreCase( "Driver Hotswap Delay" ) )
                        {
                            driverHotswapDelay = Float.parseFloat( value );
                        }
                    }
                    else if ( group.equalsIgnoreCase( "Game Options" ) )
                    {
                        if ( key.equalsIgnoreCase( "MULTI Race Length" ) )
                        {
                            multiRaceLength = Float.valueOf( value );
                        }
                        else if ( key.equalsIgnoreCase( "Show Extra Lap" ) )
                        {
                            try
                            {
                                int index = Integer.parseInt( value );
                                
                                showCurrentLap = ( index != 0 );
                            }
                            catch ( Throwable t )
                            {
                                RFDHLog.debug( "Unable to parse \"Show Extra Lap\" from PLR file. Defaulting to 'true'." );
                                showCurrentLap = true;
                            }
                        }
                        else if ( key.equalsIgnoreCase( "Measurement Units" ) )
                        {
                            try
                            {
                                int index = Integer.parseInt( value );
                                
                                measurementUnits = MeasurementUnits.values()[index];
                            }
                            catch ( Throwable t )
                            {
                                RFDHLog.debug( "Unable to parse \"Measurement Units\" from PLR file. Defaulting to METRIC." );
                                measurementUnits = MeasurementUnits.METRIC;
                            }
                        }
                        else if ( key.equalsIgnoreCase( "Speed Units" ) )
                        {
                            try
                            {
                                int index = Integer.parseInt( value );
                                
                                speedUnits = SpeedUnits.values()[index];
                            }
                            catch ( Throwable t )
                            {
                                RFDHLog.debug( "Unable to parse \"Speed Units\" from PLR file. Defaulting to KPH." );
                                speedUnits = SpeedUnits.KPH;
                            }
                        }
                    }
                    else if ( group.equalsIgnoreCase( "Race Conditions" ) )
                    {
                        if ( key.equalsIgnoreCase( "MULTI Reconnaissance" ) )
                        {
                            numReconLaps = Integer.valueOf( value );
                        }
                        else if ( key.equalsIgnoreCase( "MULTI Formation Lap" ) )
                        {
                            formationLapFlag = Integer.valueOf( value );
                        }
                    }
                    
                    return ( true );
                }
            }.parse( plrFile );
            
            this.plrFile = plrFile;
            this.profileFolder = plrFile.getParentFile();
            this.plrLastModified = plrFile.lastModified();
        }
        catch ( Throwable t )
        {
            RFDHLog.exception( t );
        }
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final File getProfileFolder()
    {
        return ( profileFolder );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final File getPLRFile()
    {
        return ( plrFile );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final File getLastUsedSceneFile()
    {
        return ( lastUsedTrackFile );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Boolean getFormationLap()
    {
        if ( formationLapFlag == null )
            return ( null );
        
        // 0=standing start, 1=formation lap & standing start, 2=lap behind safety car & rolling start, 3=use track default, 4=fast rolling start
        switch ( formationLapFlag.intValue() )
        {
            case 0:
                return ( false );
            case 1:
                return ( true );
            case 2:
                return ( true );
            case 3:
                return ( null );
            case 4:
                return ( true );
        }
        
        // Unreachable code!
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final File getCCHFileImpl( String modName )
    {
        if ( profileFolder == null )
            return ( null );
        
        if ( modName == null )
            return ( null );
        
        return ( new File( profileFolder, modName + ".cch" ) );
    }
    
    /**
     * Create a new instance.
     * 
     * @param fileSystem
     */
    public _rf1_ProfileInfo( GameFileSystem fileSystem )
    {
        this.fileSystem = fileSystem;
        this.USERDATA_FOLDER = fileSystem.getPathFromGameConfigINI( "SaveDir", "UserData" );
    }
}
