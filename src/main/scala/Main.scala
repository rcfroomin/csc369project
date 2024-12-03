object Main {
  def main(args: Array[String]): Unit = {
    val trainDataPath = "train_data.csv"
    val data = DataLoader.loadData(trainDataPath)

    val featureColumns = Array(
      "setting_1", "setting_2", "setting_3",
      "s_1", "s_2", "s_3", "s_4", "s_5", "s_6", "s_7", "s_8", "s_9", "s_10",
      "s_11", "s_12", "s_13", "s_14", "s_15", "s_16", "s_17", "s_18", "s_19", "s_20", "s_21"
    )
    val labelColumn = "RUL"

    val (features, labels) = Preprocessor.transform(data, featureColumns, labelColumn)

    val trainingData = features.zip(labels)
    val model = NaiveBayesTrainer.train(trainingData)

    val predictions = features.map(f => model.predict(f))
    Evaluator.evaluate(labels.zip(predictions))
  }
}
