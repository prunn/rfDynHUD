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
package net.ctdp.rfdynhud.input;

/**
 * The {@link InputMapping} keeps information about a single mapped input component to a Widget.
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class InputMapping
{
    private final String widgetName;
    private final InputAction action;
    private final String deviceComponent;
    
    public final String getWidgetName()
    {
        return ( widgetName );
    }
    
    public final InputAction getAction()
    {
        return ( action );
    }
    
    public final String getDeviceComponent()
    {
        return ( deviceComponent );
    }
    
    public InputMapping( String widgetName, InputAction action, String deviceComponent )
    {
        this.widgetName = widgetName;
        this.action = action;
        this.deviceComponent = deviceComponent;
    }
}
