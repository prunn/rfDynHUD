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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.ctdp.rfdynhud.editor.EditorPresets;
import net.ctdp.rfdynhud.gamedata.VehiclePhysics.Engine;
import net.ctdp.rfdynhud.input.InputAction;
import net.ctdp.rfdynhud.widgets.WidgetsConfiguration;

/**
 * Don't use this at home!
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class __GDPrivilegedAccess
{
    public static final InputAction INPUT_ACTION_RESET_FUEL_CONSUMPTION = FuelUsageRecorder.INPUT_ACTION_RESET_FUEL_CONSUMPTION;
    public static final InputAction INPUT_ACTION_RESET_TOPSPEEDS = TopspeedRecorder.INPUT_ACTION_RESET_TOPSPEEDS;
    public static final InputAction INPUT_ACTION_RESET_LAPTIMES_CACHE = DataCache.INPUT_ACTION_RESET_LAPTIMES_CACHE;
    
    public static final boolean updateProfileInfo( ProfileInfo profileInfo )
    {
        return ( profileInfo.update() );
    }
    
    public static final void updateModInfo( ModInfo modInfo )
    {
        modInfo.update();
    }
    
    public static final void updateTrackInfo( TrackInfo trackInfo )
    {
        trackInfo.update();
    }
    
    public static final void updateInfo( LiveGameData gameData )
    {
        if ( gameData.getProfileInfo().update() )
        {
            gameData.getModInfo().update();
            gameData.getTrackInfo().update();
            
            gameData.getPhysics().applyMeasurementUnits( gameData.getProfileInfo().getMeasurementUnits() );
        }
    }
    
    public static final void setUpdatedInTimescope( VehicleSetup setup )
    {
        setup.updatedInTimeScope = true;
    }
    
    public static final void loadFromPhysicsFiles( String gameId, ProfileInfo profileInfo, TrackInfo trackInfo, VehiclePhysics physics )
    {
        if ( gameId.equals( "rFactor1" ) )
            physics.loadFromPhysicsFiles( profileInfo, trackInfo );
    }
    
    public static void parsePhysicsFiles( File cchFile, File vehicleFile, String trackName, VehiclePhysics physics ) throws Throwable
    {
        VehiclePhysicsParser.parsePhysicsFiles( cchFile, vehicleFile, trackName, physics );
    }
    
    public static final boolean loadSetup( LiveGameData gameData )
    {
        return ( VehicleSetupParser.loadSetup( gameData ) );
    }
    
    public static final void applyEditorPresets( EditorPresets editorPresets, LiveGameData gameData )
    {
        gameData.applyEditorPresets( editorPresets );
    }
    
    public static final void loadFromStream( InputStream in, TelemetryData telemetryData, boolean isEditorMode ) throws IOException
    {
        telemetryData.loadFromStream( in, isEditorMode );
    }
    
    public static final void loadFromStream( InputStream in, ScoringInfo scoringInfo, EditorPresets editorPresets ) throws IOException
    {
        scoringInfo.loadFromStream( in, editorPresets );
    }
    
    public static final void loadFromStream( InputStream in, CommentaryRequestInfo commentaryInfo, boolean isEditorMode ) throws IOException
    {
        commentaryInfo.loadFromStream( in, isEditorMode );
    }
    
    public static final void loadFromStream( InputStream in, GraphicsInfo graphicsInfo, boolean isEditorMode ) throws IOException
    {
        graphicsInfo.loadFromStream( in, isEditorMode );
    }
    
    public static final void onSessionStarted( LiveGameData gameData, boolean isEditorMode )
    {
        gameData.getTelemetryData().onSessionStarted( isEditorMode );
        gameData.getScoringInfo().onSessionStarted( isEditorMode );
        gameData.getSetup().onSessionStarted();
    }
    
    public static final void onSessionStarted2( LiveGameData gameData, boolean isEditorMode )
    {
        gameData.onSessionStarted2( isEditorMode );
    }
    
    public static final void onSessionEnded( LiveGameData gameData )
    {
        gameData.getTelemetryData().onSessionEnded();
        gameData.getScoringInfo().onSessionEnded();
        gameData.getSetup().onSessionEnded();
    }
    
    public static final void setRealtimeMode( boolean realtimeMode, LiveGameData gameData, boolean isEditorMode )
    {
        gameData.setRealtimeMode( realtimeMode, isEditorMode );
    }
    
    public static final void updateSessionTime( LiveGameData gameData, boolean isEditorMode, long timestamp )
    {
        gameData.setGamePaused( gameData.getTelemetryData().checkGamePaused( timestamp ), isEditorMode );
        gameData.getScoringInfo().updateSessionTime( timestamp );
    }
    
    public static final void setEngineBoostMapping( int boost, TelemetryData telemData )
    {
        telemData.setEngineBoostMapping( boost );
    }
    
    public static final void incEngineBoostMapping( TelemetryData telemData, Engine engine )
    {
        telemData.incEngineBoostMapping( engine );
    }
    
    public static final void decEngineBoostMapping( TelemetryData telemData, Engine engine )
    {
        telemData.decEngineBoostMapping( engine );
    }
    
    public static final void setTempBoostFlag( TelemetryData telemData, boolean tempBoostFlag )
    {
        telemData.setTempBoostFlag( tempBoostFlag );
    }
    
    public static final void setTelemVect3( float x, float y, float z, TelemVect3 vect )
    {
        vect.x = x;
        vect.y = y;
        vect.z = z;
    }
    
    public static Float loadFuelUsageFromCache( GameFileSystem fileSystem, final String modName, final String trackName, final String teamName )
    {
        return ( DataCache.loadFuelUsageFromCache( fileSystem, modName, trackName, teamName ) );
    }
    
    public static final boolean setGameResolution( int gameResX, int gameResY, WidgetsConfiguration widgetsConfig )
    {
        return ( widgetsConfig.getGameResolution().setResolution( gameResX, gameResY ) );
    }
    
    public static final boolean setViewport( int x, int y, int w, int h, GameResolution gameRes )
    {
        return ( gameRes.setViewport( x, y, w, h ) );
    }
    
    public static final void toggleFixedViewedVSI( ScoringInfo scoringInfo )
    {
        scoringInfo.toggleFixedViewedVSI();
    }
    
    public static final void setControlledVSIs( ScoringInfo scoringInfo, VehicleScoringInfo controlledViewedVSI, VehicleScoringInfo controlledCompareVSI )
    {
        scoringInfo.setControlledCompareVSI( controlledCompareVSI );
        scoringInfo.setControlledViewedVSI( controlledViewedVSI );
    }
    
    public static final void setAllWidgetsDirty( WidgetsConfiguration widgetsConfig )
    {
        widgetsConfig.setAllDirtyFlags();
    }
}
