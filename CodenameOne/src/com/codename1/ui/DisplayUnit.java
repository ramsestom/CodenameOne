package com.codename1.ui;

import java.util.TreeMap;
import com.codename1.ui.Display;
import com.codename1.ui.plaf.Style;
import com.codename1.util.regex.RE;


public abstract class DisplayUnit {
    
	public static final DisplayUnit PIXEL = new DisplayUnit(Style.UNIT_TYPE_PIXELS, "px", "pixels", "pixel") {
    	protected double conversionFactor() {
        	return 1.0;
        }
    };
    //This PERCENTAGE Display unit is actually a "factice" one that can't be directly used for conversions (as we need to know the reference distance we have to consider to compute the percentage) but its is useful for units parsing and for conversions as long as a reference distance is provided
    public static final DisplayUnit PERCENTAGE = new DisplayUnit(Style.UNIT_TYPE_SCREEN_PERCENTAGE, "%", "percent", "percents") {
    	protected double conversionFactor() {
    		return 0.01; 
        }
    	boolean isReferenceDependant() {
    		return true;
    	}
    };
    public static final DisplayUnit MILLIMETER = new DisplayUnit(Style.UNIT_TYPE_DIPS, "mm", "millimeters", "millimeter") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDeviceDPI()/25.4;
        }
    };
    public static final DisplayUnit CENTIMETER = new DisplayUnit((byte)7, "cm", "centimeters", "centimeter") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDeviceDPI()/2.54;
        }
    };
    public static final DisplayUnit INCH = new DisplayUnit((byte)5, "in", "inch", "inches", "\"") {
    	protected double conversionFactor() {
        	return Display.getInstance().getDeviceDPI();
        }
    };
    public static final DisplayUnit DENSITY_PIXEL = new DisplayUnit(Style.UNIT_TYPE_DP, "dp", "dps") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDeviceDPI()/160.0;
        }
    };
    public static final DisplayUnit SCALABLE_PIXEL = new DisplayUnit(Style.UNIT_TYPE_SP, "sp", "sps", "em", "rem") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDeviceDPI()/160.0*Display.getInstance().getFontScale();
        }
    };
    public static final DisplayUnit POINT = new DisplayUnit((byte)6, "pt", "point", "points") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDeviceDPI()/72.0;
        }
    };
    public static final DisplayUnit PICA = new DisplayUnit((byte)8, "pc", "pica") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDeviceDPI()/6.0;
        }
    };
    public static final DisplayUnit VW = new DisplayUnit((byte)10, "vw", "vws") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDisplayWidth()/100.0;
        }
    };
    public static final DisplayUnit VH = new DisplayUnit((byte)11, "vh", "vws") {
    	protected double conversionFactor() {
    		return Display.getInstance().getDisplayHeight()/100.0;
        }
    };
    public static final DisplayUnit VMIN = new DisplayUnit((byte)12, "vmin", "vmins") {
    	protected double conversionFactor() {
    		return Math.min(Display.getInstance().getDisplayWidth(),Display.getInstance().getDisplayHeight())/100.0;
        }
    };
    public static final DisplayUnit VMAX = new DisplayUnit((byte)13, "vmax", "vmaxs") {
    	protected double conversionFactor() {
    		return Math.max(Display.getInstance().getDisplayWidth(),Display.getInstance().getDisplayHeight())/100.0;
        }
    };
    
    private static final TreeMap<Byte, DisplayUnit> known_units = new TreeMap<Byte, DisplayUnit>();
    static {
    	//Remark: we order them by expected usage frequency to speed up parsing tests as much as we can
    	known_units.put(PIXEL.getCode(), 			PIXEL);
    	known_units.put(PERCENTAGE.getCode(), 		PERCENTAGE);
    	known_units.put(MILLIMETER.getCode(), 		MILLIMETER);
    	known_units.put(DENSITY_PIXEL.getCode(),	DENSITY_PIXEL);
    	known_units.put(SCALABLE_PIXEL.getCode(),	SCALABLE_PIXEL);
    	known_units.put(INCH.getCode(), 			INCH);
    	known_units.put(POINT.getCode(), 			POINT);
    	known_units.put(CENTIMETER.getCode(), 		CENTIMETER);
    	known_units.put(PICA.getCode(), 			PICA);
    	known_units.put(VW.getCode(), 				VW);
    	known_units.put(VH.getCode(),				VH);
    	known_units.put(VMIN.getCode(), 			VMIN);
    	known_units.put(VMAX.getCode(), 			VMAX);
    }
    
    public static DisplayUnit getUnitForCode(byte code) {
    	return known_units.get(code);
    }
    
    public static void registerNewDisplayUnit(DisplayUnit unit) {
    	known_units.put(unit.getCode(), unit);
    }
        
    private byte code;
    private final String[] names;

    DisplayUnit(byte code, String...names) {
    	this.code = code;
        this.names = names;
    }
    
    /**
     * @return the byte code for this unit type that is used internally by codenameone 
     * to store the information of this type in some classes
     */
    public byte getCode() {
    	return this.code;
    }
        
    /**
     * Convert a value to a distance string
     * 
     * @param distance value to convert
     * @return String representation of the distance 
     */
    public String toString(double distance) {
        return distance + toString();
    }

    @Override
    public String toString() {
        return names[0];
    }
    
    /**
     * Does this unit depend from a reference distance to be computed?    
     * @return true id depend from a reference distance to be computed
     */
    boolean isReferenceDependant() {
    	return false;
    }
    
    
    /**
     * @return The conversion factor from this unit to pixels = the number of pixels for 1 unit
    */
    protected abstract double conversionFactor();
    
    
    /**
     * Convert a value into pixels
     * 
     * @param distance: distance in this unit
     * @param reference_distance: the reference distance, in pixels. Only useful for units that depends from another distance like percentage
     * @return value in pixels
     */
    public double toPixels(double distance, double reference_distance) {
        return convert(distance, this, DisplayUnit.PIXEL, reference_distance);
    }
    
    /**
     * Convert a value into pixels
     * 
     * @param distance: distance in this unit
     * @return value in pixels
     */
    public double toPixels(double distance) {
        return toPixels(distance, -1);
    }
        
    
    /**
     * Convert a value given in pixels to a value of this unit
     * 
     * @param distance: distance in pixels
     * @param reference_distance: the reference distance, in pixels. Only useful for units that depends from another distance like percentage
     * @return value in this unit
     */
    public double fromPixels(double distance, double reference_distance) {
        return convert(distance, DisplayUnit.PIXEL, this, reference_distance);
    }
    
    /**
     * Convert a value given in pixels to a value of this unit
     * 
     * @param distance: distance in pixels
     * @return value in this unit
     */
    public double fromPixels(double distance) {
        return fromPixels(distance, -1);
    }
            

    /** 
     * Convert a given value into another unit
     * 
     * @param distance: value in this unit
     * @param unit: source unit
     * @param reference_distance: a reference distance, in pixels. Only useful for units that depends from another distance like percentage
     * @return value in this unit
     * @throws IllegalArgumentException if the conversion could not be done
     */
    public double convert(double distance, DisplayUnit unit, double reference_distance) {
        return convert(distance, this, unit, reference_distance);
    }
        
    /** 
     * Convert a given value into another unit
     * 
     * @param distance: value in this unit
     * @param unit: source unit
     * @return value in this unit
     * @throws IllegalArgumentException if the conversion could not be done
     */
    public double convert(double distance, DisplayUnit unit) {
        return convert(distance, unit, -1);
    }

    
    /**
     * Converts the given distance from the given DistanceUnit, to the given DistanceUnit
     * Warning: do not use this method if one of your DisplayUnit is a unit that depend from another distance (like <code>DistanceUnit.PERCENTAGE</code>)
     * 
     * @param distance Distance to convert
     * @param from     Unit to convert the distance from
     * @param to       Unit of distance to convert to
     * @return Given distance converted to the distance in the given unit
     * @throws IllegalArgumentException if the conversion could not be done
     */
    public static double convert(double distance, DisplayUnit from, DisplayUnit to) {
       return convert(distance, from, to, -1);
    }
        
    /**
     * Converts the given distance from the given DistanceUnit, to the given DistanceUnit
     *
     * @param distance Distance to convert
     * @param from     Unit to convert the distance from
     * @param to       Unit of distance to convert to
     * @return Given distance converted to the distance in the given unit
     * @throws IllegalArgumentException if the conversion could not be done
     */
    public static double convert(double distance, DisplayUnit from, DisplayUnit to, double reference_distance) {
        if (from == to) {
            return distance;
        } else {
        	double result = distance;
        	if (from.isReferenceDependant()) {
        		if (reference_distance<0) {
        			 throw new IllegalArgumentException("you must provide a reference distance to compute distances in [" + from.toString()+ "]");
        		}
        		else {
        			result *= from.conversionFactor()*reference_distance;
        		}
        	}
        	else {
        		result *= from.conversionFactor();
        	}
        	if (to.isReferenceDependant()) {
        		if (reference_distance<0) {
        			 throw new IllegalArgumentException("you must provide a reference distance to compute distances in [" + to.toString()+ "]");
        		}
        		else {
        			result /= (to.conversionFactor()*reference_distance);
        		}
        	}
        	else {
        		result /= to.conversionFactor();
        	}
        	return result;
        }
    }

    /**
     * Parses a given distance and converts it to the specified unit.
     * Warning: this can not work and will throw an IllegalArgumentException for distances expressed in units 
     * that depend from another distance (like <code>DistanceUnit.PERCENTAGE</code>)
     * 
     * @param distance String defining a distance (value and unit)
     * @param defaultUnit unit assumed if none is defined
     * @param to unit of result
     * @return parsed distance
     */
    public static double parseAndConvert(String distance, DisplayUnit defaultUnit, DisplayUnit to) {
    	Distance dist = Distance.parseDistance(distance, defaultUnit);
  		return convert(dist.value, dist.unit, to);
    }
    
    /**
     * Parses a given distance and converts it to the specified unit.
     * Warning: this can not work and will throw an IllegalArgumentException for distances expressed in units 
     * that depend from another distance (like <code>DistanceUnit.PERCENTAGE</code>)
     * 
     * @param distance String defining a distance (value and unit)
     * @param to unit of result
     * @return parsed distance
     */
    public static double parseAndConvert(String distance, DisplayUnit to) {
    	Distance dist = Distance.parseDistance(distance, DisplayUnit.PIXEL);
  		return convert(dist.value, dist.unit, to);
    }

    /**
     * Parses a given distance and converts it to this unit.
     * Warning: this can not work and will throw an IllegalArgumentException for distances expressed in units 
     * that depend from another distance (like <code>DistanceUnit.PERCENTAGE</code>)
     * 
     * @param distance String defining a distance (value and unit)
     * @param defaultUnit unit to expect if none if provided
     * @return parsed distance
     */
    public double parse(String distance, DisplayUnit defaultUnit) {
        return parseAndConvert(distance, defaultUnit, this);
    }
    
    
    /**
     * Parses a given distance and converts it to this unit.
     * Warning: this can not work and will throw an IllegalArgumentException for distances expressed in units 
     * that depend from another distance (like <code>DistanceUnit.PERCENTAGE</code>)
     * 
     * @param distance String defining a distance (value and unit)
     * @return parsed distance
     */
    public double parse(String distance) {
    	return parse(distance, DisplayUnit.PIXEL);
    }
    

    /**
     * Convert a String to a {@link DisplayUnit}
     * 
     * @param unit name of the unit
     * @return unit matching the given name
     * @throws IllegalArgumentException if no unit matches the given name
     */
    public static DisplayUnit fromString(String unit) {
        for (DisplayUnit dunit : known_units.values()) {
            for (String name : dunit.names) {
                if(name.equalsIgnoreCase(unit)) {
                    return dunit;
                }
            }
        }
        throw new IllegalArgumentException("No distance unit match [" + unit + "]");
    }

