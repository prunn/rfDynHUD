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
package net.ctdp.rfdynhud.gamedata;

import net.ctdp.rfdynhud.editor.EditorPresets;
import net.ctdp.rfdynhud.util.RFDHLog;
import net.ctdp.rfdynhud.util.__UtilHelper;

/**
 * Complete model of live game data.
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class LiveGameData
{
    private final String gameId;
    
    private final _LiveGameDataObjectsFactory gdFactory;
    
    private final GameFileSystem fileSystem;
    
    private final GameResolution gameResolution;
    
    private boolean gamePaused = false;
    private boolean realtimeMode = false;
    
    private final VehiclePhysics physics = new VehiclePhysics();
    private final VehicleSetup setup = new VehicleSetup();
    
    private final TelemetryData telemetryData;
    private final ScoringInfo scoringInfo;
    private final GraphicsInfo graphicsInfo;
    private final CommentaryRequestInfo commentaryInfo;
    
    private final ProfileInfo profileInfo;
    private final ModInfo modInfo;
    private final VehicleInfo vehicleInfo;
    private final TrackInfo trackInfo;
    
    private LiveGameDataController controller = null;
    
    public static interface GameDataUpdateListener
    {
        public void onSessionStarted( LiveGameData gameData, boolean isEditorMode );
        public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode );
        public void onGamePauseStateChanged( LiveGameData gameData, boolean isEditorMode, boolean isPaused );
        public void onRealtimeExited( LiveGameData gameData, boolean isEditorMode );
    }
    
    private GameDataUpdateListener[] updateListeners = null;
    private int[] updateListenerCounts = null;
    GameEventsListener[] gameEventsListeners = null;
    
    public void registerDataUpdateListener( GameDataUpdateListener l )
    {
        if ( updateListeners == null )
        {
            updateListeners = new GameDataUpdateListener[] { l };
            updateListenerCounts = new int[] { 1 };
        }
        else
        {
            int index = -1;
            for ( int i = 0; i < updateListeners.length; i++ )
            {
                if ( updateListeners[i] == l )
                {
                    index = i;
                    break;
                }
            }
            
            if ( index >= 0 )
            {
                updateListenerCounts[index]++;
                return;
            }
            
            GameDataUpdateListener[] tmp = new GameDataUpdateListener[ updateListeners.length + 1 ];
            System.arraycopy( updateListeners, 0, tmp, 0, updateListeners.length );
            updateListeners = tmp;
            updateListeners[updateListeners.length - 1] = l;
            
            int[] tmp2 = new int[ updateListenerCounts.length + 1 ];
            System.arraycopy( updateListenerCounts, 0, tmp2, 0, updateListenerCounts.length );
            updateListenerCounts = tmp2;
            updateListenerCounts[updateListenerCounts.length - 1] = 1;
        }
    }
    
    public void unregisterDataUpdateListener( GameDataUpdateListener l )
    {
        if ( updateListeners == null )
            return;
        
        int index = -1;
        for ( int i = 0; i < updateListeners.length; i++ )
        {
            if ( updateListeners[i] == l )
            {
                index = i;
                break;
            }
        }
        
        if ( index < 0 )
            return;
        
        if ( updateListenerCounts[index] > 1 )
        {
            updateListenerCounts[index]--;
            return;
        }
        
        if ( updateListeners.length == 1 )
        {
            updateListeners = null;
            updateListenerCounts = null;
            return;
        }
        
        GameDataUpdateListener[] tmp = new GameDataUpdateListener[ updateListeners.length - 1 ];
        if ( index > 0 )
            System.arraycopy( updateListeners, 0, tmp, 0, index );
        if ( index < updateListeners.length - 1 )
            System.arraycopy( updateListeners, index + 1, tmp, index, updateListeners.length - index - 1 );
        updateListeners = tmp;
        
        int[] tmp2 = new int[ updateListenerCounts.length - 1 ];
        if ( index > 0 )
            System.arraycopy( updateListenerCounts, 0, tmp2, 0, index );
        if ( index < updateListenerCounts.length - 1 )
            System.arraycopy( updateListenerCounts, index + 1, tmp2, index, updateListenerCounts.length - index - 1 );
        updateListenerCounts = tmp2;
    }
    
    public void registerGameEventsListener( GameEventsListener l )
    {
        if ( gameEventsListeners == null )
        {
            gameEventsListeners = new GameEventsListener[] { l };
        }
        else
        {
            for ( int i = 0; i < gameEventsListeners.length; i++ )
            {
                if ( gameEventsListeners[i] == l )
                    return;
            }
            
            GameEventsListener[] tmp = new GameEventsListener[ gameEventsListeners.length + 1 ];
            System.arraycopy( gameEventsListeners, 0, tmp, 0, gameEventsListeners.length );
            gameEventsListeners = tmp;
            gameEventsListeners[gameEventsListeners.length - 1] = l;
        }
    }
    
    public void unregisterGameEventsListener( GameEventsListener l )
    {
        if ( gameEventsListeners == null )
            return;
        
        int index = -1;
        for ( int i = 0; i < gameEventsListeners.length; i++ )
        {
            if ( gameEventsListeners[i] == l )
            {
                index = i;
                break;
            }
        }
        
        if ( index < 0 )
            return;
        
        if ( gameEventsListeners.length == 1 )
        {
            gameEventsListeners = null;
            return;
        }
        
        GameEventsListener[] tmp = new GameEventsListener[ gameEventsListeners.length - 1 ];
        if ( index > 0 )
            System.arraycopy( gameEventsListeners, 0, tmp, 0, index );
        if ( index < gameEventsListeners.length - 1 )
            System.arraycopy( gameEventsListeners, index + 1, tmp, index, gameEventsListeners.length - index - 1 );
        gameEventsListeners = tmp;
    }
    
    public final String getGameID()
    {
        return ( gameId );
    }
    
    final _LiveGameDataObjectsFactory getGameDataObjectsFactory()
    {
        return ( gdFactory );
    }
    
    public final GameFileSystem getFileSystem()
    {
        return ( fileSystem );
    }
    
    /**
     * Gets game resolution and viewport information.
     * 
     * @return game resolution and viewport information.
     */
    public final GameResolution getGameResolution()
    {
        return ( gameResolution );
    }
    
    /**
     * Sets the controller.
     * 
     * @param controller
     */
    public void setLiveGameDataController( LiveGameDataController controller )
    {
        this.controller = controller;
    }
    
    /**
     * Gets the controller.
     * 
     * @return the controller.
     */
    public final LiveGameDataController getLiveGameDataController()
    {
        return ( controller );
    }
    
    void setGamePaused( boolean paused, boolean isEditorMode )
    {
        if ( paused == this.gamePaused )
            return;
        
        this.gamePaused = paused;
        
        if ( updateListeners != null )
        {
            for ( int i = 0; i < updateListeners.length; i++ )
            {
                try
                {
                    updateListeners[i].onGamePauseStateChanged( this, isEditorMode, paused );
                }
                catch ( Throwable t )
                {
                    RFDHLog.exception( t );
                }
            }
        }
    }
    
    /**
     * Gets whether the game is paused. Since rFactor1 doesn't tell its plugins about the paused state,
     * this can only be a guess based on the last TelemetryData update. So this info can be up to some splitss of a second late.
     * 
     * @return whether the game is paused.
     */
    public final boolean isGamePaused()
    {
        return ( gamePaused );
    }
    
    void onSessionStarted2( boolean isEditorMode )
    {
        if ( updateListeners != null )
        {
            for ( int i = 0; i < updateListeners.length; i++ )
            {
                try
                {
                    updateListeners[i].onSessionStarted( this, isEditorMode );
                }
                catch ( Throwable t )
                {
                    RFDHLog.exception( t );
                }
            }
        }
    }
    
    void setRealtimeMode( boolean realtimeMode, boolean isEditorMode )
    {
        boolean was = this.realtimeMode;
        
        this.realtimeMode = realtimeMode;
        
        if ( !was && realtimeMode )
        {
            if ( updateListeners != null )
            {
                for ( int i = 0; i < updateListeners.length; i++ )
                {
                    try
                    {
                        updateListeners[i].onRealtimeEntered( this, isEditorMode );
                    }
                    catch ( Throwable t )
                    {
                        RFDHLog.exception( t );
                    }
                }
            }
            
            getTelemetryData().onRealtimeEntered();
            getScoringInfo().onRealtimeEntered();
            getSetup().onRealtimeEntered();
        }
        else if ( was && !realtimeMode )
        {
            if ( updateListeners != null )
            {
                for ( int i = 0; i < updateListeners.length; i++ )
                {
                    try
                    {
                        updateListeners[i].onRealtimeExited( this, isEditorMode );
                    }
                    catch ( Throwable t )
                    {
                        RFDHLog.exception( t );
                    }
                }
            }
            
            getTelemetryData().onRealtimeExited();
            getScoringInfo().onRealtimeExited();
            getSetup().onRealtimeExited();
        }
    }
    
    /**
     * Gets whether we're in realtime mode (cockpit).
     * 
     * @return whether we're in realtime mode (cockpit).
     */
    public final boolean isInRealtimeMode()
    {
        return ( realtimeMode );
    }
    
    /**
     * Gets the vehicle physics model.
     * 
     * @return the vehicle physics model.
     */
    public final VehiclePhysics getPhysics()
    {
        return ( physics );
    }
    
    /**
     * Gets the vehicle setup model.
     * 
     * @return the vehicle setup model.
     */
    public final VehicleSetup getSetup()
    {
        return ( setup );
    }
    
    /**
     * Gets the telemetry data.
     * 
     * @return the telemetry data.
     */
    public final TelemetryData getTelemetryData()
    {
        return ( telemetryData );
    }
    
    /**
     * Gets the scoring info.
     * 
     * @return the scoring info.
     */
    public final ScoringInfo getScoringInfo()
    {
        return ( scoringInfo );
    }
    
    /**
     * Gets the graphics info.
     * 
     * @return the graphics info.
     */
    public final GraphicsInfo getGraphicsInfo()
    {
        return ( graphicsInfo );
    }
    
    /**
     * Gets the commentary request info.
     * 
     * @return the commentary request info.
     */
    public final CommentaryRequestInfo getCommentaryRequestInfo()
    {
        return ( commentaryInfo );
    }
    
    /**
     * Gets the mod info.
     * 
     * @return the mod info.
     */
    public final ModInfo getModInfo()
    {
        return ( modInfo );
    }
    
    /**
     * Gets the vehicle info.
     * 
     * @return the vehicle info.
     */
    public final VehicleInfo getVehicleInfo()
    {
        return ( vehicleInfo );
    }
    
    /**
     * Gets the profile info.
     * 
     * @return the profile info.
     */
    public final ProfileInfo getProfileInfo()
    {
        return ( profileInfo );
    }
    
    /**
     * Gets the track info.
     * 
     * @return the track info.
     */
    public final TrackInfo getTrackInfo()
    {
        return ( trackInfo );
    }
    
    void applyEditorPresets( EditorPresets editorPresets )
    {
        telemetryData.applyEditorPresets( editorPresets );
        scoringInfo.applyEditorPresets( editorPresets );
        setup.applyEditorPresets( editorPresets );
    }
    
    /**
     * Creates an instance of LiveGameData.
     * 
     * @param gameId
     * @param gameResolution
     * @param eventsManager
     */
    public LiveGameData( String gameId, GameResolution gameResolution, GameEventsManager eventsManager )
    {
        registerDataUpdateListener( DataCache.INSTANCE );
        
        this.gameId = gameId;
        
        this.gdFactory = _LiveGameDataObjectsFactory.get( gameId );
        
        this.fileSystem = gdFactory.newGameFileSystem( __UtilHelper.PLUGIN_INI );
        
        this.gameResolution = gameResolution;
        this.telemetryData = new TelemetryData( this, gdFactory );
        this.scoringInfo = new ScoringInfo( this, gdFactory, eventsManager );
        this.graphicsInfo = new GraphicsInfo( this, gdFactory );
        this.commentaryInfo = new CommentaryRequestInfo( this, gdFactory );
        
        this.profileInfo = gdFactory.newProfileInfo( this );
        this.modInfo = gdFactory.newModInfo( this );
        this.vehicleInfo = gdFactory.newVehicleInfo();
        this.trackInfo = gdFactory.newTrackInfo( this );
        
        VehicleSetupParser.loadDefaultSetup( physics, setup );
    }
}
