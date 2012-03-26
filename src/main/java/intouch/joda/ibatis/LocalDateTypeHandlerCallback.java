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

import org.joda.time.LocalDate;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class LocalDateTypeHandlerCallback implements TypeHandlerCallback
{

	/* (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com.ibatis.sqlmap.client.extensions.ResultGetter)
	 */
	public Object getResult(ResultGetter getter) throws SQLException
	{
		// Get the java.sql.Date
		Date date = getter.getDate();

		// Handle nulls
		if (getter.wasNull())
			return null;

		// Create new LocalDate
		// Note: timezone info is lost as this is a LocalDate
		return new LocalDate(date);
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
		else if (obj instanceof LocalDate)
		{
			// We have no timezone with a LocalDate so we are left to only use the local timezone for the java.sql.Date
			LocalDate localDate = (LocalDate) obj;

			// Return millis via an Instant
			Date sqlDate = new Date(localDate.toDateMidnight().toInstant().getMillis());
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
		// Assumes format compatible with ISODateTimeFormat.localDateParser()
		return new LocalDate(string);
	}
}
