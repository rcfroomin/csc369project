import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.classification.NaiveBayesModel

object NaiveBayesTrainer {
  def train(trainingData: DataFrame): NaiveBayesModel = {
    val naiveBayes = new NaiveBayes()
      .setModelType("multinomial") 
      .setLabelCol("indexedLabel")
      .setFeaturesCol("features")
      .setSmoothing(1.0)

    naiveBayes.fit(trainingData)
  }
}

