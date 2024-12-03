import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.DataFrame

object Evaluator {
  def evaluate(predictions: DataFrame): Unit = {
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")

    val accuracy = evaluator.setMetricName("accuracy").evaluate(predictions)
    println(s"Accuracy: $accuracy")

    val f1 = evaluator.setMetricName("f1").evaluate(predictions)
    println(s"F1 Score: $f1")
  }
}

