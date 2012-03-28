See also: https://github.com/intouchfollowup/joda-mybatis

Shell code is here for Joda LocalDate and DateTime data types.  

## Comments

* Your mileage may vary. Working with time zones can yield strange results
* All databases (AFAIK) store dates in UTC (think epoch)
* For consistent results, your JVM should either
	* Be run in UTC (-Dtimezone=UTC)
	* Be in the same timezone as the database server
* Tested with PostgreSQL
	* LocalDate with 'date' data type
	* DateTime with 'timestamp with time zone' data type

Let me know your results

## Example

SQL (PostgreSQL)

	CREATE TABLE foo
	(
	  localdate date,
	  datetime timestamp with time zone
	);

POJO/VO

	public class FooVO
	{
		private LocalDate localdate;
		private DateTime datetime;
		...

iBatis XML

	<resultMap id="fooResultMap" class="intouch.model.core.FooVO" >
		<result column="localdate" property="localdate" jdbcType="DATE" typeHandler="org.joda.time.ibatis.handlers.LocalDateTypeHandlerCallback" />
		<result column="datetime" property="datetime" jdbcType="TIMESTAMP" typeHandler="org.joda.time.ibatis.handlers.DateTimeTypeHandlerCallback" />
	</resultMap>

	<parameterMap id="fooParameterMap" class="intouch.model.core.FooVO" >
		<parameter property="localdate" jdbcType="DATE" typeHandler="org.joda.time.ibatis.handlers.LocalDateTypeHandlerCallback" />
		<parameter property="datetime" jdbcType="TIMESTAMP" typeHandler="org.joda.time.ibatis.handlers.DateTimeTypeHandlerCallback" />
	</parameterMap>

	<select id="loadFoo" resultMap="fooResultMap">
		SELECT	*
		FROM	foo
	</select>

	<insert id="insertFoo" parameterMap="fooParameterMap">
		INSERT INTO foo(localdate, datetime)
		VALUES(?, ?)
	</insert>

Usage

	DateTimeZone zone = DateTimeZone.forID("America/Vancouver");

	FooVO fooVO = new FooVO();
	fooVO.setLocaldate(new LocalDate(2000, 5, 10));
	fooVO.setDatetime(new DateTime(zone));
	logger.debug("LD: " + fooVO.getLocaldate());
	logger.debug("DT: " + fooVO.getDatetime());
	coreDAO.saveFoo(fooVO);

	FooVO loadedFooVO = coreDAO.loadFoo();
	logger.debug("LD: " + loadedFooVO.getLocaldate());
	logger.debug("DT @ Def: " + loadedFooVO.getDatetime());
	logger.debug("DT @ Van: " + loadedFooVO.getDatetime().withZone(zone));

Output (JVM running in UTC)

	LD: 2000-05-10
	DT: 2012-03-27T07:29:26.748-07:00
	LD: 2000-05-10
	DT @ Def: 2012-03-27T14:29:26.748Z
	DT @ Van: 2012-03-27T07:29:26.748-07:00
