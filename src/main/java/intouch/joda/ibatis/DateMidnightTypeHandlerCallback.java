/**
 * Copyright (C) 2012 InTouch Technology
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package intouch.joda.ibatis;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

import org.joda.time.DateMidnight;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Joda DateMidnight is an instance (has a timezone).  A DateMidnight has a time but it is always... midnight
 * 
 * NOTE: As java.sql had odd/partial support for timezones... you may experience unexpected results if your 
 * database timezone is different from your JVM timezone (i.e. different machines or different forced timezone parameters
 * 
 * @author Collin Peters
 */
public class DateMidnightTypeHandlerCallback implements TypeHandlerCallback
{
	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com.ibatis.sqlmap.client.extensions.ResultGetter)
	 */
	public Object getResult(ResultGetter getter) throws SQLException
	{
		// Get the java.sql.Date (always exists in the timezone of the JVM)
		Date date = getter.getDate();

		// Handle nulls
		if (getter.wasNull())
			return null;

		// Create new DateMidnight
		// Note: timezone info defaults to JVM timezone
		return new DateMidnight(date);
	}

	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(com.ibatis.sqlmap.client.extensions.ParameterSetter, java.lang.Object)
	 */
	public void setParameter(ParameterSetter setter, Object obj) throws SQLException
	{
		// Handle nulls
		if (obj == null)
		{
			setter.setNull(Types.DATE);
		}
		// Handle the instance we want
		else if (obj instanceof DateMidnight)
		{
			// Cast to DateMidnight
			DateMidnight dateMidnight = (DateMidnight) obj;

			// Return millis (note: Timezone comes through ok here)
			Date sqlDate = new Date(dateMidnight.getMillis());
			setter.setDate(sqlDate);
		}
		else
		{
			throw new IllegalArgumentException("Illegal Date object");
		}

	}

	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#valueOf(java.lang.String)
	 */
	public Object valueOf(String string)
	{
		// Assumes format compatible with ISODateTimeFormat.dateTimeParser
		return new DateMidnight(string);
	}
}
