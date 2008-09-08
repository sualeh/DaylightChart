function Field(name, symbol) {
    this._name = name;
    this._symbol = symbol;
}

Field.prototype.toString = function () {
    return this._name;
};

Field.DEGREES = new Field('degrees', '\u00B0');
Field.MINUTES = new Field('minutes', '"');
Field.SECONDS   = new Field('seconds', '\'');

/**
 * Represents an angle in degrees or radians. Has convenience methods to do
 * trigonometric operations, and normalizations.
 * 
 * @author Sualeh Fatehi
 */
function Angle(radians) {
	
	this._radians: radians;

	this.degrees: function()
	{
		return _radians * 180.0 / Math.PI;
	}
	
	   
	this.radians: function()
	{
		return _radians;
	}
	
	this.sin: function()
	{
		return Math.sin(_radians);
	}
	  
	this.cos: function()
	{
		return Math.cos(_radians);
	}
	
  this.direction: function()
	{
		return "";
	}
	
	this.toString: function()
	{	
	    var absIntDegrees = Math.abs(getField(Field.DEGREES));
	    var absIntMinutes = Math.abs(getField(Field.MINUTES));
	    var absIntSeconds = Math.abs(getField(Field.SECONDS));
	    var direction = this.direction();
	
	    var angleString = "" + absIntDegrees + Field.DEGREES._symbol + " " + absIntMinutes + Field.MINUTES._symbol;
		if (absIntSeconds > 0)
		{
			angleString = angleString + " " + absIntSeconds + Field.SECONDS._symbol;
		}
		if (direction)
		{
		  if (radians < 0)
		  {
			  angleString = '-' + angleString;
		  }
		}
		else
		{
			angleString = angleString + " " + direction; 		
		}
		
		return angleString;
	}
	
}



/**
 * Splits a double value into it's sexagesimal parts. Each part has the same
 * sign as the provided value.
 * 
 * @param value
 *          Value to split
 * @return Split parts
 */
function sexagesimalSplit(value)
{

  var absValue;
  var units;
  var minutes;
  var seconds;
  var sign = value < 0? -1: 1;

  // Calculate absolute integer units
  absValue = Math.abs(value);
  units = Math.floor(absValue);
  seconds = Math.round((absValue - units) * 3600.0);

  // Calculate absolute integer minutes
  minutes = seconds / 60; // Integer arithmetic
  if (minutes == 60)
  {
    minutes = 0;
    units++;
  }
  minutes = Math.floor(minutes);
  
  // Calculate absolute integer seconds
  seconds = seconds % 60;

  // Correct for sign
  units = units * sign;
  minutes = minutes * sign;
  seconds = seconds * sign;

  return [units, minutes, seconds];
  
}
