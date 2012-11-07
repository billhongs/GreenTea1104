package org.ofbiz.recommender.example;

import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.neighborhood.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;
import java.io.*;
import java.util.*;

class RecommenderIntro {

  private RecommenderIntro() {
  }

  public static void main(String[] args) throws Exception {

    DataModel model = new FileDataModel(new File("specialpurpose/recommender/data/intro.csv"));

    UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
    UserNeighborhood neighborhood =
      new NearestNUserNeighborhood(2, similarity, model);

    Recommender recommender = new GenericUserBasedRecommender(
        model, neighborhood, similarity);

    List<RecommendedItem> recommendations =
        recommender.recommend(1, 1);

    for (RecommendedItem recommendation : recommendations) {
      System.out.println(recommendation);
    }

  }

}
