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

import net.ctdp.rfdynhud.gamedata.ProfileInfo.MeasurementUnits;
import net.ctdp.rfdynhud.util.Logger;


/**
 * This is a model of vehicle physics settings.
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class VehiclePhysics
{
    /**
     * Abstraction of a usual physics setting (base_value, step_size, num_steps).
     * 
     * @author Marvin Froehlich (CTDP)
     */
    public static class PhysicsSetting
    {
        private final float factor;
        private final float baseOffset;
        private float baseValue = 0f;
        private float stepSize = 1f;
        private int numSteps = 1;
        
        void set( float baseValue, float stepSize, int numSteps )
        {
            this.baseValue = baseOffset + baseValue;
            this.stepSize = stepSize;
            this.numSteps = numSteps;
        }
        
        public final float getBaseValue()
        {
            return ( baseValue * factor );
        }
        
        public final float getStepSize()
        {
            return ( stepSize * factor );
        }
        
        public final int getNumSteps()
        {
            return ( numSteps );
        }
        
        public final float getMinValue()
        {
            if ( stepSize > 0f )
                return ( baseValue * factor );
            
            return ( ( baseValue + ( numSteps - 1 ) * stepSize ) * factor );
        }
        
        public final float getMaxValue()
        {
            if ( stepSize < 0f )
                return ( baseValue * factor );
            
            return ( ( baseValue + ( numSteps - 1 ) * stepSize ) * factor );
        }
        
        public final float getValueForSetting( int setting )
        {
            // There shuold be a range check. But since this cannot be used for cheating, it isn't necessary.
            
            return ( ( baseValue + stepSize * setting ) * factor );
        }
        
        public final float limitValue( float value )
        {
            return ( Math.max( getMinValue(), Math.min( value, getMaxValue() ) ) );
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return ( getClass().getName() + " { baseOffset: " + baseOffset + ", baseValue: " + baseValue + ", stepSize: " + stepSize + ", numSteps: " + numSteps + " }" );
        }
        
        PhysicsSetting( float factor, float baseOffset )
        {
            this.factor = factor;
            this.baseOffset = baseOffset;
        }
        
        PhysicsSetting()
        {
            this( 1f, 0f );
        }
    }
    
    private final PhysicsSetting fuelRangeL = new PhysicsSetting( 1f, 0f );
    private PhysicsSetting fuelRange = new PhysicsSetting( 1f, 0f );;
    private float weightOfOneLiter = 0.742f; // weight of one liter of fuel in kg
    private final PhysicsSetting frontWingRange = new PhysicsSetting();
    
    /**
     * Gets the phyiscs setting for fule range in liters.
     * 
     * @return the phyiscs setting for fule range in liters.
     */
    public final PhysicsSetting getFuelRangeL()
    {
        return ( fuelRangeL );
    }
    
    /**
     * Gets the phyiscs setting for fule range in the selected units.
     * 
     * @return the phyiscs setting for fule range in the selected units.
     */
    public final PhysicsSetting getFuelRange()
    {
        return ( fuelRange );
    }
    
    /**
     * Gets the weight of one liter of fuel in kg.
     * 
     * @return the weight of one liter of fuel in kg.
     */
    public final float getWeightOfOneLiterOfFuel()
    {
        return ( weightOfOneLiter );
    }
    
    /**
     * Gets the front wing range of settings.
     * 
     * @return the front wing range of settings.
     */
    public final PhysicsSetting getFrontWingRange()
    {
        return ( frontWingRange );
    }
    
    /**
     * Abstraction of possible Wheel drive settings.
     * 
     * @author Marvin Froehlich (CTDP)
     */
    public static enum WheelDrive
    {
        FRONT,
        REAR,
        FOUR,
        ;
        
        public final boolean includesFront()
        {
            return ( ( this == FRONT ) || ( this == FOUR ) );
        }
        
        public final boolean includesRear()
        {
            return ( ( this == REAR ) || ( this == FOUR ) );
        }
    }
    
    WheelDrive wheelDrive;
    
    /**
     * Gets the vehicle's {@link WheelDrive}.
     * 
     * @return the vehicle's {@link WheelDrive}
     */
    public final WheelDrive getWheelDrive()
    {
        return ( wheelDrive );
    }
    
    /**
     * Model of engine physics parameters.
     * 
     * @author Marvin Froehlich (CTDP)
     */
    public static class Engine
    {
        String name = "N/A";
        int lifetimeAverage;
        int lifetimeVariance;
        float baseLifetimeOilTemperature;
        float halfLifetimeOilTempOffset;
        float optimumOilTemperature;
        float wearIncreasePerDegree;
        float baseLifetimeRPM;
        float halfLifetimeRPMOffset;
        private final PhysicsSetting revLimitRange = new PhysicsSetting();
        private final PhysicsSetting boostRange = new PhysicsSetting( 1f, 1f );
        float rpmIncreasePerBoostSetting;
        float fuelUsageIncreasePerBoostSetting;
        float wearIncreasePerBoostSetting;
        float wearIncreasePerVelocity;
        
        private MeasurementUnits measurementUnits = MeasurementUnits.METRIC;
        
        /**
         * Gets the engine's name.
         * 
         * @return the engine's name.
         */
        public final String getName()
        {
            return ( name );
        }
        
        public final int getLifetimeAverage( double raceLengthMultiplier )
        {
            return ( (int)Math.round( lifetimeAverage * raceLengthMultiplier ) );
        }
        
        public final int getLifetimeVariance( double raceLengthMultiplier )
        {
            return ( (int)Math.round( lifetimeVariance * raceLengthMultiplier ) );
        }
        
        public final boolean hasLifetimeVariance()
        {
            return ( lifetimeVariance != 0 );
        }
        
        /**
         * Gets the total lifetime in seconds, that the engine will last for sure.
         * 
         * @param raceLengthMultiplier
         * 
         * @return the total lifetime, that the engine will last for sure.
         */
        public final int getSafeLifetimeTotal( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( lifetimeAverage - lifetimeVariance - lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        /**
         * Gets the total lifetime in seconds, that the engine will most probably hold.
         * 
         * @param raceLengthMultiplier
         * 
         * @return the total lifetime in seconds, that the engine will most probably hold.
         */
        public final int getGoodLifetimeTotal( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( lifetimeAverage - lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        /**
         * Gets the total lifetime seconds of the barrier, where the engine is in really bad shape.
         * 
         * @param raceLengthMultiplier
         * 
         * @return the total lifetime seconds of the barrier, where the engine is in really bad shape.
         */
        public final int getBadLifetimeTotal( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( lifetimeAverage + lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        /**
         * Gets the maximum number of lifetime seconds, that the engine can possibly last.
         * 
         * @param raceLengthMultiplier
         * 
         * @return the maximum number of lifetime seconds, that the engine can possibly last.
         */
        public final int getMaxLifetimeTotal( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( lifetimeAverage + lifetimeVariance + lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        /**
         * Gets the lower bound of lifetime values for the "safe" range (zero).
         * 
         * @param raceLengthMultiplier
         * 
         * @return the lower bound of lifetime values for the "safe" range (zero).
         */
        public final int getLowerSafeLifetimeValue( double raceLengthMultiplier )
        {
            return ( 0 );
        }
        
        /**
         * Gets the lower bound of lifetime values for the "good" range.
         * 
         * @param raceLengthMultiplier
         * 
         * @return the lower bound of lifetime values for the "good" range.
         */
        public final int getLowerGoodLifetimeValue( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( - lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        /**
         * Gets the lower bound of lifetime values for the "bad" range.
         * 
         * @param raceLengthMultiplier
         * 
         * @return the lower bound of lifetime values for the "bad" range.
         */
        public final int getLowerBadLifetimeValue( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( - lifetimeVariance - lifetimeVariance - lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        /**
         * Gets the smalles lifetime value, that your engine can possibly have.
         * 
         * @param raceLengthMultiplier
         * 
         * @return the smalles lifetime value, that your engine can possibly have.
         */
        public final int getMinLifetimeValue( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( - lifetimeVariance - lifetimeVariance - lifetimeVariance - lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        public final int getLifetimeVarianceRange( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( lifetimeVariance + lifetimeVariance + lifetimeVariance + lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        public final int getLifetimeVarianceHalfRange( double raceLengthMultiplier )
        {
            return ( (int)Math.round( ( lifetimeVariance + lifetimeVariance ) * raceLengthMultiplier ) );
        }
        
        public final float getBaseLifetimeOilTemperatureC()
        {
            return ( baseLifetimeOilTemperature );
        }
        
        public final float getBaseLifetimeOilTemperatureF()
        {
            return ( TelemetryData.FAHRENHEIT_OFFSET + baseLifetimeOilTemperature * TelemetryData.FAHRENHEIT_FACTOR );
        }
        
        public final float getBaseLifetimeOilTemperature()
        {
            if ( measurementUnits == MeasurementUnits.IMPERIAL )
                return ( getBaseLifetimeOilTemperatureF() );
            
            return ( getBaseLifetimeOilTemperatureC() );
        }
        
        public final float getHalfLifetimeOilTempOffsetC()
        {
            return ( halfLifetimeOilTempOffset );
        }
        
        public final float getHalfLifetimeOilTempOffsetF()
        {
            return ( TelemetryData.FAHRENHEIT_OFFSET + halfLifetimeOilTempOffset * TelemetryData.FAHRENHEIT_FACTOR );
        }
        
        public final float getHalfLifetimeOilTempOffset()
        {
            if ( measurementUnits == MeasurementUnits.IMPERIAL )
                return ( getHalfLifetimeOilTempOffsetF() );
            
            return ( getHalfLifetimeOilTempOffsetC() );
        }
        
        /**
         * Gets the optimum oil temperature in Celsius. Engine will operatate optimally at this value.
         * 
         * @return the optimum oil temperature.
         */
        public final float getOptimumOilTemperatureC()
        {
            return ( optimumOilTemperature );
        }
        
        /**
         * Gets the optimum oil temperature in Fahrenheit. Engine will operatate optimally at this value.
         * 
         * @return the optimum oil temperature.
         */
        public final float getOptimumOilTemperatureF()
        {
            return ( TelemetryData.FAHRENHEIT_OFFSET + optimumOilTemperature * TelemetryData.FAHRENHEIT_FACTOR );
        }
        
        /**
         * Gets the optimum oil temperature in the selected units. Engine will operatate optimally at this value.
         * 
         * @return the optimum oil temperature.
         */
        public final float getOptimumOilTemperature()
        {
            if ( measurementUnits == MeasurementUnits.IMPERIAL )
                return ( getOptimumOilTemperatureF() );
            
            return ( getOptimumOilTemperatureC() );
        }
        
        /**
         * Gets the temperature value in Celsius at which the engine starts to overheat.
         * This value should serve as a peak level for temperatures during a race.
         * 
         * @return the temperature value at which the engine starts to overheat.
         */
        public final float getOverheatingOilTemperatureC()
        {
            return ( baseLifetimeOilTemperature );
        }
        
        /**
         * Gets the temperature value in Fahrenheit at which the engine starts to overheat.
         * This value should serve as a peak level for temperatures during a race.
         * 
         * @return the temperature value at which the engine starts to overheat.
         */
        public final float getOverheatingOilTemperatureF()
        {
            return ( TelemetryData.FAHRENHEIT_OFFSET + baseLifetimeOilTemperature * TelemetryData.FAHRENHEIT_FACTOR );
        }
        
        /**
         * Gets the temperature value in the selected units at which the engine starts to overheat.
         * This value should serve as a peak level for temperatures during a race.
         * 
         * @return the temperature value at which the engine starts to overheat.
         */
        public final float getOverheatingOilTemperature()
        {
            if ( measurementUnits == MeasurementUnits.IMPERIAL )
                return ( getOverheatingOilTemperatureF() );
            
            return ( getOverheatingOilTemperatureC() );
        }
        
        /**
         * Gets a strong overheating engine temperature in Celsius. At this level the engine will have half of its regular life time.
         * 
         * @return a strong overheating engine temperature.
         */
        public final float getStrongOverheatingOilTemperatureC()
        {
            return ( baseLifetimeOilTemperature + halfLifetimeOilTempOffset );
        }
        
        /**
         * Gets a strong overheating engine temperature in Fahrenheit. At this level the engine will have half of its regular life time.
         * 
         * @return a strong overheating engine temperature.
         */
        public final float getStrongOverheatingOilTemperatureF()
        {
            return ( TelemetryData.FAHRENHEIT_OFFSET + ( baseLifetimeOilTemperature + halfLifetimeOilTempOffset ) * TelemetryData.FAHRENHEIT_FACTOR );
        }
        
        /**
         * Gets a strong overheating engine temperature in the selected units. At this level the engine will have half of its regular life time.
         * 
         * @return a strong overheating engine temperature.
         */
        public final float getStrongOverheatingOilTemperature()
        {
            if ( measurementUnits == MeasurementUnits.IMPERIAL )
                return ( getStrongOverheatingOilTemperatureF() );
            
            return ( getStrongOverheatingOilTemperatureC() );
        }
        
        public final float getWearIncreasePerDegreeC()
        {
            return ( wearIncreasePerDegree );
        }
        
        /**
         * Gets RPM for 'normal' lifetime. No decreased and no increased lifetime.
         * 
         * @return RPM for 'normal' lifetime. No decreased and no increased lifetime.
         */
        public final float getBaseLifetimeRPM()
        {
            return ( baseLifetimeRPM );
        }
        
        public final float getHalfLifetimeRPMOffset()
        {
            return ( halfLifetimeRPMOffset );
        }
        
        /**
         * Gets the range of possible max revs.
         * 
         * @return the range of possible max revs.
         */
        public final PhysicsSetting getRevLimitRange()
        {
            return ( revLimitRange );
        }
        
        /**
         * Gets the range of possible boost mappings.
         * 
         * @return the range of possible boost mappings.
         */
        public final PhysicsSetting getBoostRange()
        {
            return ( boostRange );
        }
        
        public final float getRPMIncreasePerBoostLevel()
        {
            return ( rpmIncreasePerBoostSetting );
        }
        
        public final float getFuelUsageIncreasePerBoostLevel()
        {
            return ( fuelUsageIncreasePerBoostSetting );
        }
        
        public final float getWearIncreasePerBoostLevel()
        {
            return ( wearIncreasePerBoostSetting );
        }
        
        public final float getWearIncreasePerVelocity()
        {
            return ( wearIncreasePerVelocity );
        }
        
        /**
         * Gets the maximum RPM at the given boost level.
         * 
         * @param baseMaxRPM maxRPM coming from {@link TelemetryData#getEngineBaseMaxRPM()}
         * @param boostLevel coming from {@link TelemetryData#getEngineBoostMapping()}
         * 
         * @return the maximum RPM at the given boost level.
         */
        public final float getMaxRPM( float baseMaxRPM, int boostLevel )
        {
            /*
            if ( rpmIncreasePerBoostSetting <= 0f )
                return ( baseMaxRPM );
            
            return ( baseMaxRPM + ( boostRange.getValueForSetting( boostLevel ) - boostRange.getBaseValue() ) * rpmIncreasePerBoostSetting );
            */
            return ( baseMaxRPM + ( boostLevel - boostRange.getBaseValue() ) * rpmIncreasePerBoostSetting );
        }
        
        /**
         * Gets the maximum RPM at the highest (valued) boost mapping.
         * 
         * @param baseMaxRPM maxRPM coming from {@link TelemetryData#getEngineBaseMaxRPM()}
         * 
         * @return the maximum RPM at the highest (valued) boost mapping.
         */
        public final float getMaxRPM( float baseMaxRPM )
        {
            if ( rpmIncreasePerBoostSetting <= 0f )
                return ( baseMaxRPM );
            
            return ( baseMaxRPM + ( boostRange.getMaxValue() - boostRange.getBaseValue() ) * rpmIncreasePerBoostSetting );
        }
        
        public final int getLowestBoostLevel()
        {
            if ( rpmIncreasePerBoostSetting <= 0f )
                return ( (int)boostRange.getMaxValue() );
            
            return ( (int)boostRange.getMinValue() );
        }
        
        public final int getHighestBoostLevel()
        {
            if ( rpmIncreasePerBoostSetting <= 0f )
                return ( (int)boostRange.getMinValue() );
            
            return ( (int)boostRange.getMaxValue() );
        }
        
        Engine()
        {
        }
    }
    
    private final Engine engine = new Engine();
    
    /**
     * Get engine related physics parameters.
     * 
     * @return engine related physics parameters.
     */
    public final Engine getEngine()
    {
        return ( engine );
    }
    
    /**
     * Model of brake physics parameters.
     * 
     * @author Marvin Froehlich (CTDP)
     */
    public static class Brakes
    {
        private final PhysicsSetting balance = new PhysicsSetting();
        
        /**
         * Gets the range of possible brake distribution values.
         * 
         * @return the range of possible brake distribution values.
         */
        public final PhysicsSetting getRearDistributionRange()
        {
            return ( balance );
        }
        
        private final PhysicsSetting pressureRange = new PhysicsSetting();
        
        /**
         * Gets the range for brake pressure values [0, 1].
         * 
         * @return the range for brake pressure values [0, 1].
         */
        public final PhysicsSetting getPressureRange()
        {
            // TODO: Provide in IMPERIAL units, too.
            
            return ( pressureRange );
        }
        
        public static class WheelBrake
        {
            public static final float DEFAULT_BRAKE_FADE_RANGE = Float.MAX_VALUE / 2f;
            
            private float optimumTemperaturesLowerBound;
            private float optimumTemperaturesUpperBound;
            private float coldTemperature;
            private float overheatingTemperature;
            private float wearIncreasePerDegreeOverOptimum;
            private float wearDecreasePerDegreeBelowOptimum;
            float brakeFadeRange = DEFAULT_BRAKE_FADE_RANGE;
            
            private final PhysicsSetting discRange = new PhysicsSetting();
            float wearRate;
            float discFailureAverage;
            float discFailureVariance;
            float torque;
            
            private MeasurementUnits measurementUnits = MeasurementUnits.METRIC;
            
            /**
             * Gets the lower bound of the temperature range in Celsius, where brakes will operate optimally.
             * 
             * @return the lower bound of the temperature range, where brakes will operate optimally.
             */
            public final float getOptimumTemperaturesLowerBoundC()
            {
                return ( optimumTemperaturesLowerBound );
            }
            
            /**
             * Gets the lower bound of the temperature range in Fahrenheit, where brakes will operate optimally.
             * 
             * @return the lower bound of the temperature range, where brakes will operate optimally.
             */
            public final float getOptimumTemperaturesLowerBoundF()
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + optimumTemperaturesLowerBound * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the lower bound of the temperature range in the selected units, where brakes will operate optimally.
             * 
             * @return the lower bound of the temperature range, where brakes will operate optimally.
             */
            public final float getOptimumTemperaturesLowerBound()
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getOptimumTemperaturesLowerBoundF() );
                
                return ( getOptimumTemperaturesLowerBoundC() );
            }
            
            /**
             * Gets the upper bound of the temperature range in Celsius, where brakes will operate optimally.
             * 
             * @return the upper bound of the temperature range, where brakes will operate optimally.
             */
            public final float getOptimumTemperaturesUpperBoundC()
            {
                return ( optimumTemperaturesUpperBound );
            }
            
            /**
             * Gets the upper bound of the temperature range in Fahrenheit, where brakes will operate optimally.
             * 
             * @return the upper bound of the temperature range, where brakes will operate optimally.
             */
            public final float getOptimumTemperaturesUpperBoundF()
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + optimumTemperaturesUpperBound * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the upper bound of the temperature range in the selected units, where brakes will operate optimally.
             * 
             * @return the upper bound of the temperature range, where brakes will operate optimally.
             */
            public final float getOptimumTemperaturesUpperBound()
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getOptimumTemperaturesUpperBoundF() );
                
                return ( getOptimumTemperaturesUpperBoundC() );
            }
            
            /**
             * Gets the temperature level in Celsius under and at which brakes are cold and won't work well.
             * 
             * @return the temperature level under and at which brakes are cold.
             */
            public final float getColdTemperatureC()
            {
                return ( coldTemperature );
            }
            
            /**
             * Gets the temperature level in Fahrenheit under and at which brakes are cold and won't work well.
             * 
             * @return the temperature level under and at which brakes are cold.
             */
            public final float getColdTemperatureF()
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + coldTemperature * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the temperature level in the selected units under and at which brakes are cold and won't work well.
             * 
             * @return the temperature level under and at which brakes are cold.
             */
            public final float getColdTemperature()
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getColdTemperatureF() );
                
                return ( getColdTemperatureC() );
            }
            
            /**
             * Gets the temperature level in Celsius above at at which brakes are overheating and won't work well and increase more than regularly.
             * 
             * @return the temperature level above at at which brakes are overheating.
             */
            public final float getOverheatingTemperatureC()
            {
                return ( overheatingTemperature );
            }
            
            /**
             * Gets the temperature level in Fahrenheit above at at which brakes are overheating and won't work well and increase more than regularly.
             * 
             * @return the temperature level above at at which brakes are overheating.
             */
            public final float getOverheatingTemperatureF()
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + overheatingTemperature * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the temperature level in the selected units above at at which brakes are overheating and won't work well and increase more than regularly.
             * 
             * @return the temperature level above at at which brakes are overheating.
             */
            public final float getOverheatingTemperature()
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getOverheatingTemperatureF() );
                
                return ( getOverheatingTemperatureC() );
            }
            
            public final float getWearincreasePerDegreeCOverOptimum()
            {
                return ( wearIncreasePerDegreeOverOptimum );
            }
            
            public final float getWeardecreasePerDegreeCBelowOptimum()
            {
                return ( wearDecreasePerDegreeBelowOptimum );
            }
    		
            void setTemperatures( float coldTemperature, float optimumTemperaturesLowerBound, float optimumTemperaturesUpperBound, float overheatingTemperature )
            {
                this.coldTemperature = coldTemperature;
                this.optimumTemperaturesLowerBound = optimumTemperaturesLowerBound;
                this.optimumTemperaturesUpperBound = optimumTemperaturesUpperBound;
                this.overheatingTemperature = overheatingTemperature;
            }
            
            public final float getBrakeFadeRangeC()
            {
                return ( brakeFadeRange );
            }
            
            public final float getBrakeFadeColdTemperatureC()
            {
                return ( optimumTemperaturesLowerBound - brakeFadeRange );
            }
            
            public final float getBrakeFadeHotTemperatureC()
            {
                return ( optimumTemperaturesUpperBound + brakeFadeRange );
            }
            
            /**
             * Gets the disc thickness range in meters.
             * 
             * @return the disc thickness range in meters.
             */
            public final PhysicsSetting getDiscRange()
            {
                return ( discRange );
            }
            
            /**
             * Brake disc wear per second at optimum temperature.
             * 
             * @return Brake disc wear per second at optimum temperature
             */
            public final float getWearRate()
            {
                return ( wearRate );
            }
            
            /**
             * Gets the disc thickness at which it fails.
             * 
             * @return the disc thickness at which it fails.
             */
            public final float getDiscFailureAverage()
            {
                return ( discFailureAverage );
            }
            
            /**
             * Gets the disc thickness at which it fails.
             * 
             * @return the disc thickness at which it fails.
             */
            public final float getDiscFailureVariance()
            {
                return ( discFailureVariance );
            }
            
            public final boolean hasDiscFailureVariance()
            {
                return ( ( discFailureVariance < -0.0000001f ) || ( discFailureVariance > +0.0000001f ) );
            }
            
            /**
             * Gets the disc thickness at which it fails.
             * 
             * @return the disc thickness at which it fails.
             */
            public final float getMinDiscFailure()
            {
                return ( discFailureAverage - discFailureVariance - discFailureVariance );
                //return ( discFailureAverage );
            }
            
            public final float getGoodDiscFailure()
            {
                return ( discFailureAverage );
            }
            
            public final float getBadDiscFailure()
            {
                return ( discFailureAverage + discFailureVariance );
            }
            
            /**
             * Gets the disc thickness at which it fails.
             * 
             * @return the disc thickness at which it fails.
             */
            public final float getMaxDiscFailure()
            {
                return ( discFailureAverage + discFailureVariance + discFailureVariance );
            }
            
            public final float getDiscFailureVarianceRange()
            {
                return ( discFailureVariance + discFailureVariance + discFailureVariance + discFailureVariance );
            }
            
            public final float getDiscFailureVarianceHalfRange()
            {
                return ( discFailureVariance + discFailureVariance );
            }
            
            /**
             * Gets brake torque.
             * 
             * @return brake torque.
             */
            public final float getTorque()
            {
                return ( torque );
            }
            
    		WheelBrake()
    		{
    		}
        }
        
        private final WheelBrake brakeFrontLeft = new WheelBrake();
        private final WheelBrake brakeFrontRight = new WheelBrake();
        private final WheelBrake brakeRearLeft = new WheelBrake();
        private final WheelBrake brakeRearRight = new WheelBrake();
        
        /**
         * Gets the brake model of the given wheel.
         * 
         * @param wheel
         * 
         * @return the brake model of the given wheel.
         */
        public final WheelBrake getBrake( Wheel wheel )
        {
            switch ( wheel )
            {
                case FRONT_LEFT:
                    return ( brakeFrontLeft );
                case FRONT_RIGHT:
                    return ( brakeFrontRight );
                case REAR_LEFT:
                    return ( brakeRearLeft );
                case REAR_RIGHT:
                    return ( brakeRearRight );
            }
            
            // Unreachable code!
            return ( null );
        }
    }
    
    private final Brakes brakes = new Brakes();
    
    public final Brakes getBrakes()
    {
        return ( brakes );
    }
    
    /**
     * Model of a tire slip curve.
     * 
     * @author Marvin Froehlich (CTDP)
     */
    public static final class SlipCurve
    {
        String name = null;
        float step = -1f;
        float dropoffFunction = -1f;
        float[] data = new float[ 512 ];
        int dataLength = 0;
        
        /**
         * Gets the slip curve's name.
         * 
         * @return the slip curve's name.
         */
        public final String getName()
        {
            return ( name );
        }
        
        public final float getStep()
        {
            return ( step );
        }
        
        public final float getDropoffFunction()
        {
            return ( dropoffFunction );
        }
        
        public final float[] getData()
        {
            return ( data );
        }
        
        public final int getDataLength()
        {
            return ( dataLength );
        }
        
        SlipCurve()
        {
        }
    }
    
    File usedTBCFile;
    
    public final File getUsedTBCFile()
    {
        return ( usedTBCFile );
    }
    
    /**
     * Model of a tire compound.
     * 
     * @author Marvin Froehlich (CTDP)
     */
    public static class TireCompound
    {
        String name = "N/A";
        int index = -1;
        SlipCurve frontLatitudeSlipCurve = null;
        SlipCurve frontBrakingSlipCurve = null;
        SlipCurve frontTractiveSlipCurve = null;
        SlipCurve rearLatitudeSlipCurve = null;
        SlipCurve rearBrakingSlipCurve = null;
        SlipCurve rearTractiveSlipCurve = null;
        
        /**
         * Gets the compound's name.
         * 
         * @return the compound's name.
         */
        public final String getName()
        {
            return ( name );
        }
        
        /**
         * Gets the compound's index in the list. This is what stands in the setup file.
         * 
         * @return the compound's index in the list.
         */
        public final int getIndex()
        {
            return ( index );
        }
        
        public final SlipCurve getFrontLatitudeSlipCurve()
        {
            return ( frontLatitudeSlipCurve );
        }
        
        public final SlipCurve getFrontBrakingSlipCurve()
        {
            return ( frontBrakingSlipCurve );
        }
        
        public final SlipCurve getFrontTractiveSlipCurve()
        {
            return ( frontTractiveSlipCurve );
        }
        
        public final SlipCurve getRearLatitudeSlipCurve()
        {
            return ( rearLatitudeSlipCurve );
        }
        
        public final SlipCurve getRearBrakingSlipCurve()
        {
            return ( rearBrakingSlipCurve );
        }
        
        public final SlipCurve getRearTractiveSlipCurve()
        {
            return ( rearTractiveSlipCurve );
        }
        
        /**
         * Model of one wheel of a compound. There will always be one {@link CompoundWheel} in a {@link TireCompound} for each wheel of the vehicle.
         * 
         * @author Marvin Froehlich (CTDP)
         */
        public static class CompoundWheel
        {
            private float dryLateralGrip;
            private float dryLongitudinalGrip;
            private float optimumTemperature;
            private float gripLossPerDegreeBelowOptimum;
            private float gripLossPerDegreeAboveOptimum;
            private float optPress;
            private float optPressMult;
            private float offPressure;
            float[] gripFactorPerWear;
            
            //WearGrip1=(0.980,0.961,0.941,0.922,0.902,0.883,0.863,0.844) // Grip at 6/13/19/25/31/38/44/50% wear (defaults to 0.980->0.844), grip is 1.0 at 0% wear
            //WearGrip2=(0.824,0.805,0.785,0.766,0.746,0.727,0.707,0.688) // Grip at 56/63/69/75/81/88/94/100% wear (defaults to 0.824->0.688), tire bursts at 100% wear
            private static final float[] DEFAULT_WEAR_GRIP = { 1.0f, 0.980f, 0.961f, 0.941f, 0.922f, 0.902f, 0.883f, 0.863f, 0.844f, 0.824f, 0.805f, 0.785f, 0.766f, 0.746f, 0.727f, 0.707f, 0.688f };
            
            private MeasurementUnits measurementUnits = MeasurementUnits.METRIC;
            
            void setDryGrip( float laterial, float longitudinal )
            {
                this.dryLateralGrip = laterial;
                this.dryLongitudinalGrip = longitudinal;
            }
            
            /**
             * Gets the lateral grip value for dry weather.
             * Effective grip will always be a fraction of this value depending on tire wear, temperatures, pressure and load.
             * 
             * @return the lateral grip value for dry weather.
             */
            public final float getDryLateralGrip()
            {
                return ( dryLateralGrip );
            }
            
            /**
             * Gets the longitudinal grip value for dry weather.
             * Effective grip will always be a fraction of this value depending on tire wear, temperatures, pressure and load.
             * 
             * @return the longitudinal grip value for dry weather.
             */
            public final float getDryLongitudinalGrip()
            {
                return ( dryLongitudinalGrip );
            }
            
            void setOptimumTemperatureC( float optTemp )
            {
                this.optimumTemperature = optTemp;
            }
            
            /**
             * Gets the temperature value (in Celsius), at which the tire will operate optimally.
             * 
             * @return the temperature value (in Celsius), at which the tire will operate optimally.
             */
            public final float getOptimumTemperatureC()
            {
                return ( optimumTemperature );
            }
            
            /**
             * Gets the temperature value (in Fahrenheit), at which the tire will operate optimally.
             * 
             * @return the temperature value (in Fahrenheit), at which the tire will operate optimally.
             */
            public final float getOptimumTemperatureF()
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + optimumTemperature * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the temperature value (in the selected units), at which the tire will operate optimally.
             * 
             * @return the temperature value (in the selected units), at which the tire will operate optimally.
             */
            public final float getOptimumTemperature()
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getOptimumTemperatureF() );
                
                return ( getOptimumTemperatureC() );
            }
            
            /**
             * @param belowTempC TBC "GripTempPress" value 1
             * @param aboveTemp TBC "GripTempPress" value 2
             * @param offPress TBC "GripTempPress" value 3
             */
            void setAboveAndBelowTempsAndPressures( float belowTempC, float aboveTemp, float offPress )
            {
                float recipOptimumTemperature = ( optimumTemperature != 0.0f ) ? ( 1.0f / optimumTemperature ) : 0.0f;
                
                this.gripLossPerDegreeBelowOptimum = belowTempC * recipOptimumTemperature;
                this.gripLossPerDegreeAboveOptimum = aboveTemp * recipOptimumTemperature;
                
                this.offPressure = offPress;
            }
            
            /**
             * Gets the grip loss (fraction) per degree below {@link #getOptimumTemperature()}.
             * 
             * @return the grip loss (fraction) per degree below {@link #getOptimumTemperature()}.
             */
            public final float getGripLossPerDegreeCBelowOptimum()
            {
                return ( gripLossPerDegreeBelowOptimum );
            }
            
            /**
             * Gets the grip loss (fraction) per degree below {@link #getOptimumTemperature()}.
             * 
             * @return the grip loss (fraction) per degree below {@link #getOptimumTemperature()}.
             */
            public final float getGripLossPerDegreeFBelowOptimum()
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + gripLossPerDegreeBelowOptimum * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the grip loss (fraction) per degree below {@link #getOptimumTemperature()}.
             * 
             * @return the grip loss (fraction) per degree below {@link #getOptimumTemperature()}.
             */
            public final float getGripLossPerDegreeBelowOptimum()
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getGripLossPerDegreeFBelowOptimum() );
                
                return ( getGripLossPerDegreeCBelowOptimum() );
            }
            
            /**
             * Gets the grip loss (fraction) per degree above {@link #getOptimumTemperature()}.
             * 
             * @return the grip loss (fraction) per degree above {@link #getOptimumTemperature()}.
             */
            public final float getGripLossPerDegreeCAboveOptimum()
            {
                return ( gripLossPerDegreeAboveOptimum );
            }
            
            /**
             * Gets the grip loss (fraction) per degree above {@link #getOptimumTemperature()}.
             * 
             * @return the grip loss (fraction) per degree above {@link #getOptimumTemperature()}.
             */
            public final float getGripLossPerDegreeFAboveOptimum()
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + gripLossPerDegreeAboveOptimum * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the grip loss (fraction) per degree above {@link #getOptimumTemperature()}.
             * 
             * @return the grip loss (fraction) per degree above {@link #getOptimumTemperature()}.
             */
            public final float getGripLossPerDegreeAboveOptimum()
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getGripLossPerDegreeFAboveOptimum() );
                
                return ( getGripLossPerDegreeCAboveOptimum() );
            }
            
            /**
             * Gets the temperature in Celsius, that a tire will have at the given grip fraction value.
             * This function will always return a value below {@link #getOptimumTemperature()}.
             * 
             * @param grip
             * 
             * @return the temperature, that a tire will have at the given grip fraction value.
             */
            public final float getBelowTemperatureC( float grip )
            {
                return ( optimumTemperature - ( grip / gripLossPerDegreeBelowOptimum ) );
            }
            
            /**
             * Gets the temperature in Fahrenheit, that a tire will have at the given grip fraction value.
             * This function will always return a value below {@link #getOptimumTemperature()}.
             * 
             * @param grip
             * 
             * @return the temperature, that a tire will have at the given grip fraction value.
             */
            public final float getBelowTemperatureF( float grip )
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + getBelowTemperatureC( grip ) * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the temperature in the selected units, that a tire will have at the given grip fraction value.
             * This function will always return a value below {@link #getOptimumTemperature()}.
             * 
             * @param grip
             * 
             * @return the temperature, that a tire will have at the given grip fraction value.
             */
            public final float getBelowTemperature( float grip )
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getBelowTemperatureF( grip ) );
                
                return ( getBelowTemperatureC( grip ) );
            }
            
            /**
             * Gets the temperature in Celsius, that a tire will have at the given grip fraction value.
             * This function will always return a value above {@link #getOptimumTemperature()}.
             * 
             * @param grip
             * 
             * @return the temperature, that a tire will have at the given grip fraction value.
             */
            public final float getAboveTemperatureC( float grip )
            {
                return ( optimumTemperature + ( grip / gripLossPerDegreeAboveOptimum ) );
            }
            
            /**
             * Gets the temperature in Fahrenheit, that a tire will have at the given grip fraction value.
             * This function will always return a value above {@link #getOptimumTemperature()}.
             * 
             * @param grip
             * 
             * @return the temperature, that a tire will have at the given grip fraction value.
             */
            public final float getAboveTemperatureF( float grip )
            {
                return ( TelemetryData.FAHRENHEIT_OFFSET + getAboveTemperatureC( grip ) * TelemetryData.FAHRENHEIT_FACTOR );
            }
            
            /**
             * Gets the temperature in the selected units, that a tire will have at the given grip fraction value.
             * This function will always return a value above {@link #getOptimumTemperature()}.
             * 
             * @param grip
             * 
             * @return the temperature, that a tire will have at the given grip fraction value.
             */
            public final float getAboveTemperature( float grip )
            {
                if ( measurementUnits == MeasurementUnits.IMPERIAL )
                    return ( getAboveTemperatureF( grip ) );
                
                return ( getAboveTemperatureC( grip ) );
            }
            
            /**
             * Gets the grip fraction value of the tire at the given average temperature.
             * 
             * @param avgTemperatureC average over outer, mittle and inner temperature
             * 
             * @return the grip fraction value of the tire at the given average temperature.
             */
            public final float getGripFactorByTemperatureC( float avgTemperatureC )
            {
                float diffTemp = avgTemperatureC - optimumTemperature;
                
                return ( ( diffTemp < 0.0f ) ? ( gripLossPerDegreeBelowOptimum * -diffTemp ) : ( gripLossPerDegreeAboveOptimum * diffTemp ) );
            }
            
            /**
             * @param optPress TBC "OptimuPressure" field 1
             * @param mult TBC "OptimuPressure" field 2
             */
            void setOptimumPressure( float optPress, float mult )
            {
                this.optPress = optPress;
                this.optPressMult = mult;
            }
            
            /**
             * Gets the optimum tire pressure at the given tire load.
             * 
             * @param load coming from {@link TelemetryData#getTireLoad(Wheel)}
             * 
             * @return the optimum tire pressure at the given tire load.
             */
            public final float getOptimumPressure( float load )
            {
                return ( optPress + ( optPressMult * load ) );
            }
            
            /**
             * Computes the optimum tire pressure for the given grip fraction and load.
             * 
             * @param grip
             * @param load coming from {@link TelemetryData#getTireLoad(Wheel)}
             * 
             * @return the optimum tire pressure for the given grip fraction and load.
             */
            public final float getPressureForGrip( float grip, float load )
            {
                if ( grip <= 0.0f )
                    return ( 0.0f );
                
                float optPressLoad = getOptimumPressure( load );
                float recipOptPress = ( optPressLoad != 0.0f ) ? ( 1.0f / optPressLoad ) : 0.0f;
                
                float offPressureGrip = offPressure * recipOptPress;
                
                return ( Math.abs( optPressLoad + grip / offPressureGrip ) );
            }
            
            /**
             * Computes the grip fraction of the tire at the given pressure and load.
             * 
             * @param pressure coming from {@link TelemetryData#getTirePressure(Wheel)}
             * @param load coming from {@link TelemetryData#getTireLoad(Wheel)}
             * 
             * @return the grip fraction of the tire at the given pressure and load.
             */
            public final float getGripFactorByPressure( float pressure, float load )
            {
                if ( optPress <= 0.0f )
                    return ( 0.0f );
                
                float optPressLoad = getOptimumPressure( load );
                float recipOptPress = ( optPressLoad != 0.0f ) ? ( 1.0f / optPressLoad ) : 0.0f;
                
                float offPressureGrip = offPressure * recipOptPress;
                
                return ( offPressureGrip * Math.abs( pressure - optPressLoad ) );
            }
            
            /**
             * Computes the fraction of maximum grip at the given wear, average temperature, pressure and load.
             * 
             * @param wear see {@link TelemetryData#getTireWear(Wheel)} and {@link #getWearGripFactor(float)}
             * @param avgTemperatureC average over outer, mittle and inner temperature
             * @param pressure coming from {@link TelemetryData#getTirePressure(Wheel)}
             * @param load coming from {@link TelemetryData#getTireLoad(Wheel)}
             * 
             * @return the fraction of maximum grip at the given wear, average temperature, pressure and load.
             */
            public final float getGripFraction( float wear, float avgTemperatureC, float pressure, float load )
            {
                if ( optPress <= 0.0f )
                    return ( 0.0f );
                
                float gfTemp = getGripFactorByTemperatureC( avgTemperatureC );
                float gfPress = getGripFactorByPressure( pressure, load );
                float gfTotal = Math.min( gfTemp + gfPress, 1.0f );
                
                return ( getWearGripFactor( wear ) * ( 1.0f - ( 0.5f * gfTotal * gfTotal ) ) );
            }
            
            private static final float WEAR0  = 0.00f;
            private static final float WEAR1  = 0.06f;
            private static final float WEAR2  = 0.13f;
            private static final float WEAR3  = 0.19f;
            private static final float WEAR4  = 0.25f;
            private static final float WEAR5  = 0.31f;
            private static final float WEAR6  = 0.38f;
            private static final float WEAR7  = 0.44f;
            private static final float WEAR8  = 0.50f;
            private static final float WEAR9  = 0.56f;
            private static final float WEAR10 = 0.63f;
            private static final float WEAR11 = 0.69f;
            private static final float WEAR12 = 0.75f;
            private static final float WEAR13 = 0.81f;
            private static final float WEAR14 = 0.88f;
            private static final float WEAR15 = 0.94f;
            private static final float WEAR16 = 1.00f;
            
            /**
             * Selects the grip fraction at the given wear level.
             * 
             * @param wear coming from {@link TelemetryData#getTireWear(Wheel)}
             * 
             * @return the grip fraction at the given wear level.
             */
            public final float getWearGripFactor( float wear )
            {
                final float[] w = ( gripFactorPerWear == null ) ? DEFAULT_WEAR_GRIP : gripFactorPerWear;
                
                // w[0] will always be set to 1.0.
                
                // WearGrip1=(0.989,0.981,0.9745,0.9715,0.969,0.967,0.9655,0.9645) // Grip at 6/13/19/25/31/38/44/50% wear (defaults to 0.980->0.844), grip is 1.0 at 0% wear
                // WearGrip2=(0.964,0.9638,0.963,0.961,0.9535,0.936,0.850,0.775) // Grip at 56/63/69/75/81/88/94/100% wear (defaults to 0.824->0.688), tire bursts at 100% wear
                
                wear = 1.0f - wear;
                
                if ( wear <= WEAR1 )
                    return ( w[0] - ( w[0] - w[1] ) * ( wear - WEAR0 ) / ( WEAR1 - WEAR0 ) );
                
                if ( wear <= WEAR2 )
                    return ( w[1] - ( w[1] - w[2] ) * ( wear - WEAR1 ) / ( WEAR2 - WEAR1 ) );
                
                if ( wear <= WEAR3 )
                    return ( w[2] - ( w[2] - w[3] ) * ( wear - WEAR2 ) / ( WEAR3 - WEAR2 ) );
                
                if ( wear <= WEAR4 )
                    return ( w[3] - ( w[3] - w[4] ) * ( wear - WEAR3 ) / ( WEAR4 - WEAR3 ) );
                
                if ( wear <= WEAR5 )
                    return ( w[4] - ( w[4] - w[5] ) * ( wear - WEAR4 ) / ( WEAR5 - WEAR4 ) );
                
                if ( wear <= WEAR6 )
                    return ( w[5] - ( w[5] - w[6] ) * ( wear - WEAR5 ) / ( WEAR6 - WEAR5 ) );
                
                if ( wear <= WEAR7 )
                    return ( w[6] - ( w[6] - w[7] ) * ( wear - WEAR6 ) / ( WEAR7 - WEAR6 ) );
                
                if ( wear <= WEAR8 )
                    return ( w[7] - ( w[7] - w[8] ) * ( wear - WEAR7 ) / ( WEAR8 - WEAR7 ) );
                
                if ( wear <= WEAR9 )
                    return ( w[8] - ( w[8] - w[9] ) * ( wear - WEAR8 ) / ( WEAR9 - WEAR8 ) );
                
                if ( wear <= WEAR10 )
                    return ( w[9] - ( w[9] - w[10] ) * ( wear - WEAR9 ) / ( WEAR10 - WEAR9 ) );
                
                if ( wear <= WEAR11 )
                    return ( w[10] - ( w[10] - w[11] ) * ( wear - WEAR10 ) / ( WEAR11 - WEAR10 ) );
                
                if ( wear <= WEAR12 )
                    return ( w[11] - ( w[11] - w[12] ) * ( wear - WEAR11 ) / ( WEAR12 - WEAR11 ) );
                
                if ( wear <= WEAR13 )
                    return ( w[12] - ( w[12] - w[13] ) * ( wear - WEAR12 ) / ( WEAR13 - WEAR12 ) );
                
                if ( wear <= WEAR14 )
                    return ( w[13] - ( w[13] - w[14] ) * ( wear - WEAR13 ) / ( WEAR14 - WEAR13 ) );
                
                if ( wear <= WEAR15 )
                    return ( w[14] - ( w[14] - w[15] ) * ( wear - WEAR14 ) / ( WEAR15 - WEAR14 ) );
                
                if ( wear < WEAR16 )
                    return ( w[15] - ( w[15] - w[16] ) * ( wear - WEAR15 ) / ( WEAR16 - WEAR15 ) );
                
                return ( 0.0f );
            }
            
            public final float getMinGrip()
            {
                final float[] w = ( gripFactorPerWear == null ) ? DEFAULT_WEAR_GRIP : gripFactorPerWear;
                
                return ( w[w.length - 1] );
            }
            
            CompoundWheel()
            {
            }
        }
        
        private final CompoundWheel frontLeft = new CompoundWheel();
        private final CompoundWheel frontRight = new CompoundWheel();
        private final CompoundWheel rearLeft = new CompoundWheel();
        private final CompoundWheel rearRight = new CompoundWheel();
        
        /**
         * Gets the {@link CompoundWheel} for the given wheel.
         * 
         * @param wheel
         * 
         * @return the {@link CompoundWheel} for the given wheel.
         */
        public final CompoundWheel getWheel( Wheel wheel )
        {
            switch ( wheel )
            {
                case FRONT_LEFT:
                    return ( frontLeft );
                case FRONT_RIGHT:
                    return ( frontRight );
                case REAR_LEFT:
                    return ( rearLeft );
                case REAR_RIGHT:
                    return ( rearRight );
            }
            
            // Unreachable code!
            return ( null );
        }
        
        void setOptimumTempForAll4( float optimumTempC )
        {
            frontLeft.optimumTemperature = optimumTempC;
            frontRight.optimumTemperature = optimumTempC;
            rearLeft.optimumTemperature = optimumTempC;
            rearRight.optimumTemperature = optimumTempC;
        }
        
        TireCompound()
        {
        }
    }
    
    private TireCompound[] tireCompounds;
    private TireCompound tcBestGripFrontLeft = null;
    private TireCompound tcBestGripFrontRight = null;
    private TireCompound tcBestGripRearLeft = null;
    private TireCompound tcBestGripRearRight = null;
    
    void setTireCompounds( TireCompound[] tireCompounds )
    {
        this.tireCompounds = tireCompounds;
        
        this.tcBestGripFrontLeft = null;
        this.tcBestGripFrontRight = null;
        this.tcBestGripRearLeft = null;
        this.tcBestGripRearRight = null;
    }
    
    /**
     * Gets the number of available {@link TireCompound}s.
     * 
     * @return the number of available {@link TireCompound}s.
     */
    public final int getNumTireCompounds()
    {
        if ( tireCompounds == null )
            return ( 0 );
        
        return ( tireCompounds.length );
    }
    
    /**
     * Gets the {@link TireCompound} by the given index.
     * 
     * @param index zero-based
     * 
     * @return the {@link TireCompound} by the given index.
     */
    public final TireCompound getTireCompound( int index )
    {
        if ( index >= tireCompounds.length )
        {
            Logger.log( "WARNING: Unknown tire compound index " + index + ". Using closest one." );
            index = tireCompounds.length - 1;
        }
        
        return ( tireCompounds[index] );
    }
    
    /**
     * Gets the {@link TireCompound} for the given wheel, that has the best grip compared to allother available ones.
     * 
     * @param wheel
     * 
     * @return the {@link TireCompound} for the given wheel, that has the best grip.
     */
    public final TireCompound getTireCompoundBestGrip( Wheel wheel )
    {
        if ( tireCompounds == null )
            return ( null );
        
        switch ( wheel )
        {
            case FRONT_LEFT:
                if ( tcBestGripFrontLeft != null )
                    return ( tcBestGripFrontLeft );
                break;
            case FRONT_RIGHT:
                if ( tcBestGripFrontRight != null )
                    return ( tcBestGripFrontRight );
                break;
            case REAR_LEFT:
                if ( tcBestGripRearLeft != null )
                    return ( tcBestGripRearLeft );
                break;
            case REAR_RIGHT:
                if ( tcBestGripRearRight != null )
                    return ( tcBestGripRearRight );
                break;
        }
        
        float maxGrip = 0.0f;
        TireCompound maxGripCompound = null;
        
        for ( int i = 0; i < tireCompounds.length; i++ )
        {
            TireCompound.CompoundWheel tcw = tireCompounds[i].getWheel( wheel );
            float grip = ( tcw.getDryLateralGrip() + tcw.getDryLongitudinalGrip() ) / 2.0f;
            
            if ( grip > maxGrip )
            {
                maxGrip = grip;
                maxGripCompound = tireCompounds[i];
            }
        }
        
        switch ( wheel )
        {
            case FRONT_LEFT:
                this.tcBestGripFrontLeft = maxGripCompound;
                return ( tcBestGripFrontLeft );
            case FRONT_RIGHT:
                this.tcBestGripFrontRight = maxGripCompound;
                return ( tcBestGripFrontRight );
            case REAR_LEFT:
                this.tcBestGripRearLeft = maxGripCompound;
                return ( tcBestGripRearLeft );
            case REAR_RIGHT:
                this.tcBestGripRearRight = maxGripCompound;
                return ( tcBestGripRearRight );
        }
        
        // Unreachable code!
        return ( null );
    }
    
    private final PhysicsSetting tirePressureRangeFL = new PhysicsSetting();
    private final PhysicsSetting tirePressureRangeFR = new PhysicsSetting();
    private final PhysicsSetting tirePressureRangeRL = new PhysicsSetting();
    private final PhysicsSetting tirePressureRangeRR = new PhysicsSetting();
    
    /**
     * Gets the range of possible values for tire pressure.
     */
    public final PhysicsSetting getTirePressureRange( Wheel wheel )
    {
        switch ( wheel )
        {
            case FRONT_LEFT:
                return ( tirePressureRangeFL );
            case FRONT_RIGHT:
                return ( tirePressureRangeFR );
            case REAR_LEFT:
                return ( tirePressureRangeRL );
            case REAR_RIGHT:
                return ( tirePressureRangeRR );
        }
        
        // Unreachable code!
        return ( null );
    }
    
    public static class UpgradeIdentifier
    {
        private final String upgradeType;
        private final String upgradeLevel;
        private final String description;
        
        public final String getUpgradeType()
        {
            return ( upgradeType );
        }
        
        public final String getUpgradeLevel()
        {
            return ( upgradeLevel );
        }
        
        public final String getDescription()
        {
            return ( description );
        }
        
        UpgradeIdentifier( String upgradeType, String upgradeLevel, String description )
        {
            this.upgradeType = upgradeType;
            this.upgradeLevel = upgradeLevel;
            this.description = description;
        }
    }
    
    UpgradeIdentifier[] installedUpgrades = null;
    
    /**
     * Gets the list of installed upgrades.
     * 
     * @return the list of installed upgrades or <code>null</code> if no upgrades are installed.
     */
    public final UpgradeIdentifier[] getInstalledUpgrades()
    {
        return ( installedUpgrades );
    }
    
    private static TireCompound createDefaultCompound( String name, int index, float optTemp )
    {
        float optPress = 64.5f;
        float optPressMult = 0.018841f;
        
        TireCompound tc = new TireCompound();
        
        tc.name = name;
        tc.index = index;
        tc.frontLeft.setDryGrip( 2.19570f, 2.30307f );
        tc.frontLeft.optimumTemperature = optTemp;
        tc.frontLeft.gripFactorPerWear = new float[] { 1.0f, 0.9400f, 0.9371f, 0.9344f, 0.9321f, 0.9301f, 0.9282f, 0.9264f, 0.9248f, 0.9233f, 0.9219f, 0.9205f, 0.9191f, 0.9177f, 0.9161f, 0.9066f, 0.7588f };
        tc.frontLeft.setOptimumPressure( optPress, optPressMult );
        tc.frontLeft.setAboveAndBelowTempsAndPressures( 3.870f, 2.269f, 0.814f );
        tc.frontRight.setDryGrip( 2.19570f, 2.30307f );
        tc.frontRight.optimumTemperature = tc.frontLeft.optimumTemperature;
        tc.frontRight.gripFactorPerWear = tc.frontLeft.gripFactorPerWear;
        tc.frontRight.setOptimumPressure( optPress, optPressMult );
        tc.frontRight.setAboveAndBelowTempsAndPressures( 3.870f, 2.269f, 0.814f );
        tc.rearLeft.setDryGrip( 2.19570f, 2.30307f );
        tc.rearLeft.optimumTemperature = tc.frontLeft.optimumTemperature;
        tc.rearLeft.gripFactorPerWear = tc.frontLeft.gripFactorPerWear;
        tc.rearLeft.setOptimumPressure( optPress, optPressMult );
        tc.rearLeft.setAboveAndBelowTempsAndPressures( 3.870f, 2.269f, 0.814f );
        tc.rearRight.setDryGrip( 2.19570f, 2.30307f );
        tc.rearRight.optimumTemperature = tc.frontLeft.optimumTemperature;
        tc.rearRight.gripFactorPerWear = tc.frontLeft.gripFactorPerWear;
        tc.rearRight.setOptimumPressure( optPress, optPressMult );
        tc.rearRight.setAboveAndBelowTempsAndPressures( 3.870f, 2.269f, 0.814f );
        
        return ( tc );
    }
    
    void applyMeasurementUnits( MeasurementUnits measurementUnits )
    {
        this.engine.measurementUnits = measurementUnits;
        this.brakes.brakeFrontLeft.measurementUnits = measurementUnits;
        this.brakes.brakeFrontRight.measurementUnits = measurementUnits;
        this.brakes.brakeRearLeft.measurementUnits = measurementUnits;
        this.brakes.brakeRearRight.measurementUnits = measurementUnits;
        
        if ( tireCompounds != null )
        {
            for ( int i = 0; i < tireCompounds.length; i++ )
            {
                tireCompounds[i].frontLeft.measurementUnits = measurementUnits;
                tireCompounds[i].frontRight.measurementUnits = measurementUnits;
                tireCompounds[i].rearLeft.measurementUnits = measurementUnits;
                tireCompounds[i].rearRight.measurementUnits = measurementUnits;
            }
        }
        
        this.fuelRange = new PhysicsSetting( ( measurementUnits == MeasurementUnits.IMPERIAL ) ? TelemetryData.LITERS_TO_GALONS : 1f, 0f );
    }
    
    void loadEditorDefaults()
    {
    	try
    	{
    	    wheelDrive = WheelDrive.REAR;
    	    
	        fuelRange.set( 6.0f, 1.0f, 127 );
	        frontWingRange.set( 14.0f, 0.25f, 65 );
	        
	        engine.revLimitRange.set( 20000.0f, 250f, 9 );
	        engine.boostRange.set( 0f, 1.0f, 9 );
            engine.rpmIncreasePerBoostSetting = -200.0f;
            engine.fuelUsageIncreasePerBoostSetting = -0.001f;
            engine.wearIncreasePerBoostSetting = -0.001f;
            engine.wearIncreasePerVelocity = 3.00e-5f;
	        engine.optimumOilTemperature = 109.0f;
            engine.lifetimeAverage = 5000;// 6890;
            engine.lifetimeVariance = 1000;//1600;
	        engine.baseLifetimeOilTemperature = 126.2f; //114.7f;
            engine.halfLifetimeOilTempOffset = 4.15f;
            engine.baseLifetimeRPM = 16680.0f;
            engine.halfLifetimeRPMOffset = 510.0f;
	        
	        brakes.brakeFrontLeft.setTemperatures( 200f, 450f, 750f, 1050f );
	        brakes.brakeFrontRight.setTemperatures( 200f, 450f, 750f, 1050f );
	        brakes.brakeRearLeft.setTemperatures( 200f, 450f, 750f, 1050f );
	        brakes.brakeRearRight.setTemperatures( 200f, 450f, 750f, 1050f );
            
	        brakes.brakeFrontLeft.brakeFadeRange = Brakes.WheelBrake.DEFAULT_BRAKE_FADE_RANGE;
            brakes.brakeFrontRight.brakeFadeRange = Brakes.WheelBrake.DEFAULT_BRAKE_FADE_RANGE;
            brakes.brakeRearLeft.brakeFadeRange = Brakes.WheelBrake.DEFAULT_BRAKE_FADE_RANGE;
            brakes.brakeRearRight.brakeFadeRange = Brakes.WheelBrake.DEFAULT_BRAKE_FADE_RANGE;
	        
	        brakes.brakeFrontLeft.discRange.set( 0.023f, 0.001f, 6 );
            brakes.brakeFrontLeft.wearRate = 5.770e-011f;
            brakes.brakeFrontLeft.discFailureAverage = 1.45e-02f;
            brakes.brakeFrontLeft.discFailureVariance = 7.00e-04f;
            brakes.brakeFrontLeft.torque = 4100.0f;
            
            brakes.brakeFrontRight.discRange.set( 0.023f, 0.001f, 6 );
            brakes.brakeFrontRight.wearRate = 5.770e-011f;
            brakes.brakeFrontRight.discFailureAverage = 1.45e-02f;
            brakes.brakeFrontRight.discFailureVariance = 7.00e-04f;
            brakes.brakeFrontRight.torque = 4100.0f;
            
            brakes.brakeRearLeft.discRange.set( 0.023f, 0.001f, 6 );
            brakes.brakeRearLeft.wearRate = 5.770e-011f;
            brakes.brakeRearLeft.discFailureAverage = 1.45e-02f;
            brakes.brakeRearLeft.discFailureVariance = 7.00e-04f;
            brakes.brakeRearLeft.torque = 4100.0f;
            
            brakes.brakeRearRight.discRange.set( 0.023f, 0.001f, 6 );
            brakes.brakeRearRight.wearRate = 5.770e-011f;
            brakes.brakeRearRight.discFailureAverage = 1.45e-02f;
            brakes.brakeRearRight.discFailureVariance = 7.00e-04f;
            brakes.brakeRearRight.torque = 4100.0f;
            
	        
	        brakes.balance.set( 0.3f, 0.002f, 151 );
	        
	        usedTBCFile = null;
	        
	        TireCompound[] tireCompounds = new TireCompound[ 20 ];
	        
	        tireCompounds[ 0] = createDefaultCompound( "01-Cold",  0, 90.00f );
            tireCompounds[ 1] = createDefaultCompound( "01-Hot",   1, 93.00f );
            tireCompounds[ 2] = createDefaultCompound( "02-Cold",  2, 90.67f );
            tireCompounds[ 3] = createDefaultCompound( "02-Hot",   3, 93.67f );
            tireCompounds[ 4] = createDefaultCompound( "03-Cold",  4, 91.33f );
            tireCompounds[ 5] = createDefaultCompound( "03-Hot",   5, 94.33f );
            tireCompounds[ 6] = createDefaultCompound( "04-Cold",  6, 92.00f );
            tireCompounds[ 7] = createDefaultCompound( "04-Hot",   7, 95.00f );
            tireCompounds[ 8] = createDefaultCompound( "05-Cold",  8, 92.67f );
            tireCompounds[ 9] = createDefaultCompound( "05-Hot",   9, 95.67f );
            tireCompounds[10] = createDefaultCompound( "06-Cold", 10, 93.33f );
            tireCompounds[11] = createDefaultCompound( "06-Hot",  11, 96.33f );
            tireCompounds[12] = createDefaultCompound( "07-Cold", 12, 94.00f );
            tireCompounds[13] = createDefaultCompound( "07-Hot",  13, 97.00f );
            tireCompounds[14] = createDefaultCompound( "08-Cold", 14, 94.67f );
            tireCompounds[15] = createDefaultCompound( "08-Hot",  15, 97.67f );
            tireCompounds[16] = createDefaultCompound( "09-Cold", 16, 95.33f );
            tireCompounds[17] = createDefaultCompound( "09-Hot",  17, 98.33f );
            tireCompounds[18] = createDefaultCompound( "10-Cold", 18, 96.00f );
            tireCompounds[19] = createDefaultCompound( "10-Hot",  19, 99.00f );
            
            setTireCompounds( tireCompounds );
	        
	        tirePressureRangeFL.set( 95.0f, 1.0f, 66 );
            tirePressureRangeFR.set( 95.0f, 1.0f, 66 );
            tirePressureRangeRL.set( 95.0f, 1.0f, 66 );
            tirePressureRangeRR.set( 95.0f, 1.0f, 66 );
		}
    	catch ( Throwable t )
    	{
    		Logger.log( t );
		}
    }
    
    void loadFromPhysicsFiles( ProfileInfo profileInfo, TrackInfo trackInfo )
    {
        File cchFile = profileInfo.getCCHFile();
    	String playerVEHFile = profileInfo.getVehicleFile();
    	
    	try
    	{
    	    long t0 = System.currentTimeMillis();
    	    
			VehiclePhysicsParser.parsePhysicsFiles( cchFile, GameFileSystem.INSTANCE.getGameFolder(), playerVEHFile, trackInfo.getTrackName(), this );
			
			Logger.log( "Successfully parsed physics files. (Took " + ( System.currentTimeMillis() - t0 ) + "ms.)" );
			
			if ( getInstalledUpgrades() == null )
			{
			    Logger.log( "No upgrades installed." );
			}
			else
			{
			    Logger.log( "Installed upgrades:" );
			    for ( UpgradeIdentifier ui : getInstalledUpgrades() )
			        Logger.log( "  " + ui.getUpgradeType() + ", " + ui.getUpgradeLevel()/* + ", " + ui.getDescription()*/ );
			}
			
			if ( getUsedTBCFile() != null )
			    Logger.log( "Used TBC file: " + getUsedTBCFile().getAbsolutePath() );
		}
    	catch ( Throwable t )
    	{
    		Logger.log( t );
		}
    }
    
    public VehiclePhysics()
    {
        // We initialize with factory defaults, so that in case of parsing errors we at least have "some" values.
        loadEditorDefaults();
    }
}
