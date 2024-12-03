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
    val lambda = 1.0 // Define lambda (e.g., 1 / t)
    val m_i = 3      // Number of possible values for a feature

    // Calculate class probabilities with Laplace smoothing
    val classProbabilities = classCounts.map { case (label, count) =>
      label -> ((count + lambda) / (totalSamples + lambda * classCounts.size))
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
      val n_j = classCounts(label) // Total count for the class
      label -> sums.map(n_ij => (n_ij + lambda) / (n_j + lambda * m_i))
    }.toMap

    Model(classProbabilities, featureProbabilities)
  }
}
