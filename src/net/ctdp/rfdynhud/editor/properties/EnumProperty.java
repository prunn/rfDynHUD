package net.ctdp.rfdynhud.editor.properties;

import net.ctdp.rfdynhud.widgets.widget.Widget;

public class EnumProperty<E extends Enum<E>> extends Property
{
    private final Widget widget;
    
    private E value;
    
    public final Widget getWidget()
    {
        return ( widget );
    }
    
    protected void onValueChanged( E oldValue, E newValue )
    {
    }
    
    public void setEnumValue( E value )
    {
        if ( value == this.value )
            return;
        
        E oldValue = this.value;
        this.value = value;
        
        widget.forceAndSetDirty();
        
        onValueChanged( oldValue, value );
    }
    
    public final E getEnumValue()
    {
        return ( value );
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public void setValue( Object value )
    {
        setEnumValue( (E)value );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue()
    {
        return ( getEnumValue() );
    }
    
    public final boolean loadProperty( String key, String value )
    {
        if ( key.equals( getPropertyName() ) )
        {
            for ( Enum<?> e : this.value.getClass().getEnumConstants() )
            {
                if ( e.name().equals( value ) )
                {
                    setValue( e );
                }
            }
            
            return ( true );
        }
        
        return ( false );
    }
    
    public EnumProperty( Widget widget, String propertyName, String nameForDisplay, E defaultValue, boolean readonly )
    {
        super( propertyName, nameForDisplay, readonly, PropertyEditorType.ENUM, null, null );
        
        this.widget = widget;
        this.value = defaultValue;
    }
    
    public EnumProperty( Widget widget, String propertyName, String nameForDisplay, E defaultValue )
    {
        this( widget, propertyName, nameForDisplay, defaultValue, false );
    }
    
    public EnumProperty( Widget widget, String propertyName, E defaultValue, boolean readonly )
    {
        this( widget, propertyName, propertyName, defaultValue, readonly );
    }
    
    public EnumProperty( Widget widget, String propertyName, E defaultValue )
    {
        this( widget, propertyName, defaultValue, false );
    }
}