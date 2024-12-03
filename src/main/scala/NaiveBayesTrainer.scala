object NaiveBayesTrainer {
  case class Model(
    classProbabilities: Map[Int, Double],
    featureProbabilities: Map[Int, Array[Double]]
  ) {
    def predict(features: Array[Double]): Int = {
      val probabilities = classProbabilities.map { case (label, classProb) =>
        val featureProb = featureProbabilities(label).zip(features).map {
          case (prob, featureValue) => Math.pow(prob, featureValue)
        }.product
        label -> (classProb * featureProb)
      }
      probabilities.maxBy(_._2)._1
    }
  }

  def train(trainingData: Array[(Array[Double], Int)]): Model = {
    val classCounts = trainingData.groupBy(_._2).mapValues(_.length).toMap
    val totalSamples = trainingData.length
    val numFeatures = trainingData.head._1.length

    // Calculate class probabilities with Laplace smoothing
    val classProbabilities = classCounts.map { case (label, count) =>
      label -> ((count + 1).toDouble / (totalSamples + classCounts.size))
    }.toMap

    // Calculate feature probabilities per class with Laplace smoothing
    val featureSums = trainingData.groupBy(_._2).mapValues { samples =>
      val featureSums = Array.fill(numFeatures)(0.0)
      samples.foreach { case (features, _) =>
        features.zipWithIndex.foreach { case (value, index) =>
          featureSums(index) += value
        }
      }
      featureSums
    }.toMap

    val featureProbabilities = featureSums.map { case (label, sums) =>
      val totalFeatures = classCounts(label) + numFeatures // Laplace smoothing
      label -> sums.map(sum => (sum + 1) / totalFeatures)
    }.toMap

    Model(classProbabilities, featureProbabilities)
  }
}
