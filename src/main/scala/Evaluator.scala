object Evaluator {
  def evaluate(predictions: Array[(Int, Int)]): Unit = {
    val accuracy = predictions.count { case (actual, predicted) => actual == predicted }.toDouble / predictions.length
    println(s"Accuracy: $accuracy")

    val labelCounts = predictions.groupBy(_._1).mapValues(_.length)
    val f1Scores = predictions.groupBy(_._1).map { case (label, instances) =>
      val tp = instances.count { case (actual, predicted) => actual == predicted }
      val fp = predictions.count { case (_, predicted) => predicted == label } - tp
      val fn = labelCounts(label) - tp

      val precision = if (tp + fp > 0) tp.toDouble / (tp + fp) else 0.0
      val recall = if (tp + fn > 0) tp.toDouble / (tp + fn) else 0.0
      val f1 = if (precision + recall > 0) 2 * (precision * recall) / (precision + recall) else 0.0
      f1
    }

    println(s"F1 Scores: $f1Scores")
  }
}
