import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.log4j.Logger

object SparkStreamingProcessor {
  val logger: Logger = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("SparkStreamingProcessor")
      .getOrCreate()

    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(10))

    try {
      // Define the schema for ad impressions data
      val adImpressionsSchema = new StructType()
        .add("impression_id", StringType)
        .add("ad_creative_id", StringType)
        .add("user_id", StringType)
        .add("timestamp", StringType)
        .add("website", StringType)

      // Read ad impressions data from Kafka
      val impressionsDF = spark.readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", "localhost:9092")
        .option("subscribe", "ad_impressions_topic")
        .load()
        .selectExpr("CAST(value AS STRING)")
        .select(from_json($"value", adImpressionsSchema).as("data"))
        .select("data.*")

      // Perform transformations (e.g., count impressions by website)
      val processedDF = impressionsDF
        .groupBy("website")
        .count()

      // Write the results to the console (for demonstration purposes)
      val query = processedDF.writeStream
        .outputMode("complete")
        .format("console")
        .start()

      query.awaitTermination()
    } catch {
      case e: Exception =>
        logger.error("Error in Spark Streaming job: ", e)
    } finally {
      ssc.stop(stopSparkContext = true, stopGracefully = true)
    }
  }
}