//    /**
//     * Parses the suffix of a given distance string and return the corresponding {@link DisplayUnit}
//     * 
//     * @param distance string representing a distance
//     * @param defaultUnit default unit to use, if no unit is provided by the string
//     * @return unit of the given distance
//     */
//    public static DisplayUnit parseUnit(String distance, DisplayUnit defaultUnit) {
//        for (DisplayUnit unit : known_units) {
//            for (String name : unit.names) {
//                if(distance.endsWith(name)) {
//                    return unit;
//                }
//            }
//        }
//        return defaultUnit;
//    }
  
    
    
    
    /**
     * This class implements a value+unit tuple.
     */
    public static class Distance implements Comparable<Distance> 
    {
        public final double value;
        public final DisplayUnit unit;

        public Distance(double value, DisplayUnit unit) {
            super();
            this.value = value;
            this.unit = unit;
        }

        /**
         * Converts a {@link Distance} value given in a specific {@link DisplayUnit} into
         * a value equal to the specified value but in a other {@link DisplayUnit}.
         *  Warning: this will not work if this distance or the target on is expressed a unit that depend from another distance (like <code>DistanceUnit.PERCENTAGE</code>)
         * 
         * @param unit unit of the result
         * @return converted distance
         */
        public Distance convert(DisplayUnit unit) {
            if(this.unit == unit) {
                return this;
            } else {
                return new Distance(DisplayUnit.convert(value, this.unit, unit), unit);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            } else if (obj instanceof Distance) {
                Distance other = (Distance) obj;
                return DisplayUnit.convert(value, unit, other.unit) == other.value;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Double.valueOf(value * unit.conversionFactor()).hashCode();
        }

        @Override
        public int compareTo(Distance o) {
        	return compare(value, DisplayUnit.convert(o.value, o.unit, unit));
        }

        @Override
        public String toString() {
            return unit.toString(value);
        }

        /**
         * Parse a {@link Distance} from a given String. If no unit is given
         * <code>DistanceUnit.PIXEL</code> will be used 
         * 
         * @param distance String defining a {@link Distance} 
         * @return parsed {@link Distance}
         */
        public static Distance parseDistance(String distance) {
            return parseDistance(distance, DisplayUnit.PIXEL);
        }

        
        private static final RE distRE = new RE("([\\d\\,\\.]+)([^\\d].*)?");
        
        /**
         * Parse a {@link Distance} from a given String
         * 
         * @param distance String defining a {@link Distance} 
         * @param defaultUnit {@link DisplayUnit} to be assumed
         *          if not unit is provided in the first argument  
         * @return parsed {@link Distance}
         * @throws IllegalArgumentException if the distance failed to be parsed
         */
        private static Distance parseDistance(String distance, DisplayUnit defaultUnit) 
        {
        	String ds = distance.trim();
        	if (distRE.match(ds)) {
           		return parseDistanceElem(distRE.getParen(1), distRE.getParen(2), defaultUnit);
        	}
        	else {
        		throw new IllegalArgumentException("Incorrect format for distance: "+distance);
        	}
        }
        
        
        private static Distance parseDistanceElem(String dval, String dunit, DisplayUnit defaultUnit) 
        {
        	if ((dunit != null) && (dunit.length()>0)) 
        	{
	        	String distunit = dunit.trim();
	        	for (DisplayUnit unit : DisplayUnit.known_units.values()) 
	            {
	                for (String name : unit.names) {
	                	if(distunit.equalsIgnoreCase(name)) {
	                        return new Distance(Double.parseDouble(dval), unit);
	                    }
	                }
	            }
        	}
        	return new Distance(Double.parseDouble(dval), defaultUnit);
        }
        
        
        private static int compare(Double x, Double y) {
    		if (Double.isNaN(x))
        		return Double.isNaN(y) ? 0 : 1;
        	if (Double.isNaN(y))
        		return -1;
        	// recall that 0.0 == -0.0, so we convert to infinites and try again
        	if (x == 0 && y == 0)
        		return (int) (1 / x - 1 / y);
        	if (x == y)
        		return 0;

        	return x > y ? 1 : -1;
    	}
    }
}