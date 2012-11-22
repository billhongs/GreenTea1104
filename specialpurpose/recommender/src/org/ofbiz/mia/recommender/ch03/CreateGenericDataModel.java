package org.ofbiz.mia.recommender.ch03;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

class CreateGenericDataModel {

  private CreateGenericDataModel() {
  }

  public static void main(String[] args) {
    FastByIDMap<PreferenceArray> preferences =
      new FastByIDMap<PreferenceArray>();
    PreferenceArray prefsForUser1 = new GenericUserPreferenceArray(10);
    prefsForUser1.setUserID(0, 1L);
    prefsForUser1.setItemID(0, 101L);
    prefsForUser1.setValue(0, 3.0f);
    prefsForUser1.setItemID(1, 102L);
    prefsForUser1.setValue(1, 4.5f);
    
    PreferenceArray prefsForUser2 = new GenericUserPreferenceArray(10);
    prefsForUser2.setUserID(0, 2L);
    prefsForUser2.setItemID(0, 101L);
    prefsForUser2.setValue(0, 3.0f);
    prefsForUser2.setItemID(1, 102L);
    prefsForUser2.setValue(1, 4.5f);

    preferences.put(1L, prefsForUser1);
    preferences.put(3L, prefsForUser2);

    DataModel model = new GenericDataModel(preferences);
    System.out.println(model);
  }

}
