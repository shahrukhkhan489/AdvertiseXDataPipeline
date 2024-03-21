import org.apache.spark.sql.SparkSession

object SparkBatchProcessor {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("SparkBatchProcessor")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    try {
        // Read CSV data from HDFS
        val clicksConversionsDF = spark.read
        .option("header", "false")
        .option("inferSchema", "true")
        .csv("/user/flume/clicks_conversions/*/*/*.csv")
        .toDF("timestamp", "user_id", "ad_campaign_id", "conversion_type")

        // Perform transformations (e.g., filter, aggregate)
        val processedDF = clicksConversionsDF
        .filter($"conversion_type" === "purchase")
        .groupBy("ad_campaign_id")
        .count()

        // Store the results in a Hive table
        processedDF.write.mode("overwrite").saveAsTable("ad_campaign_purchases")
    } catch {
        case e: Exception =>
        logger.error("Error in Spark batch processing job: ", e)
    } finally {
        spark.stop()
    }
  }
}
