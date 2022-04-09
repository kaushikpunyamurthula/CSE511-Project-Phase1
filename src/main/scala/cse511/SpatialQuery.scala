package cse511

import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

object SpatialQuery extends App{

  def ST_Contains(queryRectangle: String, pointString: String): Boolean = {
    if(queryRectangle.isEmpty || pointString.isEmpty)
      return false

    val x1 :: y1 :: x2 :: y2 :: _ = queryRectangle.split(",").map(_.trim().toDouble).toList
    val p_x :: p_y :: _ = pointString.split(",").map(_.trim().toDouble).toList

    return ((p_x >= x1 && p_x <= x2 || p_x <= x1 && p_x >= x2) && (p_y >= y1 && p_y <= y2 || p_y <= y1 && p_y >= y2))
  }

  def ST_Within(pointString1: String, pointString2: String, distance: Double): Boolean = {
    if(pointString1.isEmpty || pointString2.isEmpty)
      return false

    val x1 :: y1 :: _ = pointString1.split(",").map(_.trim().toDouble).toList
    val x2 :: y2 :: _ = pointString2.split(",").map(_.trim().toDouble).toList
    val pointsDistance = math.pow(math.pow((x1 - x2), 2) + math.pow((y1 - y2), 2), 0.5)

    return (pointsDistance <= distance)
  }

  def runRangeQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains", (queryRectangle: String, pointString: String)=>((ST_Contains(queryRectangle, pointString))))

    val resultDf = spark.sql("select * from point where ST_Contains('"+arg2+"',point._c0)")
    resultDf.show()

    return resultDf.count()
  }

  def runRangeJoinQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    val rectangleDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    rectangleDf.createOrReplaceTempView("rectangle")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains", (queryRectangle: String, pointString: String)=>((ST_Contains(queryRectangle, pointString))))

    val resultDf = spark.sql("select * from rectangle,point where ST_Contains(rectangle._c0,point._c0)")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within", (pointString1: String, pointString2: String, distance: Double)=>((ST_Within(pointString1, pointString2, distance))))

    val resultDf = spark.sql("select * from point where ST_Within(point._c0,'"+arg2+"',"+arg3+")")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceJoinQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point1")

    val pointDf2 = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    pointDf2.createOrReplaceTempView("point2")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within", (pointString1: String, pointString2: String, distance: Double)=>((ST_Within(pointString1, pointString2, distance))))
    val resultDf = spark.sql("select * from point1 p1, point2 p2 where ST_Within(p1._c0, p2._c0, "+arg3+")")
    resultDf.show()

    return resultDf.count()
  }
}
